package com.example.projecte_2dam_grup6;

public class Producte {

    private String head;
    private String user;
    private String imageURL;
    private String preu;
    private String id_producte;

    public Producte(String head, String user, String imageURL, String preu, String id_producte) {
        this.head = head;
        this.user = user;
        this.imageURL = imageURL;
        this.preu = preu;
        this.id_producte = id_producte;
    }

    public String getId_producte() {
        return id_producte;
    }

    public String getPreu() {
        return preu;
    }

    public String getHead() {
        return head;
    }

    public String getUser() {
        return user;
    }

    public String getImageURL() {
        return imageURL;
    }

}
