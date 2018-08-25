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
import javax.xml.namespace.NamespaceContext;
import javax.xml.XMLConstants;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;
import org.xml.sax.SAXException;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

public class QueryRL
{
  // MAIN
  public static void main(String[] args) throws XPathExpressionException, ParserConfigurationException, SAXException, IOException {
    QueryRL process = new QueryRL();
    process.query();
  }
  
  public void query() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
    // standard for reading an XML file
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    
    DocumentBuilder builder = factory.newDocumentBuilder();
    factory.setNamespaceAware(true);
    Document doc = builder.parse("xml/RL.xml");
    System.out.println("Document URI: " + doc.getDocumentURI());

    // create an XPathFactory
    XPathFactory xFactory = XPathFactory.newInstance();

    // create an XPath object
    XPath xpath = xFactory.newXPath();

    xpath.setNamespaceContext(new UniversalNamespaceCache(doc, true, false));

    // compile the XPath expression
    // get id values
//    XPathExpression exprId = xpath.compile("//gnc:transaction[trn:description='RNLK Investments']/trn:id/text()");
    // get note values
    XPathExpression exprNote = xpath.compile("//gnc:transaction[trn:description='RNLK Investments']/trn:slots/slot[slot:key='notes']/slot:value/text()");
    
    // run the query and get a nodeset
    Object result = exprNote.evaluate(doc, XPathConstants.NODESET);

    // cast the result to a DOM NodeList
    NodeList nodes = (NodeList) result;

    // print out the value of each node
//    System.out.println("Trn ids of Trns with description 'RNLK Investments':");
    System.out.println("Note values of Trns with description 'RNLK Investments':");
    for (int i=0; i<nodes.getLength(); i++) {
      System.out.println("\t" + nodes.item(i).getNodeValue());
    }

    // new XPath expression to COUNT the number of trns with description 'RNLK Investments'
    XPathExpression exprCt = xpath.compile("count(//gnc:transaction[trn:description='RNLK Investments'])");
    // run the query and get the number of nodes
    Double ct = (Double) exprCt.evaluate(doc, XPathConstants.NUMBER);
    System.out.println("\nNumber of nodes = " + ct);
  }

}// class QueryRL

/** delegate the lookup to the document */
class UniversalNamespaceResolver implements NamespaceContext {
    // the delegate
    private Document sourceDoc;

    /**
     * This constructor stores the source document to search the namespaces in it.
     * 
     * @param document source document
     */
    public UniversalNamespaceResolver(Document doc) {
        sourceDoc = doc;
    }

    /**
     * The lookup for the namespace uris is delegated to the stored document.
     * 
     * @param prefix to search for
     * @return uri
     */
    public String getNamespaceURI(String prefix) {
        if (prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return sourceDoc.lookupNamespaceURI(null);
        } else {
            return sourceDoc.lookupNamespaceURI(prefix);
        }
    }

    /**
     * This method is not needed in this context, but can be implemented in a similar way.
     */
    public String getPrefix(String namespaceURI) {
        return sourceDoc.lookupPrefix(namespaceURI);
    }

    public Iterator getPrefixes(String namespaceURI) {
        // not implemented yet
        return null;
    }
}// class UniversalNamespaceResolver

class UniversalNamespaceCache implements NamespaceContext {
    private static final String DEFAULT_NS = "DEFAULT";
    private Map<String, String> prefix2Uri = new HashMap<String, String>();
    private Map<String, String> uri2Prefix = new HashMap<String, String>();
 
    /**
     * CONSTRUCTOR parses the document and stores all namespaces it can find.
     * If 'toplevelOnly' is true, only namespaces in the root are used.
     * 
     * @param document
     *            source document
     * @param toplevelOnly
     *            restriction of the search to enhance performance
     * @param print
     *            print the list of cached namespaces
     */
    public UniversalNamespaceCache(Document document, boolean toplevelOnly, boolean print) {
        examineNode(document.getFirstChild(), toplevelOnly);
        if(print)
        {
          System.out.println("The list of the cached namespaces:");
          for (String key : prefix2Uri.keySet()) {
            System.out.println("prefix " + key + ": uri " + prefix2Uri.get(key));
          }
        }
    }
 
    /**
     * A single node is read, the namespace attributes are extracted and stored.
     * 
     * @param node
     *            to examine
     * @param attributesOnly,
     *            if true no recursion happens
     */
    private void examineNode(Node node, boolean attributesOnly) {
        NamedNodeMap attributes = node.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node attribute = attributes.item(i);
            storeAttribute((Attr) attribute);
        }
 
        if (!attributesOnly) {
            NodeList chields = node.getChildNodes();
            for (int i = 0; i < chields.getLength(); i++) {
                Node chield = chields.item(i);
                if (chield.getNodeType() == Node.ELEMENT_NODE)
                    examineNode(chield, false);
            }
        }
    }
 
    /**
     * This method looks at an attribute and stores it, if it is a namespace attribute.
     * 
     * @param attribute
     *            to examine
     */
    private void storeAttribute(Attr attribute) {
        // examine the attributes in namespace xmlns
        if (attribute.getNamespaceURI() != null
                && attribute.getNamespaceURI().equals(
                        XMLConstants.XMLNS_ATTRIBUTE_NS_URI)) {
            // Default namespace xmlns="uri goes here"
            if (attribute.getNodeName().equals(XMLConstants.XMLNS_ATTRIBUTE)) {
                putInCache(DEFAULT_NS, attribute.getNodeValue());
            } else {
                // The defined prefixes are stored here
                putInCache(attribute.getLocalName(), attribute.getNodeValue());
            }
        }
 
    }
 
    private void putInCache(String prefix, String uri) {
        prefix2Uri.put(prefix, uri);
        uri2Prefix.put(uri, prefix);
    }
 
    /**
     * This method is called by XPath. It returns the default namespace, if the prefix is null or "".
     * 
     * @param prefix
     *            to search for
     * @return uri
     */
    public String getNamespaceURI(String prefix) {
        if (prefix == null || prefix.equals(XMLConstants.DEFAULT_NS_PREFIX)) {
            return prefix2Uri.get(DEFAULT_NS);
        } else {
            return prefix2Uri.get(prefix);
        }
    }
 
    /**
     * This method is not needed in this context, but can be implemented in a similar way.
     */
    public String getPrefix(String namespaceURI) {
        return uri2Prefix.get(namespaceURI);
    }
 
    public Iterator getPrefixes(String namespaceURI) {
        // Not implemented
        return null;
    }
}// class UniversalNamespaceCache
