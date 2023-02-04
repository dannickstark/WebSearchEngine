package Indexer;

import DB.*;

import DB.Entities.DocumentEntity;
import DB.Entities.LinkEntity;
import com.shekhargulati.urlcleaner.UrlCleaner;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Indexer {
    public DB db;
    double LAMBDA = 0.5;

    private static HashMap<String, HashMap<String, Integer>> freqOfWords;

    public Indexer(){
        db = new DB(DBVars.dbPort, DBVars.dbName, DBVars.dbUser, DBVars.dbPass);
        freqOfWords = new HashMap<>();
    }

    public void index(String url, Document doc, ArrayList<String> outgoingLinks) throws XPathExpressionException, TransformerException {
        // 1- parse html
        HashMap<String, String> parsedDoc = Parser.parse(doc);
        String parsedText = parsedDoc.get("doc");
        // 2- split words
        List<String> words = TextManipulator.splitWords(parsedText);
        // 10- detect language
        String language = TextManipulator.classify(words);
        // 4-convert to lowercase
        words = TextManipulator.convertToLower(words);
        // 5- remove stop words
        words = TextManipulator.removeStopWords(words, language);
        // 6- stemming
        List<String> stemmedWords = TextManipulator.stemming(words, language);
        // 7- calculate the score of each term
        calcTermsFreq(stemmedWords, url);
        // 8- build processed words
        // buildProcessedFiles(fileName, stemmedWords);
        // 9- build inverted index
        //buildInvertedIndex(stemmedWords, fileName, invertedIndex);

        // Save document
        String[] list = stemmedWords.toArray(new String[0]);
        //Integer docEnID = db.insert_document(url, parsedDoc.get("title"), parsedDoc.get("doc"), list);
        Boolean checkIfInternal = checkIfInternalDoc(url);
        Integer docEnID = db.insert_document(
                url,
                parsedDoc.get("title"),
                parsedDoc.get("doc"),
                checkIfInternal,
                language
        );
        //DocumentEntity docEn = db.getDocuments(db.searchByAtt("documents", "url", url)).get(0);
        // Save terms
        HashMap<String, Integer> termsFreqs = freqOfWords.get(url);

        for(String term : termsFreqs.keySet()){
            db.insert_feature(docEnID, term, termsFreqs.get(term));
        }
        // Save links
        // --- Save outgoing links
        for (String link : outgoingLinks) {
            // skip the same link
            if(link == url)
                continue;

            // get target document
            var tmpList = db.getDocuments(db.searchByAtt("documents", "url", link));
            if(tmpList.size() > 0){
                DocumentEntity targetDoc = tmpList.get(0);
                db.insert_link(docEnID, targetDoc.getDocid(), link);
            } else {
                db.insert_link(docEnID, link);
            }
        }
        // --- Update ingoing link
        ArrayList<LinkEntity> ingoingLinks = db.getLinks(db.searchByAtt("links", "url", url));
        for (LinkEntity linkEn : ingoingLinks) {
            db.updateEntityByKey("links", "linkid", linkEn.getLinkid(), "to_docid", docEnID);
        }

        // Index images
        indexImage(docEnID, doc, url, language);
    }

    private Boolean checkIfInternalDoc(String path) {
        try {
            URL url = new URL(path);
            InetAddress address = InetAddress.getByName(url.getHost());
            if (address.isSiteLocalAddress()) {
                return true;
            } else {
                return false;
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private static synchronized void calcTermsFreq(List<String> stemmedWords, String url) {
        HashMap<String, Integer> tempFreq = new HashMap<>();
        for (String word : stemmedWords) {
            if (tempFreq.containsKey(word)) {
                tempFreq.put(word, 1 + tempFreq.get(word));
            } else
                tempFreq.put(word, 1);
        }
        tempFreq.keySet().remove(""); //remove empty string
        freqOfWords.put(url, tempFreq);
    }


    public void indexImage(Integer docid, Document doc, String docUrl, String language) {
        Map<String, Map<String, Double>> index = new HashMap<>();

        try {
            NodeList images = doc.getElementsByTagName("img");
            for (int i = 0; i < images.getLength(); i++) {
                Node image = images.item(i);
                NamedNodeMap attrs = image.getAttributes();
                Node srcAttr = attrs.getNamedItem("src");

                if(srcAttr != null){
                    String src = srcAttr.getNodeValue();

                    if(src != null){
                        String url = construcURL(docUrl, src);
                        src = UrlCleaner.normalizeUrl(url);
                        Map<String, Double> features = extractFeatures(image, language);
                        index.put(src, features);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        for(String url : index.keySet()){
            Map<String, Double> features = index.get(url);

            // Insert image
            Integer imageid = db.insert_image(url, docid);

            // Insert features
            for(String term : features.keySet()){
                db.insert_imagefeature(docid, imageid, term, features.get(term));
            }
        }
    }

    private Map<String, Double> extractFeatures(Node image, String language) {
        Map<String, Double> features = new HashMap<>();

        XPath xpath = XPathFactory.newInstance().newXPath();

        Node nextNode;

        try {
            nextNode = (Node) xpath.evaluate("preceding-sibling::text()[1]", image, XPathConstants.NODE);
            extractFeatureSections(nextNode, image.getParentNode(), features, 0, true, language);

            nextNode = (Node) xpath.evaluate("following-sibling::text()[1]", image, XPathConstants.NODE);
            extractFeatureSections(nextNode, image.getParentNode(), features, 0, false, language);

            analyseAlt(image, features, language);
        } catch (XPathExpressionException e) {
            throw new RuntimeException(e);
        }

        return features;
    }

    public void analyseAlt(Node node, Map<String, Double> features, String language){
        NamedNodeMap attrs = node.getAttributes();
        Node altAttr = attrs.getNamedItem("alt");

        if(altAttr != null){
            String alt = altAttr.getNodeValue();
            List<String> terms = TextManipulator.splitWords(alt);
            terms = analyseTerms(terms, language);

            for (String term : terms) {
                double score = Math.exp(-LAMBDA * 1);
                features.put(term, features.getOrDefault(term, 0.0) + score);
            }
        }
    }

    public Boolean extractFeatureSections(Node node, Node parent, Map<String, Double> features, int distance, Boolean previous, String language){
        while (true) {
            if (node == null) {
                if(parent == null) return false;

                Node nextNode;
                if(previous){
                    nextNode = parent.getPreviousSibling();
                } else {
                    nextNode = parent.getNextSibling();
                }

                if(nextNode != null){
                    if(nextNode.getNodeType() == Node.TEXT_NODE){
                        return extractFeatureSections(nextNode, nextNode.getParentNode(), features, distance, previous, language);
                    } else if(nextNode.hasChildNodes()){
                        if(previous){
                            return extractFeatureSections(nextNode.getLastChild(), nextNode, features, distance, previous, language);
                        } else {
                            return extractFeatureSections(nextNode.getFirstChild(), nextNode, features, distance, previous, language);
                        }
                    }
                }

                return false;
            } else {
                distance++;
                String content = node.getNodeValue();

                if(content != null && content != ""){
                    List<String> terms = TextManipulator.splitWords(content);
                    terms = analyseTerms(terms, language);

                    for (String term : terms) {
                        double score = Math.exp(-LAMBDA * distance);
                        features.put(term, features.getOrDefault(term, 0.0) + score);
                    }

                    return true;
                } else {
                    Node nextNode;

                    XPath xpath = XPathFactory.newInstance().newXPath();

                    try {
                        if(previous){
                            nextNode = (Node) xpath.evaluate("preceding-sibling::text()[1]", node, XPathConstants.NODE);
                            return extractFeatureSections(nextNode, parent, features, distance, previous, language);
                        } else {
                            nextNode = (Node) xpath.evaluate("following-sibling::text()[1]", node, XPathConstants.NODE);
                            return extractFeatureSections(nextNode, parent, features, distance, previous, language);
                        }
                    } catch (XPathExpressionException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    public List<String> analyseTerms(List<String> terms, String language){
        // convert to lowercase
        terms = TextManipulator.convertToLower(terms);
        // remove stop words
        terms = TextManipulator.removeStopWords(terms, language);
        // stemming
        terms = TextManipulator.stemming(terms, language);

        return terms;
    }


    public String construcURL(String baseURL, String url){
        String patternString = "https://?((W|w){3}.)?([a-zA-Z0-9]+)?";
        Pattern pattern = Pattern.compile(patternString);

        Matcher matcher = pattern.matcher(url);
        boolean matches = matcher.matches();

        boolean checkSlash = url.startsWith("/");

        if(!matches) return baseURL + (checkSlash ? "" : "/") + url;
        return url;
    }
}
