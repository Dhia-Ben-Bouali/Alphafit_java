package GUI;

import entite.Article;
import entite.Categorie;
import entite.user;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.*;

import javax.mail.MessagingException;
import java.awt.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class articleController implements Initializable {

    @FXML
    private Button btnadd;

    @FXML
    private ImageView image_view;

    @FXML
    private Button insert_image;

    @FXML
    private ComboBox<String> tfcategorie;

    @FXML
    private TextField tfdescription;

    @FXML
    private TextField tfnom;

    @FXML
    private TextField tfprix;

    @FXML
    private Spinner<Integer> tfquantite;
    @FXML
    private Label descriptionErrorLabel;

    @FXML
    private Label imageErrorLabel;
    @FXML
    private Label nomErrorLabel;

    @FXML
    private Label prixErrorLabel;
    private ArticleService articleService;
    private listarticleController listArticleController;
    private CategorieService categorieService;
    private Categorie selectedCategory;
    private String imageUrl;
    private Article selectedArticle;

// ces méthodes permettent d'établir des connexions entre différentes parties de l'application, en fournissant au contrôleur articleController l'accès à des services et à d'autres contrôleurs nécessaires à son fonctionnement.

    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }

    public void setListArticleController(listarticleController listArticleController) {
        this.listArticleController = listArticleController;//Cette méthode semble permettre au articleController de communiquer avec un autre contrôleur appelé listarticleController. Il est probable que listarticleController soit responsable de l'affichage de la liste des articles quelque part dans l'interface utilisateur.
       // En injectant listarticleController dans articleController, on permet au articleController d'appeler des méthodes ou d'accéder à des fonctionnalités de listarticleController, comme l'ajout d'un nouvel article à la liste des articles affichée.
    }

    @FXML
    private void handleInsertImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        File selectedFile = fileChooser.showOpenDialog(insert_image.getScene().getWindow());
        if (selectedFile != null) {
            imageUrl = selectedFile.getName();
            try {
                Image image = new Image(selectedFile.toURI().toString());//essaie ensuite de charger l'image à partir du fichier sélectionné en utilisant Image image = new Image(selectedFile.toURI().toString())
                image_view.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        tfquantite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 100, 1));
        categorieService = new CategorieService();
        List<String> categorieLabels = categorieService.getAllLibelles();
        tfcategorie.getItems().addAll(categorieLabels);

        tfcategorie.setOnAction(event -> {
            String selectedLabel = tfcategorie.getSelectionModel().getSelectedItem();
            selectedCategory = categorieService.getByLibelle(selectedLabel);
        });


    }

    @FXML
    private void handleAddArticle(ActionEvent event) {
        // Valider les champs d'entrée
        if (!validateInputs()) {
            // Arrêter le traitement si les champs ne sont pas valides
            return;
        }

        // Si les champs sont valides, continuer avec l'ajout de l'article
        String nom = tfnom.getText();
        String description = tfdescription.getText();
        String prixText = tfprix.getText();
        int quantite = tfquantite.getValue();
        String imageUrl = getImageUrl();

        double prix;
        try {
            prix = Double.parseDouble(prixText);
        } catch (NumberFormatException e) {
            System.out.println("Le prix doit être un nombre valide.");
            return;
        }

        Article article = new Article(nom, prix, quantite, description, imageUrl, selectedCategory);
        articleService.add(article);
        try {
            String s;
            s = "Product Added With Success  " ;
            NotificationUtil.showNotification("Product ",s);
        } catch (AWTException | MalformedURLException ex) {
            ex.printStackTrace();
        }
        try {
            userService us = new userService();
            List<user> userList = new ArrayList<>();
            String s;
            s = "Product Added With Success  " ;
            NotificationUtil.showNotification("Product ",s);
            Email emailSender=new Email();
            for(user u: userList){
                String recipientEmail = u.getEmail();
                String subject = "New Package has been added!";
                String body = "Hello " + u.getPrenom() + ",\n\n";
                body += "We are excited to inform you that a new package has been added to our Packages:\n";
                body += "Package Name: " + article.getNom() + "\n";
                body += "Price: " + article.getPrix() + " TND\n\n";
                body += "Check out our website for more details!\n";
                body += "Best regards,\nAlpha Fit";

                try {
                    emailSender.sendEmail(recipientEmail, subject, body);
                } catch (MessagingException e) {
                    System.err.println("Failed to send email: " + e.getMessage());
                }
            }

        } catch (AWTException | MalformedURLException ex) {
            ex.printStackTrace();
        }
        if (listArticleController != null) {
            listArticleController.addArticleToListView(article);
        } else {
            System.out.println("listArticleController is null. Make sure it is properly initialized.");
        }

        // Fermer la fenêtre d'ajout d'article
        Stage stage = (Stage) btnadd.getScene().getWindow();
        stage.close();

    }



    private boolean validateInputs() {
        boolean isValid = true;

        if (tfnom.getText().isEmpty()) {
            nomErrorLabel.setText("Le titre de l'article ne peut pas être vide.");
            isValid = false;
        } else if (!tfnom.getText().matches("[a-zA-Z]+")) {
            nomErrorLabel.setText("Le titre de l'article ne doit contenir que des lettres.");
            isValid = false;
        } else {
            nomErrorLabel.setText("");
        }

        String prixText = tfprix.getText();
        if (prixText.isEmpty()) {
            prixErrorLabel.setText("Le prix de l'article ne peut pas être vide.");
            isValid = false;
        } else {
            try {
                double prix = Double.parseDouble(prixText);
                if (prix <= 0.0) {
                    prixErrorLabel.setText("Le prix de l'article doit être supérieur à zéro.");
                    isValid = false;
                } else {
                    prixErrorLabel.setText("");
                }
            } catch (NumberFormatException e) {
                prixErrorLabel.setText("Le prix de l'article doit être un nombre valide.");
                isValid = false;
            }
        }

        if (tfdescription.getText().isEmpty()) {
            descriptionErrorLabel.setText("La description de l'article ne peut pas être vide.");
            isValid = false;
        } else {
            descriptionErrorLabel.setText("");
        }

        if (image_view.getImage() == null) {
            imageErrorLabel.setText("Veuillez choisir une image pour l'article.");
            isValid = false;
        } else {
            imageErrorLabel.setText("");
        }



        return isValid;
    }

}
