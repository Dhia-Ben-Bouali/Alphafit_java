package GUI;

import entite.Article;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import services.ArticleService;
import test.MainFx;

import java.io.InputStream;

public class itemarticleController {
    @FXML
    private Label cat;
    private listarticleController listarticleController;
    private String imageUrl; // Déclaration du champ imageUrl

    @FXML
    private Label des;

    @FXML
    private ImageView img;

    @FXML
    private Label name;

    @FXML
    private Label prix;

    @FXML
    private Label quan;

    @FXML
    private Button updtButton;

    @FXML
    private Button delButton;
    private Article article;
    private ArticleService articleService;
    private listarticleController parentController;
    public void setListarticleController(listarticleController parentController) {
        this.parentController = parentController;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    public void setData(Article article) {
        this.article = article;
        name.setText(article.getNom());
        des.setText(article.getDescription());
        cat.setText(article.getCategorie().getLibelle());
        quan.setText(String.valueOf(article.getQuantite())); // Mettre à jour la quantité de l'article
        prix.setText(article.getPrix() + MainFx.CURRENCY);

        // Charger l'image de l'article
        loadImage(article.getImage());
    }

    public void loadImage(String imageName) {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/image/" + imageName);
            if (inputStream != null) {
                Image image = new Image(inputStream);
                img.setImage(image);
            } else {
                System.err.println("L'image '" + imageName + "' est introuvable.");
            }
        } catch (Exception e) {
            System.err.println("Une erreur s'est produite lors du chargement de l'image : " + e.getMessage());
            e.printStackTrace();
        }
    }
    @FXML
    public void click(MouseEvent event) {
        System.out.println("L'utilisateur a cliqué sur l'élément.");
        // Mettez ici le code que vous souhaitez exécuter lorsque l'utilisateur clique sur l'élément.
    }

    @FXML
    public void handleUpdateButtonClicked(ActionEvent event) {
        if (article != null && parentController != null) {
            parentController.handleUpdateButtonClicked(article);
        } else {
            System.out.println("Article ou contrôleur parent est null.");
        }
    }


    @FXML
    public void handleDeleteButtonClicked(ActionEvent event) {
        if (article != null && articleService != null) {
            articleService.delete(article.getId());
            // Rafraîchir la liste d'articles dans le contrôleur parent
            parentController.refreshListView();
        } else {
            System.out.println("Article ou service d'article est null.");
        }
    }

}