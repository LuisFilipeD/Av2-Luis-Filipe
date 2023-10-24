package AvMain3;

public class Rota {

    private String idRota;
    private String [] rota;

    public Rota(String idRota, String[] rota){
        this.idRota = idRota;
        this.rota = rota;
    }

    public String getId(){
        return idRota;
    }

    public String[] getRota(){
        return rota;
    }
}