package com.gmail.petraccaro.angelo.placesofinterest;

public class User {
    private String nome;
    private String cognome;
    private String username;
    private String email;
    private String password;
   // private String uploadId;
    private String uriFotoDelProfilo;

    public User(String nome, String cognome, String username, String email, String password, String uriFotoDelProfilo) {
        this.nome = nome;
        this.cognome = cognome;
        this.username = username;
        this.email = email;
        this.password = password;
       // this.uploadId = uploadId;
        this.uriFotoDelProfilo = uriFotoDelProfilo;
    }
    public  User(){}
    public String getCognome() {
        return cognome;
    }
    public String getUriFotoDelProfilo() {
        return uriFotoDelProfilo;
    }
    public String getPassword() {
        return password;
    }
    public String getNome() {
        return nome;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
}
