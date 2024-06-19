package GUI;

/*import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Style;
import com.itextpdf.layout.element.IBlockElement;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;*/
import com.mysql.cj.xdevapi.Table;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import entite.user;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.userService;

import javax.swing.text.html.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import java.io.FileNotFoundException;*/
public class listusersController {
    @FXML
    private TableView<user> table;
    @FXML
    private ListView<HBox> liste;
    @FXML
    private Button profileButton;
    @FXML
    private TableColumn<user, String> email;

    @FXML
    private TableColumn<user, String> prenom;

    @FXML
    private TableColumn<user, String> nom;

    @FXML
    private TableColumn<user, String> adresse;

    @FXML
    private TextField filterField;
    @FXML
    private AnchorPane addUserForm;

    @FXML
    private Button addButton;
    @FXML
    private TextField nomform;
    @FXML
    private Label nomError;

    @FXML
    private TextField prenomform;
    @FXML
    private Label prenomError;

    @FXML
    private TextField adresseform;
    @FXML
    private Label adresseError;

    @FXML
    private TextField emailform;
    @FXML
    private Label emailError;

    @FXML
    private PasswordField passwordform;
    @FXML
    private Label passwordError;

    @FXML
    private PasswordField confirmpasswordform;
    @FXML
    private Label confirmpasswordError;
    @FXML
    private ComboBox<String> roleChoiceBox;
    @FXML
    private user selectedUser;
    @FXML
    private ImageView profile;
    @FXML
    private TextField salireform;
    @FXML
    private Button exit;
    private userService userService;
    private ObservableList<user> userList;
    @FXML
    private ComboBox<String> menuComboBox;

    private String selectedUserEmail;
    @FXML
    private HBox statisticsLayout;
    @FXML
    private AnchorPane updateUserForm;

    @FXML
    private Button updateButton;

    @FXML
    private TextField salireformupdate;

    @FXML
    private ComboBox<String> roleChoiceBoxupdate;

    @FXML
    private TextField nomformupdate;

    @FXML
    private Label nomErrorupdate;

    @FXML
    private TextField prenomformupdate;

    @FXML
    private Label prenomErrorupdate;

    @FXML
    private TextField adresseformupdate;

    @FXML
    private Label adresseErrorupdate;

    @FXML
    private TextField emailformupdate;

    @FXML
    private Label emailErrorupdate;

    @FXML
    private PasswordField passwordformupdate;

    @FXML
    private Label passwordErrorupdate;

    @FXML
    private PasswordField confirmpasswordformupdate;

    @FXML
    private Label confirmpasswordErrorupdate;
    @FXML
    private Button update;
    @FXML
    private ChoiceBox<String> trirole;
    private Timer timer;
    public listusersController() {

        userService = new userService();
    }

