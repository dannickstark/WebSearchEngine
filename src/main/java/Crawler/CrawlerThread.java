package Crawler;

import java.io.IOException;
import java.util.ArrayList;

public class CrawlerThread implements Runnable
{
    public int ID;
    public Crawler crawler;

    private Thread thread;
    private SimpleCrawler simpleCrawler;

    public CrawlerThread(int i, Crawler crawler){
        this.ID = i;
        this.crawler = crawler;

        this.simpleCrawler = new SimpleCrawler(this);

        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {
        try {
            simpleCrawler.crawl();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Thread getThread(){
        return this.thread;
    }

    public void sleep(int i) throws InterruptedException {
        Thread.sleep(i);
    }
}
