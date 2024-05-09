package com.alphafit.alphafit;

public class Pack {
    private int id;
    private String nom;
    private double prix;
    private String description;

    public Pack() {

    }

    @Override
    public String toString() {
        return "Pack{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prix=" + prix +
                ", description='" + description + '\'' +
                '}';
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setPrix(double prix) {
        this.prix = prix;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public double getPrix() {
        return prix;
    }

    public String getDescription() {
        return description;
    }

    public Pack(int id, String nom, double prix, String description) {
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.description = description;
    }
}
