package entite;

import java.sql.Date;

public class Reclamation {
    private int id;
    private user user;
    private int recuserId;
    private String contenu;
    private Date date;
    private String etat;

    public Reclamation(int recuserId, String contenu, Date date, String etat) {
        this.recuserId = recuserId;
        this.contenu = contenu;
        this.date = date;
        this.etat = etat;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEtat() {
        return etat;
    }

    public Reclamation(int id, user user, String contenu, Date date, String etat) {
        this.id = id;
        this.user = user;
        this.contenu = contenu;
        this.date = date;
        this.etat = etat;
    }

    public Reclamation(user user, String contenu, Date date, String etat) {
        this.user = user;
        this.contenu = contenu;
        this.date = date;
        this.etat = etat;
    }

    public Reclamation() {

    }

    public user getUser() {
        return user;
    }

    public String getContenu() {
        return this.contenu;
    }

    public Date getDate() {
        return date;
    }

    public String isEtat() {
        return etat;
    }

    public void setUser(user user) {
        this.user = user;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    @Override
    public String toString() {
        return "Reclamation{" +
                "id=" + id +
                ", user=" + user +
                ", contenu='" + contenu + '\'' +
                ", date=" + date +
                ", etat='" + etat + '\'' +
                '}';
    }
}
