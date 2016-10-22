package health;

import health.generated.ObjectFactory;
import health.generated.People;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;

public class Evaluator {

    private static final String INDENT = "  ";
    private static final Random RANDOM = new Random();

    public static void main(String[] args) {
        try {
            Document doc = getDocument("people.xml");
            XPath xpath = XPathFactory.newInstance().newXPath();

            System.out.println("People list:");
            printPeople(doc, xpath);

            System.out.println("\nHealth profile of person 0005:");
            printHealthProfile(doc, xpath, "0005");

            System.out.println("\nPeople with weight>90:");
            searchByWeight(doc, xpath, Operator.GREATER, 90d);

            String fileName = "generated_people";
            System.out.println("\nMarshalling random people to XML...");
            runXMLMarshalling(3, fileName + ".xml");

            System.out.println("\nUnmarshalling random people from XML...");
            runXMLUnmarshalling(fileName + ".xml");

            System.out.println("\nMarshalling random people to JSON...");
            runJSONMarshalling(3, fileName + ".json");
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static Document getDocument(String filename) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        return builder.parse(filename);
    }


    public static Double getWeight(Document doc, XPath xPath, String personId) throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/people/person[@id='" + personId + "']/healthprofile/weight");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        return Double.parseDouble(node.getTextContent());
    }

    public static Double getHeight(Document doc, XPath xPath, String personId) throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/people/person[@id='" + personId + "']/healthprofile/height");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        return Double.parseDouble(node.getTextContent());
    }

    public static void printPeople(Document doc, XPath xPath) throws XPathExpressionException {
        XPathExpression expr = xPath.compile("//person");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        printNodeInfo(nodeList);
    }

    public static void printHealthProfile(Document doc, XPath xPath, String personId) throws XPathExpressionException {
        XPathExpression expr = xPath.compile("/people/person[@id='" + personId + "']/healthprofile");
        Node node = (Node) expr.evaluate(doc, XPathConstants.NODE);
        System.out.println(node.getNodeName() + ": " + node.getTextContent());
    }

    public static void searchByWeight(Document doc, XPath xPath, Operator operator, double weight) throws XPathExpressionException {
        XPathExpression expr = xPath.compile("//healthprofile[weight" + operator.symbol() + weight + "]/parent::person");
        NodeList nodeList = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        printNodeInfo(nodeList);
    }

    private static void runXMLMarshalling(int peopleNumber, String outputFile) throws JAXBException, DatatypeConfigurationException {
        People people = generateRandomPeople(peopleNumber);
        Marshaller marshaller = JAXBContext.newInstance(People.class).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(people, new File(outputFile));
        marshaller.marshal(people, System.out);
    }

    private static void runJSONMarshalling(int peopleNumber, String outputFile) throws JAXBException, DatatypeConfigurationException, IOException {
        People people = generateRandomPeople(peopleNumber);
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JaxbAnnotationModule());
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        mapper.configure(SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS, true);

        String result = mapper.writeValueAsString(people);
        System.out.println(result);
        mapper.writeValue(new File(outputFile), people);
    }

    private static People generateRandomPeople(int peopleNumber) throws JAXBException, DatatypeConfigurationException {
        ObjectFactory factory = new ObjectFactory();
        People people = factory.createPeople();
        for (int i = 0; i < peopleNumber; i++) {
            People.Person newPerson = factory.createPeoplePerson();
            newPerson.setId("000" + i);
            newPerson.setFirstname("Random_firstname_" + i);
            newPerson.setLastname("Random_lastname_" + i);
            GregorianCalendar c = new GregorianCalendar();
            c.setTime(new Date());
            XMLGregorianCalendar gregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
            newPerson.setBirthdate(gregorianCalendar);
            People.Person.Healthprofile newHealthProfile = factory.createPeoplePersonHealthprofile();
            newHealthProfile.setLastupdate(gregorianCalendar);
            newHealthProfile.setWeight(60 + RANDOM.nextInt(20));
            newHealthProfile.setHeight(1.60 + RANDOM.nextInt(50)/100);
            newHealthProfile.setBmi(20 + RANDOM.nextInt(15));
            newPerson.setHealthprofile(newHealthProfile);
            people.getPerson().add(newPerson);
        }
        return people;
    }

    private static void runXMLUnmarshalling(String inputFile) throws JAXBException, FileNotFoundException {
        JAXBContext jaxbContext = JAXBContext.newInstance(People.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        People people = (People) unmarshaller.unmarshal(new FileReader(inputFile));
        List<People.Person> personList = people.getPerson();
        for (People.Person p : personList) {
            System.out.println("Id: " + p.getId() +
                    "\nFirst name: " + p.getFirstname() +
                    "\nLast name: " + p.getLastname() +
                    "\nBirth date: " + p.getBirthdate() +
                    "\nHealth profile: " +
                    "\n" + INDENT + "Last update: " + p.getHealthprofile().getLastupdate() +
                    "\n" + INDENT + "Weight: " + p.getHealthprofile().getWeight() +
                    "\n" + INDENT + "Height: " + p.getHealthprofile().getHeight() +
                    "\n" + INDENT + "BMI: " + p.getHealthprofile().getBmi() + "\n");
        }
    }

    private static void printNodeInfo(NodeList nodeList) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList children = nodeList.item(i).getChildNodes();
            if (children.getLength() == 1) {
                System.out.println(nodeList.item(i).getNodeName() + ": " + nodeList.item(i).getTextContent());
            } else {
                printNodeInfoHelper(children, INDENT);
            }
        }
    }

    private static void printNodeInfoHelper(NodeList nodeList, String indent) {
        for (int i = 0; i < nodeList.getLength(); i++) {
            NodeList children = nodeList.item(i).getChildNodes();
            if (children.getLength() == 1) {
                System.out.println(indent + nodeList.item(i).getNodeName() + ": " + nodeList.item(i).getTextContent());
            } else {
                printNodeInfoHelper(children, indent + INDENT);
            }
        }
    }
}
