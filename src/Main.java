import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Cliente> clienteList = initializeClientes();
        serializeClientes(clienteList);
        List<Cliente> deserializedClientes = deserializeClientes();
        createXML(deserializedClientes);
        readXML();
    }

    private static List<Cliente> initializeClientes() {
        List<Cliente> clienteList = new ArrayList<>();
        clienteList.add(new Cliente("Paco", 30));
        clienteList.add(new Cliente("Chaval", 25));
        clienteList.add(new Cliente("Destroyer", 40));
        clienteList.add(new Cliente("Manolo", 50));
        clienteList.add(new Cliente("Tin", 35));
        return clienteList;
    }

    private static void serializeClientes(List<Cliente> clienteList) {
        try (FileOutputStream fos = new FileOutputStream("clientes.dat");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(clienteList);
            System.out.println("Serialización fue bien. Escrito en clientes.dat");
        } catch (Exception e) {
            System.out.println("Error en Serialización: " + e.getMessage());
        }
    }

    private static List<Cliente> deserializeClientes() {
        List<Cliente> deserializedClientes = null;
        try (FileInputStream fis = new FileInputStream("clientes.dat");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            deserializedClientes = (List<Cliente>) ois.readObject();
            System.out.println("Deserialización fue bien");
        } catch (Exception e) {
            System.out.println("Error en Deserialización: " + e.getMessage());
        }
        return deserializedClientes;
    }

    private static void createXML(List<Cliente> deserializedClientes) {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.newDocument();
            Element rootElement = doc.createElement("clientes");
            doc.appendChild(rootElement);
            for (Cliente cliente : deserializedClientes) {
                Element clienteElement = doc.createElement("cliente");
                Element nombre = doc.createElement("nombre");
                nombre.appendChild(doc.createTextNode(cliente.getNombre()));
                clienteElement.appendChild(nombre);
                Element edad = doc.createElement("edad");
                edad.appendChild(doc.createTextNode(String.valueOf(cliente.getEdad())));
                clienteElement.appendChild(edad);
                rootElement.appendChild(clienteElement);
            }
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("clientes.xml"));
            transformer.transform(source, result);
            System.out.println("XML creado: clientes.xml");
        } catch (Exception e) {
            System.out.println("Error en crear XML: " + e.getMessage());
        }
    }

    private static void readXML() {
        try {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File("clientes.xml"));
            doc.getDocumentElement().normalize();
            NodeList clienteNodes = doc.getElementsByTagName("cliente");
            System.out.println("Leyendo desde XML:");
            for (int i = 0; i < clienteNodes.getLength(); i++) {
                Node node = clienteNodes.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String nombre = element.getElementsByTagName("nombre").item(0).getTextContent();
                    int edad = Integer.parseInt(element.getElementsByTagName("edad").item(0).getTextContent());
                    System.out.println("Nombre de Cliente: " + nombre + ", Edad: " + edad);
                }
            }
        } catch (Exception e) {
            System.out.println("Error en leer XML: " + e.getMessage());
        }
    }
}

class Cliente implements Serializable {
    private String nombre;
    private int edad;

    public Cliente(String nombre, int edad) {
        this.nombre = nombre;
        this.edad = edad;
    }

    public String getNombre() {
        return nombre;
    }

    public int getEdad() {
        return edad;
    }
}
