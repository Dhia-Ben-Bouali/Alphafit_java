package GUI;

import entite.user;
import services.LoggedInUserManager;
import services.ServiceReclamation;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;


public class ReclamationClientController implements Initializable {

    @FXML
    private HBox hboxvalid;
    @FXML
    private Label labelvalide;
    @FXML
    private Label labelcontenu;

    @FXML
    private Label labeldate;

    @FXML
    private Button saveButton;

    @FXML
    private TextField tcontenu;

    @FXML
    private DatePicker tdate;


    @FXML
    private TextField tuser;

    @FXML
    private Hyperlink  MyRecButton;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    void AjouterReclamation(ActionEvent event) {
        try {
            String contenu = tcontenu.getText();
            LocalDate date = tdate.getValue();

            if (contenu.length() <= 10) {
                labelcontenu.setText("Le contenu doit contenir plus de 10 caractères.");
                highlightErrorFields(tcontenu,labelcontenu);
                resetFieldStylesWithDelay(tcontenu);
                return;
            }

            if (date != null && !date.isBefore(LocalDate.now())) {
                labeldate.setText("La date doit être dans le passé.");
                highlightErrorFields(tdate.getEditor(),labeldate);
                resetFieldStylesWithDelay(tdate.getEditor());
                return;
            }
            user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

            ServiceReclamation.insertReclamation(loggedInUser.getId(), "#", contenu, date);
            highlightValidReclamation(labelvalide,hboxvalid );
            clear();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void MyReclamation(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/MyReclamation.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Client Reclamation");
        stage.show();
    }

    /**********************************************Custom function*****************************************************/

    void resetFieldStyles(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.setStyle(null);
        }
    }

    void resetFieldStylesWithDelay(TextField... textFields) {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> resetFieldStyles(textFields));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /*private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void showAlertsucces(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }*/


    void highlightErrorFields(TextField textField, Label LabelField) {
        textField.setStyle("-fx-border-color: red;");;
        LabelField.setStyle("-fx-font-size: 15;");
        LabelField.setStyle("-fx-text-fill: red;");

    }
    void highlightValidReclamation(Label LabelField, HBox VerticalBox){
        LabelField.setStyle("-fx-font-size: 15; -fx-text-fill: Green;");
        VerticalBox.setStyle("-fx-background-color: #8cff8c; -fx-background-radius: 30px;");
        LabelField.setText("La réclamation a été ajoutée avec succès.");
    }



    void clear(){
        tcontenu.setText(null);
        tdate.setValue(null);
    }
}
