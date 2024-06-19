package entite;




public class lignecommande {


    private Long id;

    private commande commande;

    private int quantite;

    private Long articleId;

    // Constructeurs
    public lignecommande() {
    }

    public lignecommande(commande commande, int quantite, Long articleId) {
        this.commande = commande;
        this.quantite = quantite;
        this.articleId = articleId;
    }

    // Getters et setters
    // Id
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // Commande
    public commande getCommande() {
        return commande;
    }

    public void setCommande(commande commande) {
        this.commande = commande;
    }

    // Quantite
    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    // ArticleId
    public Long getArticleId() {
        return articleId;
    }

    public void setArticleId(Long articleId) {
        this.articleId = articleId;
    }
}
