package services;

import entite.Categorie;
import util.DataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CategorieService implements IServicea<Categorie> {
    private Connection cnx;
    private Statement ste;
   public  CategorieService(){
        cnx= DataSource.getInstance().getConnection();

    }
    @Override
    public void add(Categorie c) {
        // Vérifier si la catégorie est valide
        if (!isValidCategorie(c)) {
            throw new IllegalArgumentException("La catégorie est invalide.");
        }

        String requete = "INSERT INTO categorie(libelle) VALUES('" + c.getLibelle() + "')";
        try {
            ste = cnx.createStatement();
            ste.executeUpdate(requete, Statement.RETURN_GENERATED_KEYS);
            ResultSet rs = ste.getGeneratedKeys();
            if (rs.next()) {
                c.setId(rs.getInt(1)); // Mettre à jour l'ID de la catégorie avec l'ID généré par la base de données
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de la catégorie : " + e.getMessage());
        } finally {
            // Fermer les ressources JDBC
            try {
                if (ste != null) {
                    ste.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la fermeture du Statement : " + e.getMessage());
            }
        }
    }
    public void delete(int id) {
        String requete = "DELETE FROM categorie WHERE id=" + id;
        try {
            ste = cnx.createStatement();
            ste.executeUpdate(requete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void update(Categorie categorie, int id) {
        String requete = "UPDATE categorie SET libelle='" + categorie.getLibelle() + "' WHERE id=" + id;
        try {
            ste = cnx.createStatement();
            ste.executeUpdate(requete);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public List<Categorie> getAll() {
        String requete="select * from categorie";
        List<Categorie> list=new ArrayList<>();
        try {
            ste= cnx.createStatement();
            ResultSet rs=ste.executeQuery(requete);
            while(rs.next()){
                list.add(new Categorie(rs.getInt("id"),rs.getString(2)));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public Categorie getById(int id) {
        String requete = "SELECT * FROM categorie WHERE id = " + id;
        try {
            ste = cnx.createStatement();
            ResultSet rs = ste.executeQuery(requete);
            if (rs.next()) {
                return new Categorie(rs.getInt("id"), rs.getString("libelle"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null; // Retourner null si aucune catégorie avec cet ID n'est trouvée
    }
    public int getIdByLibelle(String libelle) {
        String query = "SELECT id FROM categorie WHERE libelle = '" + libelle + "'";
        try {
            ste = cnx.createStatement();
            ResultSet rs = ste.executeQuery(query);
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new RuntimeException("La catégorie avec le libellé '" + libelle + "' n'existe pas.");
                // Ou vous pouvez retourner -1 pour indiquer que la catégorie n'existe pas
                // return -1;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'ID de la catégorie : " + e.getMessage());
        } finally {
            // Fermer les ressources JDBC
            try {
                if (ste != null) {
                    ste.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la fermeture du Statement : " + e.getMessage());
            }
        }
    }

    // Méthode pour valider une catégorie
    public boolean isValidCategorie(Categorie categorie) {
        if (categorie == null) {
            System.out.println("La catégorie est nulle.");
            return false;
        }
        String libelle = categorie.getLibelle();
        if (libelle == null || libelle.isEmpty()) {
            System.out.println("Le libellé de la catégorie est vide.");
            return false;
        }
        if (!libelle.matches("[a-zA-Z]+")) {
            System.out.println("Le libellé de la catégorie ne doit contenir que des lettres.");
            return false;
        }
        return true; // La catégorie est valide
    }


    public List<String> getAllLibelles() {
        List<Categorie> categories = getAll(); // Récupérer toutes les catégories
        List<String> libelles = new ArrayList<>(); // Liste pour stocker les libellés

        // Parcourir chaque catégorie pour extraire son libellé
        for (Categorie categorie : categories) {
            libelles.add(categorie.getLibelle()); // Ajouter le libellé à la liste
        }

        return libelles; // Retourner la liste des libellés
    }
    public Categorie getByLibelle(String libelle) {
        String query = "SELECT * FROM categorie WHERE libelle = '" + libelle + "'";
        try {
            ste = cnx.createStatement();
            ResultSet rs = ste.executeQuery(query);
            if (rs.next()) {
                return new Categorie(rs.getInt("id"), rs.getString("libelle"));
            } else {
                throw new RuntimeException("La catégorie avec le libellé '" + libelle + "' n'existe pas.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de la catégorie par libellé : " + e.getMessage());
        } finally {
            // Fermer les ressources JDBC
            try {
                if (ste != null) {
                    ste.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Erreur lors de la fermeture du Statement : " + e.getMessage());
            }
        }
    }


}
