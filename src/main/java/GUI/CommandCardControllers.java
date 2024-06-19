package GUI;

import entite.commande;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;

public class CommandCardControllers {
    @FXML
    private Label adr;

    @FXML
    private Label date;

    @FXML
    private Label fname;

    @FXML
    private Label id;

    @FXML
    private Label lname;

    @FXML
    private Label paytype;

    @FXML
    private Label tel;

    @FXML
    private Label total;

    @FXML
    private Label validity;
    commande com;
    CommandListController commandListController;

    @FXML
    void handleShowCommand() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commandDetails.fxml"));
            Parent root = loader.load();
            CommandDetailsController commandDetailsController = loader.getController();
            commandDetailsController.setData(com);
            commandDetailsController.setCommandDetails();
            // Create the scene
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setTitle("Command Details");
            stage.initModality(Modality.APPLICATION_MODAL);
            // Show the window
            stage.showAndWait();
            commandListController.refreshPackageList();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setData(commande c){
        this.com = c;
        id.setText(String.valueOf(c.getId()));
        date.setText(String.valueOf(c.getDate()));
        fname.setText(c.getFirstName());
        lname.setText(c.getLastName());
        adr.setText(c.getAddress());
        tel.setText(c.getPhoneNumber());
        total.setText(String.valueOf(c.getTotale()));
        paytype.setText(c.getPaymentType());
        if(c.isValid())
            validity.setText("Valid");
        else
            validity.setText("Not valid");

    }

    public void setController(CommandListController commandListController) {
        this.commandListController = commandListController;
    }
}
