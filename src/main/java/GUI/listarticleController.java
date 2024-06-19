package GUI;

import entite.Article;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import services.ArticleService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class listarticleController implements Initializable {
    @FXML
    private Button exportButton;
    @FXML
    private Button deleteButton;
    @FXML
    private GridPane grid;
    @FXML
    private VBox articleContainer;
    @FXML
    private Button updateButton;
    @FXML
    private Button rtarticle;

    @FXML
    private Button rtcategorie;

    @FXML
    private ImageView rtdashboard;
    @FXML
    private Button rtdash;
    @FXML
    public ListView<Article> articleListView;
    @FXML
    private Button rtshop;
    @FXML
    private Button btnsearch;

    @FXML
    private TextField search;
    ObservableList<Article> articles = FXCollections.observableArrayList();
    private ArticleService articleService;
    private int column = 0;
    private int row = 1;
    private ScheduledExecutorService scheduler;

    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    public listarticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize ArticleService if it's not already initialized
        if (articleService == null) {
            articleService = new ArticleService();
        }

        // Initialize the grid

        // Récupérer les données d'articles depuis la base de données en utilisant le service ArticleService
        List<Article> articlesData = articleService.getAll();

        // Mettre à jour l'interface utilisateur avec les données d'articles
        articles.addAll(articlesData);

        // Liaison de la liste d'articles à la ListView
        articleListView.setItems(articles);
scheduler = Executors.newSingleThreadScheduledExecutor();
scheduler.scheduleAtFixedRate(this::refreshPage, 0 , 20 , TimeUnit.SECONDS);
        /// Personnalisation de la manière dont chaque article est affiché dans la ListView
        // Personnalisation de la manière dont chaque article est affiché dans la ListView
        articleListView.setCellFactory(new Callback<ListView<Article>, ListCell<Article>>() {
            @Override
            public ListCell<Article> call(ListView<Article> listView) {
                return new ArticleListCell();
            }
        });


// Définir la référence à la ListView articleListView pour chaque instance de itemarticleController


        // Ajouter un écouteur de changement de texte au champ de recherche
        search.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                // Appeler la méthode pour filtrer la liste des articles en fonction du texte de recherche
                filterArticles(newValue);
            }
        });
    }
    private void refreshPage() {
        Platform.runLater(() -> {
            // Mettez ici le code pour rafraîchir la page
            // Par exemple, appelez une méthode pour actualiser les données ou redessiner l'interface utilisateur
            refreshListView();
        });
    }
    // Méthode pour arrêter le scheduler lorsque la vue est détruite
    public void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }
    @FXML
    public void goToDash(ActionEvent actionEvent) {
        try {
            // Chargez le fichier FXML de la vue des statistiques
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Dashboard.fxml"));
            Parent root = loader.load();

            // Créez une nouvelle scène avec la vue des statistiques
            Scene scene = new Scene(root);

            // Créez une nouvelle fenêtre pour afficher la scène
            Stage stage = new Stage();
            stage.setTitle("Statistiques des articles par catégorie");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour filtrer les articles en fonction du texte de recherche
    private void filterArticles(String searchText) {
        // Créer une liste filtrée pour stocker les articles filtrés
        FilteredList<Article> filteredArticles = new FilteredList<>(articles);

        // Appliquer le filtre en fonction du texte de recherche
        filteredArticles.setPredicate(article -> {
            // Si le texte de recherche est vide, afficher tous les articles
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            // Sinon, filtrer les articles en fonction du texte de recherche
            String lowerCaseFilter = searchText.toLowerCase();

            // Filtrez les articles par nom, description, etc.
            return article.getNom().toLowerCase().contains(lowerCaseFilter) ||
                    article.getDescription().toLowerCase().contains(lowerCaseFilter);
            // Ajoutez d'autres critères de filtrage si nécessaire
        });

        // Mettre à jour la ListView avec les articles filtrés
        articleListView.setItems(filteredArticles);
    }


    public void refreshListView() {
        // Récupérer à nouveau les données d'articles depuis la base de données
        List<Article> updatedArticles = articleService.getAll();

        // Effacer la liste actuelle des articles
        articles.clear();

        // Ajouter les nouveaux articles à la liste
        articles.addAll(updatedArticles);
    }

    @FXML
    public void goToShop(ActionEvent actionEvent) {
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
            Scene currentScene = rtshop.getScene();

            // Changer la scène de la fenêtre actuelle
            currentScene.setRoot(root);
            // Afficher l'alerte si nécessaire
            shopController.showAlertIfNeeded();

            // Afficher l'alerte uniquement lorsque vous êtes sur la page du magasin
        } catch (Exception e) {
            System.out.println("Exception lors du chargement du fichier FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public void goToArticle(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/article.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Page des catégories");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void goToCategories(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/categorie.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Page des catégories");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    private class ArticleListCell extends ListCell<Article> {
        @FXML
        protected void updateItem(Article article, boolean empty) {
            super.updateItem(article, empty);

            if (article != null && !empty) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/itemarticle.fxml"));
                try {
                    AnchorPane anchorPane = loader.load();
                    itemarticleController itemController = loader.getController();

                    // Passez l'instance de listarticleController à itemarticleController
                    itemController.setListarticleController(listarticleController.this);

                    // Définir d'autres données et actions
                    itemController.setArticleService(articleService);
                    itemController.setData(article);

                    setGraphic(anchorPane);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                setText(null);
                setGraphic(null);
            }
        }

    }

    private void addArticleToUI(Article article) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/itemarticle.fxml"));
        try {
            AnchorPane anchorPane = fxmlLoader.load();
            itemarticleController itemController = fxmlLoader.getController();
            itemController.setData(article);
            itemController.setArticleService(articleService);
            itemController.setListarticleController(this);
            itemController.setArticle(article); // Définir l'article pour cet item

            // Ajouter l'élément d'article à la VBox dans l'interface utilisateur
            articleContainer.getChildren().add(anchorPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public ImageView loadImage(String imageName) {
        try {
            // Charge l'image à partir du chemin d'accès spécifié
            InputStream inputStream = getClass().getResourceAsStream("/image/" + imageName);
            if (inputStream != null) {
                Image image = new Image(inputStream);
                ImageView imageView = new ImageView(image);

                return imageView;
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






    // Méthode pour ajouter un article à la liste des articles
    public void addArticleToListView(Article article) {
        articleListView.getItems().add(article);
    }

    @FXML
    void handleAddButtonClicked(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/article.fxml"));
        try {
            Parent root = loader.load();
            articleController controller = loader.getController();
            controller.setArticleService(articleService);
            controller.setListArticleController(this);
            Stage stage = new Stage();
            stage.setTitle("Ajoute  r un nouvel article");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Méthode pour gérer la suppression d'un article
    public void refreshArticleInListView(Article updatedArticle) {
        // Parcourez la liste d'articles et mettez à jour l'article mis à jour
        for (Article article : articles) {
            if (article.getId() == updatedArticle.getId()) {
                // Mettez à jour l'article
                article.setNom(updatedArticle.getNom());
                article.setDescription(updatedArticle.getDescription());
                article.setPrix(updatedArticle.getPrix());
                article.setQuantite(updatedArticle.getQuantite());
                article.setImage(updatedArticle.getImage());
                // Sortez de la boucle une fois l'article mis à jour
                break;
            }
        }

        // Rafraîchissez la ListView pour refléter les modifications
        articleListView.refresh();
    }
    @FXML
    public void handleUpdateButtonClicked(Article articleToUpdate) {
        // Vérifiez si l'article à mettre à jour est valide
        if (articleToUpdate == null) {
            System.err.println("Aucun article à mettre à jour.");
            return;
        }

        try {
            // Charger le formulaire de mise à jour avec les données de l'article à mettre à jour
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/updatearticle.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur du formulaire updatearticle
            updatearticleController controller = loader.getController();
            controller.setListController(this);

            // Passer les données de l'article à mettre à jour au contrôleur
            controller.setArticle(articleToUpdate);
            controller.setArticleService(articleService);

            // Remplir les champs avec les données de l'article
            controller.setData(articleToUpdate);

            // Créer une nouvelle scène avec le formulaire de mise à jour
            Scene scene = new Scene(root);

            // Créer une nouvelle fenêtre pour afficher la scène
            Stage stage = new Stage();
            stage.setTitle("Modifier l'article");
            stage.setScene(scene);
            stage.showAndWait(); // Attendre que la fenêtre de mise à jour soit fermée

            // Rafraîchir la ListView après la mise à jour
            refreshListView();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleDeleteButtonClicked(Article articleToDelete) {
        // Vérifier si l'article à supprimer est valide
        if (articleToDelete != null) {
            // Supprimer l'article de la base de données
            articleService.delete(articleToDelete.getId());

            // Rafraîchir la liste d'articles
            refreshListView();
        } else {
            System.out.println("Article to delete is null.");
        }
    }

    public void OnExport(ActionEvent actionEvent) {
        try {
            // Récupérer les articles depuis la ListView
            ObservableList<Article> articles = articleListView.getItems();

            // Créer un nouveau classeur Excel
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Article Data");

            // Créer l'en-tête du classeur Excel
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Nom");
            headerRow.createCell(1).setCellValue("Prix");
            headerRow.createCell(2).setCellValue("Description");
            headerRow.createCell(3).setCellValue("Categorie");
            headerRow.createCell(4).setCellValue("Quantité");

            // Remplir les données des articles dans le classeur Excel
            int rowNum = 1;
            for (Article article : articles) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(article.getNom());
                row.createCell(1).setCellValue(article.getPrix());
                row.createCell(2).setCellValue(article.getDescription());
                row.createCell(3).setCellValue(article.getCategorie().getLibelle());
                row.createCell(4).setCellValue(article.getQuantite());
                // Ajoutez d'autres colonnes si nécessaire
            }

            // Afficher la boîte de dialogue de sauvegarde de fichier
            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialFileName("article_data.xlsx");
            File file = fileChooser.showSaveDialog(null);

            // Écrire les données dans le fichier Excel si un fichier est sélectionné
            if (file != null) {
                try (FileOutputStream outputStream = new FileOutputStream(file)) {
                    workbook.write(outputStream);
                    // Afficher une boîte de dialogue d'information
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "Données exportées avec succès!", ButtonType.OK);
                    alert.showAndWait();
                }
            }

            // Fermer le classeur Excel
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Afficher une boîte de dialogue d'erreur en cas d'échec d'exportation
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'exportation des données: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }

}
