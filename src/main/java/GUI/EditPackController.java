package GUI;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import entite.Pack;
import entite.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.PackService;
import services.ServiceService;

import java.sql.SQLException;

public class EditPackController {
    @FXML
    private JFXButton addButton;

    @FXML
    private JFXTextArea description;

    @FXML
    private TextField name;
    @FXML
    private TextField price;
    private Pack pack;
    @FXML
    private Label descriptionError;
    @FXML
    private Label nameError;
    @FXML
    private Label priceError;
    private PackService packService;
    public void setData(Pack pack){
        this.pack = pack;
        name.setText(pack.getName());
        description.setText(pack.getDescription());
        price.setText(String.valueOf(pack.getPrice()));
    }

    @FXML
    void handleEditButtonClick(ActionEvent event) {
        clearErrorMessages();

        boolean isValid = validateInputs();
        if (!isValid) {
            return;
        }
        int id = pack.getId();
        String n = name.getText();
        int p = Integer.parseInt(price.getText());
        String descr = description.getText();
        if (!isPriceValid(String.valueOf(p))) {
            showErrorPopup("Price must be a number.");
            return;
        }
        Pack pack1 = new Pack(id,n,descr,p,this.pack.getServices());
        PackService packService= new PackService();
        try {
            packService.update(pack1);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Package has been edited!");
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
        priceError.setText("");
    }
    private boolean validateInputs() {
        boolean isValid = true;


        if (name.getText().isEmpty()) {
            nameError.setText("Please enter the name");
            nameError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }


        if (description.getText().isEmpty()) {
            descriptionError.setText("Please enter the description");
            descriptionError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }
        if (price.getText().isEmpty()) {
            priceError.setText("Please enter the price");
            priceError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }
        return isValid;
    }
    private boolean isPriceValid(String priceText) {
        try {
            int price = Integer.parseInt(priceText);
            return true; // Price is a valid integer
        } catch (NumberFormatException e) {
            return false; // Price is not a valid integer
        }
    }
    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
