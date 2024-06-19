package GUI;

import entite.user;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.userService;
import javafx.scene.input.MouseEvent;
import javafx.event.EventHandler;

public class signupController {
    public Button register;
    @FXML
    private TextField nom;
    @FXML
    private Label nomError;

    @FXML
    private TextField prenom;
    @FXML
    private Label prenomError;

    @FXML
    private TextField adresse;
    @FXML
    private Label adresseError;

    @FXML
    private TextField email;
    @FXML
    private Label emailError;

    @FXML
    private PasswordField password;
    @FXML
    private Label passwordError;

    @FXML
    private PasswordField confirmpassword;
    @FXML
    private Label confirmpasswordError;

    private userService userService;
    @FXML
    private Label signInLabel;
    @FXML
    private TextField cheminImageTextField;
    @FXML
    private Button selectImageButton;

    @FXML
    public void initialize() {
        selectImageButton.setOnAction(this::parcourirImage);
        userService = new userService();
        signInLabel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                loadSignInPage();
            }
        });
    }

    @FXML
    private void handleRegisterButtonClick() {
        clearErrorMessages();
        boolean isValid = validateInputs();
        if (!isValid) {
            return;
        }

        String firstName = nom.getText();
        String lastName = prenom.getText();
        String userAddress = adresse.getText();
        String userEmail = email.getText();
        String userPassword = password.getText();
        String confirmPassword = confirmpassword.getText();
        float salaire = 0;
        List<String> roles = new ArrayList<>();
        roles.add("ROLE_USER");

        if (!userPassword.equals(confirmPassword)) {
            confirmpasswordError.setText("Passwords do not match");
            confirmpasswordError.setStyle("-fx-text-fill: red;");
            return;
        }

        if (userService.email_existant(userEmail)) {
            showErrorPopup("Email already exists");
            return;
        }

        String imagePath = cheminImageTextField.getText();
        if (imagePath.isEmpty()) {
            showErrorPopup("Please select a profile image");
            return;
        }

        user user = new user(userEmail, roles, userPassword, firstName, lastName, userAddress, salaire);
        user.setImage(imagePath);
        userService.Add(user);
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Registration Completed");
        alert.setHeaderText(null);
        alert.setContentText("Registration was successful.");
        alert.showAndWait();
        loadSignInPage();
    }

    @FXML
    private void handleSelectImageButtonClick(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose Profile Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            cheminImageTextField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void showErrorPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearErrorMessages() {
        nomError.setText("");
        prenomError.setText("");
        adresseError.setText("");
        emailError.setText("");
        passwordError.setText("");
        confirmpasswordError.setText("");
    }
    private boolean validateInputs() {
        boolean isValid = true;

        if (nom.getText().isEmpty()) {
            nomError.setText("Please enter your first name");
            nomError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        if (prenom.getText().isEmpty()) {
            prenomError.setText("Please enter your last name");
            prenomError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        if (adresse.getText().isEmpty()) {
            adresseError.setText("Please enter your address");
            adresseError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        if (email.getText().isEmpty()) {
            emailError.setText("Please enter your email address");
            emailError.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (!isValidEmail(email.getText())) {
            emailError.setText("Invalid email format");
            emailError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        if (password.getText().isEmpty()) {
            passwordError.setText("Please enter a password");
            passwordError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        if (confirmpassword.getText().isEmpty()) {
            confirmpasswordError.setText("Please confirm your password");
            confirmpasswordError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }

        return isValid;
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private void loadSignInPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signin.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) signInLabel.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void parcourirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Images (*.png, *.jpg, *.jpeg)", "*.png", "*.jpg", "*.jpeg");
        fileChooser.getExtensionFilters().add(extFilter);
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            cheminImageTextField.setText(selectedFile.getAbsolutePath());
        }
    }
}
