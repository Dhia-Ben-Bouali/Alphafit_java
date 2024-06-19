package GUI;

import entite.Abonnement;
import entite.Pack;
import entite.user;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import static services.ServiceFA.genererPDF;
import static services.ServiceAbonnement.*;
import static services.userService.getCoaches;
import static services.userService.getNutrisionists;

public class AbonnementController {

    @FXML
    private Button GeneratePDF;

    @FXML
    private TableColumn<Abonnement, String> col_client;

    @FXML
    private TableColumn<Abonnement, String> col_coach;

    @FXML
    private TableColumn<Abonnement, Date> col_dateexp;

    @FXML
    private TableColumn<Abonnement, Integer> col_id;

    @FXML
    private TableColumn<Abonnement, String> col_nutrisionist;

    @FXML
    private TableColumn<Abonnement, String> col_pack;

    @FXML
    private TableView<Abonnement> Table;

    public void initialize() {
        showAbonnement();
    }

    @FXML
    void AffecterStaff(ActionEvent event) throws SQLException {
        List<user> coaches = getCoaches();
        List<user> nutrisionists = getNutrisionists();
        List<Abonnement> unassignedAbonnements = getUnassignedAbonnements();

        if (coaches.isEmpty() || nutrisionists.isEmpty()) {
            showAlertFaild("Failed","Warning: Not enough coaches, nutritionists");
            return;
        }

        if(unassignedAbonnements.isEmpty()){
            showAlertFaild("Failed","Warning: No unassigned subscriptions for assignment.");
            return;
        }

        Random random = new Random();

        for (Abonnement abonnement : unassignedAbonnements) {
            int coachIndex = random.nextInt(coaches.size());
            user coach = coaches.get(coachIndex);
            //System.out.println(coach);

            int nutritionistIndex = random.nextInt(nutrisionists.size());
            user nutritionist = nutrisionists.get(nutritionistIndex);
            //System.out.println(nutritionist);
            UpdateAbonnement(abonnement.getId(),coach,nutritionist);

        }
        showAbonnement();
        showAlertsucces("Succes", "Staff assignments completed for unassigned subscriptions");
    }

    @FXML
    public void SupprimerAbonnementExpirer(ActionEvent event) throws SQLException {
        ObservableList<Abonnement> abonnementList = Table.getItems();
        ObservableList<Abonnement> toRemove = FXCollections.observableArrayList();

        LocalDate today = LocalDate.now();

        for (Abonnement abonnement : abonnementList) {
            LocalDate expirationDate = abonnement.getDateExpiration().toLocalDate();
            if (expirationDate.isBefore(today)) {
                toRemove.add(abonnement);
            }
        }

        for (Abonnement abonnement : toRemove) {
            int idAbonnement = abonnement.getId();
            deleteAbonnement(idAbonnement);
        }
        showAlertsucces("Succes", "Expired subscriptions Deleted Successfully");
        showAbonnement();
    }

    public void showAbonnement() {
        ObservableList<Abonnement> abonnementList = getAbonnementList();
        Table.setItems(abonnementList);

        col_id.setCellValueFactory(new PropertyValueFactory<>("id"));

        col_client.setCellValueFactory(cellData -> {
            Abonnement abonnement = cellData.getValue();
            user user = abonnement.getClient();
            return new SimpleStringProperty(user != null ? user.getNom() : "");
        });


        col_coach.setCellValueFactory(cellData -> {
            Abonnement abonnement = cellData.getValue();
            user user = abonnement.getCoach();
            return new SimpleStringProperty(user != null ? user.getNom() : "");
        });

        col_nutrisionist.setCellValueFactory(cellData -> {
            Abonnement abonnement = cellData.getValue();
            user user = abonnement.getNutrisionist();
            return new SimpleStringProperty(user != null ? user.getNom() : "");
        });

        col_dateexp.setCellValueFactory(new PropertyValueFactory<>("dateExpiration"));

        col_pack.setCellValueFactory(cellData -> {
            Abonnement abonnement = cellData.getValue();
            Pack pack = abonnement.getPack();
            return new SimpleStringProperty(pack != null ? pack.getName() : "");
        });
    }

    @FXML
    void GeneratePdf(ActionEvent event) {
        genererPDF();
        showAlertsucces("Succes","Le PDF des abonnements a été généré avec succès.");
    }

    private void showAlertsucces(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlertFaild(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}


