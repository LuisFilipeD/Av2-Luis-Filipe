package AvMain3;

public class ContaCorrente extends Thread {

    private String idConta;
    private String senha;
    private double saldo;
 
    public ContaCorrente(String idConta, double saldo){
        this.idConta = idConta;
        this.senha = "senha";
        this.saldo = saldo;
    }

    @Override
    public void run(){

    }

    public void receber(double valor){
        this.saldo+=valor;
    }

    public boolean pagar(double valor){
        if(valor>this.saldo){
            System.out.println("Sem saldo suficiente");
            return false;
        }
        this.saldo-=valor;
        return true;
    }
    public double getSaldo(){
        return saldo;
    }
    public String getIdConta(){
        return idConta;
    }
    public String getSenha(){
        return senha;
    }

}