    @FXML
    private void initialize() {
        timer = new Timer();

        // Planifiez la tâche de rafraîchissement toutes les 2 secondes
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> refreshListView());
            }
        }, 0, 20000);
        updateStatistics();
        trirole.getItems().addAll("ROLE_COACH", "ROLE_NUTRITIONIST");

        updateUserForm.addEventFilter(MouseEvent.MOUSE_CLICKED, event -> {
            if (!liste.getBoundsInParent().contains(event.getX(), event.getY())) {
                refreshListView(); // Rafraîchir la liste lorsque vous cliquez en dehors d'elle
            }
        });
        addUserForm.setVisible(false);


        addButton.setOnAction(event -> {

            addUserForm.setVisible(true);
            clearErrorMessages();
        });


        menuComboBox.setOnAction(event -> {
            String selectedItem = menuComboBox.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                if (selectedItem.equals("Logout")) {
                    System.out.println("Logout selected. Redirecting to login page...");
                    redirectToLoginPage();
                }

                menuComboBox.getSelectionModel().clearSelection();
            }
        });


        List<user> allUsers = userService.getAll();
        ObservableList<HBox> items = FXCollections.observableArrayList();


        HBox header = new HBox(10);
        header.getChildren().addAll(
                new Label("Nom"),
                new Label("Prenom"),
                new Label("Adresse"),
                new Label("Email"),
                new Label("Activated")
        );
        header.setPadding(new Insets(5, 5, 5, 5));

        VBox.setVgrow(header, Priority.NEVER);
        items.add(header);

        for (user user : allUsers) {
            HBox hbox = new HBox(10);
            hbox.getChildren().addAll(
                    new Label(user.getNom()),
                    new Label(user.getPrenom()),
                    new Label(user.getAdresse()),
                    new Label(user.getEmail()),
                    new Label(user.getActivated() != null ? (user.getActivated() ? "Activated" : "Inactivated") : "Unknown"),
                    createToggleButton(user)
            );
            hbox.setPadding(new Insets(5, 5, 5, 5));
            VBox.setVgrow(hbox, Priority.NEVER);
            items.add(hbox);
        }


        refreshListView();

        liste.setCellFactory(param -> new ListCell<HBox>() {
            @Override
            protected void updateItem(HBox item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    setGraphic(item);

                    if (getIndex() == 0) {

                        return;
                    }


                    ToggleButton toggleButton = (ToggleButton) item.getChildren().get(item.getChildren().size() - 1);
                    toggleButton.setOnAction(event -> {
                        Label emailLabel = (Label) item.getChildren().get(3);
                        String email = emailLabel.getText();

                        updateUserActivation(email, toggleButton.isSelected());
                    });
                }
            }
        });

        liste.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                HBox selectedHBox = (HBox) newValue;

                Label emailLabel = (Label) selectedHBox.getChildren().get(3);
                String selectedUserEmail = emailLabel.getText();
                selectedUser = userService.findByEmail(selectedUserEmail);

                if (selectedUser != null) {
                    clearupdateErrorMessages();
                    clearErrorMessages();

                    nomformupdate.setText(selectedUser.getNom());
                    prenomformupdate.setText(selectedUser.getPrenom());
                    adresseformupdate.setText(selectedUser.getAdresse());
                    emailformupdate.setText(selectedUser.getEmail());
                    salireformupdate.setText(String.valueOf(selectedUser.getSalaire()));


                }
            }
            clearupdateErrorMessages();
        });
        liste.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                HBox selectedHBox = liste.getSelectionModel().getSelectedItem();
                if (selectedHBox != null) {
                    Label emailLabel = (Label) selectedHBox.getChildren().get(3);
                    String selectedUserEmail = emailLabel.getText();
                    selectedUser = userService.findByEmail(selectedUserEmail);

                    if (selectedUser != null) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("User Information");
                        alert.setHeaderText(null);
                        alert.setContentText("Nom: " + selectedUser.getNom() + "\n" +
                                "Prenom: " + selectedUser.getPrenom() + "\n" +
                                "Adresse: " + selectedUser.getAdresse() + "\n" +
                                "Email: " + selectedUser.getEmail() + "\n" +
                                "Rôle: " + selectedUser.getRoles());
                        alert.showAndWait();
                    }
                }
            }
        });

    }

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
        }
    }
    private ToggleButton createToggleButton(user user) {
        ToggleButton toggleButton = new ToggleButton();
        toggleButton.setSelected(user.getActivated() != null && user.getActivated());


        if (user.getActivated() != null && user.getActivated()) {
            toggleButton.setTextFill(Color.GREEN);
            toggleButton.setText("Activated");
        } else {
            toggleButton.setTextFill(Color.RED);
            toggleButton.setText("Deactivated");
        }

        return toggleButton;
    }

