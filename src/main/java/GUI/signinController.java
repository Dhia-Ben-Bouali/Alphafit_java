package GUI;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.model.Person;
import com.google.cloud.recaptchaenterprise.v1.RecaptchaEnterpriseServiceClient;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import entite.user;
import javafx.concurrent.Worker;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebEngine;
import javafx.stage.Stage;

import services.LoggedInUserManager;
import services.userService;
import javafx.event.ActionEvent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.web.WebView;
import com.google.api.services.people.v1.model.Person;
import com.google.api.services.people.v1.model.Name;

import static GUI.GoogleAuthenticator.JSON_FACTORY;

public class signinController {
    private final String RECAPTCHA_SECRET = "6LdRD8gpAAAAADi-ChgrGZTFrzHjAzbdI4C7s6yg";
    private final String RECAPTCHA_PROJECT_ID = "6LeQEn8pAAAAAKm5VhxKbONnPTq3sindgyJCJyLY";
    private final String RECAPTCHA_SITE_KEY = "6LeQEn8pAAAAAKm5VhxKbONnPTq3sindgyJCJyLY";
    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private Button register;

    @FXML
    private Label signup;
    @FXML
    private Label emailError;

    @FXML
    private Hyperlink reset;
    @FXML
    private Label passwordError;
    private userService userService = new userService();
    @FXML
    private Button googleLoginButton;

    @FXML
    private Label statusLabel;
    @FXML
    private WebView recaptchaWebView;



    @FXML
    void initialize() {
        googleLoginButton.setOnAction(event -> {
            try {
                GoogleAuthenticator googleAuthenticator = new GoogleAuthenticator();
                Credential credential = googleAuthenticator.authenticate();
                if (credential != null) {
                    statusLabel.setText("Logged in successfully");

                    String email = getEmailFromCredential(credential);
                    String nom = getFirstNameFromCredential(credential);
                    System.out.println("User's email: " + email);
                    List<String> roles = new ArrayList<>();
                    roles.add("ROLE_USER");
                    String city= getCityFromCredential(credential);

                    // Vérifier si l'e-mail existe déjà dans votre base de données
                    if (!userService.email_existant(email)) {
                        // Si l'e-mail n'existe pas, enregistrez-le avec un mot de passe généré
                        String generatedPassword = "alpha123"; // À implémenter

                        user user = new user(email, roles, generatedPassword, nom, nom, city, 0);

                        userService.Add(user);


                    }

                    redirectToUserListPagefromgoogle(email);
                } else {
                    statusLabel.setText("Failed to log in");
                }
            } catch (Exception e) {
                e.printStackTrace();
                statusLabel.setText("Error: " + e.getMessage());
            }
        });

//    setupRecaptchaWebView();


    }

//    private void setupRecaptchaWebView() {
//        WebEngine webEngine = recaptchaWebView.getEngine();
//        String RECAPTCHA_SITE_KEY = "6LeQEn8pAAAAAKm5VhxKbONnPTq3sindgyJCJyLY";
//        webEngine.loadContent("<html><head><script src='https://www.google.com/recaptcha/enterprise.js'></script></head><body><div class='g-recaptcha' data-sitekey='" + RECAPTCHA_SITE_KEY + "' data-domain='localhost'></div></body></html>");
//        webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
//            if (newValue == Worker.State.SUCCEEDED) {
//                // WebView finished loading, you can perform any necessary tasks here
//            }
//        });
//    }


