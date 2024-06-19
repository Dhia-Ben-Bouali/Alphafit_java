package services;

import entite.Article;
import entite.Categorie;
import util.DataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArticleService implements IServicea<Article> {
    private Connection cnx;
    private PreparedStatement ps;
    private CategorieService categorieService; // Déclaration de la variable d'instance

    public ArticleService() {
        cnx = DataSource.getInstance().getConnection();
        categorieService = new CategorieService(); // Initialisation de l'objet CategorieService
    }
    // Méthode pour récupérer les statistiques des articles par catégorie
    public Map<String, Integer> getArticleCountsByCategory() {
        Map<String, Integer> countsByCategory = new HashMap<>();

        // Obtenez tous les articles de la base de données
        List<Article> articles = getAll();

        // Compter le nombre d'articles pour chaque catégorie
        for (Article article : articles) {
            String categoryName = article.getCategorie().getLibelle(); // Supposons que le nom de la catégorie est utilisé comme clé
            countsByCategory.put(categoryName, countsByCategory.getOrDefault(categoryName, 0) + 1);
        }

        return countsByCategory;
    }
    public void add(Article article) {
        if (!isValidArticle(article)) {
            throw new IllegalArgumentException("Article invalide. Veuillez corriger les champs.");
        }

        String query = "INSERT INTO article (nom, prix, description, image, categorie_id, quantite) VALUES (?, ?, ?, ?, ?, ?)";
        try (
                PreparedStatement ps = cnx.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            // Récupérer l'ID de la catégorie associée à l'article
            int categorieId = article.getCategorie().getId();

            // Vérifier si la catégorie associée à l'article existe dans la base de données
            if (categorieId != -1) {
                // Vérifier si la catégorie existe réellement dans la base de données
                Categorie categorie = categorieService.getById(categorieId);
                if (categorie != null) {
                    ps.setString(1, article.getNom());
                    ps.setDouble(2, article.getPrix());
                    ps.setString(3, article.getDescription());
                    ps.setString(4, article.getImage());
                    ps.setInt(5, categorieId);
                    ps.setInt(6, article.getQuantite());

                    // Exécuter la requête SQL
                    ps.executeUpdate();

                    // Récupérer les clés générées automatiquement
                    ResultSet rs = ps.getGeneratedKeys();
                    if (rs.next()) {
                        // Mettre à jour l'ID de l'article avec la clé générée automatiquement
                        int generatedId = rs.getInt(1);
                        article.setId(generatedId);
                    }
                } else {
                    throw new RuntimeException("La catégorie spécifiée pour l'article n'existe pas.");
                }
            } else {
                throw new RuntimeException("L'ID de la catégorie spécifiée pour l'article est invalide.");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de l'ajout de l'article : " + e.getMessage());
        }
    }

    @Override
    public void delete(int id) {
        String query = "DELETE FROM article WHERE id = ?";
        try {
            ps = cnx.prepareStatement(query);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'article : " + e.getMessage());
        }
    }

    @Override
    public void update(Article article, int id) {
        String query = "UPDATE article SET nom = ?, prix = ?, description = ?, image = ?, categorie_id = ?, quantite = ? WHERE id = ?";
        try {
            ps = cnx.prepareStatement(query);
            ps.setString(1, article.getNom());
            ps.setDouble(2, article.getPrix());
            ps.setString(3, article.getDescription());
            ps.setString(4, article.getImage());
            ps.setInt(5, article.getCategorie().getId()); // Utilisation de l'ID de la catégorie
            ps.setInt(6, article.getQuantite());
            ps.setInt(7, id);

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de l'article : " + e.getMessage());
        }
    }

    @Override
    public  List<Article> getAll() {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM article";
        try {
            ps = cnx.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                String description = rs.getString("description");
                String image = rs.getString("image");
                int categorieId = rs.getInt("categorie_id");
                int quantite = rs.getInt("quantite");
                Categorie categorie = categorieService.getById(categorieId); // Obtenir l'objet complet de la catégorie
                Article article = new Article(id, nom, prix, description, image, categorie,quantite);
                articles.add(article);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des articles : " + e.getMessage());
        }
        return articles;
    }

    @Override
    public Article getById(int id) {
        String query = "SELECT * FROM article WHERE id = ?";
        try {
            ps = cnx.prepareStatement(query);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                String description = rs.getString("description");
                String image = rs.getString("image");
                int categorieId = rs.getInt("categorie_id");
                int quantite = rs.getInt("quantite");

                Categorie categorie = categorieService.getById(categorieId); // Obtenir l'objet complet de la catégorie
                return new Article(id, nom, prix, description, image, categorie,quantite);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de l'article : " + e.getMessage());
        }
        return null;
    }
    private boolean isValidArticle(Article article) {
        boolean isValid = true;

        if (article.getNom().isEmpty()) {
            System.out.println("Le nom de l'article ne peut pas être vide.");
            isValid = false;
        }

        if (article.getPrix() <= 0.0) {
            System.out.println("Le prix de l'article doit être supérieur à zéro.");
            isValid = false;
        }

        if (article.getDescription().isEmpty()) {
            System.out.println("La description de l'article ne peut pas être vide.");
            isValid = false;
        }

        if (article.getImage().isEmpty()) {
            System.out.println("L'image de l'article ne peut pas être vide.");
            isValid = false;
        }

        if (article.getCategorie() == null) {
            System.out.println("La catégorie de l'article ne peut pas être vide.");
            isValid = false;
        }

        return isValid;
    }
    public List<Article> searchByNom(String Nom) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM article WHERE nom LIKE ?";
        try {
            ps = cnx.prepareStatement(query);
            ps.setString(1, "%" + Nom + "%"); // Recherche par nom partiel
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                String image = rs.getString("image");
                Article article = new Article(nom, prix, image);
                articles.add(article);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche d'articles : " + e.getMessage());
        }
        return articles;
    }



    public List<Article> getArticlesBelowThreshold() {
        List<Article> articlesBelowThreshold = new ArrayList<>();
        String query = "SELECT * FROM article WHERE quantite < 5";
        try {
            ps = cnx.prepareStatement(query);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                String description = rs.getString("description");
                String image = rs.getString("image");
                int categorieId = rs.getInt("categorie_id");
                int quantite = rs.getInt("quantite");
                Categorie categorie = categorieService.getById(categorieId);
                Article article = new Article(id, nom, prix, description, image, categorie, quantite);
                articlesBelowThreshold.add(article);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des articles avec une quantité inférieure à 5 : " + e.getMessage());
        }
        return articlesBelowThreshold;
    }
    public List<Article> getArticlesByCategoryId(int categoryId) {
        List<Article> articles = new ArrayList<>();
        String query = "SELECT * FROM article WHERE categorie_id = ?";

        try {
            // Préparez la requête SQL avec un paramètre pour la catégorie
            PreparedStatement ps = cnx.prepareStatement(query);
            ps.setInt(1, categoryId); // Utilisez l'ID de la catégorie
            System.out.println("Query: " + ps.toString()); // Affichez la requête SQL pour le débogage

            ResultSet rs = ps.executeQuery();

            // Parcourez les résultats de la requête
            while (rs.next()) {
                int id = rs.getInt("id");
                String nom = rs.getString("nom");
                double prix = rs.getDouble("prix");
                String description = rs.getString("description");
                String image = rs.getString("image");
                int quantite = rs.getInt("quantite");

                // Créez un nouvel objet Article avec les données récupérées
                Article article = new Article(id, nom, prix, description, image, quantite);

                // Ajoutez l'article à la liste des articles
                articles.add(article);
                System.out.println("Nombre d'articles récupérés pour la catégorie '" + categoryId + "' : " + articles.size());
            }
        } catch (SQLException e) {
            // Gérez les exceptions
            System.err.println("Une erreur s'est produite lors de la récupération des articles par catégorie : " + e.getMessage());
            e.printStackTrace();
        }
        // Retournez la liste des articles de la catégorie spécifiée
        return articles;
    }


}
