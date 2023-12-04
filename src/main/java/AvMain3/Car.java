package AvMain3;

import java.util.ArrayList;

import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoColor;
import de.tudresden.sumo.objects.SumoPosition2D;
import it.polito.appeal.traci.SumoTraciConnection;


public class Car extends Vehicle implements Runnable {
    private static int num = 0;
    private boolean on_off;
    private String idCar;
    private SumoColor colorCar;
    private SumoTraciConnection sumo;
    private long taxaDeAquisicao;
    private double precoCombustivel;
    private double fuelTank;
    private double lastKm;
    private double last1Km;
    private int capacidadeDePessoas;
    private int numeroDePessoas;
    private ArrayList <DadosConducao> dadosconducao;
    private boolean precisaAbastecer;
    private boolean jaAbasteceu;
    private boolean andou1Km;
    //Av 2
    private ArrayList <Float> deslocamento; // Vetor dos Deslocamentos
    private ArrayList <Integer> restricao;  // Vetor das Restrições
    private double vel;             // Velocidade do carro (Variável)
    private double tempoTotal;      // Tempo total para o carro percorrer 
    private double tempoInicial;    // Tempo inicial entre cada Medidor
    private double tempoInicial2;   // Tempo inicial do carro
    private double tempoInicial3;   // Tempo inicial da Thread
    private double ultimoM;         // Valor onde reconciliou pela ultima vez
    private int numMedidores;       // Número Medidores
    private int distanciaTotal;     // Distancia que será percorrida
    private int distanciaEntreM;    // Distancia entre os Medidores


    public Car (boolean on_off, String idCar, SumoTraciConnection sumo, long taxaDeAquisicao, double precoCombustivel,
    int capacidadeDePessoas, int numeroDePessoas){
    this.on_off = on_off;
    this.idCar = idCar;
    this.colorCar = new SumoColor(0, 255, 0, 126);
    this.sumo = sumo;
    this.taxaDeAquisicao = taxaDeAquisicao;
    this.precoCombustivel = precoCombustivel;
    this.fuelTank = 10.0;
    this.lastKm=0.0;
    this.precisaAbastecer=false;
    this.capacidadeDePessoas = capacidadeDePessoas;
    this.numeroDePessoas = numeroDePessoas;
    this.dadosconducao = new ArrayList<>();
    this.jaAbasteceu = false;
    this.andou1Km = false;
    this.last1Km=0.0;
    //Av 2
    this.tempoInicial2 = 0.0;
    this.numMedidores=10;
    this.distanciaTotal = 5000000; //mm
    this.tempoTotal=400000; //ms
    this.ultimoM = 0.0;
    this.distanciaEntreM = (distanciaTotal/1000)/numMedidores;
    this.vel = definirVel(35); // 14 m/s
    deslocamento = new ArrayList<>();
    restricao = new ArrayList<>();
    }

