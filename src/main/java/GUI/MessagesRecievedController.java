package GUI;

import entite.Messagerie;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import entite.Messagerie;
import services.LoggedInUserManager;
import services.MessagerieService;
import entite.user;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import javafx.event.ActionEvent;






public class MessagesRecievedController implements Initializable {

    @FXML
    private ListView<Messagerie> messsageListView;

    @FXML
    private TextArea areaTF;
    @FXML
    private StackPane details;

    @FXML
    private CheckBox check;

    @FXML
    private TextField titleTF;

    @FXML
    private Label recived,fromdetails,subjectdetails,contentdetails,created_atdetails,lettre;

    @FXML
    private Label subject;


    private MessagerecievedItemController messageItemController;


    @FXML
    private TextField searchField;

    private List<Messagerie> allMessages;
    private FilteredList<Messagerie> filteredMessages;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("yessssssssssssssssssssssssssssssssss");
        // Set custom cell factory
        details.setVisible(false);

        // Load data into the ListView
        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();
        System.out.println(loggedInUser);

        MessagerieService service = new MessagerieService();
        allMessages = service.getAllByRecipient(loggedInUser.getId());
        messsageListView.getItems().addAll(allMessages);

        // Créer un FilteredList lié à la liste observable de messages
        filteredMessages = new FilteredList<>(messsageListView.getItems());

        // Appliquer le filtre lorsque le texte de recherche change
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filterMessages(newValue.trim().toLowerCase()));

        // Afficher les messages filtrés dans la ListView
        messsageListView.setItems(filteredMessages);

        // Utiliser votre CellFactory pour personnaliser l'affichage des messages dans la ListView
        messsageListView.setCellFactory(listView -> new MessageListCell());
    }

    private void filterMessages(String searchText) {
        if (searchText.isEmpty()) {
            filteredMessages.setPredicate(null); // Afficher tous les messages si la recherche est vide
        } else {
            // Filtrer les messages par nom de l'expéditeur
            Predicate<Messagerie> filterBySenderName = message ->
                    message.getSender().getNom().toLowerCase().contains(searchText);
            filteredMessages.setPredicate(filterBySenderName);
        }
    }
    public void goToInbox(MouseEvent mouseEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessagesRecieved.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Inbox");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToAdd(MouseEvent mouseEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMessage.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Add Message");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void goToFavoraiteMessages(MouseEvent mouseEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessagesFavoriate.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sent Messages ");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goToSentMessages(MouseEvent mouseEvent) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MessagesSent.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Sent Messages ");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public class MessageListCell extends ListCell<Messagerie> {
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

                    MessagerecievedItemController controller = loader.getController();

                    controller.setMessageData(message);

                    setGraphic(pane);

                    Button readButton = controller.read;


                    readButton.setOnAction(event -> {
                        Messagerie selectedMessage = getItem();
                        details.setVisible(true);
                        fromdetails.setText(selectedMessage.getSender().getEmail());
                        subjectdetails.setText(selectedMessage.getTitre());
                        created_atdetails.setText(selectedMessage.getCreated_at().toString());
                        lettre.setText(String.valueOf(Character.toUpperCase(message.getSender().getNom().charAt(0))));
                        contentdetails.setText(selectedMessage.getContenu());

                        System.out.println("Read button clicked!"+selectedMessage);

                    });
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



}