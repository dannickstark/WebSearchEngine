package Crawler;

import com.shekhargulati.urlcleaner.UrlCleaner;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import javax.xml.xpath.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleCrawler {
    private CrawlerThread ct;

    ArrayList<String> visited;
    public volatile Queue<String> que;
    public volatile HashMap<String, Integer> levelMap;

    int maxDepth;
    int maxDoc;
    boolean multipleDomain;
    int threadID;

    String hostName;

    public SimpleCrawler(CrawlerThread ct) {
        this.ct = ct;

        this.threadID = ct.ID;
        this.maxDepth = ct.crawler.maxDepth;
        this.maxDoc = ct.crawler.maxDoc;
        this.multipleDomain = ct.crawler.multipleDomain;
        this.hostName = ct.crawler.hostName;

        this.que = ct.crawler.que;
        this.visited = ct.crawler.visited;
        this.levelMap = ct.crawler.levelMap;
    }

    public void crawl() throws IOException, InterruptedException {
        while (!this.que.isEmpty() && this.visited.size() < this.maxDoc){
            this.ct.sleep(2000);

            String url = this.que.poll();
            if(url == null || url.length() == 0)
                continue;

            url = UrlCleaner.normalizeUrl(url);

            int currentLevel = this.levelMap.get(url);
            if(currentLevel < this.maxDepth){
                System.out.println("[" + this.threadID + "] Visiting (Level " + currentLevel + "): " + url);
                this.visited.add(url);

                ArrayList<String> nextLinks = visitPage(url);
                System.out.println("[" + this.threadID + "] ===> Collecting links in the page");

                if(nextLinks != null){
                    for(String link : nextLinks){
                        var newLink = UrlCleaner.normalizeUrl(link);

                        if(!this.multipleDomain){
                            String patternString = "https://?((W|w){3}.)?([a-zA-Z0-9]+\\.)?" + hostName + "(/.*)?";
                            Pattern pattern = Pattern.compile(patternString);

                            Matcher matcher = pattern.matcher(newLink);
                            boolean matches = matcher.matches();

                            if(!matches) continue;
                        }

                        if(!visited.contains(newLink)){
                            this.que.add(newLink);
                            this.levelMap.put(newLink, currentLevel + 1);
                        }
                    }
                }
            }
        }
    }

    public ArrayList<String> visitPage(String path){
        try {

            // Create a new JTidy instance and set options
            Tidy tidy = new Tidy();
            tidy.setInputEncoding("UTF-8");
            tidy.setOutputEncoding("UTF-8");
            tidy.setMakeClean(true);
            tidy.setXHTML(true);
            tidy.setTidyMark(false);
            tidy.setQuiet(true);
            tidy.setShowWarnings(false);
            tidy.setUpperCaseTags(true);
            tidy.setUpperCaseAttrs(true);
            tidy.setHideComments(true);
            tidy.setShowErrors(0);

            // Parse an HTML page into a DOM document
            URL url = new URL(path);

            // Check if the page is accessible
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            int code = connection.getResponseCode();
            if (code==200)
            {
                System.out.println("[" + this.threadID + "] ===> Connection established");
                Document doc = tidy.parseDOM(url.openStream(), null);
                var docx = doc.toString();

                // Use XPath to obtain whatever you want from the (X)HTML
                XPath xpath = XPathFactory.newInstance().newXPath();
                XPathExpression expr = xpath.compile("//a[starts-with(@href, 'https')]/@href");
                NodeList nodes = (NodeList)expr.evaluate(doc, XPathConstants.NODESET);

                ArrayList<String> urls = new ArrayList<>();
                for(int i=0;i<nodes.getLength();i++)
                {
                    urls.add(nodes.item(i).getNodeValue());
                }
                return urls;
            } else {
                System.out.println("[" + this.threadID + "] ===> Not accessible : " + path);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (XPathExpressionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}