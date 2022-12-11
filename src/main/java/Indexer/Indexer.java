package Indexer;

import DB.*;

import DB.Entities.DocumentEntity;
import DB.Entities.LinkEntity;
import org.w3c.dom.Document;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Indexer {
    public DB db;
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
        // 3-get indices of each word
        //getIndexOfWord(words, originalDoc, fileName); // TODO: Synchronized threads
        // 4-convert to lowercase
        words = TextManipulator.convertToLower(words);
        // 5- remove stop words
        words = TextManipulator.removeStopWords(words);
        // 6- stemming
        List<String> stemmedWords = TextManipulator.stemming(words);
        // 7- calculate the score of each term
        calcTermsFreq(stemmedWords, url);
        // 8- build processed words
        // buildProcessedFiles(fileName, stemmedWords);
        // 9- build inverted index
        //buildInvertedIndex(stemmedWords, fileName, invertedIndex);

        // Save document
        Integer docEnID = db.insert_document(url, parsedDoc.get("title"), parsedDoc.get("description"));
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
}
