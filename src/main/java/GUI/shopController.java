package GUI;


import entite.Article;
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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import services.ArticleService;
import services.CartService;
import services.CategorieService;

import test.MainFx;

import java.io.*;
import java.net.URL;
import java.util.*;

import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.scene.control.Alert.AlertType;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import javafx.scene.image.WritableImage;

import javax.imageio.ImageIO;

public class shopController implements Initializable {
    @FXML
    private ComboBox<String> categoryComboBox;
    @FXML
    public Button pdf;
    @FXML
    private VBox chosenArticleCard;

    @FXML
    private ImageView articleImg;

    @FXML
    private GridPane grid;
    private ArticleService articleService;
    private CategorieService categorieService;

    private int column = 0;
    private int row = 1;
    @FXML
    private Button btnsearch;
    @FXML
    private TextField search;
    @FXML
    private VBox chosenCard;

    @FXML
    private ImageView Img;

    @FXML
    private Label NameLabel;
    @FXML
    private Label description;
    @FXML
    private Label PriceLabel;
    @FXML
    private Button btnGeneratePDF;
    @FXML
    private shopController shopController;
    private CartService cartService = CartService.getInstance();
    private Article selectedArticle;
    @FXML
    private AnchorPane contentArea;
    private ScheduledExecutorService scheduler;

    // Default constructor
    public shopController() {
        System.out.println("Constructeur shopController appelé.");
    }

    @FXML
    public void setShopController(shopController shopController) {
        this.shopController = shopController;
    }
    public shopController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println("Initialisation du contrôleur shopController.");

        // Initialize ArticleService if it's not already initialized
        if (articleService == null) {
            articleService = new ArticleService();
        }

        // Récupérer les données d'articles depuis la base de données en utilisant le service ArticleService
        List<Article> articles = articleService.getAll();

        // Mettre à jour l'interface utilisateur avec les données d'articles
        for (Article article : articles) {
            addArticleToUI(article);
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::refreshArticleList, 0, 20, TimeUnit.SECONDS);

        // Initialiser le service CategorieService
        categorieService = new CategorieService();

        // Récupérer les catégories depuis le service
        List<String> categories = categorieService.getAllLibelles();

        // Créer une liste observable à partir des catégories
        ObservableList<String> observableCategories = FXCollections.observableArrayList(categories);

