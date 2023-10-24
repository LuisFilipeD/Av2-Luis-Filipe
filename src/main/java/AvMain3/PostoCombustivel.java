package AvMain3;

public class PostoCombustivel extends Thread {

    private ContaCorrente conta;

    public PostoCombustivel(){
        conta= new ContaCorrente("ContaPosto", 5000);
        BancoSv.adicionarConta(conta);
    }

    @Override
    public void run(){
        while (true){
            
        }
    }

    public static void abastecerCarro(Car carro){
        try{
            System.out.println(carro.getId()+" Esta abastecendo...");
            Thread.sleep(1000);

            carro.abastecerCarro();

        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}