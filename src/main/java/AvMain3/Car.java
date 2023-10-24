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
    }

    @Override
    public void run (){

        while(this.on_off){
            try {
                Thread.sleep(3*taxaDeAquisicao);
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

                    //System.out.println("CarroID: " + this.getId());
                    //System.out.println("RoadID: " + (String) this.sumo.do_job_get(Vehicle.getRoadID(this.idCar)));
                    //System.out.println("RouteID: " + (String) this.sumo.do_job_get(Vehicle.getRouteID(this.idCar)));
                    //System.out.println("RouteIndex: " + this.sumo.do_job_get(Vehicle.getRouteIndex(this.idCar)));

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
                    this.numeroDePessoas
                    );

                    this.dadosconducao.add(reportes);
                    //System.out.println("speed = " + this.dadosconducao.get(this.dadosconducao.size() - 1).getSpeed());
                    //System.out.println(this.idCar+" Andou = " + this.dadosconducao.get(this.dadosconducao.size() - 1).getOdometer());
                    //System.out.println("Fuel Consumption = "
                           // + this.dadosconducao.get(this.dadosconducao.size() - 1).getFuelConsumption());

                    //System.out.println(
                           // "CO2 Emission = " + this.dadosconducao.get(this.dadosconducao.size() - 1).getCo2Emission());


                    sumo.do_job_set(Vehicle.setSpeedMode(this.idCar, 0));
                    sumo.do_job_set(Vehicle.setSpeed(this.idCar, 5.7));
                    
                    
                    //System.out.println("getPersonNumber = " + sumo.do_job_get(Vehicle.getPersonNumber(this.idCar)));
                    
                    
                    //System.out.println("************************");
                    gastarGasolina();
                    logicaAndou1Km();

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

                }
                  
            }
            catch(Exception e) {
                e.printStackTrace(); 
        }
    }

    public void logicaAndou1Km(){

        double km = this.dadosconducao.get(this.dadosconducao.size()-1).getOdometer();

        // a cada 100m (Mudar para 1km) altera andou1Km pra true
        if(Math.abs(km-this.last1Km)>=100.0){
            this.andou1Km = true;
            this.last1Km = km;
        }else{
            this.andou1Km = false;
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
        double km = this.dadosconducao.get(this.dadosconducao.size() - 1).getOdometer();
        double gasto = this.dadosconducao.get(this.dadosconducao.size() - 1).getFuelConsumption();
        //sempre que variar 10m gasta gasolina
        if (Math.abs(km - this.lastKm) >= 10.0) // a cada 10m percorridos gasta gasolina
        {
            this.fuelTank -= (gasto / 1000);
            this.lastKm = km;
        }
        //System.out.println("Combustivel "+this.idCar+" = " + this.fuelTank);
    }
    public void abastecerCarro(){
        double gas=10-this.fuelTank;
        fuelTank+=gas;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
}