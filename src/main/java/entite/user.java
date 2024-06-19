package entite;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.List;

public class user {

    public int id;

    public String email;
    public List<String> roles;

    public String password;
    public String nom;
    private static int nextId = 1;
    public String prenom;
    public String adresse;
    public float salaire;

    public String image ;

    public Boolean activated;

    public user(int id, String email, List<String> roles, String password, String nom, String prenom, String adresse, float salaire, Boolean activated) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.salaire = salaire;
        this.activated = activated;
    }

    public user( String email, List<String> roles, String password, String nom, String prenom, String adresse, float salaire, String image) {

        this.email = email;
        this.roles = roles;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.salaire = salaire;
        this.image = image;

    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getActivated() {
        return activated;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public user() {
    }

    public user(int id, String nom, String prenom) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
    }

    public user( String email, List<String> roles, String password, String nom, String prenom, String adresse, float salaire) {
        this.id = nextId++;
        this.email = email;
        this.roles = roles;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.salaire = salaire;
    }
    public user( int id,String email, List<String> roles, String password, String nom, String prenom, String adresse, float salaire) {
        this.id = id;
        this.email = email;
        this.roles = roles;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.salaire = salaire;
    }

    public user(String nom, String prénom, String email) {
    }

    public user(String email, String nom, String prenom, String adresse) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public  String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List getRoles() {
        return roles;
    }

    public void setRoles(List roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public  String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public float getSalaire() {
        return salaire;
    }

    public void setSalaire(float salaire) {
        this.salaire = salaire;
    }

    @Override
    public String toString() {
        return email ;

    }
}
