package GUI;

import com.jfoenix.controls.JFXTextArea;
import entite.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
/*import services.PDFController;*/
import services.PDFController;
import services.ServiceService;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ServiceListController implements Initializable {
    private List<Service> serviceList;
    @FXML
    private ImageView logo;
    @FXML
    private ListView<HBox> list = new ListView<>();
    @FXML
    private TextField name;
    @FXML
    private JFXTextArea description;
    @FXML
    private Label descriptionError;
    @FXML
    private Label nameError;
    @FXML
    private AnchorPane contentArea;




    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            serviceList = new ArrayList<>(servicesList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        try {
            for(int i=0;i<serviceList.size();i++){
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/serviceCard.fxml"));
                HBox cardBox = fxmlLoader.load();
                ServiceCardController serviceCardController = fxmlLoader.getController();
                serviceCardController.setData(serviceList.get(i));
                serviceCardController.setController(this);
                serviceCardController.setRoot(contentArea);
                list.getItems().add(cardBox);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private List<Service> servicesList() throws SQLException {
        List<Service> ls =new ArrayList<>();
        ServiceService serviceService= new ServiceService();
        ls = serviceService.getAll();
        for (Service service : ls) {
            System.out.println(service.getName());
        }
        return ls;
    }
    @FXML
    private void handleRegisterButtonClick() {

        clearErrorMessages();

        boolean isValid = validateInputs();
        if (!isValid) {
            return;
        }
        ServiceService serviceService= new ServiceService();
        String n = name.getText();
        String descr = description.getText();

        if (serviceService.service_existant(n)) {
            showErrorPopup("Service already exists");
            return;
        }
        Service service = new Service(n,descr);
        try {
            serviceService.Add(service);
            name.setText("");
            description.setText("");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Service has been added!");
            alert.setHeaderText(null);
            alert.setContentText("Added successfully.");
            alert.show();
            refreshServiceList();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void clearErrorMessages() {
        nameError.setText("");
        descriptionError.setText("");
    }
    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private boolean validateInputs() {
        boolean isValid = true;

        // Validate nom
        if (name.getText().isEmpty()) {
            nameError.setText("Please enter the name");
            nameError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Validate prenom
        if (description.getText().isEmpty()) {
            descriptionError.setText("Please enter the description");
            descriptionError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }
        return isValid;
    }
   public void refreshServiceList() {
        list.getItems().clear();

        try {
            serviceList = new ArrayList<>(servicesList());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            for (int i = 0; i < serviceList.size(); i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/serviceCard.fxml"));
                HBox cardBox = fxmlLoader.load();
                ServiceCardController serviceCardController = fxmlLoader.getController();
                serviceCardController.setData(serviceList.get(i));
                list.getItems().add(cardBox);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void handleRefreshButtonClick(){
        refreshServiceList();
    }
    @FXML
    void handlePDFButtonClick() {
       /* PDFController pdfController = new PDFController();
        pdfController.generatePDF();*/
    }

}
