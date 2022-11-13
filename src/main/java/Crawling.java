import Crawler.Crawler;
import Crawler.CrawlerThread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Crawling {

    public static void main(String[] args){
        ArrayList<String> testURLS = new ArrayList<>();
        testURLS.add("https://www.cs.uni-kl.de");
        testURLS.add("https://www.cs.uni-kl.de/studium");
        testURLS.add("https://www.cs.uni-kl.de/organisation");

        Crawler cr = new Crawler(testURLS, 5, 50, false);
    }
}
