package GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import entite.Messagerie;
import services.LoggedInUserManager;
import services.MessagerieService;
import entite.user;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Month;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;

public class MessagesFavoraiteController  implements Initializable {

    @FXML
    private ListView<Messagerie> messsageListView;

    @FXML
    private TextArea areaTF;
    @FXML
    private StackPane details;
    @FXML
    private ComboBox monthComboBox;

    @FXML
    private CheckBox check;
    @FXML
  private AnchorPane updatepane;
    @FXML
    private TextField titleTF;

    @FXML
    private Label recived,subject,contentdetails,fromdetails,lettre,namedetails,subjectdetails,created_atdetails;
@FXML
    public Messagerie message_temp;

    @FXML
    private Button read,delete,update;

    private MessagesentItemController messageItemController ;

    private List<Messagerie> allMessages;
    private FilteredList<Messagerie> filteredMessages;
    @FXML
    private TextField searchField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("yesssss");
        details.setVisible(false);
        updatepane.setVisible(false);

        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();
        System.out.println("''''''''''''''''''''''''''''''''''''''''''''");
        System.out.println(loggedInUser);

        System.out.println("''''''''''''''''''''''''''''''''''''''''''''");
        MessagerieService service = new MessagerieService();
        allMessages = service.getAllFavoritesByUserId(loggedInUser.getId()); // Remplacez 2 par l'ID de l'utilisateur
        messsageListView.getItems().addAll(allMessages);

        // Créer un FilteredList lié à la liste observable de messages
        filteredMessages = new FilteredList<>(messsageListView.getItems());

