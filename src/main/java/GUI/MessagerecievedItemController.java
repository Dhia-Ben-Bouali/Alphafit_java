package GUI;

import entite.Messagerie;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

import javafx.event.ActionEvent;

public class MessagerecievedItemController {

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
     Button read;

    @FXML
    private Pane details;

    @FXML
    private MessagesRecievedController messagerieController;

    // Méthode pour définir les données du message
    public void setMessageData(Messagerie message) {
        sendername.setText(message.getSender().getNom());
        subject.setText(message.getTitre());
        created_at.setText(message.getCreated_at().toString());
        lettre.setText(String.valueOf(Character.toUpperCase(message.getSender().getNom().charAt(0))));
    }

    // Méthode appelée lors du clic sur le bouton de lecture
    @FXML
    public void handleReadButtonClicked(ActionEvent event) {
        // Vérifier si le contrôleur de messagerie est défini
        System.out.println("===================================================");

        if (messagerieController != null) {
            // Récupérer le message associé à cet élément
            Messagerie message = hbox1.getUserData() instanceof Messagerie ? (Messagerie) hbox1.getUserData() : null;
            // Vérifier si le message est non nul
            if (message != null) {
                // Afficher les détails du message dans le contrôleur de messagerie
                messagerieController.showMessageDetails(message);
                System.out.println("===================================================");
                System.out.println(message);
            }
        }
    }
}
