package AvMain3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AdquirirRotas {
    
    // Método para importar os dados do arquivo XML e adicionar as rotas e o Id em um ArrayList 
    public static List<Rota> importarRotas(String urlXML){
        List<Rota> rotas = new ArrayList<>();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(urlXML);

            NodeList vehicleList = doc.getElementsByTagName("vehicle");

            for (int i = 0; i < vehicleList.getLength(); i++) {
                Node vehicleNode = vehicleList.item(i);

                if (vehicleNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element vehicleElement = (Element) vehicleNode;
                    String idRoute = vehicleElement.getAttribute("id");

                    Node routeNode = vehicleElement.getElementsByTagName("route").item(0);
                    Element edges = (Element) routeNode;
                    String[] itinerary = {edges.getAttribute("edges") };

                    Rota route = new Rota(idRoute, itinerary);
                    rotas.add(route);
                }
            }

        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        return rotas;
    }
}

