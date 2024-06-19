package GUI;

import entite.Article;
import entite.Categorie;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import services.ArticleService;
import services.CategorieService;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class updatearticleController implements Initializable {
    @FXML
    private Button btnaup;

    @FXML
    private Label file_path;

    private String imageUrl;

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
    private Article selectedArticle;
    private ArticleService articleService;

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
    private listarticleController listController;
    private CategorieService categorieService;
    private Categorie selectedCategory;

    public void setListController(listarticleController listController) {
        this.listController = listController;
    }
    public void setArticleService(ArticleService articleService) {
        this.articleService = articleService;
    }


    @FXML
    private void handleInsertImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose an Image");
        File selectedFile = fileChooser.showOpenDialog(insert_image.getScene().getWindow());
        if (selectedFile != null) {
            imageUrl = selectedFile.getName();
            try {
                Image image = new Image(selectedFile.toURI().toString());
                image_view.setImage(image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setArticle(Article article) {
        this.selectedArticle = article;
        if (selectedArticle != null) {
            tfnom.setText(selectedArticle.getNom());
            tfdescription.setText(selectedArticle.getDescription());
            tfprix.setText(String.valueOf(selectedArticle.getPrix()));
            // Vérifiez si la valeur de la SpinnerValueFactory n'est pas null avant de l'utiliser
            if (tfquantite.getValueFactory() != null) {
                tfquantite.getValueFactory().setValue(selectedArticle.getQuantite());
            }            tfcategorie.setValue(article.getCategorie().getLibelle());

            String imageUrl = selectedArticle.getImage();
            // Vérifier si le chemin d'accès de l'image n'est pas vide
            if (imageUrl != null && !imageUrl.isEmpty()) {
                try {
                    // Charger l'image à partir du chemin d'accès
                    Image image = new Image(new File(imageUrl).toURI().toString());
                    image_view.setImage(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }
    @FXML
    void handleUpdateArticle(ActionEvent event) {
        // Valider les champs d'entrée
        if (!validateInputs()) {
            // Arrêter le traitement si les champs ne sont pas valides
            return;
        }

        // Mettez à jour les propriétés de l'article avec les valeurs des champs
        selectedArticle.setNom(tfnom.getText());
        selectedArticle.setDescription(tfdescription.getText());
        selectedArticle.setQuantite(tfquantite.getValue());

        // Mettez à jour le chemin d'accès de l'image dans l'article
        selectedArticle.setImage(imageUrl); // Assurez-vous que imageUrl est correctement mis à jour

        // Convertissez le prix du champ en double et affectez-le à l'article
        try {
            double prix = Double.parseDouble(tfprix.getText());
            selectedArticle.setPrix(prix);
        } catch (NumberFormatException e) {
            System.err.println("Le format du prix n'est pas valide : " + e.getMessage());
            return;
        }

        try {
            // Utilisez le service d'article pour mettre à jour l'article dans la base de données
            articleService.update(selectedArticle, selectedArticle.getId());
            System.out.println("Article mis à jour avec succès !");

            // Appel de la méthode setData() après la mise à jour
            setData(selectedArticle); // Assurez-vous que cette ligne est correcte
            // Mettez à jour l'article dans la liste observable de listarticleController
            if (listController != null) {
                listController.refreshArticleInListView(selectedArticle);
            }            // Fermez la fenêtre de mise à jour de l'article
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de l'article : " + e.getMessage());
            e.printStackTrace();
        }

    }


    public void setData(Article article) {
        if (article != null) {
            tfnom.setText(article.getNom());
            tfdescription.setText(article.getDescription());
            tfprix.setText(String.valueOf(article.getPrix()));

            // Vérifiez si la valeur de la SpinnerValueFactory n'est pas null avant de l'utiliser
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Integer.MAX_VALUE, article.getQuantite());
            tfquantite.setValueFactory(valueFactory);
            // Assurez-vous que la liste déroulante est correctement configurée
            tfcategorie.setValue(article.getCategorie().getLibelle());

            // Assurez-vous que l'image est correctement chargée dans l'interface utilisateur
            imageUrl = article.getImage();

        }
    }
    private boolean validateInputs() {
        boolean isValid = true;

        // Valider le nom pour qu'il ne contienne que des lettres
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


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        categorieService = new CategorieService();
        List<String> categorieLabels = categorieService.getAllLibelles();
        tfcategorie.getItems().addAll(categorieLabels);

        tfcategorie.setOnAction(event -> {
            String selectedLabel = tfcategorie.getSelectionModel().getSelectedItem();
            selectedCategory = categorieService.getByLibelle(selectedLabel);
        });
    }
}
