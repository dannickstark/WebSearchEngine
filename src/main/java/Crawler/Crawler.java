package Crawler;

import com.shekhargulati.urlcleaner.UrlCleaner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Crawler {
    ArrayList<String> urlSet;
    int maxDepth;
    int maxDoc;
    boolean multipleDomain;

    public volatile ArrayList<String> visited;
    public volatile Queue<String> que;
    public volatile HashMap<String, Integer> levelMap;

    public String hostName;

    public Crawler(ArrayList<String> urlSet, int maxDepth, int maxDoc, boolean multipleDomain){
        this.urlSet = urlSet;
        this.maxDepth = maxDepth;
        this.maxDoc = maxDoc;
        this.multipleDomain = multipleDomain;

        this.visited = new ArrayList<>();
        this.que = new LinkedList<>(urlSet);
        this.levelMap = new HashMap<>();

        this.work();
    }

    public void work(){
        this.hostName = UrlCleaner.normalizeUrl(this.urlSet.get(0)).split("/")[2];

        for(String link : this.urlSet){
            this.levelMap.put(UrlCleaner.normalizeUrl(link), 1);
        }

        ArrayList<CrawlerThread> bots = new ArrayList<>();

        bots.add(new CrawlerThread(1, this));
        bots.add(new CrawlerThread(2, this));
        bots.add(new CrawlerThread(3, this));

        for(CrawlerThread c : bots){
            try {
                c.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveState(){
        System.out.println(que);
        System.out.println(visited);
        System.out.println(levelMap);
    }
}
