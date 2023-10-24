package AvMain3;

import java.util.List;
import de.tudresden.sumo.cmd.Route;
import de.tudresden.sumo.cmd.Vehicle;
import de.tudresden.sumo.objects.SumoStringList;
import it.polito.appeal.traci.SumoTraciConnection;

public class ServicoT2 extends Thread{
	private SumoTraciConnection sumo;
	private Car carro;
	private List<Rota> rotas;

    public ServicoT2 (SumoTraciConnection sumo, Car carro, List<Rota> rotas){
        this.sumo = sumo;
        this.carro = carro;
		this.rotas=rotas;
    }

	@Override
    public void run() {
		try{
            this.executaRota(this.carro);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void executaRota(Car carro) {
        try {
            //Definindo variáveis úteis
            Rota rota = this.obterProximaRota();
            String idRota = rota.getId();
            String vehicleId = this.carro.getId();
           //System.out.println(this.carro.getId() + " percorrendo a rota " + idRota);
            boolean on = true;
    
            // Adicionando as rotas em edgeList
            SumoStringList edgeList = obterEdges(carro,rota);
            //System.out.println(edgeList);

            // Definir a rota
            sumo.do_job_set(Route.add(idRota, edgeList));
			//Definindo as informações do veículo
            sumo.do_job_set(Vehicle.addFull(vehicleId,                              //vehID
                                                idRota, 							//routeID 
                                                "DEFAULT_VEHTYPE", 					//typeID 
                                                "now", 								//depart  
                                                "0", 								//departLane 
                                                "0", 								//departPos 
                                                "0",								//departSpeed
                                                "current",							//arrivalLane 
                                                "max",								//arrivalPos 
                                                "current",							//arrivalSpeed 
                                                "",									//fromTaz 
                                                "",									//toTaz 
                                                "", 								//line 
                                                this.carro.getCapacidadeDePessoas(),		//personCapacity 
                                                this.carro.getNumeroDePessoas())		//personNumber
                        );
        
            while(!rotas.isEmpty()){
                while(on){
                if(!rotaConcluida(rota)){
                    if(!carro.getPrecisaAbastecer()){
                        try{
                            this.sumo.do_timestep();
                            Thread.sleep(this.carro.getTaxaDeAquisicao());
                            //System.out.println(!rotas.isEmpty()+" Faltam "+rotas.size()+" rotas");
                            //System.out.println("Ultima edge "+getUltimaEdge(rota));
                            //System.out.println("Edge do "+this.carro.getId()+" "+(String) this.sumo.do_job_get(Vehicle.getRoadID(this.carro.getId())));
                        }catch(Exception e){
                        }
                        }
                }else{
                    on = false;
                }
            }
            for(int i=0;i<3;i++){
                this.sumo.do_timestep();
                Thread.sleep(this.carro.getTaxaDeAquisicao());
                //System.out.println(!rotas.isEmpty()+" Faltam "+rotas.size()+" rotas");
                //System.out.println("Ultima edge "+getUltimaEdge(rota));
                //System.out.println("Edge depois do "+carro.getId()+" "+(String) this.sumo.do_job_get(Vehicle.getRoadID(carro.getId())));
            }
            System.out.println(this.carro.getId()+" Terminou a rota " + rota.getId());
            Rota novaR = obterProximaRota();
            SumoStringList proximosEdges = obterEdges(this.carro, novaR);
            System.out.println(this.carro.getId() + " percorrendo a rota " + novaR.getId());
            sumo.do_job_set(Vehicle.setRoute(vehicleId, proximosEdges));
            on = true;  
            System.out.println(this.carro.getId() + " percorrendo a rota " + novaR.getId());

        }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public SumoStringList obterEdges (Car carro,Rota proximaRota){       
        SumoStringList edgeList = new SumoStringList();
        edgeList.clear();
        String[]aux = proximaRota.getRota();
        //System.out.println("Tamanho de edges"+aux.length);

        for (String edge : aux[0].split(" ")) {
             edgeList.add(edge);
         }
        return edgeList;
    }
    // Obter Borda 
    public String getUltimaEdge(Rota r){
        String[] edges = r.getRota();
        String[] ultimaEdge = edges[0].split(" ");
        return ultimaEdge[ultimaEdge.length-1];
    }

    public Rota obterProximaRota() {
        Rota proximaRota;
        proximaRota=rotas.remove(0);
        return proximaRota;
    }
	
	public boolean rotaConcluida(Rota rota) {
		try {
            if(((String) this.sumo.do_job_get(Vehicle.getRoadID(this.carro.getId()))).equals(this.getUltimaEdge(rota))){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
	}

    /*
	private boolean veiculoExiste(String vehicleId) {
		try {
			// Tente obter informações sobre o veículo
			sumo.do_job_get(Vehicle.getTypeID(vehicleId));
			return true;
		} catch (Exception e) {
			return false;
		}
	}  */
	
}