//activated
    private void updateUserActivation(String email, boolean activate) {
        int id = userService.findIdByEmail(email);
        boolean success = activate ? userService.activateUser(id) : userService.deleteById(id);

        if (success) {
            updateStatistics();
            Platform.runLater(() -> {
                refreshListView();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("User Activation");
                alert.setHeaderText(null);
                alert.setContentText("The user has been successfully " + (activate ? "activated" : "deactivated") + ".");
                alert.showAndWait();
            });
        } else {
            System.out.println("User activation/deactivation failed for user with email: " + email);
        }
    }



    @FXML
    private void updateUser() {
        if (!validateInputs(true)) {
            return;
        }

        if (selectedUser != null) {

            String newNom = nomformupdate.getText();
            String newPrenom = prenomformupdate.getText();
            String newAdresse = adresseformupdate.getText();
            String newEmail = emailformupdate.getText();
            String newPassword = passwordformupdate.getText();
            float newSalaire = Float.parseFloat(salireformupdate.getText());

            selectedUser.setNom(newNom);
            selectedUser.setPrenom(newPrenom);
            selectedUser.setAdresse(newAdresse);
            selectedUser.setEmail(newEmail);
            selectedUser.setSalaire(newSalaire);
            selectedUser.setPassword(newPassword);

            userService.update(selectedUser);
            refreshListView();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Update Successful");
            alert.setHeaderText(null);
            alert.setContentText("User information has been updated successfully.");
            alert.showAndWait();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No User Selected");
            alert.setHeaderText(null);
            alert.setContentText("Please select a user before updating.");
            alert.showAndWait();

        }
    }

    private void updateStatistics() {
        int totalUsers = userService.getAll().size();
        int activatedUsers = (int) userService.getAll().stream().filter(user -> user.getActivated() != null && user.getActivated()).count();
        int inactivatedUsers = totalUsers - activatedUsers;

        statisticsLayout.getChildren().clear();

        ObservableList<PieChart.Data> pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("Activated (" + activatedUsers + ")", activatedUsers),
                        new PieChart.Data("Inactivated (" + inactivatedUsers + ")", inactivatedUsers)
                );

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("User Activation Status");

        Color[] blueShades = {
                Color.rgb(0, 0, 255),
                Color.rgb(30, 144, 255),
                Color.rgb(135, 206, 250),
                Color.rgb(173, 216, 230),
                Color.rgb(135, 206, 235)
        };


        for (int i = 0; i < pieChartData.size(); i++) {
            PieChart.Data data = pieChartData.get(i);
            Node slice = data.getNode();
            String style = "-fx-pie-color: rgba(" +
                    (int)(blueShades[i].getRed() * 255) + "," +
                    (int)(blueShades[i].getGreen() * 255) + "," +
                    (int)(blueShades[i].getBlue() * 255) + ", 1);";
            slice.setStyle(style);
        }


        pieChartData.forEach(data ->
                data.nameProperty().bind(
                        Bindings.concat(data.getName(), " ", (int) data.getPieValue())
                )
        );


        statisticsLayout.getChildren().add(pieChart);
    }
    private void refreshListView() {
        List<user> allUsers = userService.getAll();
        ObservableList<HBox> items = FXCollections.observableArrayList();


        HBox header = new HBox(10);
        header.getChildren().addAll(
                createLabel("Nom", 70),
                createLabel("Prenom", 100),
                createLabel("Adresse", 100),
                createLabel("Email", 150),
                createLabel("Activated", 100)
        );
        header.setPadding(new Insets(5, 5, 5, 5));
        VBox.setVgrow(header, Priority.NEVER);
        items.add(header);


        for (user user : allUsers) {
            HBox hbox = new HBox(10);
            hbox.getChildren().addAll(
                    createLabel(user.getNom(), 70),
                    createLabel(user.getPrenom(), 100),
                    createLabel(user.getAdresse(), 100),
                    createLabel(user.getEmail(), 150),
                    createLabel(user.getActivated() != null ? (user.getActivated() ? "Activated" : "Inactivated") : "Unknown", 100),
                    createToggleButton(user)
            );
            hbox.setPadding(new Insets(5, 5, 5, 5));
            VBox.setVgrow(hbox, Priority.NEVER);
            items.add(hbox);
        }


        liste.setItems(items);


        // Création des filtres et du tri
        FilteredList<HBox> filteredData = new FilteredList<>(items, p -> true);
        SortedList<HBox> sortedData = new SortedList<>(filteredData);
        liste.setItems(sortedData);

        // Filtrage par texte
        filterField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(hbox -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true; // Afficher tous les utilisateurs si aucun texte saisi
                }
                String lowerCaseFilter = newValue.toLowerCase();
                return hbox.getChildren().stream()
                        .filter(Label.class::isInstance)
                        .map(Label.class::cast)
                        .anyMatch(label -> label.getText().toLowerCase().contains(lowerCaseFilter));
            });
        });

        // Filtrage par rôle
        trirole.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, selectedRole) -> {
            if (selectedRole == null || selectedRole.isEmpty() || selectedRole.equals("Tous les rôles")) {
                filteredData.setPredicate(p -> true); // Afficher tous les utilisateurs si aucun rôle sélectionné
            } else {
                List<user> usersByRole = userService.getUsersByRole(selectedRole);
                filteredData.setPredicate(hbox -> {
                    Label emailLabel = (Label) hbox.getChildren().get(3);
                    String email = emailLabel.getText();
                    return usersByRole.stream().anyMatch(user -> user.getEmail().equals(email));
                });
            }
        });

    }

    private Label createLabel(String text, double prefWidth) {
        Label label = new Label(text);
        label.setPrefWidth(prefWidth);
        return label;
    }

    @FXML
    private void handleExitButtonClick() {

        addUserForm.setVisible(false);
        clearFormFields();
    }

    @FXML
    private void handleRegisterButtonClick() {

        clearErrorMessages();
        if (!validateInputs(false)) {
            return;
        }

        String firstName = nomform.getText();
        String lastName = prenomform.getText();
        String userAddress = adresseform.getText();
        String userEmail = emailform.getText();
        String userPassword = passwordform.getText();
        String confirmPassword = confirmpasswordform.getText();
        float salaire = Float.parseFloat(salireform.getText());

        List<String> roles = new ArrayList<>();
        String selectedRole = String.valueOf(roleChoiceBox.getValue());
        roles.add(selectedRole);


        if (!userPassword.equals(confirmPassword)) {
            confirmpasswordError.setText("Passwords do not match");
            confirmpasswordError.setStyle("-fx-text-fill: red;");
            return;
        }


            user newUser = new user(userEmail, roles, userPassword, firstName, lastName, userAddress, salaire);
            userService.Add(newUser);


        updateStatistics();
            refreshListView();
            clearFormFields();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration Completed");
            alert.setHeaderText(null);
            alert.setContentText("Registration was successful.");
            alert.showAndWait();
        clearFormFields();
        }









    private void clearFormFields() {

        nomform.clear();
        prenomform.clear();
        adresseform.clear();
        emailform.clear();
        passwordform.clear();
        confirmpasswordform.clear();
        salireform.clear();


        nomError.setText("");
        prenomError.setText("");
        adresseError.setText("");
        emailError.setText("");
        passwordError.setText("");
        confirmpasswordError.setText("");
    }


    private void clearErrorMessages() {
        nomError.setText("");
        prenomError.setText("");
        adresseError.setText("");
        emailError.setText("");
        passwordError.setText("");
        confirmpasswordError.setText("");
    }
    private void clearupdateErrorMessages() {
        nomErrorupdate.setText("");
        prenomErrorupdate.setText("");
        adresseErrorupdate.setText("");
        emailErrorupdate.setText("");
        passwordErrorupdate.setText("");
        confirmpasswordErrorupdate.setText("");
    }
    private boolean validateInputs(boolean isUpdate) {
        boolean isValid = true;


        if ((isUpdate && nomformupdate.getText().isEmpty()) || (!isUpdate && nomform.getText().isEmpty())) {
            if (isUpdate) {
                nomErrorupdate.setText("Please enter your first name");
                nomErrorupdate.setStyle("-fx-text-fill: red;");
            } else {
                nomError.setText("Please enter your first name");
                nomError.setStyle("-fx-text-fill: red;");
            }
            isValid = false;
        } else {
            nomErrorupdate.setText("");
            nomError.setText("");
        }


        if ((isUpdate && prenomformupdate.getText().isEmpty()) || (!isUpdate && prenomform.getText().isEmpty())) {
            if (isUpdate) {
                prenomErrorupdate.setText("Please enter your last name");
                prenomErrorupdate.setStyle("-fx-text-fill: red;");
            } else {
                prenomError.setText("Please enter your last name");
                prenomError.setStyle("-fx-text-fill: red;");
            }
            isValid = false;
        } else {
            prenomErrorupdate.setText("");
            prenomError.setText("");
        }


        if ((isUpdate && adresseformupdate.getText().isEmpty()) || (!isUpdate && adresseform.getText().isEmpty())) {
            if (isUpdate) {
                adresseErrorupdate.setText("Please enter your address");
                adresseErrorupdate.setStyle("-fx-text-fill: red;");
            } else {
                adresseError.setText("Please enter your address");
                adresseError.setStyle("-fx-text-fill: red;");
            }
            isValid = false;
        } else {
            adresseErrorupdate.setText("");
            adresseError.setText("");
        }


        String userEmail = isUpdate ? emailformupdate.getText() : emailform.getText();
        if (userEmail.isEmpty()) {
            if (isUpdate) {
                emailErrorupdate.setText("Please enter your email address");
                emailErrorupdate.setStyle("-fx-text-fill: red;");
            } else {
                emailError.setText("Please enter your email address");
                emailError.setStyle("-fx-text-fill: red;");
            }
            isValid = false;
        } else if (!isValidEmail(userEmail)) {
            if (isUpdate) {
                emailErrorupdate.setText("Invalid email format");
                emailErrorupdate.setStyle("-fx-text-fill: red;");
            } else {
                emailError.setText("Invalid email format");
                emailError.setStyle("-fx-text-fill: red;");
            }
            isValid = false;
        } else if (isUpdate && userEmail.equals(selectedUser.getEmail())) {

            emailErrorupdate.setText("");
        } else if (isUpdate && !userEmail.equals(selectedUser.getEmail()) && userService.email_existant(userEmail)) {

            emailErrorupdate.setText("Email already exists");
            emailErrorupdate.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (!isUpdate && userService.email_existant(userEmail)) {

            emailError.setText("Email already exists");
            emailError.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else {
            emailErrorupdate.setText("");
            emailError.setText("");
        }


        String password = isUpdate ? passwordformupdate.getText() : passwordform.getText();
        if (password.isEmpty()) {
            if (isUpdate) {
                passwordErrorupdate.setText("Please enter a password");
                passwordErrorupdate.setStyle("-fx-text-fill: red;");
            } else {
                passwordError.setText("Please enter a password");
                passwordError.setStyle("-fx-text-fill: red;");
            }
            isValid = false;
        } else {
            passwordErrorupdate.setText("");
            passwordError.setText("");
        }


        String confirmPassword = isUpdate ? confirmpasswordformupdate.getText() : confirmpasswordform.getText();
        if (confirmPassword.isEmpty()) {
            if (isUpdate) {
                confirmpasswordErrorupdate.setText("Please confirm your password");
                confirmpasswordErrorupdate.setStyle("-fx-text-fill: red;");
            } else {
                confirmpasswordError.setText("Please confirm your password");
                confirmpasswordError.setStyle("-fx-text-fill: red;");
            }
            isValid = false;
        } else {
            confirmpasswordErrorupdate.setText("");
            confirmpasswordError.setText("");
        }

        return isValid;
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


   @FXML
   private void redirectToLoginPage() {
       try {
           stopTimer();
           FXMLLoader loader = new FXMLLoader(getClass().getResource("/signin.fxml"));
           Parent root = loader.load();

           Scene scene = new Scene(root);

           Stage stage = (Stage) menuComboBox.getScene().getWindow();

           stage.setScene(scene);
           stage.show();
       } catch (IOException e) {
           System.err.println("Erreur lors de la redirection vers la page de connexion : " + e.getMessage());
           e.printStackTrace();
       }
   }

   @FXML
    public void handleDownloadPDFButtonClick(ActionEvent event) {
//        try {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Save PDF File");
//            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF files (*.pdf)", "*.pdf"));
//
//            // Show the file save dialog
//            File file = fileChooser.showSaveDialog(((Node) event.getSource()).getScene().getWindow());
//
//            if (file != null) {
//                String filePath = file.getAbsolutePath();
//                if (!filePath.endsWith(".pdf")) {
//                    filePath += ".pdf";
//                }
//
//                PdfWriter writer = new PdfWriter(filePath);
//                PdfDocument pdf = new PdfDocument(writer);
//                com.itextpdf.layout.Document document = new com.itextpdf.layout.Document(pdf);
//
//                // Add the logo
//                String logoPath = "C:/Users/ikram/IdeaProjects/AlphaFit - Copie/src/main/resources/imagess/logo.png";
//                com.itextpdf.layout.element.Image logo = new com.itextpdf.layout.element.Image(ImageDataFactory.create(logoPath));
//                logo.setWidth(100); // Adjust the width of the logo as needed
//                document.add(logo);
//
//                // Add title
//                Style titleStyle = new Style()
//                        .setTextAlignment(TextAlignment.CENTER)
//                        .setFontSize(18);
//                document.add(new Paragraph("Liste des Utilisateurs\n\n").addStyle(titleStyle));
//
//                // Create table
//                Table table = new Table(new float[]{1, 1, 1, 1, 1}); // Définir les largeurs des colonnes en pourcentage
//                table.setWidth(UnitValue.createPercentValue(100)); // Définir la largeur de la table en pourcentage de la page
//
//                // Add table header
//                addTableHeader(table);
//
//                // Add details of users from the ListView
//                for (HBox hbox : liste.getItems()) {
//                    addTableRow(table, hbox);
//                }
//
//                document.add(table);
//
//                document.close();
//                System.out.println("PDF créé avec succès à l'emplacement : " + filePath);
//            } else {
//                System.out.println("La création du PDF a été annulée par l'utilisateur.");
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    private void addTableHeader(Table table) {
//        String[] headers = {"Nom", "Prénom", "Adresse", "Email", "Activé"};
//        for (String header : headers) {
//            table.addHeaderCell(header);
//        }
    }

    private void addTableRow(Table table, HBox hbox) {
//        for (Node node : hbox.getChildren()) {
//            if (node instanceof Label) {
//                Label label = (Label) node;
//                com.itextpdf.layout.element.Cell cell = new com.itextpdf.layout.element.Cell().add(new com.itextpdf.layout.element.Paragraph(label.getText()));
//
//                cell.setBorder(new com.itextpdf.layout.borders.SolidBorder(new DeviceRgb(0, 0, 255), 1));
//                table.addCell(cell);
//            }
//        }
    }


}