        // Appliquer le filtre lorsque le texte de recherche change
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filterMessages(newValue.trim(), (String) monthComboBox.getValue()));

        // Écouter les changements de sélection dans la liste déroulante
        monthComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Filtrer les messages en fonction du mois sélectionné
            filterMessages(searchField.getText().trim(), (String) newValue);
        });

        // Afficher les messages filtrés dans la ListView
        messsageListView.setItems(filteredMessages);

        ObservableList<String> months = FXCollections.observableArrayList(
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December");

        // Ajouter les mois à la liste déroulante
        monthComboBox.setItems(months);

        // Utiliser votre CellFactory pour personnaliser l'affichage des messages dans la ListView
        messsageListView.setCellFactory(listView -> new MessageListCell());
    }

    private void filterMessages(String searchText, String selectedMonth) {
        Predicate<Messagerie> searchPredicate = message -> {
            // Filtrer les messages par nom de l'expéditeur
            return message.getRecipient().getNom().toLowerCase().contains(searchText.toLowerCase());
        };

        Predicate<Messagerie> monthPredicate = message -> {
            if (selectedMonth.isEmpty()) {
                return true; // Retourne true pour tous les messages si aucun mois n'est sélectionné
            } else {
                // Convertir la timestamp en une date
                Timestamp timestamp = message.getCreated_at();
                Date date = new Date(timestamp.getTime());
                // Extraire le mois de la date
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);
                int messageMonth = cal.get(Calendar.MONTH) + 1; // Ajouter 1 car les mois dans Calendar commencent à 0
                // Comparer avec le mois sélectionné
                String messageMonthName = Month.of(messageMonth).name().toUpperCase(); // Convertir le nom du mois en anglais
                return messageMonthName.equalsIgnoreCase(selectedMonth.toUpperCase());
            }
        };

        // Combinez les prédicats de recherche et de tri par mois
        Predicate<Messagerie> combinedPredicate = searchPredicate.and(monthPredicate);

        // Appliquer le prédicat combiné à la liste filtrée
        filteredMessages.setPredicate(combinedPredicate);
    }



    public void update2(ActionEvent event) {


        Messagerie mess=new Messagerie(message_temp.getId(),titleTF.getText(),areaTF.getText(),check.isSelected());
        System.out.println(titleTF.getText());
        MessagerieService service= new MessagerieService();
        service.update(mess);
        System.out.println("afterrrr");
        updatepane.setVisible(false);
        messsageListView.refresh();




    }

    public void exportdata(ActionEvent event) {
        try {
            // Récupérer les données à exporter
            List<Messagerie> messages = messsageListView.getItems();

            // Créer un StringBuilder pour stocker les données CSV
            StringBuilder csvContent = new StringBuilder();

            // Ajouter l'en-tête CSV
            csvContent.append("Sender,Recipient,Title,Content,Created At,Is Read,Is Favorite\n");

            // Remplir les données
            for (Messagerie message : messages) {
                csvContent.append(message.getSender().getNom()).append(",")
                        .append(message.getRecipient().getNom()).append(",")
                        .append(message.getTitre()).append(",")
                        .append(message.getContenu()).append(",")
                        .append(message.getCreated_at().toString()).append(",")
                        .append(message.isIs_read()).append(",")
                        .append(message.isIs_favorite()).append("\n");
            }

            // Afficher une boîte de dialogue de sauvegarde de fichier pour choisir l'emplacement de sauvegarde
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("messages_favoriate_data.csv");
            File file = fileChooser.showSaveDialog(null);

            // Écrire les données dans le fichier CSV
            if (file != null) {
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(csvContent.toString());
                    // Afficher une boîte de dialogue d'information si l'exportation réussit
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Data exported successfully!", ButtonType.OK);
                    alert.showAndWait();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Afficher une boîte de dialogue d'erreur si une exception se produit pendant l'exportation
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error exporting data: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
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
                    // Load FXML file for custom cell
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/messagesentitem.fxml"));
                    AnchorPane pane = loader.load();

                    MessagesentItemController controller = loader.getController();

                    controller.setMessageData(message);

                    setGraphic(pane);

                    // Get the button from the controller and attach an event handler
                    Button readButton = controller.read;
                    Button deleteButton = controller.delete;
                    Button updateButton = controller.update;


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
                    deleteButton.setOnAction(event -> {
                        Messagerie selectedMessage = getItem();
                        MessagerieService service=new MessagerieService();
                        long Id = selectedMessage.getId();
                        int id = (int) Id;
                        service.deleteById(id);
                        messsageListView.getItems().remove(selectedMessage);
                        messsageListView.refresh();


                        System.out.println("delete button clicked!"+selectedMessage);

                    });
                    updateButton.setOnAction(event -> {
                        Messagerie selectedMessage = getItem();
                        message_temp=selectedMessage;
                        updatepane.setVisible(true);

                        titleTF.setText(selectedMessage.getTitre());
                        areaTF.setText(selectedMessage.getContenu());
                        check.setSelected(selectedMessage.isIs_favorite());
                        System.out.println("update button clicked!"+selectedMessage);

                    });



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }}

    @FXML
    void reset(ActionEvent event) {

        titleTF.setText("");
        areaTF.setText("");
        check.setSelected(false);
    }

    @FXML
    void send(ActionEvent event) {
        String titre = titleTF.getText();
        String contenu = areaTF.getText();
        user sender = new user(1, "ghassen", "sakka");
        user recipient = new user(1, "ghassen", "sakka");
        boolean isFavorite = check.isSelected();

        Messagerie message = new Messagerie(sender, recipient, titre, contenu, isFavorite);

        MessagerieService messagerieService = new MessagerieService();
        messagerieService.Add(message);

        System.out.println("Message sent successfully.");

        titleTF.clear();
        areaTF.clear();
        check.setSelected(false);
    }

    public void handleExitButtonClick(ActionEvent event) {

        details.setVisible(false);
    }

    public void handleExitButtonClick2(ActionEvent event) {
        updatepane.setVisible(false);
    }



    public void setMessageItemController(MessagesentItemController messageItemController) {
        this.messageItemController = messageItemController;
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



}
