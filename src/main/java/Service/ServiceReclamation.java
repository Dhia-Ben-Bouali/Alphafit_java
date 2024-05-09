package Service;

import Util.BDConnexion;
import com.alphafit.alphafit.Reclamation;
import com.alphafit.alphafit.user;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ServiceReclamation {
    public static void insertReclamation(String userId, String etat, String contenu, LocalDate date) throws SQLException {
        String insert = "INSERT INTO Reclamation (recuser_id, contenu, date,  etat ) VALUES (?, ?, ?,'Pending')";
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = BDConnexion.getcon();
            st = con.prepareStatement(insert);
            st.setString(1, userId);
            st.setString(2, contenu);
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            st.setDate(3, sqlDate);
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

    public static void deleteReclamation(int id) throws SQLException {
        String delete = "DELETE FROM Reclamation WHERE id = ?";
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

    public static void updateReclamation(int id, int userId, String etat, String contenu, LocalDate date) throws SQLException {
        String update = "UPDATE Reclamation SET recuser_id = ?, etat = ?, contenu = ?, date = ? WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        try {
            con = BDConnexion.getcon();
            st = con.prepareStatement(update);
            st.setInt(1, userId);
            st.setString(2, etat);
            st.setString(3, contenu);
            java.sql.Date sqlDate = java.sql.Date.valueOf(date);
            st.setDate(4, sqlDate);
            st.setInt(5, id);
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

    public static ObservableList<Reclamation> getReclamationList() {
        ObservableList<Reclamation> reclamationList = FXCollections.observableArrayList();
        String query = "SELECT * FROM Reclamation";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = BDConnexion.getcon();
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

        return reclamationList;
    }

    public static user getUserById(int userId) {
        String query = "SELECT * FROM User WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        user user = null;
        try {
            con = BDConnexion.getcon();
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
        return user;
    }


    public static List<Reclamation> getReclamationsByUser(int userId) {
        List<Reclamation> reclamations = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = BDConnexion.getcon();
            String sql = "SELECT recuser_id, contenu, date, etat FROM reclamation WHERE recuser_id = ?";
            stmt = conn.prepareStatement(sql);
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
            // Fermer les ressources
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return reclamations;
    }


}

