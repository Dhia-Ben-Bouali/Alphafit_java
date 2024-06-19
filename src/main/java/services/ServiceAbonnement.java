package services;

import entite.Pack;
import entite.user;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import entite.Abonnement;
import util.DataSource;


public class ServiceAbonnement {
    Connection con;

    public ServiceAbonnement() {
        con = DataSource.getInstance().getConnection();
    }

    public static void UpdateAbonnement(int id, user newCoach, user newNutritionist) throws SQLException {
        String update = "UPDATE Abonnement SET coach_id = ?, nutri_id = ?  WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        try {
            con= DataSource.getInstance().getConnection();
            st = con.prepareStatement(update);
            st.setInt(1, newCoach.getId());
            st.setInt(2, newNutritionist.getId());
            st.setInt(3, id);
            st.executeUpdate();
        } finally {

        }
    }

    public static void deleteAbonnement(int id) throws SQLException {
        String delete = "DELETE FROM Abonnement WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        try {
            con= DataSource.getInstance().getConnection();
            st = con.prepareStatement(delete);
            st.setInt(1, id);
            st.executeUpdate();
        } finally {
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
                "INNER JOIN user u ON ab.client_id = u.id"; // Assuming a nutrisionist_id field in Abonnement
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con= DataSource.getInstance().getConnection();
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
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }

        return abonnementList;
    }

    public static List<Abonnement> getUnassignedAbonnements() throws SQLException {
        List<Abonnement> unassignedAbonnements = new ArrayList<>();

        String query = "SELECT * FROM abonnement WHERE nutri_id IS NULL OR coach_id IS NULL";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = DataSource.getInstance().getConnection();
            st = con.prepareStatement(query);
            rs = st.executeQuery();

            while (rs.next()) {
                Abonnement abonnement = new Abonnement(/* Abonnement constructor arguments */); // Replace with actual constructor logic
                abonnement.setId(rs.getInt("id")); // Assuming an "id" column exists
                unassignedAbonnements.add(abonnement);
            }
        } finally {
        }

        return unassignedAbonnements;
    }

    public List<user> getAdherentsByCoach(int coachId) {
        List<user> adherentsList = new ArrayList<>();
        String query = "SELECT u.* FROM user u " +
                "JOIN Abonnement ab ON u.id = ab.client_id " +
                "WHERE ab.coach_id = ?";
        try (PreparedStatement stm = con.prepareStatement(query)) {
            stm.setInt(1, coachId);
            try (ResultSet rs = stm.executeQuery()) {
                while (rs.next()) {
                    int userId = rs.getInt("id");
                    userService service = new userService();
                    user adherent = new user();
                    adherent = service.getById(userId);
                    if (adherent != null) {
                        adherentsList.add(adherent);
                    } else {
                        System.out.println("User with ID " + userId + " not found.");
                    }
                }
            }
        } catch (SQLException ex) {
            System.out.println("Error fetching adherents for coach: " + ex.getMessage());
        }
        userService service = new userService();

        adherentsList.add(service.getById(coachId));
        return adherentsList;
    }


    public List<user> getCoachAndNutritionist(int userId) {
        List<user> coachAndNutritionist = new ArrayList<>();
        String query = "SELECT coach_id, nutri_id FROM abonnement WHERE client_id = ?";
        try (PreparedStatement stm = con.prepareStatement(query)) {
            stm.setInt(1, userId);
            try (ResultSet rs = stm.executeQuery()) {
                if (rs.next()) {
                    int coachId = rs.getInt("coach_id");
                    int nutritionistId = rs.getInt("nutri_id");
                    userService service =new userService();
                    user coach=new user();
                    user nutri=new user();

                    coach=service.getById(coachId);
                    coachAndNutritionist.add(coach);
                    nutri=service.getById(nutritionistId);
                    coachAndNutritionist.add(nutri);


                }
            }
        } catch (SQLException ex) {
            System.out.println("Erreur lors de la récupération du coach et du nutritionniste : " + ex.getMessage());
        }
        return coachAndNutritionist;
    }



}

