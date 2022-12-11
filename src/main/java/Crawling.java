import Crawler.Crawler;
import Crawler.CrawlerThread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Crawling {

    public static void main(String[] args){
        ArrayList<String> testURLS = new ArrayList<>();
        testURLS.add("https://www.uni-kl.de/");
        testURLS.add("https://www.cs.uni-kl.de");
        testURLS.add("https://www.bio.uni-kl.de/");
        testURLS.add("https://www.chemie.uni-kl.de/");
        testURLS.add("https://www.informatik.uni-kl.de/");

        Crawler cr = new Crawler(testURLS, 100, 50, false);
    }
}
