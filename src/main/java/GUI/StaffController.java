package GUI;

import Service.ServiceReclamation;
import Service.ServiceStaff;
import com.alphafit.alphafit.Abonnement;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;

import java.sql.SQLException;

import static Service.ServiceUser.ClientIdRetriever.getClientIdByName;


public class StaffController {


    @FXML
    private Text nameStaff;

    @FXML
    private TilePane tilePaneAbonnements;

    private ServiceStaff  ServiceStaff;

    public void initialize() throws SQLException {
        System.out.println("hello");
        ServiceStaff = new ServiceStaff();
        afficherAbonnements("Amira_Coach3");
    }

    public void afficherAbonnements(String coachOrNutriId) throws SQLException {
        ObservableList<Abonnement> affectedAbonnements = ServiceStaff.extractByCoachOrNutritionist(coachOrNutriId);

        nameStaff.setText(coachOrNutriId);

        if (affectedAbonnements.isEmpty()) {
            ShowInfo("Info","No abonnements found for coach/nutritionist ID: " + coachOrNutriId);
        } else {
            for (Abonnement abonnement : affectedAbonnements) {
                VBox vbox1 = new VBox();
                VBox vbox = new VBox();
                // Assuming you have getters for the client, coach, and nutritionist names
                Label packLabel = new Label("Pack: " + abonnement.getPack().getNom());
                String namatoId = abonnement.getClient().getNom();
                int id = getClientIdByName(namatoId);
                Label clientLabel = new Label("Client: " + id);

                // Create buttons
                Button PlanningtButton = new Button("Planning");
                Button MessagerieButton = new Button("Messagerie");

                // Add event handlers to buttons if needed
                PlanningtButton.setOnAction(e -> handleMessagerieAbonnement(abonnement));
                MessagerieButton.setOnAction(e -> handlePlanningAbonnement(abonnement));

                vbox.getChildren().addAll(packLabel, clientLabel);
                vbox1.getChildren().addAll(vbox, PlanningtButton, MessagerieButton);

                tilePaneAbonnements.getChildren().add(vbox1);
            }
        }
    }
    // Define methods to handle edit and delete actions
    private void handleMessagerieAbonnement(Abonnement abonnement) {

    }

    private void handlePlanningAbonnement(Abonnement abonnement) {

    }

    private void ShowInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
