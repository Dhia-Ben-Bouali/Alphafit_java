package GUI;

import com.jfoenix.controls.JFXButton;
import entite.Article;
import entite.user;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.commons.math3.analysis.function.StepFunction;
import services.ArticleService;
import services.LoggedInUserManager;
import services.userService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BaseController implements Initializable {
    @FXML
    private ImageView exit;

    @FXML
    private Label menu;

    @FXML
    private Label menuBack;

    @FXML
    private AnchorPane slider;
    @FXML
    public AnchorPane contentArea;
    @FXML
    private JFXButton logout, messagerie;
    @FXML
    private Label usernameLabel;
    @FXML
    private ImageView userImageView;
    @FXML
    private shopController shopController;
    private BaseController base;


    // Méthode pour injecter le contrôleur de la boutique
    public void setShopController(shopController shopController) {
        this.shopController = shopController;
    }

    private userService userService = new userService();
    user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String fullName = loggedInUser.getNom() + " " + loggedInUser.getPrenom();

        // Set the concatenated full name in the usernameLabel
        usernameLabel.setText(fullName);
        String imagePath = loggedInUser.getImage();
        if (imagePath != null && !imagePath.isEmpty()) {
            if (imagePath.startsWith("file:/")) {
                imagePath = imagePath.substring(6);
            }
            File file = new File(imagePath);
            if (file.exists()) {
                Image image = new Image(file.toURI().toString());
                userImageView.setImage(image);
            } else {
                // Set default image if user's image does not exist
                userImageView.setImage(new Image("imagess/user.png"));
            }
        } else {
            // Set default image if user's image path is null or empty
            userImageView.setImage(new Image("imagess/user.png"));
        }


        exit.setOnMouseClicked(event -> {
            System.exit(0);
        });
        slider.setTranslateX(-261);
        menu.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(slider);
            slide.setToX(0);
            slide.play();
            slider.setTranslateX(-261);
            slide.setOnFinished(animationEvent -> {
                menu.setVisible(false);
                menuBack.setVisible(true);
            });
        });
        menuBack.setOnMouseClicked(event -> {
            TranslateTransition slide = new TranslateTransition();
            slide.setDuration(Duration.seconds(0.4));
            slide.setNode(slider);
            slide.setToX(-261);
            slide.play();
            slider.setTranslateX(0);
            slide.setOnFinished(animationEvent -> {
                menu.setVisible(true);
                menuBack.setVisible(false);
            });
        });
    }

    @FXML
    private void handleOurPackagesButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ourPackages.fxml"));
            Pane packageListContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(packageListContent, 0.0);
            AnchorPane.setBottomAnchor(packageListContent, 0.0);
            AnchorPane.setLeftAnchor(packageListContent, 0.0);
            AnchorPane.setRightAnchor(packageListContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(packageListContent); // Assuming the second child is the AnchorPane to replace
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    @FXML
    private void handleCalorieCalculatorButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/calorieCalculator.fxml"));
            Pane packageListContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(packageListContent, 0.0);
            AnchorPane.setBottomAnchor(packageListContent, 0.0);
            AnchorPane.setLeftAnchor(packageListContent, 0.0);
            AnchorPane.setRightAnchor(packageListContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(packageListContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void handleLogOutButtonClick(javafx.event.ActionEvent actionEvent) {
        userService.logout();
        redirectToLoginPage();
    }

    @FXML
    private void handleDetailsProfileButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/userprofile.fxml"));
            Pane userProfileContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(userProfileContent, 0.0);
            AnchorPane.setBottomAnchor(userProfileContent, 0.0);
            AnchorPane.setLeftAnchor(userProfileContent, 0.0);
            AnchorPane.setRightAnchor(userProfileContent, 0.0);
            contentArea.getChildren().removeAll();
            // Ajoutez userProfileContent à contentArea tout en conservant la barre latérale et la barre de navigation
            contentArea.getChildren().add(userProfileContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    @FXML
    private void redirectToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signin.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = (Stage) logout.getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la redirection vers la page de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void gotoshop() {
        try {
            // Load the FXML file shop.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shop.fxml"));
            AnchorPane shopContent = loader.load();

            // Retrieve a reference to the ShopController
            shopController = loader.getController();
            shopContent.getProperties().put("controller", shopController);
            shopController.setContentArea(contentArea);

            // Replace the content of contentArea with the loaded content
            contentArea.getChildren().clear();
            contentArea.getChildren().add(shopContent);

            // Initialize the ArticleService
            ArticleService articleService = new ArticleService();

            // Set the ArticleService for the ShopController
            shopController.setArticleService(articleService);

        } catch (IOException e) {
            System.out.println("Erreur lors du chargement du fichier FXML : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleMessagerieButtonClick(ActionEvent event) {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddMessage.fxml"));
            Pane packageListContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(packageListContent, 0.0);
            AnchorPane.setBottomAnchor(packageListContent, 0.0);
            AnchorPane.setLeftAnchor(packageListContent, 0.0);
            AnchorPane.setRightAnchor(packageListContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(packageListContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }


    public void handleOurReclamationButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ReclamationClient.fxml"));
            Pane packageListContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(packageListContent, 0.0);
            AnchorPane.setBottomAnchor(packageListContent, 0.0);
            AnchorPane.setLeftAnchor(packageListContent, 0.0);
            AnchorPane.setRightAnchor(packageListContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(packageListContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void handleSuivieButtonClick() {
        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();
        String role = loggedInUser.getRoles().toString();


        if (role.contains("ROLE_COACH")) {


            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/StaffAbonnement.fxml"));
                Pane packageListContent = loader.load();

                // Replace the content of the second AnchorPane with the loaded content
                AnchorPane.setTopAnchor(packageListContent, 0.0);
                AnchorPane.setBottomAnchor(packageListContent, 0.0);
                AnchorPane.setLeftAnchor(packageListContent, 0.0);
                AnchorPane.setRightAnchor(packageListContent, 0.0);
                contentArea.getChildren().removeAll();
                contentArea.getChildren().setAll(packageListContent);

            } catch (IOException e) {
                e.printStackTrace(); // Handle the exception appropriately
            }

        }


    if (role.contains("ROLE_USER")) {


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowAllSuiviCoach.fxml"));
            Pane packageListContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(packageListContent, 0.0);
            AnchorPane.setBottomAnchor(packageListContent, 0.0);
            AnchorPane.setLeftAnchor(packageListContent, 0.0);
            AnchorPane.setRightAnchor(packageListContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(packageListContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }

    }
}

    public void handleOurHomeButtonClick(ActionEvent event) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/home.fxml"));
            Pane packageListContent = loader.load();

            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(packageListContent, 0.0);
            AnchorPane.setBottomAnchor(packageListContent, 0.0);
            AnchorPane.setLeftAnchor(packageListContent, 0.0);
            AnchorPane.setRightAnchor(packageListContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(packageListContent);

        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }
}