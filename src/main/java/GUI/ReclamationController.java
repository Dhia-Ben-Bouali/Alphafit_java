package GUI;

import Service.ServiceReclamation;
import Util.BDConnexion;
import com.alphafit.alphafit.Reclamation;
import com.alphafit.alphafit.user;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.ResourceBundle;

public class ReclamationController implements Initializable {
    Connection con = null;
    PreparedStatement st = null;
    ResultSet rs = null;

    @FXML
    private Button btnSearch;

    @FXML
    private TextField tSearch;


    @FXML
    private TableColumn<Reclamation, Integer> colid;

    @FXML
    private TableColumn<Reclamation, String> colcontenu;

    @FXML
    private TableColumn<Reclamation, Date> coldate;

    @FXML
    private TableColumn<Reclamation, String> coletat;

    @FXML
    private TableColumn<Reclamation, String> colid_user;

    @FXML
    private TableView<Reclamation> Table;
    int id = 0;

    @FXML
    private Button Chart;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnUpdate;

    @FXML
    private TextField tContenu;

    @FXML
    private DatePicker tDate;

    @FXML
    private TextField tUser;

    @FXML
    private ComboBox tEtat;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> list = FXCollections.observableArrayList("Pending","Under Treatment","Treated");
        tEtat.setItems(list);
        showReclamation();
    }

    public ObservableList<Reclamation> getReclamation() {
        return ServiceReclamation.getReclamationList();
    }


    public void showReclamation() {
        ObservableList<Reclamation> list = getReclamation();
        Table.setItems(list);
        colid.setCellValueFactory(new PropertyValueFactory<>("id"));
        colid_user.setCellValueFactory(cellData -> {
            Reclamation reclamation = cellData.getValue();
            user user = reclamation.getUser();
            if (user != null) {
                return new SimpleStringProperty(user.getNom());
            } else {
                return new SimpleStringProperty("");
            }
        });
        coldate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colcontenu.setCellValueFactory(new PropertyValueFactory<>("contenu"));
        coletat.setCellValueFactory(new PropertyValueFactory<>("etat"));
    }

    @FXML
    void ClearReclamation(ActionEvent event) {
        clear();
    }

    @FXML
    void DeleteReclamation(ActionEvent event) {
        try {
            ServiceReclamation.deleteReclamation(id);
            clear();
            showReclamation();
            btnSave.setDisable(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void UpdateReclamation(ActionEvent event) {
        try {
            String contenu = tContenu.getText();
            LocalDate date = tDate.getValue();
            String user = tUser.getText();
            int idUtilisateur = getIdUtilisateurParNom(user);


            if (contenu.length() <= 10) {

                showAlert("Erreur de saisie","Le contenu doit contenir plus de 10 caractères.");
                highlightErrorFields(tContenu);
                //vibrateIfEmpty(tContenu);
                resetFieldStylesWithDelay(tContenu);
                return;
            }


            if (date != null && !date.isBefore(LocalDate.now())) {
                showAlert("Erreur de saisie",  "La date doit être dans le passé.");
                highlightErrorFields(tDate.getEditor());
                //vibrateIfEmpty(tContenu);
                resetFieldStylesWithDelay(tDate.getEditor());
                return;
            }


            ServiceReclamation.updateReclamation(id, idUtilisateur, tEtat.getSelectionModel().getSelectedItem().toString(), contenu, date);
            showAlertsucces("Succès", "La réclamation a été mise à jour.");

            clear();
            showReclamation();
            btnSave.setDisable(false);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    void getDate(MouseEvent event) {
        Reclamation reclamation = Table.getSelectionModel().getSelectedItem();
        ObservableList<String> list = FXCollections.observableArrayList("Pending","Under Treatment","Treated");
        id = reclamation.getId();

        tUser.setText(reclamation.getUser().getNom());

        tEtat.setValue(reclamation.getEtat());
        tEtat.setItems(list);

        Date date = reclamation.getDate();
        LocalDate localDate = ((java.sql.Date) date).toLocalDate();
        tDate.setValue(localDate);

        tContenu.setText(reclamation.getContenu());
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Tooltip tooltip = new Tooltip();
        tooltip.setText(dateFormat.format(date));
        tDate.setTooltip(tooltip);
        tContenu.setEditable(false);
        tDate.setDisable(true);
        tUser.setEditable(false);
        tContenu.setDisable(true);
        tUser.setDisable(true);

    }


    @FXML
    String select(ActionEvent event) {
        String s = tEtat.getSelectionModel().getSelectedItem().toString();
        return s;
    }


    @FXML
    void Navigate(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/ReclamationClient.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Client interface");
        stage.show();
    }


    @FXML
    void GestionAbonnement(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/Abonnement.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Gestion Abonnement");
        stage.show();
    }

    @FXML
    void ShowStats(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Fxml/ChartReclamation.fxml"));
        Stage stage = new Stage();
        stage.setScene(new Scene(root));
        stage.setTitle("Statistique");
        stage.show();
    }

    @FXML
    void Search(ActionEvent event) {
        String searchTerm = tSearch.getText().trim();
        if (!searchTerm.isEmpty()) {
            ObservableList<Reclamation> filteredList = FXCollections.observableArrayList();
            for (Reclamation reclamation : getReclamation()) {
                if (reclamation.getUser() != null && reclamation.getUser().getNom().equalsIgnoreCase(searchTerm)) {
                    filteredList.add(reclamation);
                }
            }
            Table.setItems(filteredList);
        } else {
            // If the search term is empty, show all reclamations
            showReclamation();
        }
    }


    /**********************************************Custom function*****************************************************/

    void resetFieldStyles(TextField... textFields) {
        for (TextField textField : textFields) {
            textField.setStyle(null);
        }
    }

    void resetFieldStylesWithDelay(TextField... textFields) {
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                Platform.runLater(() -> resetFieldStyles(textFields));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }


    void clear(){
        tUser.setText(null);
        tEtat.setItems(null);
        tContenu.setText(null);
        tDate.setValue(null);
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertsucces(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    void highlightErrorFields(TextField textField) {
        textField.setStyle("-fx-border-color: red;");
    }


    public static int getIdUtilisateurParNom(String nomUtilisateur) throws SQLException {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        int idUtilisateur = -1; // Valeur par défaut si l'utilisateur n'est pas trouvé

        try {
            con = BDConnexion.getcon();
            String query = "SELECT id FROM user WHERE nom = ?";
            st = con.prepareStatement(query);
            st.setString(1, nomUtilisateur);
            rs = st.executeQuery();

            if (rs.next()) {
                idUtilisateur = rs.getInt("id");
            }
        } finally {
            if (rs != null) {
                rs.close();
            }
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }

        return idUtilisateur;
    }



}
