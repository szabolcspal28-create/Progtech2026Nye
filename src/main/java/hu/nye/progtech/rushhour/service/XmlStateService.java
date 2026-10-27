package hu.nye.progtech.rushhour.service;

import hu.nye.progtech.rushhour.model.Board;
import hu.nye.progtech.rushhour.model.Orientation;
import hu.nye.progtech.rushhour.model.Position;
import hu.nye.progtech.rushhour.model.Vehicle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class XmlStateService {
    private static final Logger logger = LoggerFactory.getLogger(XmlStateService.class);

    public void saveGame(Board board, String filePath) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            Element rootElement = doc.createElement("GameSave");
            doc.appendChild(rootElement);

            Element rowsElement = doc.createElement("Rows");
            rowsElement.setTextContent(String.valueOf(board.getRows()));
            rootElement.appendChild(rowsElement);

            Element colsElement = doc.createElement("Cols");
            colsElement.setTextContent(String.valueOf(board.getCols()));
            rootElement.appendChild(colsElement);

            Element vehiclesElement = doc.createElement("Vehicles");
            rootElement.appendChild(vehiclesElement);

            for (Vehicle v : board.getVehicles()) {
                Element vehicleElement = doc.createElement("Vehicle");
                vehicleElement.setAttribute("id", String.valueOf(v.getId()));
                vehicleElement.setAttribute("row", String.valueOf(v.getPosition().getRow()));
                vehicleElement.setAttribute("col", String.valueOf(v.getPosition().getCol()));
                vehicleElement.setAttribute("orientation", v.getOrientation().name());
                vehicleElement.setAttribute("length", String.valueOf(v.getLength()));
                vehiclesElement.appendChild(vehicleElement);
            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(filePath));

            transformer.transform(source, result);
            logger.info("Játékállás sikeresen kimentve az XML fájlba: {}", filePath);

        } catch (Exception e) {
            logger.error("Hiba az XML mentésekor", e);
        }
    }

    public Board loadGame(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                logger.warn("A mentés fájl nem található: {}", filePath);
                return null;
            }

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(file);
            doc.getDocumentElement().normalize();

            int rows = Integer.parseInt(doc.getElementsByTagName("Rows").item(0).getTextContent());
            int cols = Integer.parseInt(doc.getElementsByTagName("Cols").item(0).getTextContent());

            List<Vehicle> vehicles = new ArrayList<>();
            NodeList nList = doc.getElementsByTagName("Vehicle");

            for (int i = 0; i < nList.getLength(); i++) {
                Element element = (Element) nList.item(i);
                char id = element.getAttribute("id").charAt(0);
                int row = Integer.parseInt(element.getAttribute("row"));
                int col = Integer.parseInt(element.getAttribute("col"));
                Orientation orientation = Orientation.valueOf(element.getAttribute("orientation"));
                int length = Integer.parseInt(element.getAttribute("length"));

                vehicles.add(new Vehicle(id, new Position(row, col), orientation, length));
            }

            logger.info("Játékállás sikeresen betöltve az XML fájlból: {}", filePath);
            return new Board.Builder()
                    .withRows(rows)
                    .withCols(cols)
                    .withVehicles(vehicles)
                    .build();

        } catch (Exception e) {
            logger.error("Hiba az XML beolvasásakor", e);
            return null;
        }
    }
}