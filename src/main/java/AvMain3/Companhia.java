package AvMain3;
  
import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import it.polito.appeal.traci.SumoTraciConnection;

public class Companhia extends Thread {
    private List<Rota> rotasAExecutar; 
    private List<Rota> rotasEmExecução;
    private List<Rota> rotasExecutadas;
    private Car carro;
    private Driver driver;
    private List<Car> carros;
    private List<Driver> drivers;
    private SumoTraciConnection sumo;
    private ContaCorrente contaC;

    private Socket socket;
    private int porta;
    private String host;
    //private DataOutputStream out;

    
    public Companhia(int porta, String host){
        new AdquirirRotas();
        //Armazenando o ArrayList improtado do XML em rotasAExecutar (900 rotas)
        this.rotasAExecutar = AdquirirRotas.importarRotas("data/dados.xml");
        this.rotasEmExecução = new ArrayList<>();
        this.rotasExecutadas = new ArrayList<>();
        this.carros = new ArrayList<>();
        this.drivers = new ArrayList<>();
        this.porta = porta;
        this.host = host;
        definirSumo();
        contaC = new ContaCorrente("Eltman",50000);
        BancoSv.adicionarConta(contaC);
        contaC.start();
    }

    @Override
    public void run(){
        try{
            System.out.println("Companhia se Conectou");
            socket = new Socket(host, porta);
            DataInputStream in = new DataInputStream(socket.getInputStream());
            
            while(!this.rotasAExecutar.isEmpty()){
                System.out.println("Leitura da Companhia");
                //não esta passando daqui
                // Recebe se o carro andou 1km - caso true realizar pagamento
                //String quemRecebe = ;
                //System.out.println(quemRecebe);
                    realizarPagamento(socket,3.25,lerQuemRecebe(in.readUTF()));
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                
            }
        }catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String lerQuemRecebe(String qJson){
        JSONObject json = new JSONObject(qJson);
        String quemR = json.getString("quemR");
        return quemR;
    }

    public void realizarPagamento(Socket socket, double valor,String quemRecebe){
        System.out.println("Recebe "+ quemRecebe);
        Pagamento p = new Pagamento("Eltman",quemRecebe,"senha",valor, socket);
        p.start();
    }

    public void definirSumo(){
        String sumo_bin = "sumo"; //Caso quiser abrir a tela do simulador trocar por "sumo-gui"
		String config_file = "map/map.sumo.cfg";
		
		// Sumo connection
		this.sumo = new SumoTraciConnection(sumo_bin, config_file);
		sumo.addOption("start", "1");  
		sumo.addOption("quit-on-end", "1"); 
    }
    
    public void conectarSumo(){
        try {
            sumo.runServer(12345);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void criarDriversECarros2(){
        carro = new Car(true,"Carro Principal", sumo,
        1000,3,4,2);
        carros.add(carro);
        driver = new Driver(carro, rotasAExecutar.get(0),
         "Motorista Principal", this,54321,"localhost");
        drivers.add(driver);
    }
    /*
    public void criarDriversECarros(){
        
        //Criar 100 carros e 100 Motoristas e distribuir as rotas a serem executadas por cada motorista
        int num = 0;
        for(int i=1;i<=100;i++){
            List<Rota> aux = new ArrayList<>();
            carro = new Car(true,"Carro "+i,sumo,500,3,4,2);
            carros.add(carro);
            for(int j=0;j<9;j++){
                aux.add(rotasAExecutar.get(num));
                num++;
            }
            driver = new Driver(carro, aux,"Motorista "+i,this,54321,"localhost");
            drivers.add(driver);
            aux = null;
        }
    } */

    public synchronized void executarProximaRotaC(){
        Rota r = new Rota(rotasAExecutar.get(0).getId(),rotasAExecutar.get(0).getRota());
        rotasEmExecução.add(r);
        rotasAExecutar.remove(0);
    }

    public synchronized void finalizarRotaC(){
        Rota r = new Rota(rotasEmExecução.get(0).getId(), rotasEmExecução.get(0).getRota());
        rotasExecutadas.add(r);
        rotasEmExecução.remove(0);
    }

    public List<Rota> getRotasAExecutar(){
        return rotasAExecutar;
    }

    public List<Driver> getDrivers(){
        return drivers;
    }
    public SumoTraciConnection getSumo(){
        return sumo;
    }




    // *************** Métodos para testes ************************* 


/*
    public void imprimirRotasDosDrivers(){
        for(Driver d: drivers){
            System.out.println("Rotas a serem executadas pelo "+d.getIdDriver());
            for(Rota r: d.getRotasAExecutar())    
            System.out.println("Rota "+r.getRota()[0]);
            System.out.println();
        }
    }

    public void testarFinalizarRota(){
        testarExecutarRota();
        finalizarRota();
        finalizarRota();

        for(Rota rota : rotasExecutadas){
            System.out.println("Rota executada: "+rota.getId());
        }
        System.out.println(rotasAExecutar.size());
        System.out.println(rotasEmExecução.size());
        System.out.println(rotasExecutadas.size());        
    }

    public void testarExecutarRota(){
        System.out.println("Rota a ser executada: "+rotasAExecutar.get(0).getId());
        executarProximaRota();
        System.out.println("Rota a ser executada: "+rotasAExecutar.get(0).getId());
        executarProximaRota();
        //System.out.println(rotasAExecutar.get(0).getId());

        for(Rota r : rotasEmExecução){
            System.out.println("Rota em execução: "+r.getId());
        }
    }

    public void imprimirRotas(){
        for(Rota r:rotasAExecutar){
            //r.getRota()[0] = rota
            System.out.println(""+r.getRota()[0]);
        }
    } */
}
