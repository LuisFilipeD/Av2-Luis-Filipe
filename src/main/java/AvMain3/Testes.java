package AvMain3;

import java.io.IOException;
import java.net.ServerSocket;

public class Testes {
    public static void main (String[]args){
        int portabanco = 54321;

        ServerSocket BancoServer;
        try {
            
            BancoServer = new ServerSocket(portabanco);
            BancoSv banco = new BancoSv(BancoServer);
            banco.start();
            PostoCombustivel posto = new PostoCombustivel();
            Companhia eltman = new Companhia(54321,"localhost");
            eltman.conectarSumo();
            eltman.criarDriversECarros2();
            eltman.start();
            for(Driver d: eltman.getDrivers()){
            d.start();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}