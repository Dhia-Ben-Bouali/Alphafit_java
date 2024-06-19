package GUI;

import entite.Pack;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import services.PackService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class PackageCardController {
    @FXML
    private Label packName;
    @FXML
    private Label price;
    private PackService packService;

    private Pack pack;
    @FXML
    private ShowPackController showPackController;
    @FXML
    private AnchorPane hello;
    private PackageListController packageListController;


    public void setData(Pack pack){
        this.pack = pack;
        packName.setText(pack.getName());
        price.setText(String.valueOf(pack.getPrice()));

    }
    public void setRoot(AnchorPane ap){
        this.hello = ap;

    }
    @FXML
    void handleShowService() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/showPack.fxml"));
            Parent showServiceRoot = loader.load();
            showPackController = loader.getController();
            // Assuming `root` is your existing AnchorPane
            AnchorPane parentPane = (AnchorPane) hello.getParent();

            Pane darkOverlay = new Pane();
            darkOverlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5);");
            darkOverlay.setPrefSize(parentPane.getWidth(), parentPane.getHeight());
            parentPane.getChildren().add(darkOverlay);

            hello.setOpacity(0.5);
            parentPane.getChildren().add(showServiceRoot);
            showServiceRoot.toFront();
            showPackController.setData(pack);
            showPackController.setV(hello,parentPane,showServiceRoot,darkOverlay);

            // Create the scene
            Scene scene = new Scene(parentPane.getScene().getRoot());
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Show Service");
            stage.initModality(Modality.APPLICATION_MODAL);

            // Show the window
            stage.showAndWait();
            parentPane.getChildren().remove(darkOverlay);
            hello.setOpacity(1.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void handleDeleteButtonClick() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Are you sure you want to delete this package?");
        confirmAlert.setContentText("This action cannot be undone.");

        Optional<ButtonType> result = confirmAlert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            packService = new PackService();
            packService.deleteById(pack.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Package has been deleted!");
            alert.setHeaderText(null);
            alert.setContentText("Deleted successfully.");
            alert.showAndWait();
            packageListController.refreshPackageList();
        }

    }
    @FXML
    void handleEditButtonClick() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/editPack.fxml"));
            Parent root = loader.load();
            EditPackController editPackController = loader.getController();
            editPackController.setData(pack);
            // Create the scene
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Edit Package");
            stage.initModality(Modality.APPLICATION_MODAL);
            // Show the window
            stage.showAndWait();
            packageListController.refreshPackageList();
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


    public void setController(PackageListController packageListController) {
        this.packageListController = packageListController;
    }
}
