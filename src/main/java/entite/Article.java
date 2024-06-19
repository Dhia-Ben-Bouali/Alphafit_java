package entite;

public class Article {
    private int id,quantite;
    private String nom;
    private double prix;
    private String description;
    private String image;


   // private int id_categorie;

    private Categorie categorie;


    public Article(int id, String nom, double prix, String description, String image, Categorie categorie,int quantite) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.image = image;
        this.categorie = categorie;
        this.quantite=quantite;
    }

    public Article(String nom, double prix,int quantite, String description, String image, Categorie categorie) {
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.description = description;
        this.image = image;
        this.categorie = categorie;
    }
    public Article(int id,String nom, double prix, String description, String image,int quantite) {
        this.id=id;
        this.nom = nom;
        this.prix = prix;
        this.quantite = quantite;
        this.description = description;
        this.image = image;
    }

    public Article(String nom, double prix, String image) {
        this.nom=nom;
        this.prix=prix;
        this.image=image;
    }

    public Article(int id, String nom) {
        this.id=id;
        this.nom=nom;
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public double getPrix() {
        return prix;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Categorie getCategorie() {
        return this.categorie;
    }

    public void setCategorie(Categorie categorie) {
        this.categorie = categorie;
    }

    @Override
    public String toString() {
        String categorieLibelle = (categorie != null) ? categorie.getLibelle() : "Catégorie non définie";
        return "Article{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", quantite=" + quantite +
                ", categorie='" + categorieLibelle + '\'' + // Utilisation de la variable categorieLibelle pour éviter les NullPointerException
                '}';
    }
    }

