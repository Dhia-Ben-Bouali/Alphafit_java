package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import entite.Messagerie;
import services.LoggedInUserManager;
import services.MessagerieService;
import entite.user;
import  services.ServiceAbonnement;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MessagerieController implements Initializable {

    @FXML
    private AnchorPane content;
    @FXML
    private ListView<Messagerie> messsageListView;

    @FXML
    private TextArea areaTF;
    @FXML
    private Pane details;

    @FXML
    private CheckBox check;

    @FXML
    private TextField titleTF;

    @FXML
    private Label recived, fromdetails, subjectdetails, contentdetails;

    @FXML
    private Label subject;

    @FXML
    private ComboBox<user> to;
    private MessagerecievedItemController messageItemController;


    public void initialize(URL location, ResourceBundle resources) {


        ServiceAbonnement service = new ServiceAbonnement();
        List<user> list = new ArrayList<>();
        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

        String role = loggedInUser.getRoles().toString();




        if (role.contains("ROLE_COACH")) {


            list = service.getAdherentsByCoach(loggedInUser.getId());

        }



        if (role.contains("ROLE_USER"))
        {
            System.out.println("-------------------------------");
            System.out.println(list);

            System.out.println("-------------------------------");

            list = service.getCoachAndNutritionist(loggedInUser.getId());



        }


        System.out.println(list);
        ObservableList<user> adherentsList = FXCollections.observableArrayList(list);
        to.setItems(adherentsList);
        System.out.println(adherentsList);


    }

    public void goToInbox(MouseEvent mouseEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessagesRecieved.fxml"));
            Pane userProfileContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(userProfileContent, 0.0);
            AnchorPane.setBottomAnchor(userProfileContent, 0.0);
            AnchorPane.setLeftAnchor(userProfileContent, 0.0);
            AnchorPane.setRightAnchor(userProfileContent, 0.0);

            // Ajoutez userProfileContent à contentArea tout en conservant la barre latérale et la barre de navigation
            content.getChildren().add(userProfileContent);
            content.getChildren().setAll(userProfileContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void goToAdd(MouseEvent mouseEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMessage.fxml"));
            Pane userProfileContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(userProfileContent, 0.0);
            AnchorPane.setBottomAnchor(userProfileContent, 0.0);
            AnchorPane.setLeftAnchor(userProfileContent, 0.0);
            AnchorPane.setRightAnchor(userProfileContent, 0.0);

            // Ajoutez userProfileContent à contentArea tout en conservant la barre latérale et la barre de navigation
            content.getChildren().add(userProfileContent);
            content.getChildren().setAll(userProfileContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void goToSentMessages(MouseEvent mouseEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessagesSent.fxml"));
            Pane userProfileContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(userProfileContent, 0.0);
            AnchorPane.setBottomAnchor(userProfileContent, 0.0);
            AnchorPane.setLeftAnchor(userProfileContent, 0.0);
            AnchorPane.setRightAnchor(userProfileContent, 0.0);

            // Ajoutez userProfileContent à contentArea tout en conservant la barre latérale et la barre de navigation
            content.getChildren().add(userProfileContent);
            content.getChildren().setAll(userProfileContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

    }


    // Inner class for custom list cell
    static class MessageListCell extends ListCell<Messagerie> {
        @Override
        protected void updateItem(Messagerie message, boolean empty) {
            super.updateItem(message, empty);

            if (empty || message == null) {
                setText(null);
                setGraphic(null);
            } else {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/messageirecievedtem.fxml"));
                    AnchorPane pane = loader.load();

                    // Access controller of the loaded FXML
                    MessagerecievedItemController controller = loader.getController();

                    // Pass message data to the controller
                    controller.setMessageData(message);

                    // Set the custom cell
                    setGraphic(pane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    void reset(ActionEvent event) {
        titleTF.setText("");
        areaTF.setText("");
        check.setSelected(false);
    }


    public void handleExitButtonClick(ActionEvent event) {

        details.setVisible(false);
    }

    public void showMessageDetails(Messagerie message) {
        fromdetails.setText(message.getSender().getNom());
        subjectdetails.setText(message.getTitre());
        contentdetails.setText(message.getContenu());
        details.setVisible(true);
    }

    public void setMessageItemController(MessagerecievedItemController messageItemController) {
        this.messageItemController = messageItemController;
    }


    @FXML
    void send(ActionEvent event) {
        String titre = titleTF.getText();
        String contenu = areaTF.getText();
        boolean isFavorite = check.isSelected();

        String errorMessage = "";

        if (titre.isEmpty() || contenu.isEmpty()) {
            errorMessage += "- Please fill in all fields.\n";
        }

        if (!titre.matches("[a-zA-Z]+")) {
            errorMessage += "- Subject must contain only alphabetical characters.\n";
        }

        if (!errorMessage.isEmpty()) {
            // Afficher une alerte d'erreur avec tous les messages regroupés
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(errorMessage);
            alert.showAndWait();
        } else {
            user sender = LoggedInUserManager.getInstance().getLoggedInUser();


            user recipient = (user) to.getSelectionModel().getSelectedItem();
            Messagerie message = new Messagerie(sender, recipient, titre, contenu, isFavorite);

            MessagerieService messagerieService = new MessagerieService();
            messagerieService.Add(message);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Success");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Message sent successfully.");
            successAlert.showAndWait();

            titleTF.clear();
            areaTF.clear();
            check.setSelected(false);
        }
    }


    public void goToFavoraiteMessages(MouseEvent mouseEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessagesFavoriate.fxml"));
            Pane userProfileContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(userProfileContent, 0.0);
            AnchorPane.setBottomAnchor(userProfileContent, 0.0);
            AnchorPane.setLeftAnchor(userProfileContent, 0.0);
            AnchorPane.setRightAnchor(userProfileContent, 0.0);

            // Ajoutez userProfileContent à contentArea tout en conservant la barre latérale et la barre de navigation
            content.getChildren().add(userProfileContent);
            content.getChildren().setAll(userProfileContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

    }
}
