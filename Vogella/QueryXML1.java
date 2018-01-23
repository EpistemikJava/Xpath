//package myxml;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class QueryXML1
{
  public void query() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
    // standard for reading an XML file
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    
    DocumentBuilder builder = factory.newDocumentBuilder();
    Document doc = builder.parse("xml/people1.xml");
    System.out.println("Document URI: " + doc.getDocumentURI());

    // create an XPathFactory
    XPathFactory xFactory = XPathFactory.newInstance();

    // create an XPath object
    XPath xpath = xFactory.newXPath();

    // compile the XPath expression
    XPathExpression expr = xpath.compile("//person[firstname='Lars']/lastname/text()");
    
    // run the query and get a nodeset
    Object result = expr.evaluate(doc, XPathConstants.NODESET);

    // cast the result to a DOM NodeList
    NodeList nodes = (NodeList) result;
    
    // print out the value of each node (last names of all people with first name Lars)
    System.out.println("Last names of people with first name Lars:");
    for (int i=0; i<nodes.getLength(); i++) {
      System.out.println("\t" + nodes.item(i).getNodeValue());
    }

    // new XPath expression to get the number of people with name Lars
    expr = xpath.compile("count(//person[firstname='Lars'])");
    // run the query and get the number of nodes
    Double number = (Double) expr.evaluate(doc, XPathConstants.NUMBER);
    System.out.println("\nNumber of nodes = " + number);

    // do we have more than 2 people with name Lars?
    expr = xpath.compile("count(//person[firstname='Lars']) >2");
    // run the query and get the answer as a Boolean
    Boolean check = (Boolean) expr.evaluate(doc, XPathConstants.BOOLEAN);
    System.out.println("\nAre there more than 2 people with first name Lars?\nAnswer: " + check);
  }

  // MAIN
  public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
    QueryXML1 process = new QueryXML1();
    process.query();
  }
  
}// class QueryXML1
