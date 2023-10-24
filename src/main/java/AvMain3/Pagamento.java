package AvMain3;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import org.json.JSONObject;

public class Pagamento extends Thread {

    private String quemPaga;
    private String quemRecebe;
    private String senha;
    private double valor;
    private Socket socket;

    public Pagamento(String quemPaga, String quemRecebe, String senha,double valor,Socket socket){
        this.quemPaga = quemPaga;
        this.quemRecebe = quemRecebe;
        this. senha = senha;
        this.valor = valor;
        this.socket = socket;
    }

    @Override
    public void run(){
        try {

            // Atributo para saída de dados
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());

            String login [] = {quemPaga , senha};

            //Envia Login para o BancoSv
            out.writeUTF(transfLoginJson(login));

            DadosTransf dados = new DadosTransf(quemPaga,quemRecebe, "Pagamento", valor);

            //Envia os dados para o BancoSv
            out.writeUTF(transfDadosJson(dados));

        } catch (IOException e) {
            e.printStackTrace();
        }
    } 

    public String transfDadosJson(DadosTransf dadosTransf) {
        JSONObject dadosJSON = new JSONObject();
        dadosJSON.put("quemPaga", dadosTransf.getQuemPaga());
        dadosJSON.put("quemRecebe", dadosTransf.geteQuemRecebe());
        dadosJSON.put("operacao", dadosTransf.getOperacao());
        dadosJSON.put("valor", dadosTransf.getValor());
        return dadosJSON.toString();
    }

    public String transfLoginJson(String[] login){
        JSONObject dadosJSON = new JSONObject();
        dadosJSON.put("login",login[0]);
        dadosJSON.put("senha",login[1]);
        return dadosJSON.toString();
    }
}