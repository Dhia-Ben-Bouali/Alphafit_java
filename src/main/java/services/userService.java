package services;
import GUI.UserDeactivationScheduler;
import com.google.gson.Gson;
import entite.user;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import util.DataSource;

import javax.mail.MessagingException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static services.password.hashPassword;

public class userService implements IService<user> {
    static Connection cnx;
    private List<user> users = new ArrayList<>();
    private user loggedInUser;
    public userService() {
        cnx = DataSource.getInstance().getConnection();
        System.out.println(cnx);
    }

    public void migrationUser() {
        try (Statement stm = cnx.createStatement()) {
            String query = "CREATE TABLE IF NOT EXISTS user (id INT PRIMARY KEY AUTO_INCREMENT, email VARCHAR(255), roles VARCHAR(255), password VARCHAR(255), nom VARCHAR(255), prenom VARCHAR(255), adresse VARCHAR(255), salaire FLOAT)";
            stm.executeUpdate(query);
            System.out.println("User table created or already exists.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void Add(user user) {

        if (isValidUser(user)) {
            try {
                if (!email_existant(user.getEmail())) {
                    try (PreparedStatement stm = cnx.prepareStatement("INSERT INTO user (email, roles, password, nom, prenom, adresse, salaire, image, activated) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS)) {

                        String rolesJson = new Gson().toJson(user.getRoles());


                        stm.setString(1, user.getEmail());
                        stm.setString(2, rolesJson);
                        stm.setString(3, hashPassword(user.getPassword()));
                        stm.setString(4, user.getNom());
                        stm.setString(5, user.getPrenom());
                        stm.setString(6, user.getAdresse());
                        stm.setFloat(7, user.getSalaire());
                        stm.setString(8, user.getImage());

                        stm.setBoolean(9, true);


                        int affectedRows = stm.executeUpdate();

                        if (affectedRows > 0) {
                            ResultSet rs = stm.getGeneratedKeys();
                            if (rs.next()) {
                                int generatedId = rs.getInt(1);
                                user.setId(generatedId);
                                System.out.println("User added successfully with ID: " + generatedId);


                            String recipientEmail = user.getEmail();
                            String subject = "Welcome to Our AlphaFit Application";
                            String body = "Dear " + user.getPrenom() + ",\n\nWelcome to our application! Thank you for joining us.";

                            EmailSender.sendEmail(recipientEmail, subject, body);

                            }
                        } else {
                            System.out.println("Error adding user: No rows affected.");
                        }
                    } catch (MessagingException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    System.out.println("User with email " + user.getEmail() + " already exists.");
                }
            } catch (SQLException ex) {
                System.out.println("Error adding user: " + ex.getMessage());
            }
        } else {
            System.out.println("Invalid user information.");
        }
    }





    public List<user> getAll() {
        List<user> users = new ArrayList<>();
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM user");
            while (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String rolesString = rs.getString("roles");
                String[] rolesArray = rolesString.split(",");
                List<String> roles = new ArrayList<>();
                for (String role : rolesArray) {
                    roles.add(role);
                }
                String password = rs.getString("password");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String adresse = rs.getString("adresse");
                float salaire = rs.getFloat("salaire");
                boolean activated = rs.getBoolean("activated");

                user user = new user(id, email, roles, password, nom, prenom, adresse, salaire, activated);
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println("Error fetching users: " + ex.getMessage());
        }
        return users;
    }


    public List<user> getUsersByRole(String role) {
        List<user> users = new ArrayList<>();
        try {
            PreparedStatement stmt = cnx.prepareStatement("SELECT * FROM user WHERE roles LIKE ?");
            stmt.setString(1, "%" + role + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String email = rs.getString("email");
                String rolesString = rs.getString("roles");
                String[] rolesArray = rolesString.split(",");
                List<String> roles = new ArrayList<>();
                for (String r : rolesArray) {
                    roles.add(r);
                }
                String password = rs.getString("password");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String adresse = rs.getString("adresse");
                float salaire = rs.getFloat("salaire");
                boolean activated = rs.getBoolean("activated");

                user user = new user(id, email, roles, password, nom, prenom, adresse, salaire, activated);
                users.add(user);
            }
        } catch (SQLException ex) {
            System.out.println("Error fetching users by role: " + ex.getMessage());
        }
        return users;
    }

    public boolean email_existant(String mail) {
        boolean result = false;
        for (user p : getAll()) {
            if (p.getEmail().equals(mail)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean deleteById(int id) {
        try {
            String query = "UPDATE user SET activated = ? WHERE id = ?";
            PreparedStatement pstmt = cnx.prepareStatement(query);
            pstmt.setBoolean(1, false);
            pstmt.setInt(2, id);
            int updatedRows = pstmt.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("User with ID " + id + " deactivated successfully.");
                UserDeactivationScheduler scheduler = new UserDeactivationScheduler();
                scheduler.scheduleNotification(id);
                return true;
            } else {
                System.out.println("No user found with ID " + id + ".");
                return false; // Return false here as no user was found
            }
        } catch (SQLException ex) {
            System.out.println("Error deactivating user: " + ex.getMessage());
            return false; // Return false if an exception occurred
        }
    }

    public List<String> getEmailsOfUsersToBeActivatedWithin24Hours() {
        List<String> userEmails = new ArrayList<>();
        try {
            // Construction de la requête SQL pour récupérer les utilisateurs à activer dans les prochaines 24 heures
            String sql = "SELECT email FROM user WHERE activation_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 1 DAY)";
            PreparedStatement statement = cnx.prepareStatement(sql);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String email = resultSet.getString("email");
                userEmails.add(email);
            }
        } catch (SQLException ex) {
            System.out.println("Error fetching users' emails to be activated: " + ex.getMessage());
        }
        return userEmails;
    }

    @Override
    public user getById(int id) {
        user user = null;
        try {
            Statement stm = cnx.createStatement();
            ResultSet rs = stm.executeQuery("SELECT * FROM user WHERE id = " + id);
            if (rs.next()) {
                String email = rs.getString("email");
                String rolesString = rs.getString("roles");
                String[] rolesArray = rolesString.split(",");
                List<String> roles = new ArrayList<>();
                for (String role : rolesArray) {
                    roles.add(role);
                }
                String password = rs.getString("password");
                String nom = rs.getString("nom");
                String prenom = rs.getString("prenom");
                String adresse = rs.getString("adresse");
                float salaire = rs.getFloat("salaire");
                user = new user(id, email, roles, password, nom, prenom, adresse, salaire);
            } else {
                System.out.println("No user found with ID " + id + ".");
            }
        } catch (SQLException ex) {
            System.out.println("Error fetching user information: " + ex.getMessage());
        }
        System.out.println(user);
        return user;

    }



    @Override
    public boolean update(user updatedUser) {
        try {
            PreparedStatement stm = cnx.prepareStatement("UPDATE user SET email=?, nom=?, prenom=?, adresse=?, salaire=?, password=? WHERE id=?");
            stm.setString(1, updatedUser.getEmail());
            stm.setString(2, updatedUser.getNom());
            stm.setString(3, updatedUser.getPrenom());
            stm.setString(4, updatedUser.getAdresse());
            stm.setFloat(5, updatedUser.getSalaire());

            stm.setString(6, hashPassword(updatedUser.getPassword()));
            stm.setInt(7, updatedUser.getId());

            int updatedRows = stm.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("User with ID " + updatedUser.getId() + " updated successfully.");
                return true;
            } else {
                System.out.println("No user found with ID " + updatedUser.getId() + ".");
                return false;
            }
        } catch (SQLException ex) {
            System.out.println("Error updating user: " + ex.getMessage());
        }
        return false;
    }




    private boolean isValidUser(user user) {
        boolean isValid = true;


        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(user.getEmail());
        if (!matcher.matches()) {
            System.out.println("Invalid email format.");
            isValid = false;
        }


        if (user.getNom().isEmpty()) {
            System.out.println("Nom cannot be empty.");
            isValid = false;
        }
        if (user.getPrenom().isEmpty()) {
            System.out.println("Prenom cannot be empty.");
            isValid = false;
        }
        if (user.getAdresse().isEmpty()) {
            System.out.println("Adresse cannot be empty.");
            isValid = false;
        }


        if (user.getSalaire() < 0) {
            System.out.println("Salaire cannot be negative.");
            isValid = false;
        }

        return isValid;
    }

    public user login(String email, String password) {
        user loggedInUser = null;
        try {
            PreparedStatement stm = cnx.prepareStatement("SELECT * FROM user WHERE email = ?");
            stm.setString(1, email);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                boolean activated = rs.getBoolean("activated");
                if (activated) {
                    String hashedPassword = rs.getString("password");
                    if (BCrypt.checkpw(password, hashedPassword)) {

                        int id = rs.getInt("id");
                        String userEmail = rs.getString("email");
                        String rolesString = rs.getString("roles");
                        String[] rolesArray = rolesString.split(",");
                        List<String> roles = new ArrayList<>();
                        for (String role : rolesArray) {
                            roles.add(role);
                        }
                        String nom = rs.getString("nom");
                        String prenom = rs.getString("prenom");
                        String adresse = rs.getString("adresse");
                        float salaire = rs.getFloat("salaire");


                        String imagePath = rs.getString("image");


                        loggedInUser = new user(id, userEmail, roles, hashedPassword, nom, prenom, adresse, salaire);
                        loggedInUser.setImage(imagePath);

                        LoggedInUserManager.getInstance().setLoggedInUser(loggedInUser);
                    } else {

                        System.out.println("Incorrect password.");
                    }
                } else {

                    System.out.println("User account is inactive.");
                    return null;
                }
            } else {

                System.out.println("User not found with email: " + email);
            }
        } catch (SQLException ex) {
            System.out.println("Error logging in: " + ex.getMessage());
        }
        return loggedInUser;
    }

    public boolean isUserActive(String userEmail) {
        try {
            PreparedStatement stm = cnx.prepareStatement("SELECT activated FROM user WHERE email = ?");
            stm.setString(1, userEmail);
            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                return rs.getBoolean("activated");
            }
        } catch (SQLException ex) {
            System.out.println("Error checking user activation status: " + ex.getMessage());
        }
        return false;
    }

    public user getLoggedInUser() {
        return loggedInUser;
    }
    public void updatePasswordInDatabase(TextField emailTextField, String newPassword) {
        String email = emailTextField.getText();
        try {

            String hashedPassword = hashPassword(newPassword);


            PreparedStatement stm = cnx.prepareStatement("UPDATE user SET password=? WHERE email=?");
            stm.setString(1, hashedPassword);
            stm.setString(2, email);
            int updatedRows = stm.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("Password updated successfully for email: " + email);

                String recipientEmail = email;
                String subject = "New Password modified ";
                String body = "Dear user ,\n\nWelcome to our application! This is your new password : "+newPassword;

                EmailSender.sendEmail(recipientEmail, subject, body);


                showAlert(Alert.AlertType.INFORMATION, "Success", "Password Updated", "Password updated successfully.");
            } else {
                System.out.println("Failed to update password for email: " + email);

                showAlert(Alert.AlertType.ERROR, "Error", "Password Update Failed", "Failed to update password.");
            }
        } catch (SQLException ex) {
            System.out.println("Error updating password: " + ex.getMessage());

            showAlert(Alert.AlertType.ERROR, "Error", "Database Error", "Error updating password: " + ex.getMessage());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public boolean logout() {
     LoggedInUserManager.getInstance().setLoggedInUser(null);
        return true;
    }
    public int findIdByEmail(String email) {
        String query = "SELECT id FROM user WHERE email = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setString(1, email);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                throw new RuntimeException("User with email " + email + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteByEmail(String email) {
        String query = "DELETE FROM user WHERE email = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setString(1, email);
            int rowsDeleted = statement.executeUpdate();
            if (rowsDeleted == 0) {
                throw new RuntimeException("User with email " + email + " not found");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public boolean updateProfileImage(int userId, String newImagePath) {
        try {
            PreparedStatement stm = cnx.prepareStatement("UPDATE user SET image = ? WHERE id = ?");
            stm.setString(1, newImagePath);
            stm.setInt(2, userId);
            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException ex) {
            System.out.println("Error updating profile image: " + ex.getMessage());
            return false;
        }
    }
    public user findByEmail(String email) {
        String query = "SELECT * FROM user WHERE email = ?";
        try (PreparedStatement statement = cnx.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    user user = new user();

                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setAdresse(resultSet.getString("adresse"));
                    user.setEmail(resultSet.getString("email"));
                    user.setPassword(resultSet.getString("password"));
                    user.setSalaire(resultSet.getFloat("salaire"));
                    user.setActivated(resultSet.getBoolean("activated"));
                    user.setRoles(Collections.singletonList(resultSet.getString("roles")));

                    user.setImage(resultSet.getString("image"));
                    return user;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public boolean activateUser(int id) {
        try {
            String query = "UPDATE user SET activated = ? WHERE id = ?";
            PreparedStatement pstmt = cnx.prepareStatement(query);
            pstmt.setBoolean(1, true);
            pstmt.setInt(2, id);
            int updatedRows = pstmt.executeUpdate();
            if (updatedRows > 0) {
                System.out.println("User with ID " + id + " activated successfully.");
                return true;
            } else {
                System.out.println("No user found with ID " + id + ".");
            }
        } catch (SQLException ex) {
            System.out.println("Error activating user: " + ex.getMessage());
        }
        return false;
    }

    public String getUserEmailById(int userId) {
        String userEmail = null;
        try {
            String query = "SELECT email FROM user WHERE id = ?";
            PreparedStatement pstmt = cnx.prepareStatement(query);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                userEmail = rs.getString("email");
            }
        } catch (SQLException ex) {
            System.out.println("Error retrieving user email: " + ex.getMessage());
        }
        return userEmail;
    }


    public Image getUserPhoto(String imagePath) {
        // Check if the imagePath is not null or empty
        if (imagePath != null && !imagePath.isEmpty()) {
            File file = new File(imagePath);
            if (file.exists()) {
                return new Image(file.toURI().toString());
            } else {
                // Return a default image or null if the file doesn't exist
                return getDefaultImage();
            }
        } else {
            // Return a default image or null if imagePath is null or empty
            return getDefaultImage();
        }
    }

    private Image getDefaultImage() {
        // Return the default image
        return new Image("@imagess/user.png");
    }


    /********************************************dh***************************************************/
    public static List<user> getCoaches() throws SQLException {
        List<user> coaches = new ArrayList<>();

        String query = "SELECT * FROM user WHERE roles LIKE ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = cnx.prepareStatement(query);
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

        }

        return coaches;
    }


    public static List<user> getNutrisionists() throws SQLException {
        List<user> nutrisionists = new ArrayList<>();

        String query = "SELECT * FROM user WHERE roles LIKE ?";
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            st = cnx.prepareStatement(query);
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

        }

        return nutrisionists;
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
                con = DataSource.getInstance().getConnection();                st = con.prepareStatement(query);
                st.setString(1, clientName);
                rs = st.executeQuery();

                if (rs.next()) {
                    clientId = rs.getInt("id");
                }
            } finally {



            }

            return clientId;
        }

    }


    /********************************************dh***************************************************/
}

