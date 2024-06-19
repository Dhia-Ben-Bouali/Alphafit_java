package services;

import entite.lignecommande;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LigneCommandeService implements IServicea<lignecommande> {

    private Connection cnx;

    public LigneCommandeService() {
        cnx = DataSource.getInstance().getConnection();
    }

    @Override
    public void add(lignecommande ligneCommande) {
        String query = "INSERT INTO lignecommande ( id_commande_id, quantite, article_id) VALUES (?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setLong(1, ligneCommande.getCommande().getId());
            ps.setInt(2, ligneCommande.getQuantite());
            ps.setLong(3, ligneCommande.getArticleId());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM lignecommande WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void update(lignecommande ligneCommande, int id) {
        String query = "UPDATE lignecommande SET id_commande_id = ?, quantite = ?, article_id = ? WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setLong(1, ligneCommande.getCommande().getId());
            ps.setInt(2, ligneCommande.getQuantite());
            ps.setLong(3, ligneCommande.getArticleId());
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<lignecommande> getAll() {
        List<lignecommande> lignes = new ArrayList<>();
        String query = "SELECT * FROM lignecommande";
        try (Statement st = cnx.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                lignecommande ligne = new lignecommande();
                ligne.setId(rs.getLong("id"));
                // Suppose you have a method to get a Commande by its ID
                ligne.setCommande(new CommandeService().getById(rs.getInt("id_commande_id")));
                ligne.setQuantite(rs.getInt("quantite"));
                ligne.setArticleId(rs.getLong("article_id"));
                lignes.add(ligne);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lignes;
    }
    public List<lignecommande> getByCommandId(long commandId) {
        List<lignecommande> lignes = new ArrayList<>();
        String query = "SELECT * FROM lignecommande WHERE id_commande_id = ?"; // Using prepared statements for safety

        try (PreparedStatement pst = cnx.prepareStatement(query)) {
            pst.setLong(1, commandId); // Set the commandId in the query

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    lignecommande ligne = new lignecommande();
                    ligne.setId(rs.getLong("id"));
                    // Assume there's a method to get a Commande by its ID
                    ligne.setCommande(new CommandeService().getById(rs.getInt("id_commande_id")));
                    ligne.setQuantite(rs.getInt("quantite"));
                    ligne.setArticleId(rs.getLong("article_id"));
                    lignes.add(ligne);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Proper exception handling recommended (e.g., logging)
        }
        return lignes;
    }

    @Override
    public lignecommande getById(int id) {
        String query = "SELECT * FROM lignecommande WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(query)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                lignecommande ligne = new lignecommande();
                ligne.setId(rs.getLong("id"));
                ligne.setCommande(new CommandeService().getById(rs.getInt("id_commande_id")));
                ligne.setQuantite(rs.getInt("quantite"));
                ligne.setArticleId(rs.getLong("article_id"));
                return ligne;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
