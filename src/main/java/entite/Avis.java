package entite;

public class Avis {

    private int id;
    private boolean star1,star2,star3,star4,star5;
    private String commentaire;
    private user user;

    public Avis()

    {

    }
    public Avis(int id, boolean star1, boolean star2, boolean star3, boolean star4, boolean star5, String commentaire, user user) {
        this.id = id;
        this.star1 = star1;
        this.star2 = star2;
        this.star3 = star3;
        this.star4 = star4;
        this.star5 = star5;
        this.commentaire = commentaire;
        this.user = user;
    }

    public Avis( boolean star1, boolean star2, boolean star3, boolean star4, boolean star5, String commentaire, user user) {

        this.star1 = star1;
        this.star2 = star2;
        this.star3 = star3;
        this.star4 = star4;
        this.star5 = star5;
        this.commentaire = commentaire;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStar1() {
        return star1;
    }

    public void setStar1(boolean star1) {
        this.star1 = star1;
    }

    public boolean isStar2() {
        return star2;
    }

    public void setStar2(boolean star2) {
        this.star2 = star2;
    }

    public boolean isStar3() {
        return star3;
    }

    public void setStar3(boolean star3) {
        this.star3 = star3;
    }

    public boolean isStar4() {
        return star4;
    }

    public void setStar4(boolean star4) {
        this.star4 = star4;
    }

    public boolean isStar5() {
        return star5;
    }

    public void setStar5(boolean star5) {
        this.star5 = star5;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public user getUser() {
        return user;
    }

    public void setUser(user user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return
                        "Avis : " + commentaire ;

    }
}

