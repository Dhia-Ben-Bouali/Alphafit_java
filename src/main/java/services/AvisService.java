package services;

import entite.Avis;
import entite.Avis;
import entite.user;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AvisService implements IServiceG<Avis> {

    private Connection cnx;

    public AvisService() {
        cnx = DataSource.getInstance().getConnection();
    }

    @Override
    public void Add(Avis avis) {
        try {
            String query = "INSERT INTO avis (star1, star2, star3, star4, star5, commentaire, user_id) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setBoolean(1, avis.isStar1());
            stm.setBoolean(2, avis.isStar2());
            stm.setBoolean(3, avis.isStar3());
            stm.setBoolean(4, avis.isStar4());
            stm.setBoolean(5, avis.isStar5());
            stm.setString(6, avis.getCommentaire());
            stm.setInt(7, avis.getUser().getId());

            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Avis ajouté avec succès.");
            } else {
                System.out.println("Erreur lors de l'ajout de l'avis.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'avis: " + e.getMessage());
        }

    }

    @Override
    public List<Avis> getAll() throws SQLException {
        List<Avis> avisList = new ArrayList<>();
        String query = "SELECT * FROM avis";
        try (Statement stm = cnx.createStatement();
             ResultSet rs = stm.executeQuery(query)) {
            while (rs.next()) {
                Avis avis = new Avis();
                avis.setId(rs.getInt("id"));
                avis.setStar1(rs.getBoolean("star1"));
                avis.setStar2(rs.getBoolean("star2"));
                avis.setStar3(rs.getBoolean("star3"));
                avis.setStar4(rs.getBoolean("star4"));
                avis.setStar5(rs.getBoolean("star5"));
                avis.setCommentaire(rs.getString("commentaire"));

                // Assume que vous avez une méthode pour récupérer un utilisateur par son ID
                user user = getUserById(rs.getInt("user_id"));
                avis.setUser(user);

                avisList.add(avis);
            }
        }
        return avisList;
    }

    @Override
    public boolean deleteById(int id) {
        try {
            String query = "DELETE FROM avis WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, id);

            int rowsAffected = stm.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'avis: " + e.getMessage());
        }
    }

    @Override
    public Avis getById(int userId) {
        Avis avis = null;
        try {
            String query = "SELECT * FROM avis WHERE user_id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, userId);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                avis = new Avis();
                avis.setId(rs.getInt("id"));
                avis.setStar1(rs.getBoolean("star1"));
                avis.setStar2(rs.getBoolean("star2"));
                avis.setStar3(rs.getBoolean("star3"));
                avis.setStar4(rs.getBoolean("star4"));
                avis.setStar5(rs.getBoolean("star5"));
                avis.setCommentaire(rs.getString("commentaire"));

                // Ici, vous n'avez pas besoin de récupérer l'utilisateur par son ID, car vous avez déjà l'ID de l'utilisateur en paramètre de la méthode
                // avis.setUser(user);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'avis: " + e.getMessage());
        }
        return avis;
    }


    @Override
    public void update(Avis avis) {
        try {
            String query = "UPDATE avis SET star1 = ?, star2 = ?, star3 = ?, star4 = ?, star5 = ?, commentaire = ? WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setBoolean(1, avis.isStar1());
            stm.setBoolean(2, avis.isStar2());
            stm.setBoolean(3, avis.isStar3());
            stm.setBoolean(4, avis.isStar4());
            stm.setBoolean(5, avis.isStar5());
            stm.setString(6, avis.getCommentaire());
            stm.setInt(7, avis.getId());

            int rowsAffected = stm.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Avis mis à jour avec succès.");
            } else {
                System.out.println("Aucun avis trouvé avec cet ID.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'avis: " + e.getMessage());
        }
    }

    // Méthode pour récupérer un utilisateur par son ID
    private user getUserById(int id) {
        // Implémentez la logique pour récupérer un utilisateur par son ID
        return null;
    }
    public int countStarsForAvis(int avisId) {
        int countStars = 0;
        try {
            String query = "SELECT star1, star2, star3, star4, star5 FROM avis WHERE id = ?";
            PreparedStatement stm = cnx.prepareStatement(query);
            stm.setInt(1, avisId);

            ResultSet rs = stm.executeQuery();
            if (rs.next()) {
                // Comptez le nombre de stars (étoiles) actives (true)
                if (rs.getBoolean("star1")) countStars++;
                if (rs.getBoolean("star2")) countStars++;
                if (rs.getBoolean("star3")) countStars++;
                if (rs.getBoolean("star4")) countStars++;
                if (rs.getBoolean("star5")) countStars++;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des étoiles pour l'avis: " + e.getMessage());
        }
        return countStars;
    }

}
