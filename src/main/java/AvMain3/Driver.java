package AvMain3;

import java.util.List;

import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Driver extends Thread{

    private List<Rota> rotasAExecutar;
    private Rota rotaEmExecução;
    private List<Rota> rotasExecutadas;
    private Companhia eltman;
    private Car carro;
    private ServicoT2 st;
    private String idDriver;
    private ContaCorrente conta;

    private Socket socket;
    private int porta;
    private String host;
    private int aux;


    public Driver(Car carro, List<Rota> rotasAExecutar,String idDriver,Companhia eltman,int porta, String host){
        this.porta = porta;
        this.host = host;
        this.idDriver = idDriver;
        this.rotasAExecutar = rotasAExecutar;
        this.carro=carro;
        this.eltman=eltman;
        this.conta = new ContaCorrente(idDriver,1000);
        BancoSv.adicionarConta(conta);
        conta.start();
        this.aux =0;
        //System.out.println(idDriver+" Conta adicionada ao Banco");
    }

    @Override
    public void run(){
        try {
            socket = new Socket(host, porta);
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            
            //System.out.println(this.idDriver+" se conectou ao sv");

            Thread c = new Thread(this.carro);
            c.start();
            iniciarServico();

            while(c.isAlive()){
                if(carro.getPrecisaAbastecer() && aux!=1){
                    //System.out.println(carro.getId()+" Precisa abastecer "+carro.getPrecisaAbastecer());
                    
                    // Combustivel que falta para completar o tanque
                    double v = 10-carro.getFuelTank();
                    realizarPagamento(socket, v*carro.getPrecoCombustivel());
                    //System.out.println(carro.getId()+" Abasteceu R$"+v*carro.getPrecoCombustivel());
                    aux =1;
                }
                if(carro.getJaAbasteceu()){
                    aux=0;
                }
                if(carro.getAndou1Km()){

                    //Envia mensagem que andou 1km e precisa receber 
                    //Se ativar esse método da erro de "login" not found na hora de realizar o pagamento para o Driver
                    //out.writeUTF(transfIdJson(this.idDriver));
                    carro.setAndou1Km();
                } 
            }} catch (UnknownHostException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        

    public String transfIdJson(String idDriver){
        JSONObject dadosJSON = new JSONObject();
        dadosJSON.put("quemR",idDriver);
        return dadosJSON.toString();
    }

    public void realizarPagamento(Socket socket, double valor){
            Pagamento p = new Pagamento(conta.getIdConta(),"ContaPosto","senha",valor, socket);
            p.start();
    }
    public void iniciarServico(){
        this.executarProximaRotaD();
        st = new ServicoT2(carro.getSumo(), carro, rotasAExecutar);
        st.start();
    }

    public synchronized void executarProximaRotaD(){
        this.rotaEmExecução = rotasAExecutar.get(0);
        this.rotasAExecutar.remove(0);
        eltman.executarProximaRotaC();
    }

    public synchronized void finalizarRotaD(){
        this.rotasExecutadas.add(rotaEmExecução);
        this.rotaEmExecução=null;
        eltman.finalizarRotaC();
    }

    public List<Rota> getRotasAExecutar(){
        return rotasAExecutar;
    }
    
    public Car getCarro() {
        return carro;
    }

    public String getIdDriver() {
        return idDriver;
    }
    
    public Rota getRotaEmExecução(){
        return rotaEmExecução;
    }
}

