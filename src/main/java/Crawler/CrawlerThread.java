package Crawler;

import java.io.IOException;
import java.util.ArrayList;

public class CrawlerThread implements Runnable
{
    private int ID;
    String url;

    private Thread thread;
    private SimpleCrawler simpleCrawler;

    public CrawlerThread(int i, String url, int maxDepth, int maxDoc, boolean multipleDomain, ArrayList<String> visited){
        this.ID = i;
        this.url = url;

        this.simpleCrawler = new SimpleCrawler(maxDepth, maxDoc, multipleDomain, visited);

        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {
        try {
            simpleCrawler.crawl(1, this.url);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Thread getThread(){
        return this.thread;
    }
}
