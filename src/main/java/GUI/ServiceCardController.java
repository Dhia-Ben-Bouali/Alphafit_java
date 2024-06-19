package GUI;

import com.jfoenix.controls.JFXButton;
import entite.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.PackService;
import services.ServiceService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class ServiceCardController {
    @FXML
    private JFXButton delete_btn;

    @FXML
    private JFXButton edit_btn;

    @FXML
    private Text serviceDescription;

    @FXML
    private Label serviceName;
    private ServiceService serviceService;

    private Service service;
    @FXML
    private AnchorPane page;

    @FXML
    private ShowServiceController showServiceController;
    private ServiceListController serviceListController;


    public void setRoot(AnchorPane ap){
        this.page = ap;

    }



    public void setData(Service service){
        this.service = service;
        serviceName.setText(service.getName());


    }
    @FXML
    void handleShowService() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/showService.fxml"));
            Parent showServiceRoot = loader.load();
            showServiceController = loader.getController();
            // Assuming `root` is your existing AnchorPane
            AnchorPane parentPane = (AnchorPane) page.getParent();

            Pane darkOverlay = new Pane();
            darkOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);"); // Adjust the opacity here (0.5 for 50% opacity)
            darkOverlay.setPrefSize(parentPane.getWidth(), parentPane.getHeight());
            parentPane.getChildren().add(darkOverlay);

            page.setOpacity(0.5);
            parentPane.getChildren().add(showServiceRoot);
            showServiceRoot.toFront();
            showServiceController.setData(service);
            showServiceController.setV(page,parentPane,showServiceRoot,darkOverlay);

            // Create the scene
            Scene scene = new Scene(parentPane.getScene().getRoot());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Show Service");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Show the window
            stage.showAndWait();
            parentPane.getChildren().remove(darkOverlay);
            page.setOpacity(1.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDeleteButtonClick() {
        try {

            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirm Deletion");
            confirmAlert.setHeaderText("Are you sure you want to delete this service?");
            confirmAlert.setContentText("This action cannot be undone.");

            Optional<ButtonType> result = confirmAlert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                serviceService = new ServiceService();
                serviceService.deleteById(service.getId());
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Service has been deleted!");
                alert.setHeaderText(null);
                alert.setContentText("deleted successfully.");
                alert.showAndWait();
                serviceListController.refreshServiceList();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to delete service.");
        }
    }
    @FXML
    void handleEditButtonClick() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editService.fxml"));
            Parent root = loader.load();
            EditServiceController editServiceController = loader.getController();
            editServiceController.setData(service);
            // Create the scene
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Edit Service");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Show the window
            stage.showAndWait();
            serviceListController.refreshServiceList();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void setController(ServiceListController serviceListController) {
        this.serviceListController = serviceListController;
    }
}
