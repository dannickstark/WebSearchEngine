package Crawler;

import java.util.ArrayList;

public class Crawler {
    ArrayList<String> urlSet;
    int maxDepth;
    int maxDoc;
    boolean multipleDomain;

    private volatile ArrayList<String> visited = new ArrayList<>();

    public Crawler(ArrayList<String> urlSet, int maxDepth, int maxDoc, boolean multipleDomain){
        this.urlSet = urlSet;
        this.maxDepth = maxDepth;
        this.maxDoc = maxDoc;
        this.multipleDomain = multipleDomain;

        this.work();
    }

    public void work(){
        ArrayList<CrawlerThread> bots = new ArrayList<>();

        for(String url : this.urlSet){
            bots.add(new CrawlerThread(1, url, this.maxDepth, this.maxDoc, this.multipleDomain, this.visited));
        }

        for(CrawlerThread c : bots){
            try {
                c.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
