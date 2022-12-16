package Indexer;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import java.util.HashMap;

public class Parser {

    public static HashMap<String, String> parse(Document doc) throws XPathExpressionException, TransformerException {
        HashMap<String, String> parsedDoc = new HashMap<>();

        String docCode = "";

        // Use XPath to obtain all links
        XPath xpath = XPathFactory.newInstance().newXPath();

        XPathExpression expr = xpath.compile("//title[text()]");
        String title = (String)expr.evaluate(doc, XPathConstants.STRING);
        parsedDoc.put("title", title);

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

        XPathExpression pNode1 = xpath.compile("//p[0]");
        String p1 = (String)pNode1.evaluate(doc, XPathConstants.STRING);

        XPathExpression pNode2 = xpath.compile("//p[1]");
        String p2 = (String)pNode2.evaluate(doc, XPathConstants.STRING);

        XPathExpression pNode3 = xpath.compile("//p[2]");
        String p3 = (String)pNode3.evaluate(doc, XPathConstants.STRING);

        String description = p1 + " ... " + p2 + " ... " + p3;

        parsedDoc.put("description",
                description.substring(0, Math.min(description.length(), 200))
        );

        docCode = title + " " + docCode;
        parsedDoc.put("doc", docCode);
        return parsedDoc;
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

                //ignore script and style tags
                if (e.getTagName().equals("script") || e.getTagName().equals("style")) {
                    return NodeFilter.FILTER_REJECT;
                }
            }
            return NodeFilter.FILTER_ACCEPT;
        }
    }
}
