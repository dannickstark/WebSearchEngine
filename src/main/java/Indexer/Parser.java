package Indexer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.*;

public class Parser {

    public static String parse(Document doc) throws XPathExpressionException, TransformerException {
        String docCode = "";

        // Use XPath to obtain all links
        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr = xpath.compile("//title[text()]");
        String title = (String)expr.evaluate(doc, XPathConstants.STRING);
        docCode = title;

        XPathExpression expr2 = xpath.compile("//body");
        Node body = (Node)expr2.evaluate(doc, XPathConstants.NODE);

        // ============
        Document docX = transformToDocument(body);
        if(docX != null){
            DocumentTraversal traversal = (DocumentTraversal)docX;

            NodeIterator iterator = traversal.createNodeIterator(
                    docX, NodeFilter.SHOW_TEXT, new MyFilter(), true);

            for (Node n = iterator.nextNode(); n != null; n = iterator.nextNode()) {
                String text = n.getTextContent().trim();

                if (!text.isEmpty()) {
                    docCode += "\n" + text;
                }
            }
        }

        //docCode = title + " " + getOuterXml(body);
        return docCode;
    }

    public static Document transformToDocument(Node doc){
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db;

            db = dbf.newDocumentBuilder();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Source xmlSource = new DOMSource(doc);
            Result outputTarget = new StreamResult(outputStream);
            TransformerFactory.newInstance().newTransformer().transform(xmlSource, outputTarget);
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());

            return db.parse(is);
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    static class MyFilter implements NodeFilter {

        @Override
        public short acceptNode(Node thisNode) {
            Node parentNode = thisNode.getParentNode();
            if (parentNode.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) parentNode;

                var tmp = e.getTagName();
                //ignore script and style tags
                if (e.getTagName().equals("script") || e.getTagName().equals("style")) {
                    return NodeFilter.FILTER_REJECT;
                }
            }
            return NodeFilter.FILTER_ACCEPT;
        }
    }
}
