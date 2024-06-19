package GUI;

import entite.user;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.LoggedInUserManager;
import services.userService;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdminprofileController implements Initializable {
    @FXML
    private Label addressLabel;

    @FXML
    private Label adresseError;

    @FXML
    private TextField adresseform;

    @FXML
    private Label confirmpasswordError;

    @FXML
    private PasswordField confirmpasswordform;

    @FXML
    private Label emailError;

    @FXML
    private Label emailLabel;

    @FXML
    private TextField emailform;

    @FXML
    private Button exit;

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label idUserLabel;

    @FXML
    private Label lastNameLabel;

    @FXML
    private Label nomError;

    @FXML
    private TextField nomform;

    @FXML
    private Label passwordError;

    @FXML
    private PasswordField passwordform;

    @FXML
    private Label prenomError;

    @FXML
    private TextField prenomform;

    @FXML
    private Button update;

    @FXML
    private Button updateButton;

    @FXML
    private AnchorPane updateUserForm;
    private userService userService;

    // Stored user credentials
    private String userEmail;
    private String userPassword;
    private HttpServletRequest request;
    @FXML
    private ComboBox<String> menuComboBox;
    @FXML
    private ImageView profileImage;
    @FXML
    private Button editProfileButton;
    @FXML
    private Node isAdmin;

    private ScheduledExecutorService scheduler;

    // Constructor to initialize user service
    public AdminprofileController() {
        this.userService = new userService();
    }

    // Method to set user credentials
    public void setUserCredentials(String email, String password) {
        this.userEmail = email;
        this.userPassword = password;
    }

    @FXML
    private void handleExitButtonClick() {
        // Hide the addUserForm when Exit button is clicked
        updateUserForm.setVisible(false);
        clearFormFields();
    }
    @FXML
    private void handleUpdateButtonClick(ActionEvent actionEvent) {
        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();
        clearFormFields();
        updateUserForm.setVisible(true);
        nomform.setText(loggedInUser.getNom());
        prenomform.setText(loggedInUser.getPrenom());
        adresseform.setText(loggedInUser.getAdresse());
        emailform.setText(loggedInUser.getEmail());

    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        updateUserForm.setVisible(false);
        userService = new userService();

        // Start a scheduler to refresh user info every 2 seconds
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(this::updateUserInfo, 0, 20, TimeUnit.SECONDS);
    }

    private void updateUserInfo() {
        Platform.runLater(() -> {
            user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

            if (loggedInUser != null) {
                // Retrieve the user's information from the database using their ID
                user updatedUser = userService.findByEmail(loggedInUser.getEmail());

                // Update the interface with the latest user information
                firstNameLabel.setText(updatedUser.getNom());
                lastNameLabel.setText(updatedUser.getPrenom());
                emailLabel.setText(updatedUser.getEmail());
                addressLabel.setText(updatedUser.getAdresse());
                loadProfileImage(updatedUser.getImage());

                // Set the user's information in the text fields
                nomform.setText(updatedUser.getNom());
                prenomform.setText(updatedUser.getPrenom());
                adresseform.setText(updatedUser.getAdresse());
                emailform.setText(updatedUser.getEmail());
            }
        });
    }



    @FXML
    private void loadProfilePage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/userprofile.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = (Stage) menuComboBox.getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void redirectToLoginPage() {
        try {
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
    private void redirectToAdminPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listusers.fxml"));
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
    private void handleUpdateAction(ActionEvent actionEvent) {
        // Validation and update logic
        userService = new userService();
        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();
        user updatedUser = new user();
        updatedUser.setId(loggedInUser.getId());
        updatedUser.setEmail(emailform.getText());
        updatedUser.setNom(nomform.getText());
        updatedUser.setPrenom(prenomform.getText());
        updatedUser.setAdresse(adresseform.getText());

        // Validate input fields
        if (!validateInputs()) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Validation Error");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Please fill in all required fields correctly.");
            errorAlert.showAndWait();
            return;
        }

        // Update password if it's changed
        if (!passwordform.getText().isEmpty()) {
            // Check if password matches the confirm password
            if (!passwordform.getText().equals(confirmpasswordform.getText())) {
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Password Mismatch");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Password and confirm password do not match.");
                errorAlert.showAndWait();
                return;
            }

            // Set the new password
            updatedUser.setPassword(passwordform.getText());
        }

        // Call the update function of the service to update the user
        boolean success = userService.update(updatedUser);

        // Check if the update was successful
        if (success) {
            // Hide the update form
            updateUserForm.setVisible(false);

            // Update the text fields with the new user information
            loggedInUser.setNom(updatedUser.getNom());
            loggedInUser.setPrenom(updatedUser.getPrenom());
            loggedInUser.setAdresse(updatedUser.getAdresse());
            loggedInUser.setEmail(updatedUser.getEmail());

            // Update profile information in labels
            firstNameLabel.setText(loggedInUser.getNom());
            lastNameLabel.setText(loggedInUser.getPrenom());
            emailLabel.setText(loggedInUser.getEmail());
            addressLabel.setText(loggedInUser.getAdresse());

            // Show success notification
            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Update Successful");
            successAlert.setHeaderText(null);
            successAlert.setContentText("Your profile has been updated successfully.");
            successAlert.showAndWait();
        } else {
            // Show failure notification
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setTitle("Update Failed");
            errorAlert.setHeaderText(null);
            errorAlert.setContentText("Failed to update your profile. Please try again.");
            errorAlert.showAndWait();
        }
    }



    private boolean validateInputs() {
        boolean isValid = true;

        // Validate nom
        if (nomform.getText().isEmpty()) {
            nomError.setText("Please enter your first name");
            nomError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Validate prenom
        if (prenomform.getText().isEmpty()) {
            prenomError.setText("Please enter your last name");
            prenomError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Validate adresse
        if (adresseform.getText().isEmpty()) {
            adresseError.setText("Please enter your address");
            adresseError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Validate email
        String userEmail = emailform.getText();
        if (userEmail.isEmpty()) {
            emailError.setText("Please enter your email address");
            emailError.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (!isValidEmail(userEmail)) {
            emailError.setText("Invalid email format");
            emailError.setStyle("-fx-text-fill: red;");
            isValid = false;}

        // Validate password
        if (passwordform.getText().isEmpty()) {
            passwordError.setText("Please enter a password");
            passwordError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        // Validate confirmPassword
        if (confirmpasswordform.getText().isEmpty()) {
            confirmpasswordError.setText("Please confirm your password");
            confirmpasswordError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        return isValid;
    }

    // Email validation method
    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    @FXML
    private void handleEditProfileButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

        if (selectedFile != null) {
            String imagePath = selectedFile.toURI().toString();
            boolean success = userService.updateProfileImage(loggedInUser.getId(), imagePath);
            if (success) {
                // Update profile image in the interface immediately
                Image image = new Image(imagePath);
                profileImage.setImage(image);
                // Show success alert
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Profile Image Updated");
                successAlert.setHeaderText(null);
                successAlert.setContentText("Your profile image has been updated successfully.");
                successAlert.showAndWait();
            } else {
                // Show error alert
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Update Failed");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Failed to update your profile image. Please try again.");
                errorAlert.showAndWait();
            }
        }
    }
    private void clearFormFields() {
        // Clear input fields
        nomform.clear();
        prenomform.clear();
        adresseform.clear();
        emailform.clear();
        passwordform.clear();
        confirmpasswordform.clear();

    }
    private void loadProfileImage(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("file:/")) {
                imagePath = imagePath.substring(6);
            }
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                profileImage.setImage(image);
            } else {
                System.out.println("Image file not found at: " + imagePath);
            }
        } else {
            System.out.println("No image found for the user.");
        }
    }
}
