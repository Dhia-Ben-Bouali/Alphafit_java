package Service;

import com.alphafit.alphafit.Abonnement;
import com.alphafit.alphafit.Pack;
import com.alphafit.alphafit.user;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import Util.BDConnexion;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class ServiceAbonnement {
    public static void UpdateAbonnement(int id, user newCoach, user newNutritionist) throws SQLException {
        String update = "UPDATE Abonnement SET coach_id = ?, nutri_id = ?  WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = BDConnexion.getcon();
            st = con.prepareStatement(update);;
            st.setInt(1, newCoach.getId());
            st.setInt(2, newNutritionist.getId());
            st.setInt(3, id);
            st.executeUpdate();
        } finally {
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

    public static void deleteAbonnement(int id) throws SQLException {
        String delete = "DELETE FROM Abonnement WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = BDConnexion.getcon();
            st = con.prepareStatement(delete);
            st.setInt(1, id);
            st.executeUpdate();
        } finally {
            if (st != null) {
                st.close();
            }
            if (con != null) {
                con.close();
            }
        }
    }

    public static ObservableList<Abonnement> getAbonnementList() {
        ObservableList<Abonnement> abonnementList = FXCollections.observableArrayList();
        String query = "SELECT ab.id AS id_abonnement,\n" +
                "       p.nom AS pack_name,\n" +
                "       COALESCE(c.nom, '<--Not Assigned-->') AS coach_name,\n" +
                "       COALESCE(n.nom, '<--Not Assigned-->') AS nutri_name,\n" +
                "       u.nom AS client_name,\n" +
                "       ab.expiration_date\n" +
                "FROM abonnement ab\n" +
                "INNER JOIN pack p ON ab.npack_id = p.id\n" +
                "LEFT JOIN user c ON ab.coach_id = c.id  -- Use LEFT JOIN for coach and nutritionist\n" +
                "LEFT JOIN user n ON ab.nutri_id = n.id  -- Use LEFT JOIN for coach and nutritionist\n" +
                "INNER JOIN user u ON ab.client_id = u.id";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = BDConnexion.getcon();
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Abonnement abonnement = new Abonnement();
                abonnement.setId(rs.getInt("id_abonnement"));

                // Pack
                String packName = rs.getString("pack_name");
                if (packName != null) {
                    Pack pack = new Pack();
                    pack.setNom(packName);
                    abonnement.setPack(pack);
                }

                String UserName = rs.getString("client_name");
                if (UserName != null) {
                    user user = new user();
                    user.setNom(UserName);
                    abonnement.setClient(user);
                }


                String coachName = rs.getString("coach_name");
                if (coachName != null) {
                    user coach = new user();
                    coach.setNom(coachName);
                    abonnement.setCoach(coach);
                }


                String nutritionistName = rs.getString("nutri_name");
                if (nutritionistName != null) {
                    user nutritionist = new user();
                    nutritionist.setNom(nutritionistName);
                    abonnement.setNutrisionist(nutritionist);
                }

                abonnement.setDateExpiration(rs.getDate("expiration_date"));
                abonnementList.add(abonnement);
                System.out.println(abonnement.getCoach());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
                if (st != null) {
                    st.close();
                }
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return abonnementList;
    }

    public static List<Abonnement> getUnassignedAbonnements() throws SQLException {
        List<Abonnement> unassignedAbonnements = new ArrayList<>();

        String query = "SELECT * FROM Abonnement WHERE nutri_id IS NULL OR coach_id IS NULL";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = BDConnexion.getcon();
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                Abonnement abonnement = new Abonnement(/* Abonnement constructor arguments */); // Replace with actual constructor logic
                abonnement.setId(rs.getInt("id")); // Assuming an "id" column exists
                // Set other Abonnement attributes based on your table schema (e.g., client name, start date, etc.)
                unassignedAbonnements.add(abonnement);
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

        return unassignedAbonnements;
    }
}