    private String getEmailFromCredential(Credential credential) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        PeopleService peopleService = new PeopleService.Builder(httpTransport, JSON_FACTORY, credential).build();
        PeopleService.People people = peopleService.people();
        Person profile = people.get("people/me").setPersonFields("emailAddresses").execute();
        return profile.getEmailAddresses().get(0).getValue();
    }
    private String getFirstNameFromCredential(Credential credential) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        PeopleService peopleService = new PeopleService.Builder(httpTransport, JSON_FACTORY, credential)
                .build();
        Person profile = peopleService.people().get("people/me").setPersonFields("names").execute();

        List<Object> names = (List<Object>) profile.get("names");
        if (names != null && !names.isEmpty()) {
            for (Object nameObj : names) {
                Map<String, String> nameMap = (Map<String, String>) nameObj;
                String displayName = nameMap.get("displayName");
                if (displayName != null && !displayName.isEmpty()) {
                    return displayName;
                }
            }
        }
        return "Unknown";
    }

    private String getCityFromCredential(Credential credential) throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        PeopleService peopleService = new PeopleService.Builder(httpTransport, JSON_FACTORY, credential)
                .build();
        Person profile = peopleService.people().get("people/me").setPersonFields("addresses").execute();

        List<Object> addresses = (List<Object>) profile.get("addresses");
        if (addresses != null && !addresses.isEmpty()) {
            for (Object addressObj : addresses) {
                Map<String, Object> addressMap = (Map<String, Object>) addressObj;
                String city = (String) addressMap.get("city");
                if (city != null && !city.isEmpty()) {
                    return city;
                }
            }
        }
        return "Unknown";
    }




    private void redirectToUserListPagefromgoogle(String userEmail) {
        user loggedInUser = userService.login(userEmail, "alpha123");
        // Définir l'utilisateur connecté dans LoggedInUserManager
        LoggedInUserManager.getInstance().setLoggedInUser(loggedInUser);
        try {
            user uuser = userService.findByEmail(userEmail);
            // Vérifier si l'utilisateur est actif
            if (uuser != null && userService.isUserActive(userEmail)) {
                String role = uuser.getRoles().toString();
                if (role.contains("ROLE_ADMIN")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard2.fxml"));
                    Parent root = loader.load();
                    DashboardController controller = loader.getController();
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) googleLoginButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                } else {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/base2.fxml"));
                    Parent root = loader.load();
                    BaseController controller = loader.getController();
                    Scene scene = new Scene(root);
                    Stage stage = (Stage) googleLoginButton.getScene().getWindow();
                    stage.setScene(scene);
                    stage.show();
                }
            } else {
                // Afficher un message d'erreur si le compte est inactif
                showPopup("Your account is inactive. Please contact the administrator.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    @FXML
    private void redirectToSignUpPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signup.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) signup.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void handleRegister() throws IOException {
        clearErrorMessages();
        boolean isValid = validateInputs();
        if (!isValid) {
            return;
        }

        String userEmail = email.getText();
        String userPassword = password.getText();

        user loggedInUser = userService.login(userEmail, userPassword);

        if (loggedInUser != null) {
            System.out.println("Login successful.");
            showPopup("Login successful.");
            redirectToUserListPage(loggedInUser);
        } else {
            if (!userService.isUserActive(userEmail)) {
                System.out.println("Your account is inactive. Please contact the administrator.");
                showPopup("Your account is inactive. Please contact the administrator.");
            } else {
                System.out.println("Login failed. Incorrect email or password.");
                showPopup("Login failed. Incorrect email or password.");
            }
        }
//        verifyReCaptcha();
    }

    private void verifyReCaptcha() throws IOException {
        // Get the user response token from the WebView
        WebEngine webEngine = recaptchaWebView.getEngine();
        String userResponseToken = (String) webEngine.executeScript("document.getElementById('g-recaptcha-response').value");

        // Verify the reCAPTCHA response token using Google's reCAPTCHA API
        boolean isValid = verifyReCaptchaWithGoogle(userResponseToken);

        if (isValid) {
            // reCAPTCHA verification succeeded
            System.out.println("reCAPTCHA verification successful. Proceeding with login...");
        } else {
            // reCAPTCHA verification failed
            System.out.println("reCAPTCHA verification failed. Score too low.");
            showPopup("reCAPTCHA verification failed. Please try again.");
            // You might want to refresh the reCAPTCHA challenge here
        }
    }

    private boolean verifyReCaptchaWithGoogle(String userResponseToken) throws IOException {
        // Make a request to Google's reCAPTCHA API to verify the response token
        String recaptchaUrl = "https://www.google.com/recaptcha/api/siteverify";
        String secretKey = "6LdRD8gpAAAAADi-ChgrGZTFrzHjAzbdI4C7s6yg"; // Replace with your reCAPTCHA secret key
        String urlParameters = "secret=" + secretKey + "&response=" + userResponseToken;

        URL url = new URL(recaptchaUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
            outputStream.writeBytes(urlParameters);
            outputStream.flush();
        }

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                // Parse the JSON response
                JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
                // Check if the response indicates success
                return jsonResponse.get("success").getAsBoolean();
            }
        } else {
            System.out.println("Failed to verify reCAPTCHA. HTTP error code: " + responseCode);
            return false;
        }
    }
    private void clearErrorMessages() {
        emailError.setText("");
        passwordError.setText("");
    }
    private boolean validateInputs() {
        boolean isValid = true;
        if (email.getText().isEmpty()) {
            emailError.setText("Please enter your email address");
            emailError.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (!isValidEmail(email.getText())) {
            emailError.setText("Invalid email format");
            emailError.setStyle("-fx-text-fill: red;");
            isValid = false;
        } else if (!userService.email_existant(email.getText())) {
            emailError.setText("Email does not exist. Please register.");
            emailError.setStyle("-fx-text-fill: red;");
            isValid = false;
        }
        if (password.getText().isEmpty()) {
            passwordError.setText("Please enter your password");
            passwordError.setStyle("-fx-text-fill: red;");
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

    private void showPopup(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sign-in Status");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void redirectToUserListPage(user loggedInUser) {
        try {
            String role = loggedInUser.getRoles().toString();

            if (role.contains("ROLE_ADMIN")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard2.fxml"));
                Parent root = loader.load();
                DashboardController controller = loader.getController();
                Scene scene = new Scene(root);
                Stage stage = (Stage) register.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } else {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/base2.fxml"));
                Parent root = loader.load();
                BaseController controller = loader.getController();
                Scene scene = new Scene(root);
                Stage stage = (Stage) register.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigatetoresetpassword(){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/resetpassword.fxml"));
            Parent root = loader.load();
            resetpasswordController controller = loader.getController();
            Scene scene = new Scene(root);
            Stage stage = (Stage) reset.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
