
import java.io.IOException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import javax.xml.parsers.*;
import javax.xml.xpath.*;
            
public class XPathExample
{
    public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
    {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true); // never forget this!
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        Document doc = builder.parse("books.xml");
        
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        
        // get the text content of the 'title' elements
        XPathExpression expr = xpath.compile("//book[author='Neal Stephenson']/title/text()");
        // get the titles in an XPath Nodeset
        Object result = expr.evaluate(doc, XPathConstants.NODESET);
        // cast the result to a DOM NodeList
        NodeList nodes = (NodeList) result;
        
        // iterate through the Nodelist to find all the titles
        System.out.println("Books by Neal Stephenson:");
        for (int i = 0; i < nodes.getLength(); i++)
        {
          System.out.println("\t" + nodes.item(i).getNodeValue());
        }
    }
}

