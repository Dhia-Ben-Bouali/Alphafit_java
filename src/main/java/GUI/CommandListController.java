package GUI;

import entite.commande;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import services.CommandeService;

import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

import org.apache.commons.math3.stat.regression.SimpleRegression;

public class CommandListController implements Initializable {

    @FXML
    private LineChart<String, Number> salesChart;
    @FXML
    private Button exportCsvButton;
    CommandeService commandeService = new CommandeService();
    @FXML
    private ListView<HBox> commandList;
    private List<commande> list= new ArrayList<>();

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> sortComboBox;

    private void loadCommands() {
        try {
            List<commande> commands = commandeService.searchAndSortCommands(searchField.getText(), sortComboBox.getValue());
            updateListView(commands);
        } catch (SQLException e) {
            e.printStackTrace();
            // Ideally, show a user-friendly error message
        }
    }


    private void updateListView(List<commande> commands) {
        commandList.getItems().clear();
        for (commande cmd : commands) {
            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/commandCard.fxml"));
                HBox cardBox = fxmlLoader.load();
                CommandCardControllers cardController = fxmlLoader.getController();
                cardController.setData(cmd);
                cardController.setController(this);
                commandList.getItems().add(cardBox);
            } catch (IOException e) {
                e.printStackTrace();
                // Handle loading errors, possibly show an error message
            }
        }
    }

    private HBox createCommandCard(commande cmd) {
        // Implement the card creation logic
        return new HBox();  // Placeholder
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Set up sorting options in the ComboBox
        sortComboBox.setItems(FXCollections.observableArrayList("date", "totale"));
        sortComboBox.getSelectionModel().selectFirst(); // Select the first sorting option by default.

        // Add listeners to automatically reload the list when the search text or sort option changes
        searchField.textProperty().addListener((observable, oldValue, newValue) -> loadCommands());
        sortComboBox.valueProperty().addListener((observable, oldValue, newValue) -> loadCommands());

        // Load the initial list of commands
        loadCommands();
        setupSalesChart();
    }



    private void setupSalesChart() {
        // Series for actual sales
        XYChart.Series<String, Number> actualSeries = new XYChart.Series<>();
        actualSeries.setName("Actual Sales");

        // Series for predicted sales
        XYChart.Series<String, Number> predictedSeries = new XYChart.Series<>();
        predictedSeries.setName("Predicted Sales");

        try {
            // Fetch actual sales data from the service
            Map<String, Double> salesData = commandeService.getDailySales();
            salesData.forEach((date, value) -> {
                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(date, value);
                actualSeries.getData().add(dataPoint);
            });

            // Fetch predicted sales data from the service
            Map<String, Double> predictions = commandeService.predictSales(salesData);
            predictions.forEach((date, value) -> {
                XYChart.Data<String, Number> dataPoint = new XYChart.Data<>(date, value);
                predictedSeries.getData().add(dataPoint);
            });
        } catch (SQLException e) {
            e.printStackTrace(); // Consider showing a user-friendly error message
        }

        // Add nodes and tooltips to actual data points
        actualSeries.getData().forEach(data -> {
            Node node = new StackPane();
            node.resize(10, 10); // Adjust size for visibility
            data.setNode(node);
            Tooltip.install(node, new Tooltip(data.getXValue() + "\n€" + data.getYValue()));
        });

        // Add nodes and tooltips to predicted data points
        predictedSeries.getData().forEach(data -> {
            Node node = new StackPane();
            node.setStyle("-fx-background-color: blue; -fx-background-radius: 5;"); // Styling for prediction nodes
            node.resize(10, 10);
            data.setNode(node);
            Tooltip.install(node, new Tooltip("Predicted on " + data.getXValue() + "\n€" + data.getYValue()));
        });

        // Add both series to the chart
        salesChart.getData().addAll(actualSeries, predictedSeries);
    }




    private List<commande> commandsList() throws SQLException {
        List<commande> ls = new ArrayList<>();
        CommandeService commandeService= new CommandeService();
        ls = commandeService.getAll();
        for (commande p : ls) {
            System.out.println(p.getId());
        }
        return ls;
    }
    public void refreshPackageList() {
        commandList.getItems().clear();

        try {
            list = new ArrayList<>(commandsList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            for(int i=0;i<list.size();i++){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/commandCard.fxml"));
                HBox cardBox = null;
                cardBox = fxmlLoader.load();
                CommandCardControllers packCardController = fxmlLoader.getController();
                packCardController.setData(list.get(i));
                packCardController.setController(this);
                commandList.getItems().add(cardBox);
            }
        }catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void handleSearch(javafx.event.ActionEvent actionEvent) {
        loadCommands();
    }
    @FXML
    public void handleSort(javafx.event.ActionEvent actionEvent) {
        loadCommands();
    }

    @FXML
    private void handleExportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName("DailySales.csv");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File file = fileChooser.showSaveDialog(null); // Replace 'null' with a reference to your Window if available

        if (file != null) {
            exportDataToCSV(file);
        }
    }

    private void exportDataToCSV(File file) {
        try (PrintWriter writer = new PrintWriter(file)) {
            Map<String, Double> salesData = commandeService.getDailySales();
            writer.println("Date,Total Sales");
            for (Map.Entry<String, Double> entry : salesData.entrySet()) {
                writer.printf("%s,%.2f%n", entry.getKey(), entry.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Show an error message to the user
        }
    }


}