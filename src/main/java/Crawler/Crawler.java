package Crawler;

import DB.*;
import DB.Entities.GlobalVarEntity;
import DB.Entities.LinkEntity;
import DB.Entities.UrlEntity;
import com.shekhargulati.urlcleaner.UrlCleaner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;

public class Crawler {
    public volatile DB db;

    ArrayList<String> urlSet;
    int maxDepth;
    int maxDoc;
    boolean multipleDomain;

    public volatile ArrayList<String> visited;
    public volatile Queue<String> que;
    public volatile HashMap<String, Integer> levelMap;

    public String hostName;

    public volatile Object o1, o2, o3, o4, o5;

    public Crawler(ArrayList<String> urlSet, int maxDepth, int maxDoc, boolean multipleDomain){
        db = new DB(DBVars.dbName, DBVars.dbUser, DBVars.dbPass);

        this.urlSet = urlSet;
        this.maxDepth = maxDepth;
        this.maxDoc = maxDoc;
        this.multipleDomain = multipleDomain;

        this.visited = new ArrayList<>();
        this.levelMap = new HashMap<>();

        o1 = new Object();
        o2 = new Object();
        o3 = new Object();
        o4 = new Object();
        o5 = new Object();

        this.work();
    }

    public void work(){
        String oldState = checkState();

        ArrayList<UrlEntity> links = db.getUrls(db.selectTable("urls"));

        for(UrlEntity l : links){
            if(l.visited){
                this.visited.add(l.url);
            } else {
                if(oldState.equals("Interrupted")){
                    this.que.add(l.url);
                }
            }
        }

        if (!oldState.equals("Interrupted")) {
            this.que = new LinkedList<>(urlSet);
        }

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

        changeState("Finished");
        System.out.println("Crawling finished successfully");

        // Calculate the TF*IDF Score
        System.out.println("Starting to compute the TF*IDF Score");
        db.calculateTF_IDF();
    }

    private void changeState(String state) {
        db.insert_globalVar("crawlerState", state);
    }

    private String checkState() {
        ArrayList<GlobalVarEntity> varList = db.getGlobalVars(db.searchByAtt("globalvars", "name", "crawlerState"));

        if(varList.size() > 0){
            return varList.get(0).content;
        }
        return "Empty";
    }
}
