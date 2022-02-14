package hr.java.production.model;

public class Booking {

    String oznaka;
    String slanjePostom;
    String napomena;
    String DatumKreiranja;

    public Booking(String oznaka, String slanjePostom, String napomena, String datumKreiranja) {
        this.oznaka = oznaka;
        this.slanjePostom = slanjePostom;
        this.napomena = napomena;
        DatumKreiranja = datumKreiranja;
    }

    public String getOznaka() {
        return oznaka;
    }

    public void setOznaka(String oznaka) {
        this.oznaka = oznaka;
    }

    public String getSlanjePostom() {
        return slanjePostom;
    }

    public void setSlanjePostom(String slanjePostom) {
        this.slanjePostom = slanjePostom;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }

    public String getDatumKreiranja() {
        return DatumKreiranja;
    }

    public void setDatumKreiranja(String datumKreiranja) {
        DatumKreiranja = datumKreiranja;
    }
}
