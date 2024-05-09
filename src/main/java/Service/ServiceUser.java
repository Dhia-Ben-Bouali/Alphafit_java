package Service;

import Util.BDConnexion;
import com.alphafit.alphafit.user;

import java.sql.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ServiceUser {
    public static List<user> getCoaches() throws SQLException {
        List<user> coaches = new ArrayList<>();

        String query = "SELECT * FROM user WHERE roles LIKE ?";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = BDConnexion.getcon();
            st = con.prepareStatement(query);
            st.setString(1, "[\"ROLE_COACH\"]"); // Use LIKE with wildcard for role search
            rs = st.executeQuery();

            while (rs.next()) {
                user coach = new user();
                coach.id = rs.getInt("id");
                coach.email = rs.getString("email");
                coach.roles = new ArrayList<>(); // Initialize roles list
                String roleString = rs.getString("roles");
                for (String role : roleString.split(",")) { // Split roles string and add to list
                    coach.roles.add(role.trim());
                }
                coach.password = rs.getString("password");
                coach.nom = rs.getString("nom");
                coach.prenom = rs.getString("prenom");
                coach.adresse = rs.getString("adresse");
                coach.salaire = rs.getFloat("salaire");
                coaches.add(coach);
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

        return coaches;
    }


    public static List<user> getNutrisionists() throws SQLException {
        List<user> nutrisionists = new ArrayList<>();

        String query = "SELECT * FROM user WHERE roles LIKE ?";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            con = BDConnexion.getcon();
            st = con.prepareStatement(query);
            st.setString(1, "[\"ROLE_NUTRITIONIST\"]");
            rs = st.executeQuery();

            while (rs.next()) {
                user nutrisionist = new user();
                nutrisionist.id = rs.getInt("id");
                nutrisionist.email = rs.getString("email");
                nutrisionist.roles = new ArrayList<>();
                String roleString = rs.getString("roles");
                for (String role : roleString.split(",")) {
                    nutrisionist.roles.add(role.trim());
                }
                nutrisionist.password = rs.getString("password");
                nutrisionist.nom = rs.getString("nom");
                nutrisionist.prenom = rs.getString("prenom");
                nutrisionist.adresse = rs.getString("adresse");
                nutrisionist.salaire = rs.getFloat("salaire");
                nutrisionists.add(nutrisionist);
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

        return nutrisionists;
    }

    public static String getUserNameById(int userId) throws SQLException {
        String userName = null; // Default value if user not found

        String query = "SELECT nom FROM user WHERE id = ?";
        Connection con = null;
        PreparedStatement st = null;
        ResultSet rs = null;

        try {
            con = BDConnexion.getcon();
            st = con.prepareStatement(query);
            st.setInt(1, userId);
            rs = st.executeQuery();

            if (rs.next()) {
                userName = rs.getString("nom");
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

        return userName;
    }



    public class ClientIdRetriever {

        // Method to retrieve client ID by name
        public static int getClientIdByName(String clientName) throws SQLException {
            int clientId = -1; // Default value if client not found

            String query = "SELECT id FROM user WHERE nom = ?";
            Connection con = null;
            PreparedStatement st = null;
            ResultSet rs = null;

            try {
                con = BDConnexion.getcon();
                st = con.prepareStatement(query);
                st.setString(1, clientName);
                rs = st.executeQuery();

                if (rs.next()) {
                    clientId = rs.getInt("id");
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

            return clientId;
        }

    }


}
