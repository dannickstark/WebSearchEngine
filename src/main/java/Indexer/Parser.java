package Indexer;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.StringWriter;

public class Parser {

    public static String parse(Document doc) throws XPathExpressionException, TransformerException {
        String docCode = "";

        // Use XPath to obtain all links
        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr = xpath.compile("//title[text()]");
        String title = (String)expr.evaluate(doc, XPathConstants.STRING);

        XPathExpression expr2 = xpath.compile("//body");
        Node body = (Node)expr.evaluate(doc, XPathConstants.NODE);

        docCode = title + " " + getOuterXml(body);
        return docCode;
    }

    public static String getOuterXml(Node node) throws TransformerConfigurationException, TransformerException {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty("omit-xml-declaration", "yes");

        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(node), new StreamResult(writer));
        return writer.toString();
    }
}
