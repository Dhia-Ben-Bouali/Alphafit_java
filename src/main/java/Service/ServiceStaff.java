package Service;

import com.alphafit.alphafit.Abonnement;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Objects;

import static Service.ServiceAbonnement.getAbonnementList;

public class ServiceStaff {
    public static ObservableList<Abonnement> extractByCoachOrNutritionist(String name) {
        ObservableList<Abonnement> affectedAbonnementsToStaff = FXCollections.observableArrayList();
        ObservableList<Abonnement> allAbonnements = getAbonnementList();

        for (Abonnement abonnement : allAbonnements) {
            System.out.println(abonnement.getCoach());
            if (abonnement.getNutrisionist().getNom() != null && Objects.equals(abonnement.getCoach().getNom(), name)) {
                affectedAbonnementsToStaff.add(abonnement);
            } else if (abonnement.getCoach().nom != null && Objects.equals(abonnement.getNutrisionist().getNom(), name)) {
                affectedAbonnementsToStaff.add(abonnement);
            }
        }
        return affectedAbonnementsToStaff;
    }
}
