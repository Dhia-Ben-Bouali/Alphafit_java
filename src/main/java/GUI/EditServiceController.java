package GUI;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import entite.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import services.ServiceService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class EditServiceController {
    @FXML
    private JFXButton addButton;

    @FXML
    private JFXTextArea description;

    @FXML
    private TextField name;
    private Service service;
    @FXML
    private Label descriptionError;
    @FXML
    private Label nameError;
    private ServiceService serviceService;
    public void setData(Service service){
        this.service = service;
        name.setText(service.getName());
        description.setText(service.getDescription());

    }

    @FXML
    void handleEditButtonClick(ActionEvent event) {
        clearErrorMessages();

        boolean isValid = validateInputs();
        if (!isValid) {
            return;
        }
        int id = service.getId();
        String n = name.getText();
        String descr = description.getText();
        Service service = new Service(id,n,descr);
        ServiceService serviceService= new ServiceService();
        try {
            serviceService.update(service);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Service has been edited!");
            alert.setHeaderText(null);
            alert.setContentText("edited successfully.");
            alert.show();
            Node sourceNode = (Node) event.getSource();
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    private void clearErrorMessages() {
        nameError.setText("");
        descriptionError.setText("");
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
}
