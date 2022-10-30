import Crawler.Crawler;
import Crawler.CrawlerThread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Main {

    public static void main(String[] args){
        ArrayList<String> testURLS = new ArrayList<>();
        testURLS.add("https://www.w3schools.com/");

        Crawler cr = new Crawler(testURLS, 5, 50, false);
    }
}
