package GUI;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import services.ArticleService;
import services.userService;

import javax.swing.*;
import java.io.IOException;

public class DashboardController {
    @FXML
    private AnchorPane contentArea;
    private services.userService userService = new userService();
    @FXML
    private JFXButton btn_logout;
    @FXML
    private JFXButton btn_menu11312;
    @FXML
    private void handlePackageListButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/packageList.fxml"));
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
    private void handleServiceListButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/serviceList.fxml"));
            Pane serviceListContent = loader.load();
            ServiceListController controller = loader.getController();


            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(serviceListContent, 0.0);
            AnchorPane.setBottomAnchor(serviceListContent, 0.0);
            AnchorPane.setLeftAnchor(serviceListContent, 0.0);
            AnchorPane.setRightAnchor(serviceListContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(serviceListContent); // Assuming the second child is the AnchorPane to replace
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }
    @FXML
    private void handleUsersListButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listusers.fxml"));
            Pane serviceListContent = loader.load();
            listusersController controller = loader.getController();


            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(serviceListContent, 0.0);
            AnchorPane.setBottomAnchor(serviceListContent, 0.0);
            AnchorPane.setLeftAnchor(serviceListContent, 0.0);
            AnchorPane.setRightAnchor(serviceListContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(serviceListContent); // Assuming the second child is the AnchorPane to replace
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }


    public void handleAdminPaneClick(MouseEvent mouseEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/adminprofile.fxml"));
            Pane profileContent = loader.load();
            AdminprofileController controller = loader.getController();
            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(profileContent, 0.0);
            AnchorPane.setBottomAnchor(profileContent, 0.0);
            AnchorPane.setLeftAnchor(profileContent, 0.0);
            AnchorPane.setRightAnchor(profileContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(profileContent);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }
    @FXML
    public void handleCommandePaneClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/commandList.fxml"));
            Pane packageListContent = loader.load();
            CommandListController controller = loader.getController();

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


    public void handleLogoutButtonClick(ActionEvent actionEvent) {

            userService.logout();
            redirectToLoginPage();

    }

    @FXML
    private void redirectToLoginPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/signin.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = (Stage) contentArea.getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la redirection vers la page de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleProductListButtonClick() {
        try {
            // Initialisez le service ArticleService
            ArticleService articleService = new ArticleService();

            // Chargez le fichier FXML listarticle.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/listarticle.fxml"));
            loader.setControllerFactory(param -> {
                if (param.equals(listarticleController.class)) {
                    return new listarticleController(articleService); // Instanciez shopController avec le service ArticleService
                } else {
                    try {
                        return param.getConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e); // Gérer les erreurs de création de contrôleur
                    }
                }
            });
            Pane serviceListContent = loader.load();
            listarticleController controller = loader.getController();


            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(serviceListContent, 0.0);
            AnchorPane.setBottomAnchor(serviceListContent, 0.0);
            AnchorPane.setLeftAnchor(serviceListContent, 0.0);
            AnchorPane.setRightAnchor(serviceListContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(serviceListContent); // Assuming the second child is the Anchor
        }catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void handlecategorieListButtonClick(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/categorie.fxml"));
            Pane profileContent = loader.load();
            CategorieController controller = loader.getController();
            // Replace the content of the second AnchorPane with the loaded content
            AnchorPane.setTopAnchor(profileContent, 0.0);
            AnchorPane.setBottomAnchor(profileContent, 0.0);
            AnchorPane.setLeftAnchor(profileContent, 0.0);
            AnchorPane.setRightAnchor(profileContent, 0.0);
            contentArea.getChildren().removeAll();
            contentArea.getChildren().setAll(profileContent);
        } catch (IOException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    public void handleReclamationClick(ActionEvent actionEvent){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation.fxml"));
            Parent root = loader.load();

            Scene scene = new Scene(root);

            Stage stage = (Stage) contentArea.getScene().getWindow();

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de la redirection vers la page de connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handleAbonnementClick(ActionEvent actionEvent){
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Abonnement.fxml"));
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
}
