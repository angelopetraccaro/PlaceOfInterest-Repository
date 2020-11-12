package com.gmail.petraccaro.angelo.placesofinterest;

/**
 * Dichiara variabili e metodi dell'item appartenente alla lista visualizzata nella MainActivity
 */
public class ElementoLista {
    private String nome;
    private String breve_descrizione;
    private String descrizione;
    private String latitude,longitude;
    private String url_foto,url_foto1,url_foto2;
    private String didascalia,didascalia1,didascalia2;
    private String nome_documento;
    private String owner;

    public ElementoLista(String nome, String breve_descrizione, String descrizione, String latitude, String longitude, String url_foto, String url_foto1, String url_foto2, String didascalia, String didascalia1, String didascalia2, String nome_documento, String owner) {
        this.nome = nome;
        this.breve_descrizione = breve_descrizione;
        this.descrizione = descrizione;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url_foto = url_foto;
        this.url_foto1 = url_foto1;
        this.url_foto2 = url_foto2;
        this.didascalia = didascalia;
        this.didascalia1 = didascalia1;
        this.didascalia2 = didascalia2;
        this.nome_documento = nome_documento;
        this.owner = owner;
    }

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

    public String getUrl_foto1() { return url_foto1; }

    public String getUrl_foto2() { return url_foto2; }

    public String getDidascalia() { return didascalia; }

    public String getDidascalia1() { return didascalia1; }

    public String getDidascalia2() { return didascalia2; }

    public String getNome_documento() {
        return nome_documento;
    }

    public String getOwner() {
        return owner;
    }
}
