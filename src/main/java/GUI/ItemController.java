package GUI;

import entite.Article;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import test.MainFx;

import java.io.InputStream;



public class ItemController {
    @FXML
    private Label nameLabel;
    private shopController shopController;

    @FXML
    private Label fruitPriceLabel;

    @FXML
    private ImageView img;

    private Article article;

    // Méthode pour définir le contrôleur ShopController

    public void setShopController(shopController shopController) {
        this.shopController = shopController;
    }

    // Méthode pour charger une image depuis le chemin donné
    public ImageView loadImage(String imageName) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/image/" + imageName);
            if (inputStream != null) {
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(100);
                imageView.setFitHeight(100);
                return imageView; // Retourne l'objet ImageView chargé
            } else {
                System.err.println("L'image '" + imageName + "' est introuvable.");
                return null;
            }
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite lors du chargement de l'image : " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    // Méthode appelée lorsqu'un clic est effectué sur l'image
    @FXML
    public void click(MouseEvent event) {
        System.out.println("L'utilisateur a cliqué sur l'élément.");
        try {
            // Vérifiez si le contrôleur shopController n'est pas null avant de définir l'article choisi
            if (shopController != null) {
                shopController.setChosenArticle(article);
            } else {
                System.out.println("ShopController est null. Impossible de mettre à jour l'article.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Méthode pour définir les données de l'article
    public void setData(Article article) {
        this.article = article;
        nameLabel.setText(article.getNom());
        fruitPriceLabel.setText(article.getPrix() + MainFx.CURRENCY);
        String description = article.getDescription();

        // Charger l'image de l'article et la définir dans l'ImageView
        ImageView loadedImage = loadImage(article.getImage());
        if (loadedImage != null) {
            img.setImage(loadedImage.getImage());
        } else {
            System.out.println("Impossible de charger l'image.");
        }
    }
}
