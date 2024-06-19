package GUI;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import javafx.stage.Stage;
import services.userService;

import java.io.IOException;
import java.security.SecureRandom;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class resetpasswordController {
    @FXML
    private TextField email;

    @FXML
    private Label emailError;


    @FXML
    private Button register;

    @FXML
    private Hyperlink reset;
    private services.userService userService = new userService();
    public void handleSendReset(ActionEvent actionEvent) {
        String enteredEmail = email.getText();


        if (enteredEmail.isEmpty()) {
            emailError.setText("Email field is empty.");
            emailError.setStyle("-fx-text-fill: red;");
            return;
        }
        if (!isValidEmail(enteredEmail)) {
            emailError.setText("Invalid email format.");
            emailError.setStyle("-fx-text-fill: red;");
            return;
        }

        if (userService.email_existant(enteredEmail)) {
            String newPassword = generateRandomPassword();
            userService.updatePasswordInDatabase(email, newPassword);
            navigateback();
        } else {
            emailError.setText("Email does not exist in the database.");
            emailError.setStyle("-fx-text-fill: red;");
            emailError.setStyle("-fx-text-fill: red;");
        }
    }


    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    private String generateRandomPassword() {

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder newPassword = new StringBuilder();
        Random random = new SecureRandom();
        for (int i = 0; i < 10; i++) {
            newPassword.append(characters.charAt(random.nextInt(characters.length())));
        }
        return newPassword.toString();
    }



    public void navigateback() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signin.fxml"));
            Parent root = loader.load();

            signinController controller = loader.getController();

            Scene scene = new Scene(root);

            Stage stage = (Stage) reset.getScene().getWindow();


            stage.setScene(scene);

            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
