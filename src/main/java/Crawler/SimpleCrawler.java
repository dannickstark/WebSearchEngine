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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SimpleCrawler {
    ArrayList<String> visited;
    String hostName;

    int maxDepth;
    int maxDoc;
    boolean multipleDomain;

    public SimpleCrawler(int maxDepth, int maxDoc, boolean multipleDomain, ArrayList<String> visited) {
        this.maxDepth = maxDepth;
        this.maxDoc = maxDoc;
        this.multipleDomain = multipleDomain;
        this.visited = visited;
    }

    public void crawl(int level, String url) throws IOException, InterruptedException {
        if(url == null || url.length() == 0)
            return;

        if (level < this.maxDepth && this.visited.size() < this.maxDoc){
            url = UrlCleaner.normalizeUrl(url);

            if(level == 1){
                this.hostName = url.split("/")[2];
            }

            System.out.println("Visiting: " + url);

            ArrayList<String> nextLinks = visitLink(url);
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
                        //Thread.sleep(1000);
                        crawl(level++, newLink);
                    }
                }
            }
        }
    }

    public ArrayList<String> visitLink(String path){
        try {
            this.visited.add(path);

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
                System.out.println("===> Connection established");
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
                System.out.println("Not accessible : " + path);
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
