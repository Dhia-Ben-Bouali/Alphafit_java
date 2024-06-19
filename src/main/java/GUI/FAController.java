package GUI;

import services.ServiceReclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static services.ServiceFA.getReclamationsByDay;

public class FAController {


    @FXML
    private BarChart<String, Number> barChart;

    public void initialize() throws SQLException {
        Map<String, Integer> reclamationsByDay = new HashMap<>();
        reclamationsByDay=getReclamationsByDay();
        displayReclamationsBarChart(reclamationsByDay);
    }
    public void displayReclamationsBarChart(Map<String, Integer> reclamationsParJour) {

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Reclamations par jour");

        // Add data points to the series
        for (Map.Entry<String, Integer> entry : reclamationsParJour.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        // Add the series to the bar chart
        barChart.getData().add(series);
    }


}