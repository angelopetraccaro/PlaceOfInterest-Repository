package com.gmail.petraccaro.angelo.placesofinterest;

/**
 * Dichiara variabili e metodi dell'item appartenente alla lista visualizzata nella MainActivity
 */
public class ElementoLista {
    private String nome;
    private String breve_descrizione;
    private String descrizione;
    private String latitude,longitude;
    private String url_foto;
    private String didascalia;
    private String owner;
    private String keyOnDb;
    private boolean available;

    public ElementoLista(String nome, String breve_descrizione, String descrizione, String latitude, String longitude, String url_foto,  String didascalia, String owner,String key,boolean available) {
        this.nome = nome;
        this.breve_descrizione = breve_descrizione;
        this.descrizione = descrizione;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url_foto = url_foto;
        this.keyOnDb = key;
        this.didascalia = didascalia;
        this.owner = owner;
        this.available = available;
    }

    public  ElementoLista(){}
    public String getNome() {
        return nome;
    }
    public String getBreve_descrizione() {
        return breve_descrizione;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public String getLatitude() { return latitude; }
    public String getLongitude() { return longitude; }
    public String getUrl_foto() {
        return url_foto;
    }
    public String getDidascalia() { return didascalia; }
    public String getOwner() {
        return owner;
    }
    public boolean getAvailable(){return  available;}
    public String getKeyOnDb() {
        return this.keyOnDb;
    }

}
