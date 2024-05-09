package com.alphafit.alphafit;

import java.sql.Date;

public class Abonnement {
    private int id;
    private Pack pack;
    private user coach;
    private user nutrisionist;
    private user client;
    private Date dateExpiration;


    public Abonnement() {

    }
    public Abonnement(int id, Pack pack, user coach, user nutrisionist, user client, Date dateExpiration) {
        this.id = id;
        this.pack = pack;
        this.coach = coach;
        this.nutrisionist = nutrisionist;
        this.client = client;
        this.dateExpiration = dateExpiration;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Pack getPack() {
        return pack;
    }

    public void setPack(Pack pack) {
        this.pack = pack;
    }

    public user getCoach() {
        return coach;
    }

    public void setCoach(user coach) {
        this.coach = coach;
    }

    public user getNutrisionist() {
        return nutrisionist;
    }

    public void setNutrisionist(user nutrisionist) {
        this.nutrisionist = nutrisionist;
    }

    public user getClient() {
        return client;
    }

    public void setClient(user client) {
        this.client = client;
    }

    public Date getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(Date dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

   /* public void setCoachId(int coachId) {
        this.coachId = coachId;
    }

    public void setNutritionistId(int nutritionistId) {
        this.nutritionistId = nutritionistId;
    }*/
}
