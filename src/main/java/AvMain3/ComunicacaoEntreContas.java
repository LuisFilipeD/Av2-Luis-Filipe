package AvMain3;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONObject;

public class ComunicacaoEntreContas extends Thread {
    
    private Socket socket;
    private DataInputStream in;
    private BancoSv banco;
    private boolean on = true;

    public ComunicacaoEntreContas(Socket socket, BancoSv banco){
        this.socket = socket;
        this.banco = banco;
    }

    @Override
    public void run(){
        try {
            in = new DataInputStream(socket.getInputStream());
              
            while(on){
                String[] login = lerLogin(in.readUTF());

                if(banco.verificarLogin(login)){

                    DadosTransf d = lerDados(in.readUTF());
                    
                    //Realiza a transferencia entre contras
                    banco.transferencia(d.getQuemPaga(), d.geteQuemRecebe(), d.getValor());
                    
                    //Adiciona a transferencia ao ArrayList de dados
                    banco.adicionarDados(d);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String[] lerLogin(String sJson){
        JSONObject json = new JSONObject(sJson);
        String[] login = new String[] { json.getString("login"), json.getString("senha") };
        return login;
    }

    public DadosTransf lerDados(String dJson){
        JSONObject json = new JSONObject(dJson);
        String quemPaga = json.getString("quemPaga");
        String quemRecebe = json.getString("quemRecebe");
        String operacao = json.getString("operacao");
        double valor = json.getDouble("valor");

        DadosTransf d = new DadosTransf(quemPaga, quemRecebe, operacao, valor);
        return d;
    }
}
