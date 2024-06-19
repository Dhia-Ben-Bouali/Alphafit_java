package GUI;

import entite.user;
import io.grpc.internal.JsonUtil;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.LoggedInUserManager;
import services.ServiceReclamation;


import entite.Abonnement;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.fxml.FXML;
import javafx.scene.layout.TilePane;
import javafx.scene.text.Text;
import javafx.scene.control.Alert;
import services.ServiceStaff;

import java.io.IOException;
import java.sql.SQLException;

import static services.userService.ClientIdRetriever.getClientIdByName;

public class StaffController {


    @FXML
    private Text nameStaff;

    @FXML
    private TilePane tilePaneAbonnements;

    private ServiceStaff  ServiceStaff;

    public void initialize() throws SQLException {
        System.out.println("hello");
        ServiceStaff = new ServiceStaff();
        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

        afficherAbonnements(loggedInUser.getNom());




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
                PlanningtButton.setOnAction(e -> handleMessagerieAbonnement(abonnement,id));
                MessagerieButton.setOnAction(e -> handlePlanningAbonnement(abonnement,id));

                vbox.getChildren().addAll(packLabel, clientLabel);
                vbox1.getChildren().addAll(vbox, PlanningtButton, MessagerieButton);

                tilePaneAbonnements.getChildren().add(vbox1);
            }
        }
    }
    // Define methods to handle edit and delete actions
    private void handleMessagerieAbonnement(Abonnement abonnement,int userid) {


        try {
            System.out.println("hiiiiiiiiiii");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowAllSuiviCoach.fxml"));
            Parent root = loader.load(); // Charger la vue et créer le contrôleur
            System.out.println("hiiiiiiiiiii11111111");

            CalendarControllerCoach controller = loader.getController(); // Obtenir une référence au contrôleur
            user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();
            System.out.println(abonnement);
            System.out.println("hiiiiiiiiiii222222222++++++++++++++++++"+userid);

            controller.setId(userid); // Appeler la méthode pour transmettre categoryId
            System.out.println("hiiiiiiiiiii33333333333333333");

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Message");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void handlePlanningAbonnement(Abonnement abonnement,int userid) {
        System.out.println(abonnement);


        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowAllSuiviCoach.fxml"));
            Parent root = loader.load(); // Charger la vue et créer le contrôleur
            System.out.println("hiiiiiiiiiii11111111");

            CalendarControllerCoach controller = loader.getController(); // Obtenir une référence au contrôleur
            user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();
            System.out.println("hiiiiiiiiiii222222222"+ userid);

            controller.setId(userid); // Appeler la méthode pour transmettre categoryId
            System.out.println("hiiiiiiiiiii33333333333333333");

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Message");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void ShowInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
