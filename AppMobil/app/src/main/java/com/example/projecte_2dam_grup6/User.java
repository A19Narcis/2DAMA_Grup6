package com.example.projecte_2dam_grup6;

public class User {
    private String email;
    private String nom;
    private String cognoms;
    private String ubicacio;
    private String rol;
    private String descripcio;
    private String dataN;

    public User (String email, String nom, String cognoms, String ubicacio, String rol, String descripcio, String dataN){
        this.email=email;
        this.nom=nom;
        this.cognoms=cognoms;
        this.ubicacio=ubicacio;
        this.rol=rol;
        this.descripcio=descripcio;
        this.dataN=dataN;
    }

    public String GetEmailUser(){
        return email;
    }

    public String GetNomUser(){
        return nom;
    }

    public String GetCognomsUser(){
        return cognoms;
    }

    public String GetUbicacioUser(){
        return ubicacio;
    }

    public String GetRolUser(){
        return rol;
    }

    public String GetDescripcioUser(){
        return descripcio;
    }

    public String GetDataNUser(){
        return dataN;
    }


}
