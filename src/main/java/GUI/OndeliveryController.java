package GUI;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import services.ArticleService;

import java.awt.event.ActionEvent;

public class OndeliveryController {
    @FXML
    private Button bckShop;

    @FXML
    void handleBackToShopButton() {
        try {
            // Initialisez le service ArticleService
            ArticleService articleService = new ArticleService();

            // Chargez le fichier FXML shop.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/shop.fxml"));
            Parent root = loader.load();

            // Obtenir une référence au contrôleur ShopController
            shopController shopController = loader.getController();

            // Vérifier si le contrôleur ShopController n'est pas null
            if (shopController != null) {
                // Définir le service ArticleService pour le contrôleur ShopController
                shopController.setArticleService(articleService);

                // Définir le userData de la racine avec le contrôleur ShopController
                root.setUserData(shopController);
            } else {
                System.out.println("ShopController est null.");
            }

            // Récupérer la scène actuelle
            Scene currentScene = bckShop.getScene();

            // Changer la scène de la fenêtre actuelle
            currentScene.setRoot(root);

            // Afficher l'alerte uniquement lorsque vous êtes sur la page du magasin
            //shopController.displayLowQuantityAlert();
        } catch (Exception e) {
            System.out.println("Exception lors du chargement du fichier FXML: " + e.getMessage());
            e.printStackTrace();
        }

    }

}