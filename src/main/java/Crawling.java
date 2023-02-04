import Crawler.Crawler;
import Crawler.CrawlerThread;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Crawling {

    // java -jar Crawling.jar --max-depth 5 --max-doc 50 --multiple-domain true --number-threads 10
    public static void main(String[] args){
        ArrayList<String> testURLS = new ArrayList<>();
        testURLS.add("https://rptu.de");
        testURLS.add("https://www.cs.uni-kl.de");
        testURLS.add("https://www.bio.uni-kl.de/");
        testURLS.add("https://www.chemie.uni-kl.de/");
        testURLS.add("https://www.informatik.uni-kl.de/");

        testURLS.add("https://rptu.de/studium/im-studium/rptu-in-kaiserslautern");
        testURLS.add("https://rptu.de/studium/im-studium/rptu-in-landau");
        testURLS.add("https://rptu.de/weiterbildung");
        testURLS.add("https://rptu.de/ueber-die-rptu/organisation");
        testURLS.add("https://kai.informatik.uni-kl.de");

        int maxDepth = 5;
        int maxDoc = 500;
        boolean multipleDomain = false;
        int numberThreads = 3;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--max-depth":
                    maxDepth = Integer.parseInt(args[++i]);
                    break;
                case "--max-doc":
                    maxDoc = Integer.parseInt(args[++i]);
                    break;
                case "--multiple-domain":
                    multipleDomain = Boolean.parseBoolean(args[++i]);
                    break;
                case "--number-threads":
                    numberThreads = Integer.parseInt(args[++i]);
                    break;
                default:
                    System.err.println("Invalid option: " + args[i]);
                    System.exit(1);
            }
        }

        Crawler cr = new Crawler(testURLS, maxDepth, maxDoc, multipleDomain, numberThreads);
    }
}
