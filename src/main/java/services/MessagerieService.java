package services;

import entite.Messagerie;

import entite.user;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessagerieService implements IServiceG<Messagerie> {
    Connection cnx;
    public MessagerieService()
    {
        cnx= DataSource.getInstance().getConnection();

    }

    @Override
    public void Add(Messagerie message) {
        if (messageVerified(message)) {
            user loggedInUser = LoggedInUserManager.getInstance().getLoggedInUser();

            try {
                String query = "INSERT INTO messagerie (sender_id, recipient_id, titre, contenu, created_at, is_read, is_favorite) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stm = cnx.prepareStatement(query);
                stm.setInt(1, message.getSender().getId());
                stm.setInt(2, message.getRecipient().getId());
                stm.setString(3, message.getTitre());
                stm.setString(4, message.getContenu());
                stm.setTimestamp(5, message.getCreated_at());
                stm.setBoolean(6, message.isIs_read());
                stm.setBoolean(7, message.isIs_favorite());

                int rowsAffected = stm.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Message successfully added.");
                    // Mettre à jour les listes sentMessages et receivedMessages de l'utilisateur

                } else {
                    System.out.println("Error adding message.");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error adding message: " + e.getMessage());
            }
        } else {
            System.out.println("Invalid message information.");
        }
    }


    @Override
    public List<Messagerie> getAll() {
        List<Messagerie> messages = new ArrayList<>();
        try {
            String query = "SELECT * FROM messagerie";
            PreparedStatement stm = cnx.prepareStatement(query);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                // Create Messagerie object from ResultSet data
                // and add it to the list
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving messages: " + e.getMessage());
        }
        return messages;
    }

    public List<Messagerie> getAllBySender(int senderId) {
        List<Messagerie> messages = new ArrayList<>();

        try {
            userService userservice = new userService();

            String query = "SELECT * FROM messagerie WHERE sender_id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, senderId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                user sender = userservice.getById(rs.getInt("sender_id"));
                user recipient = userservice.getById(rs.getInt("recipient_id"));
                String titre = rs.getString("titre");
                String contenu = rs.getString("contenu");
                Timestamp created_at = rs.getTimestamp("created_at");
                boolean is_read = rs.getBoolean("is_read");
                boolean is_favorite = rs.getBoolean("is_favorite");

                Messagerie message = new Messagerie(sender, recipient, titre, contenu, created_at, is_read, is_favorite);
                message.setId(rs.getLong("id"));

                messages.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving messages by recipient: " + e.getMessage());
        }
        System.out.println("**************************************");
        System.out.println(messages);
        System.out.println("*******************************************");
        return messages;

    }
        public List<Messagerie> getAllByRecipient(int recipientId) {
        List<Messagerie> messages = new ArrayList<>();

        try {
            userService userservice =new userService();

            String query = "SELECT * FROM messagerie WHERE recipient_id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, recipientId);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                user sender = userservice.getById(rs.getInt("sender_id"));
                user recipient = userservice.getById(rs.getInt("recipient_id"));
                String titre = rs.getString("titre");
                String contenu = rs.getString("contenu");
                Timestamp created_at = rs.getTimestamp("created_at");
                boolean is_read = rs.getBoolean("is_read");
                boolean is_favorite = rs.getBoolean("is_favorite");

                Messagerie message = new Messagerie(sender, recipient, titre, contenu,created_at, is_read, is_favorite);
                message.setId(rs.getLong("id"));

                messages.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving messages by recipient: " + e.getMessage());
        }
        System.out.println("**************************************");
        System.out.println(messages);
        System.out.println("*******************************************");
        return messages;


    }


    @Override
    public boolean deleteById(int id) {
        try {
            String query = "DELETE FROM messagerie WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, id);

            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Message with ID " + id + " successfully deleted.");
                return true;
            } else {
                System.out.println("No message found with ID " + id + ".");
                return false;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting message: " + e.getMessage());
        }

    }

    @Override
    public Messagerie getById(int id) {
        Messagerie message = null;
            userService userservice =new userService();
        try {
            String query = "SELECT * FROM messagerie WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                user sender = userservice.getById(rs.getInt("sender_id"));
                user recipient = userservice.getById(rs.getInt("recipient_id"));
                String titre = rs.getString("titre");
                String contenu = rs.getString("contenu");
                Timestamp created_at = rs.getTimestamp("created_at");
                boolean is_read = rs.getBoolean("is_read");
                boolean is_favorite = rs.getBoolean("is_favorite");

                message = new Messagerie(sender, recipient, titre, contenu, is_read, is_favorite);
                message.setId(rs.getLong("id"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving message: " + e.getMessage());
        }
        return message;
    }



   @Override
    public void update(Messagerie message) {

            try {
                String query = "UPDATE messagerie SET titre = ?, contenu = ?, is_favorite = ? WHERE id = ?";
                PreparedStatement stm = cnx.prepareStatement(query);
                stm.setString(1, message.getTitre());
                stm.setString(2, message.getContenu());
                stm.setBoolean(3, message.isIs_favorite());
                stm.setLong(4, message.getId());

                int rowsAffected = stm.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Message with ID " + message.getId() + " successfully updated.");
                } else {
                    System.out.println("No message found with ID " + message.getId() + ".");
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error updating message: " + e.getMessage());
            }
        }



    public boolean messageVerified(Messagerie message) {
        boolean isValid = true;

        if (message.getSender() == null) {
            System.out.println("The 'sender' field is required.");
            isValid = false;
        }
        if (message.getRecipient() == null) {
            System.out.println("The 'recipient' field is required.");
            isValid = false;
        }
        if (message.getTitre() == null || message.getTitre().isEmpty()) {
            System.out.println("The 'title' field is required.");
            isValid = false;
        }
        if (message.getContenu() == null || message.getContenu().isEmpty()) {
            System.out.println("The 'content' field is required.");
            isValid = false;
        }
        if (message.getCreated_at() == null) {
            System.out.println("The 'created_at' field is required.");
            isValid = false;
        }
        if (message.getTitre().trim().isEmpty()) {
            System.out.println("The 'title' field cannot be empty.");
            isValid = false;
        }
        if (message.getContenu().trim().isEmpty()) {
            System.out.println("The 'content' field cannot be empty.");
            isValid = false;
        }

        return isValid;
    }
    public user getUserById(int id) {
        user user = null;
        try {
            String query = "SELECT * FROM user WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, id);
            ResultSet rs = stm.executeQuery();

            if (rs.next()) {
                user = new user();
                user.setId(rs.getInt("id"));
                user.setEmail(rs.getString("email"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving user: " + e.getMessage());
        }
        return user;
    }


    public List<Messagerie> getAllFavoritesByUserId(int userId) {
        List<Messagerie> favoriteMessages = new ArrayList<>();

        try {
            userService userService = new userService();

            String query = "SELECT * FROM messagerie WHERE sender_id = ? AND is_favorite = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, userId);
            stm.setBoolean(2, true);
            ResultSet rs = stm.executeQuery();

            while (rs.next()) {
                user sender = userService.getById(rs.getInt("sender_id"));
                user recipient = userService.getById(rs.getInt("recipient_id"));
                String titre = rs.getString("titre");
                String contenu = rs.getString("contenu");
                Timestamp created_at = rs.getTimestamp("created_at");
                boolean is_read = rs.getBoolean("is_read");
                boolean is_favorite = rs.getBoolean("is_favorite");

                Messagerie message = new Messagerie(sender, recipient, titre, contenu, created_at, is_read, is_favorite);
                message.setId(rs.getLong("id"));

                favoriteMessages.add(message);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving favorite messages: " + e.getMessage());
        }

        return favoriteMessages;
    }

}
