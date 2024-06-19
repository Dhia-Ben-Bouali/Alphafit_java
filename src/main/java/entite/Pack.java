package entite;

import java.util.List;

public class Pack {
    private int id;
    private String name;
    private String description;
    private double price;
    private List<Service> services;

    public Pack() {
    }

    public Pack(int id, String name, String description, double price, List<Service> services) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.services = services;
    }
    public Pack(String name, String description, double price, List<Service> services) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.services = services;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void setNom(String packName) {
        this.name=packName;
    }

    public String getNom() {
        return this.name;
    }
}