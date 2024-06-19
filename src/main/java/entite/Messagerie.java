package entite;


import java.sql.Timestamp;

public class Messagerie {


    private Long id;

    private user sender;


    private user recipient;
    private String titre, contenu;
    private Timestamp created_at;
    private boolean is_read, is_favorite;


    public Messagerie(user sender, user recipient, String titre, String contenu, boolean is_read, boolean is_favorite) {
        this.sender = sender;
        this.recipient = recipient;
        this.titre = titre;
        this.contenu = contenu;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.is_favorite = is_favorite;
    }

    public Messagerie(user sender, user recipient, String titre, String contenu, Timestamp created_at,boolean is_read, boolean is_favorite) {
        this.sender = sender;
        this.recipient = recipient;
        this.titre = titre;
        this.contenu = contenu;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.is_favorite = is_favorite;
        this.created_at=created_at;
    }
    public Messagerie(user sender, user recipient, String titre, String contenu, boolean is_favorite) {
        this.sender = sender;
        this.recipient = recipient;
        this.titre = titre;
        this.contenu = contenu;
        this.created_at = new Timestamp(System.currentTimeMillis());
        this.is_favorite = is_favorite;
    }

    public Messagerie(Long id,String titre, String contenu, boolean is_favorite) {
        this.titre = titre;
        this.contenu = contenu;
        this.is_favorite = is_favorite;
        this.id=id;
    }

    public user getSender() {
        return sender;
    }

    public void setSender(user sender) {
        this.sender = sender;
    }

    public user getRecipient() {
        return recipient;
    }

    public void setRecipient(user recipient) {
        this.recipient = recipient;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getContenu() {
        return contenu;
    }

    public void setContenu(String contenu) {
        this.contenu = contenu;
    }

    public Timestamp getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Timestamp created_at) {
        this.created_at = created_at;
    }

    public boolean isIs_read() {
        return is_read;
    }

    public void setIs_read(boolean is_read) {
        this.is_read = is_read;
    }

    public boolean isIs_favorite() {
        return is_favorite;
    }

    public void setIs_favorite(boolean is_favorite) {
        this.is_favorite = is_favorite;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return
                "id=" + id +
                ", sender=" + sender.getNom() +
                ", recipient=" + recipient.getNom() +
                ", titre='" + titre + '\'' +
                ", contenu='" + contenu + '\'' +
                ", created_at=" + created_at +
                ", is_read=" + is_read +
                ", is_favorite=" + is_favorite ;
    }
}