        // Remplir la liste déroulante avec les catégories
        categoryComboBox.setItems(observableCategories);
        // Obtenez la liste des articles avec une quantité inférieure à un certain seuil
//        List<Article> articlesBelowThreshold = articleService.getArticlesBelowThreshold();
//
//        // Afficher l'alerte uniquement lorsque vous êtes sur la page du magasin
//        displayLowQuantityAlert(articlesBelowThreshold);
    }
    private void refreshArticleList() {
        // Retrieve updated articles from the database
        List<Article> updatedArticles = articleService.getAll();

        // Update UI with the updated list of articles
        Platform.runLater(() -> {
            grid.getChildren().clear(); // Clear the existing articles in the UI grid
            for (Article article : updatedArticles) {
                addArticleToUI(article);
            }
        });
    }

    @FXML
    public void handleSortByCategory(ActionEvent event) {
        // Obtenir la catégorie sélectionnée
        String selectedCategory = categoryComboBox.getValue();

        // Vérifier si une catégorie est sélectionnée
        if (selectedCategory != null && !selectedCategory.isEmpty()) {
            System.out.println("Catégorie sélectionnée : " + selectedCategory);

            // Récupérer l'ID de la catégorie sélectionnée
            int categoryId = categorieService.getIdByLibelle(selectedCategory);

            // Vérifier si l'ID de la catégorie est valide (c'est-à-dire si la catégorie existe)
            if (categoryId != -1) {
                // Récupérer les articles de la catégorie sélectionnée en utilisant son ID
                List<Article> articlesByCategory = articleService.getArticlesByCategoryId(categoryId);

                // Vérifier si des articles ont été trouvés dans la catégorie sélectionnée
                if (!articlesByCategory.isEmpty()) {
                    System.out.println("Nombre d'articles trouvés dans la catégorie : " + articlesByCategory.size());

                    // Mettre à jour l'interface utilisateur avec les articles filtrés
                    updateUIWithFilteredArticles(articlesByCategory);
                } else {
                    System.out.println("Aucun article trouvé dans la catégorie : " + selectedCategory);
                    // Afficher un message d'alerte ou un message dans la console pour informer l'utilisateur qu'aucun article n'a été trouvé dans la catégorie sélectionnée
                }
            } else {
                System.out.println("La catégorie sélectionnée n'existe pas : " + selectedCategory);
                // Afficher un message d'alerte ou un message dans la console pour informer l'utilisateur que la catégorie sélectionnée n'existe pas
            }
        } else {
            System.out.println("Aucune catégorie sélectionnée.");
            // Afficher un message d'alerte ou un message dans la console pour informer l'utilisateur qu'aucune catégorie n'a été sélectionnée
        }
    }
    // Méthode pour mettre à jour l'interface utilisateur avec les articles filtrés
    private void updateUIWithFilteredArticles(List<Article> filteredArticles) {
        grid.getChildren().clear(); // Effacer la grille actuelle
        for (Article article : filteredArticles) {
            addArticleToUI(article);
        }
    }

    private void addArticleToUI(Article article) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/item.fxml"));
        try {
            AnchorPane anchorPane = fxmlLoader.load();
            ItemController itemController = fxmlLoader.getController();
            itemController.setShopController(this); // Passer la référence du ShopController
            itemController.setData(article);

            // Ajouter l'élément d'article à la grille dans l'interface utilisateur
            grid.add(anchorPane, column++, row);
            if (column == 3) {
                column = 0;
                row++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Méthode pour mettre à jour les données de l'article choisi
    public void setChosenArticle(Article article) {
        selectedArticle = article;
        // Mettre à jour les labels avec les données de l'article choisi
        if (NameLabel != null && PriceLabel != null && description != null) {
            NameLabel.setText(article.getNom());
            PriceLabel.setText(article.getPrix() + MainFx.CURRENCY);
            description.setText(article.getDescription());

            // Charger et afficher l'image de l'article choisi
            try {
                String imagePath = "/image/" + article.getImage();
                InputStream inputStream = getClass().getResourceAsStream(imagePath);
                if (inputStream != null) {
                    Image image = new Image(inputStream);
                    Img.setImage(image);
                } else {
                    System.err.println("Impossible de charger l'image: " + imagePath);
                }
            } catch (Exception e) {
                System.err.println("Une erreur s'est produite lors du chargement de l'image: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.err.println("Certains éléments d'interface utilisateur sont null.");
        }
    }


    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    @FXML
    void handleSearchArticle(ActionEvent event) {
        String searchTerm = search.getText();
        List<Article> searchResults = articleService.searchByNom(searchTerm);
        grid.getChildren().clear(); // Effacer la grille actuelle
        for (Article article : searchResults) {
            addArticleToUI(article);
        }
    }
    private ImageView generateQRCode(Article article) {
        int width = 200;
        int height = 200;
        WritableImage qrImage = new WritableImage(width, height);
        PixelWriter pixelWriter = qrImage.getPixelWriter();
        String content = "Nom: " + article.getNom() + "\n" +
                "Description: " + article.getDescription() + "\n" +
                "Quantité: " + article.getQuantite() + "\n" +
                "Prix: " + article.getPrix();
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    pixelWriter.setArgb(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ImageView qrImageView = new ImageView(qrImage);
        return qrImageView;
    }

    @FXML
    void handleAddToCart(ActionEvent event) {
        if (selectedArticle != null) {
            cartService.addToCart(selectedArticle, 1);
            System.out.println("Article added to cart: " + selectedArticle.getNom());
        } else {
            System.err.println("No article selected");
        }
    }


    @FXML
    public void gotocart(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/cart.fxml"));
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

    public void showAlertIfNeeded() {
        // Obtenez la liste des articles avec une quantité inférieure à un certain seuil
        List<Article> articlesBelowThreshold = articleService.getArticlesBelowThreshold();

        // Afficher l'alerte uniquement lorsque vous êtes sur la page du magasin
        displayLowQuantityAlert(articlesBelowThreshold);
    }
    public void displayLowQuantityAlert(List<Article> articlesBelowThreshold) {
        if (!articlesBelowThreshold.isEmpty()) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Quantité d'articles faible");
            alert.setHeaderText(null);
            VBox content = new VBox();

            for (Article article : articlesBelowThreshold) {
                StringBuilder message = new StringBuilder("Les articles suivants ont une quantité inférieure à 5 :\n");
                message.append(article.getNom()).append("\n");

                // Appel de generateQRCode avec l'objet Article
                ImageView qrCode = generateQRCode(article);
                content.getChildren().addAll(new Label(message.toString()), qrCode);
            }
            alert.getDialogPane().setContent(content);
            alert.showAndWait();
        }
    }

    @FXML
    public void OnExport(ActionEvent actionEvent) {
        System.out.println("OnExport method called.");

        try {
            ArticleService articleService = new ArticleService();
            List<Article> articlesList = articleService.getAll();

            if (articlesList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Aucun produit", "Il n'y a aucun produit à ajouter au PDF.");
                return;
            }

            // Create a new PDF document
            PDDocument document = new PDDocument();
            PDPage page = new PDPage();
            document.addPage(page);

            // Create a new content stream for writing to the PDF
            PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true);

            // Set up the initial position for adding content
            float startX = 50; // Starting X position for content
            float startY = 700; // Starting Y position for content
            float lineHeight = 20; // Line height for each product

            // Ajouter le titre au PDF
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20); // Utilisez une police de caractères en gras pour le titre
            contentStream.setNonStrokingColor(0, 0, 255); // Définir la couleur du texte en bleu (par exemple)
            contentStream.newLineAtOffset(200, 750); // Définir la position du titre
            contentStream.showText("Notre catalogue en ligne"); // Texte du titre
            contentStream.endText();

            // Iterate through the list of articles
            for (Article article : articlesList) {
                // Imprimer le chemin d'accès de l'image
                System.out.println("Chemin d'accès de l'image : " + article.getImage());

                // Construire le chemin complet de l'image
                String imagePath = "/image/" + article.getImage();
                System.out.println("Chemin complet de l'image : " + imagePath);

                // Obtenez le flux d'entrée pour l'image
                InputStream inputStream = getClass().getResourceAsStream(imagePath);

                // Vérifiez si le flux d'entrée est null
                if (inputStream == null) {
                    System.out.println("Le flux d'entrée pour l'image est null.");
                } else {
                    System.out.println("Le flux d'entrée pour l'image n'est pas null.");
                }

                // Ajoutez l'image au PDF
                byte[] imageBytes = inputStream.readAllBytes();
                PDImageXObject imageXObject = PDImageXObject.createFromByteArray(document, imageBytes, "image");
                contentStream.drawImage(imageXObject, startX, startY - 100, 100, 100);

                // Ajoutez les informations de l'article au PDF
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.beginText();
                contentStream.newLineAtOffset(startX + 120, startY);
                contentStream.showText("Nom: " + article.getNom());
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Description: " + article.getDescription());
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.showText("Prix: " + article.getPrix());
                contentStream.newLineAtOffset(0, -lineHeight);
                contentStream.endText();

                // Move to the next position for the next product
                startY -= 150; // Adjust for image height and some spacing
            }

            // Get page dimensions
            float pageWidth = page.getMediaBox().getWidth();
            float pageHeight = page.getMediaBox().getHeight();

            // Calculate coordinates for placing the date at the bottom right corner
            float dateX = pageWidth - 200; // Adjust as needed
            float dateY = 30; // Adjust as needed

            // Ajouter la date de création du catalogue à la fin
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.beginText();
            contentStream.newLineAtOffset(dateX, dateY); // Position de la date
            contentStream.showText("Date de création du catalogue : " + getCurrentDate()); // Afficher la date
            contentStream.endText();

            // Close the content stream
            contentStream.close();
            String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";

            // Save the PDF to a file
            File file = new File(desktopPath,"shop_products.pdf");
            document.save(file);
            document.close();

            showAlert(Alert.AlertType.INFORMATION, "PDF Exporté", "Les produits ont été ajoutés au PDF avec succès.");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Une erreur s'est produite lors de la création du PDF.");
        }
    }

    private String getCurrentDate() {
        // Obtenir la date actuelle
        Date currentDate = new Date();
        // Formater la date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        return dateFormat.format(currentDate);
    }
    private void showAlert(Alert.AlertType alertType, String title, String message) {

        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setContentArea(AnchorPane contentArea) {
        this.contentArea = contentArea;
    }
}