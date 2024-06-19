package GUI;


import entite.Categorie;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import services.CategorieService;

import java.net.URL;
import java.util.ResourceBundle;

public class CategorieController  implements Initializable {

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private ListView<String> liste;

    @FXML
    private TextField tflabelle;

    @FXML
    private Label labellerror;

    private CategorieService categorieService = new CategorieService(); // Service pour gérer les catégories

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        categorieService = new CategorieService(); // Initialisez votre service

        // Chargez les libellés depuis la base de données et ajoutez-les à la ListView
        liste.getItems().addAll(categorieService.getAllLibelles());
    }

    @FXML
    void sortAscending(ActionEvent event) {
        ObservableList<String> items = liste.getItems();
        items.sort(String::compareToIgnoreCase);
    }

    @FXML
    void sortDescending(ActionEvent event) {
        ObservableList<String> items = liste.getItems();
        items.sort((s1, s2) -> s2.compareToIgnoreCase(s1));
    }

    void refreshListe() {
        liste.getItems().clear(); // Effacer la liste existante
        liste.getItems().addAll(categorieService.getAllLibelles()); // Ajouter toutes les catégories à partir du service
    }

    @FXML
    void createCategorie(ActionEvent event) {
        // Valider les champs d'entrée
        if (!validateInputs()) {
            // Arrêter le traitement si les champs ne sont pas valides
            return;
        }
        String libelle = tflabelle.getText();


            categorieService.add(new Categorie(libelle));//crée un nouvel objet de type Categorie
            refreshListe(); // Mettre à jour la liste après l'ajout

            tflabelle.clear(); // Effacez le champ de saisie après l'ajout
            labellerror.setVisible(false);

    }

    @FXML
    void clearCategorie(ActionEvent event) {
        tflabelle.clear();
    }

    @FXML
    void deleteCategorie(ActionEvent event) {
        String selectedItem = liste.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            liste.getItems().remove(selectedItem);
            int id = categorieService.getIdByLibelle(selectedItem);
            categorieService.delete(id);
        }
    }

    @FXML
    void updateCatgorie(ActionEvent event) {
        String selectedItem = liste.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            String newLibelle = tflabelle.getText();
            int id = categorieService.getIdByLibelle(selectedItem);
            categorieService.update(new Categorie(newLibelle), id);
            int selectedIndex = liste.getSelectionModel().getSelectedIndex();
            liste.getItems().set(selectedIndex, newLibelle);
        }
    }

    @FXML
    void handleListViewClick(MouseEvent event) {
        String selectedItem = liste.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            tflabelle.setText(selectedItem);
        }
    }

    public void setCategorieService(CategorieService categorieService) {
        this.categorieService = categorieService;
    }

    public boolean validateInputs() {
        boolean isValid = true;
        if (tflabelle.getText().isEmpty()) {
            labellerror.setText("Le libellé de la catégorie est vide.");
            isValid = false;
        } else if (!tflabelle.getText().matches("[a-zA-Z]+")) {
            labellerror.setText("Le libellé de la catégorie ne doit contenir que des lettres.");
            isValid = false;
        } else {
            labellerror.setText("");
        }
        return isValid;
    }
}
