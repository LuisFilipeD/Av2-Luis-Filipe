package AvMain3;

import lombok.var;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;



public class CriarArquivo {

    public void criarArquivo(String nomeArquivo, ArrayList<DadosConducao> dados) {
      //System.out.println("Gerando Arquivo");


        try (var workbook = new XSSFWorkbook();
             var outputStream = new FileOutputStream(nomeArquivo)) {
            var planilha = workbook.createSheet("Lista de Clientes");
            int numeroDaLinha = 0;

            adicionarCabecalho(planilha, numeroDaLinha++);

            for (DadosConducao dado : dados) {
                var linha = planilha.createRow(numeroDaLinha++);
                adicionarCelula(linha, 0, dado.getTimeStamp());
                adicionarCelula(linha, 1, dado.getIdCar());
                adicionarCelula(linha, 2, dado.getRouteIDSUMO());
                adicionarCelula(linha, 3, dado.getSpeed());
                adicionarCelula(linha, 4, dado.getDistanciaP());
                adicionarCelula(linha, 5, dado.getFuelConsumption());
                adicionarCelula(linha, 6, dado.getCoord()[0]);
                adicionarCelula(linha, 7, dado.getCoord()[1]);
            }

            workbook.write(outputStream);
        } catch (FileNotFoundException e) {
            //log.error("Arquivo não encontrado: {}", nomeArquivo);
        } catch (IOException e) {
            //log.error("Erro ao processar o arquivo: {} ", nomeArquivo);
        }
        //log.info("Arquivo gerado com sucesso!");
    }

    private void adicionarCabecalho(XSSFSheet planilha, int numeroLinha) {
        var linha = planilha.createRow(numeroLinha);
        adicionarCelula(linha, 0, "Timestamp");
        adicionarCelula(linha, 1, "Id Car");
        adicionarCelula(linha, 2, "Id Route");
        adicionarCelula(linha, 3, "Velocidade");
        adicionarCelula(linha, 4, "Distancia");
        adicionarCelula(linha, 5, "FuelConsumption");
        adicionarCelula(linha, 6, "longitude(lon)");
        adicionarCelula(linha, 7, "latitude(lat)");
    }

    private void adicionarCelula(Row linha, int coluna, String valor) {
        Cell cell = linha.createCell(coluna);
        cell.setCellValue(valor);
    }

    private void adicionarCelula(Row linha, int coluna, double valor) {
        Cell cell = linha.createCell(coluna);
        cell.setCellValue(valor);
    }
    
}
