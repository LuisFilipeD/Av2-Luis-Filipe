package AvMain3;

public class DadosConducao {

    private String idCar;
	private String idDriver;
    private long timeStamp; 			// System.currentTimeMillis()
	private double x_Position; 			// sumoPosition2D (x)
	private double y_Position; 			// sumoPosition2D (y)
	private String roadIDSUMO; 			// this.sumo.do_job_get(Vehicle.getRoadID(this.idAuto))
	private String routeIDSUMO; 		// this.sumo.do_job_get(Vehicle.getRouteID(this.idAuto))
	private double speed; 				// in m/s for the last time step
	private double odometer; 			// the distance in (m) that was already driven by this vehicle.
	private double fuelConsumption; 	// in mg/s for the last time step
	private double fuelPrice; 			// price in liters
	private double averageFuelConsumption;
	private int capacidadeDePessoas;	// the total number of persons that can ride in this vehicle
	private int numeroDePessoas;		// the total number of persons which are riding in this vehicle
	private double co2Emission; 		// in mg/s for the last time step
	private double HCEmission; 			// in mg/s for the last time step
    //Av2
    private double distanciaP;
    private double[] coord;

    

    public DadosConducao(String idCar, long timeStamp, double x_Position, double y_Position,String roadIDSUMO, String routeIDSUMO, double speed, double odometer, double fuelConsumption,
    double averageFuelConsumption, double fuelPrice, double co2Emission, double HCEmission, int capacidadeDePessoas, int numeroDePessoas, double distanciaP, double[] coord){
        this.idCar = idCar;
		this.timeStamp = timeStamp;
		this.x_Position = x_Position;
		this.y_Position = y_Position;
		this.roadIDSUMO = roadIDSUMO;
		this.routeIDSUMO = routeIDSUMO;
		this.speed = speed;
		this.odometer = odometer;
		this.fuelConsumption = fuelConsumption;
		this.averageFuelConsumption = averageFuelConsumption;
		this.fuelPrice = fuelPrice;
		this.co2Emission = co2Emission;
		this.HCEmission = HCEmission;
		this.capacidadeDePessoas = capacidadeDePessoas;
		this.numeroDePessoas = numeroDePessoas;
        //Av2
        this.distanciaP = distanciaP;
        this.coord=coord;
    }

    public double[] getCoord(){
        return coord;
    }

    public String getIdCar() {
        return idCar;
    }
    public String getIdDriver() {
        return idDriver;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public double getX_Position() {
        return x_Position;
    }

    public double getY_Position() {
        return y_Position;
    }

    public String getRoadIDSUMO() {
        return roadIDSUMO;
    }

    public String getRouteIDSUMO() {
        return routeIDSUMO;
    }

    public double getSpeed() {
        return speed;
    }

    public double getOdometer() {
        return odometer;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public double getFuelPrice() {
        return fuelPrice;
    }

    public double getAverageFuelConsumption() {
        return averageFuelConsumption;
    }

    public int getCapacidadeDePessoas() {
        return capacidadeDePessoas;
    }

    public int getNumeroDePessoas() {
        return numeroDePessoas;
    }

    public double getCo2Emission() {
        return co2Emission;
    }

    public double getHCEmission() {
        return HCEmission;
    }
    //Av2 
    public double getDistanciaP() {
        return distanciaP;
    }
}
