package GUI;

import entite.user;
import services.LoggedInUserManager;
import services.ServiceReclamation;
import entite.Reclamation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class MyReclamationController {

    @FXML
    private TilePane TileplaneReclamation;
    private ServiceReclamation ServiceReclamation;

    public void initialize() {
        ServiceReclamation = new ServiceReclamation();
        user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

        afficherReclamations(loggedInUser.getId());
    }

    public void afficherReclamations(int userId) {

        List<Reclamation> reclamations = ServiceReclamation.getReclamationsByUser(userId);
        for (Reclamation reclamation : reclamations) {
            VBox vbox = new VBox();

            Label contenuLabel = new Label("Contenu: " + reclamation.getContenu());
            Label etatLabel = new Label("État: " + reclamation.getEtat());
            Label dateLabel = new Label("Date: " + reclamation.getDate());

            vbox.getChildren().addAll(contenuLabel, etatLabel, dateLabel);

            TileplaneReclamation.getChildren().add(vbox);
        }
    }

}
