package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import entite.Messagerie;

import javafx.scene.control.Label;

public class MessagesentItemController {

    @FXML
    private HBox hbox1;

    @FXML
    private Label lettre;

    @FXML
    private Label sendername;

    @FXML
    private Label subject;

    @FXML
    private Label created_at;

    @FXML
    Button read,delete,update;



    @FXML
    private StackPane details;
    @FXML
    private AnchorPane updatepane;

    private MessagesSentController parentController;

    public void setMessagesSentController(MessagesSentController parentController) {
        this.parentController = parentController;
    }




    public void setMessageData(Messagerie message) {
        // Mettez à jour les éléments de l'interface utilisateur avec les données du message
        sendername.setText(message.getRecipient().getNom());
        subject.setText(message.getTitre());
        created_at.setText(message.getCreated_at().toString());
        lettre.setText(String.valueOf(Character.toUpperCase(message.getSender().getNom().charAt(0))));
    }

    public void updateMessageData(Messagerie message) {
        // Mettez à jour les champs de l'AnchorPane Update avec les données du message
        // Par exemple:
       // subject.setText(message.getTitre());
        // Autres champs à mettre à jour
    }

    public void handleUpdateButtonClicked(ActionEvent event) {

        updatepane.setVisible(true);
    }

    public void handleReadButtonClicked(ActionEvent event) {

    }


    public void handleDeleteButtonClicked(ActionEvent event) {


    }


}



