package entite;

import java.sql.Timestamp;

public class Suivi {
private int id;
    private String plan_nutritionniste, plan_coach;

  private String title,description;
    private Timestamp start,end;



    private boolean all_day;
    private  String background_color,border_color,text_color;
    private Place place;
    private user adherent;


    /*ManyToOne
    @JoinColumn( name="idUser" )
    public entite.user user;*/

public Suivi()
{

}
    public Suivi(int id , String plan_coach,String plan_nutritionniste, String title, String description, Timestamp start, Timestamp end, boolean all_day, String background_color, String border_color, String text_color, user adherent,Place place) {
        this.plan_nutritionniste = plan_nutritionniste;
        this.plan_coach = plan_coach;
        this.title = title;
        this.description = description;
        this.place=place;
        this.start = start;
        this.end = end;
        this.all_day = all_day;
        this.background_color = background_color;
        this.border_color = border_color;
        this.text_color = text_color;
        this.adherent=adherent;
        this.id=id;
    }


    public Suivi( String plan_coach,String plan_nutritionniste, String title, String description, Timestamp start, Timestamp end, boolean all_day, String background_color, String border_color, String text_color, user adherent,Place place) {
        this.plan_nutritionniste = plan_nutritionniste;
        this.plan_coach = plan_coach;
        this.title = title;
        this.description = description;
        this.place=place;
        this.start = start;
        this.end = end;
        this.all_day = all_day;
        this.background_color = background_color;
        this.border_color = border_color;
        this.text_color = text_color;
        this.adherent=adherent;
    }



    public String getPlan_nutritionniste() {
        return plan_nutritionniste;
    }

    public void setPlan_nutritionniste(String plan_nutritionniste) {
        this.plan_nutritionniste = plan_nutritionniste;
    }

    public String getPlan_coach() {
        return plan_coach;
    }

    public void setPlan_coach(String plan_coach) {
        this.plan_coach = plan_coach;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public boolean isAll_day() {
        return all_day;
    }

    public void setAll_day(boolean all_day) {
        this.all_day = all_day;
    }

    public String getBackground_color() {
        return background_color;
    }

    public void setBackground_color(String background_color) {
        this.background_color = background_color;
    }

    public String getBorder_color() {
        return border_color;
    }

    public void setBorder_color(String border_color) {
        this.border_color = border_color;
    }

    public String getText_color() {
        return text_color;
    }

    public void setText_color(String text_color) {
        this.text_color = text_color;
    }


    @Override
    public String toString() {
        return "Suivi{" +
                "id=" + id +
                ", plan_nutritionniste='" + plan_nutritionniste + '\'' +
                ", plan_coach='" + plan_coach + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", all_day=" + all_day +
                ", background_color='" + background_color + '\'' +
                ", border_color='" + border_color + '\'' +
                ", text_color='" + text_color + '\'' +
                ", place=" + place +
                ", adherent=" + adherent +
                '}';
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public user getAdherent() {
        return adherent;
    }

    public void setAdherent(user adherent) {
        this.adherent = adherent;
    }
}
