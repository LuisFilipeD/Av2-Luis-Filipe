package AvMain3;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

import java.net.Socket;


public class BancoSv extends Thread{

    private static List<ContaCorrente> contas;
    private static List<DadosTransf> dados;
    private ServerSocket sv2;
    

    public BancoSv(ServerSocket sv2){
        this.sv2 = sv2;
        contas = new ArrayList<>();
        dados = new ArrayList<>();
        System.out.println("Banco iniciado");
    }

    @Override
    public void run(){
        try{
            while(true){

                // Espera conexão do cliente
                Socket portaCliente = sv2.accept();
                
                ComunicacaoEntreContas comunicacao = new ComunicacaoEntreContas(portaCliente, this);
                comunicacao.start();
            }
        }catch(IOException e) {
            e.printStackTrace();
        }
    }
    public boolean verificarLogin(String[] login){
        for(ContaCorrente c : contas){
            if(login[0].equals(c.getIdConta())){
                if(login[1].equals(c.getSenha())){
                    return true;
                }
            }
        }return false;
    }

    public static void adicionarConta(ContaCorrente c){
        contas.add(c);
    }
    public void adicionarDados(DadosTransf d){
        dados.add(d);
    }

    public synchronized void transferencia(String idPaga, String idRecebe, double valor){
        ContaCorrente quemPaga = buscarConta(idPaga);
        ContaCorrente quemRecebe = buscarConta(idRecebe);
        if(quemPaga.pagar(valor)){
            quemRecebe.receber(valor);
            System.out.println("Operacao realizada!!  "+idPaga+" pagou R$"+valor+" para "+idRecebe);
        }else{
            System.out.println("Transação não realizada");
        }
    }

    public ContaCorrente buscarConta(String idConta){
        for(ContaCorrente c : contas){
            if(idConta.equals(c.getIdConta())){
                return c;
            }
        }
        System.out.println("Conta nao Encontrada");
        return null;
    }
}