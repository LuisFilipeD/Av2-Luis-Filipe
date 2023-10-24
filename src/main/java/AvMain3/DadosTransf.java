package AvMain3;

public class DadosTransf {
    
    private String quemPaga;
    private String quemRecebe;
    private String operacao;
    private double valor;

    public DadosTransf(String quemPaga, String quemRecebe, String operacao, double valor){
        this.quemPaga = quemPaga;
        this.quemRecebe = quemRecebe;
        this.operacao = operacao;
        this.valor = valor;
    }

    public String getQuemPaga(){
        return quemPaga;
    }

    public String geteQuemRecebe(){
        return quemRecebe;
    }

    public String getOperacao(){
        return operacao;
    }

    public double getValor(){
        return valor;
    }

    public String getDescricao() {
        String descricao = null;
        if (operacao.equals("Pagamento")) {
            descricao = "Operacao realizada!!  "+quemPaga+" pagou R$"+valor+" para "+quemRecebe;
        } else if (operacao.equals("Recebimento")) {
            descricao ="Operacao realizada!!  "+ quemRecebe + " recebeu R$" + valor + " de " + quemPaga;
        }
        return descricao;
    }
}