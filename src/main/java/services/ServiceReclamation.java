package services;

import entite.Reclamation;
import entite.user;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import util.DataSource;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation {
    public static void insertReclamation(int userId, String etat, String contenu, LocalDate date) throws SQLException {

        String insert = "INSERT INTO Reclamation (recuser_id, contenu, date,  etat ) VALUES (?, ?, ?,'Pending')";
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = DataSource.getInstance().getConnection();
            st = con.prepareStatement(insert);
            st.setInt(1, userId);
            st.setString(2, contenu);
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            st.setDate(3, sqlDate);
            st.executeUpdate();
        } finally {

        }
    }

    public static void deleteReclamation(int id) throws SQLException {
        String delete = "DELETE FROM Reclamation WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = DataSource.getInstance().getConnection();
            st = con.prepareStatement(delete);
            st.setInt(1, id);
            st.executeUpdate();
        } finally {
        }
    }

    public static void updateReclamation(int id, int userId, String etat, String contenu, LocalDate date) throws SQLException {
        String update = "UPDATE Reclamation SET recuser_id = ?, etat = ?, contenu = ?, date = ? WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = DataSource.getInstance().getConnection();
            st = con.prepareStatement(update);
            st.setInt(1, userId);
            st.setString(2, etat);
            st.setString(3, contenu);
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            st.setDate(4, sqlDate);
            st.setInt(5, id);
            st.executeUpdate();
        } finally {
        }
    }

    public static ObservableList<Reclamation> getReclamationList() {
        ObservableList<Reclamation> reclamationList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Reclamation";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = DataSource.getInstance().getConnection();
            st = con.prepareStatement(query);
            rs = st.executeQuery();
            while (rs.next()) {
                Reclamation reclamation = new Reclamation();
                reclamation.setId(rs.getInt("id"));

                user user = getUserById(rs.getInt("recuser_id"));
                // String name = user.nom;
                reclamation.setUser(user);

                reclamation.setDate(rs.getDate("date"));
                reclamation.setContenu(rs.getString("contenu"));
                reclamation.setEtat(rs.getString("etat"));

                reclamationList.add(reclamation);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
        }

        return reclamationList;
    }

    public static user getUserById(int userId) {
        String query = "SELECT * FROM User WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        user user = null;
        try {
            con = DataSource.getInstance().getConnection();
            st = con.prepareStatement(query);
            st.setInt(1, userId);
            rs = st.executeQuery();
            if (rs.next()) {
                // Assuming you have a constructor in your User class to create a User object
                user = new user(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("email")
                );
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
        }
        return user;
    }


    public static List<Reclamation> getReclamationsByUser(int userId) {
        List<Reclamation> reclamations = new ArrayList<>();
        Connection con = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            con = DataSource.getInstance().getConnection();
            String sql = "SELECT recuser_id, contenu, date, etat FROM reclamation WHERE recuser_id = ?";
            stmt = con.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int recuserId = rs.getInt("recuser_id");
                String contenu = rs.getString("contenu");
                String date = rs.getString("date");
                String etat = rs.getString("etat");


                Reclamation reclamation = new Reclamation(recuserId, contenu, Date.valueOf(date), etat);
                reclamations.add(reclamation);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
        }
        return reclamations;
    }

    public static int getIdUtilisateurParNom(String nomUtilisateur) throws SQLException {
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        int idUtilisateur = -1; // Valeur par défaut si l'utilisateur n'est pas trouvé

        try {
            con = DataSource.getInstance().getConnection();
            String query = "SELECT id FROM user WHERE nom = ?";
            st = con.prepareStatement(query);
            st.setString(1, nomUtilisateur);
            rs = st.executeQuery();

            if (rs.next()) {
                idUtilisateur = rs.getInt("id");
            }
        } finally {

        }

        return idUtilisateur;
    }



}