    @Override
    public void run (){
        inicio1(); 
        inicio3(); 
        while(this.on_off){
            try {
                Thread.sleep(taxaDeAquisicao); // 1s
                this.Atualiza();            
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void Atualiza(){
            try{
                if(!this.getSumo().isClosed()){
                    SumoPosition2D sumoPosition2D;
                    sumoPosition2D = (SumoPosition2D) sumo.do_job_get(Vehicle.getPosition(this.idCar));
                    double distanciaP = (double) sumo.do_job_get(Vehicle.getDistance(this.idCar))*10;
                    System.out.println("Distancia Percorrida: "+distanciaP+" m");
                    
                    
                    DadosConducao reportes = new DadosConducao(
                        

                    this.idCar, System.currentTimeMillis(), sumoPosition2D.x, sumoPosition2D.y,
                    (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idCar)),
                    (String) this.sumo.do_job_get(Vehicle.getRouteID(this.idCar)),
                    (double) sumo.do_job_get(Vehicle.getSpeed(this.idCar)),
                    (double) sumo.do_job_get(Vehicle.getDistance(this.idCar)),
                    (double) sumo.do_job_get(Vehicle.getFuelConsumption(this.idCar)),1,
                    this.precoCombustivel,
                    (double) sumo.do_job_get(Vehicle.getCO2Emission(this.idCar)),
                    (double) sumo.do_job_get(Vehicle.getHCEmission(this.idCar)),
                    this.capacidadeDePessoas,
                    this.numeroDePessoas,distanciaP, calcularLatLong(sumoPosition2D)
                    );

                    this.dadosconducao.add(reportes);


                    if(logicaAndouParaReconcilar()){
                        reconciliar();
                    }

                    //Onde o carro de fato começa a andar
                    if(tempoInicial2 == 0.0){
                        inicio2();
                    }
                    
                    sumo.do_job_set(Vehicle.setSpeedMode(this.idCar, 0));
                    sumo.do_job_set(Vehicle.setSpeed(this.idCar, vel)); // m/s
                    
                    gastarGasolina();
                    logicaAndou1Km();

                    criarArquivo();

                    // Lógica para abastecer carro.
                    if(this.fuelTank<=3){
                        this.precisaAbastecer= true;
                        pararCarro();
                        while(precisaAbastecer){
                            if(num<2){
                                num++;
                                this.precisaAbastecer = false;
                                PostoCombustivel.abastecerCarro(this);
                                this.jaAbasteceu = true;
                                //System.out.println(this.idCar+ " Voltou pra pista!! ");
                                num--;
                            }else{
                            }
                            this.jaAbasteceu=false;
                        }
                    } 
                    if(distanciaP > (distanciaTotal/1000)){
                        System.out.println("Percurso finalizado! ");
                        registrosT((long) tempoInicial2, System.currentTimeMillis());
                        System.exit(0);
                    }
                }   
            }
            catch(Exception e) {
                e.printStackTrace(); 
        }
    }

    public void logicaAndou1Km(){
        //double km = this.dadosconducao.get(this.dadosconducao.size()-1).getOdometer(); //Antigo
        try {
            double km = (double) sumo.do_job_get(Vehicle.getDistance(this.idCar))*10;
             // a cada 100m (Mudar para 1km) altera andou1Km pra true
            if(Math.abs(km-this.last1Km)>=1000.0){
            this.andou1Km = true;
            this.last1Km = km;
        }else{
            this.andou1Km = false;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void pararCarro(){
        try {
            sumo.do_job_set(Vehicle.setSpeedMode(this.idCar, 0));
            sumo.do_job_set(Vehicle.setSpeed(this.idCar, 0.0));
            System.out.println(this.idCar+" parou para abastecer ...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void gastarGasolina(){
        //double km = this.dadosconducao.get(this.dadosconducao.size() - 1).getOdometer();
        double gasto = this.dadosconducao.get(this.dadosconducao.size() - 1).getFuelConsumption();
        //sempre que variar 10m gasta gasolina
        double km;
        try {
            km = (double) sumo.do_job_get(Vehicle.getDistance(this.idCar))*10;
            if (Math.abs(km - this.lastKm) >= 100.0) // a cada 100m percorridos gasta gasolina
        {
            this.fuelTank -= (gasto / 1000);
            this.lastKm = km;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
       // System.out.println("Combustivel "+this.idCar+" = " + this.fuelTank);
       // System.out.println(gasto);
    }
    public void abastecerCarro(){
        double gas=10-this.fuelTank;
        fuelTank+=gas;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    //Av 2
    private double definirVel(double t) {
        this.vel =(distanciaEntreM)/t;
        System.out.println("Velocidade atual: "+vel);
        return vel;
    }

    public void inicio1(){
        tempoInicial = System.currentTimeMillis();        
        deslocamento.add((float) (tempoTotal));
        restricao.add(1);
        for(int i=0;i<numMedidores;i++){
            deslocamento.add((float) (tempoTotal/numMedidores));
            restricao.add(-1);
        }
    }
    public void inicio2(){
        tempoInicial2 = System.currentTimeMillis();
    }
        public void inicio3(){
        tempoInicial3 = System.currentTimeMillis();
    }
    
    public boolean logicaAndouParaReconcilar(){
        try {
            double m = (double) sumo.do_job_get(Vehicle.getDistance(this.idCar))*10;
            if(Math.abs(m-this.ultimoM)>=((distanciaTotal/1000)/numMedidores)){
            this.ultimoM = m;
            return true;
        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private void reconciliar() {
        double tempo0 = deslocamento.get(0)- deslocamento.get(1); //500000-50000 = 450000

        deslocamento.set(1,(float) tempo0); // [500000, 450000, 50000, ...]
        deslocamento.remove(0); // [450000, 50000,50000, ...]
        restricao.remove(1);

        // Tempo gasto nesse Pedaço
        double difTempo = (System.currentTimeMillis() - tempoInicial)/1000; // em segundos
        System.out.println("Tempo gasto entre Medidores: "+difTempo+" s");

        // Velocidade média no trajeto
        double velMedia = distanciaEntreM/(difTempo); // em m/s        
        System.out.println("Velocidade media: "+velMedia+" m/s");

        // Distancia que deveria ter sido percorrida
        double distIdeal = vel*difTempo; // em m
        System.out.println("Distancia ideal: "+distIdeal+" m");

        // Distância que deveria ter sido percorrida em condições ideais
        double difDist = distIdeal - distanciaEntreM ; // em m
        System.out.println("Diferenca entra as distancias: "+difDist);

        // Tempo de atraso ou adiantamento
        double tempoV = (difDist/velMedia)*1000; //em ms
        System.out.println("Tempo Variado: "+tempoV);
        
        double novoTempo = deslocamento.get(1)-(tempoV); // Tempo do novo trajeto - o tempo que atrasou 
                                                               // Preciso fazer o proximo trajeto de forma que recupere o atraso ou adiantamento       
        //Reconciliar
        deslocamento.set(1,(float) novoTempo);

        double[]y = new double[deslocamento.size()];
        for(int i=0;i<deslocamento.size();i++){
            y[i] = deslocamento.get(i);
        }  

        double[]v = new double[deslocamento.size()];
        for(int i=0;i<deslocamento.size();i++){
            v[i] = 0.5;
        }

        double[]A = new double[deslocamento.size()];
        for(int i=0;i<deslocamento.size();i++){
            A[i] = restricao.get(i);
        }

        Reconciliation rec = new Reconciliation(y, v, A);

        double doub []= rec.getReconciledFlow();
        for(int i=0;i<deslocamento.size();i++){
            deslocamento.set(i,(float) doub[i]);
        } 
        //System.out.println(deslocamento.size());
        
        for(int i=0;i<deslocamento.size();i++){
            System.out.println(deslocamento.get(i));
        }  
        System.out.println("Novo tempo = "+deslocamento.get(1));
        
        tempoInicial = System.currentTimeMillis();    // Atualizando tempo de inicio
        definirVel(deslocamento.get(1)/1000);
    }

    public void criarArquivo(){
        CriarArquivo c = new CriarArquivo();
        c.criarArquivo("dadosCarro.xlsx",dadosconducao);
    }

    public double [] calcularLatLong(SumoPosition2D sumoPosition2D){
        double x = sumoPosition2D.x;
        double y = sumoPosition2D.y;
        double raio = 6371000;

        double latref = -22.9867;
        double longref = -43.2170;

        double lat = latref+(y/raio)*(180/Math.PI);
        double lon = longref + (x / raio) * (180 / Math.PI) / Math.cos(latref * Math.PI / 180);

        double coord[] = new double[] {lat,lon};
        return coord;
    }

    public void registrosT(long tInicial, long tFinal){
        double tPeriodo = (tFinal-tInicial)/1000; // em s
        double tCompleto = ((System.currentTimeMillis() - tempoInicial3))/1000; // em s

        System.out.println("T1" + " - Tempo de Inicio (Ji): " + tInicial);
        System.out.println("T1" + " - Tempo de Computacao (Ci): " + tFinal);
        System.out.println("T1" + " - Periodo da Tarefa (Pi): " + tPeriodo);
        System.out.println("T1" + " - Tempo de Atraso (Di): " + (tCompleto-tPeriodo));
    }

    public String getId(){
        return idCar;
    }
    public double getFuelTank() {
        return fuelTank;
    }

    public SumoTraciConnection getSumo() {
        return sumo;
    }

    public boolean isOn_off() {
        return on_off;
    }

    public SumoColor getColorCar() {
        return colorCar;
    }
    public int getCapacidadeDePessoas() {
        return capacidadeDePessoas;
    }
    public int getNumeroDePessoas() {
        return numeroDePessoas;
    }
    public long getTaxaDeAquisicao() {
        return taxaDeAquisicao;
    }
    public boolean getPrecisaAbastecer(){
        return precisaAbastecer;
    }
    public double getPrecoCombustivel(){
        return precoCombustivel;
    }
    public boolean getJaAbasteceu(){
        return jaAbasteceu;
    }
    public boolean getAndou1Km(){
        return andou1Km;
    }
    public void setAndou1Km(){
        andou1Km = false;
    }
    public double getDistanceP() throws Exception{
        return (double) sumo.do_job_get(Vehicle.getDistance(this.idCar))*10;
    }

}