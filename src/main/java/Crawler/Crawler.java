package Crawler;

import DB.*;
import DB.Entities.GlobalVarEntity;
import DB.Entities.LinkEntity;
import DB.Entities.UrlEntity;
import com.shekhargulati.urlcleaner.UrlCleaner;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Crawler {
    public volatile DB db;

    ArrayList<String> urlSet;
    int maxDepth;
    int maxDoc;
    boolean multipleDomain;

    public volatile ArrayList<String> visited;
    public volatile Queue<String> que;
    public volatile HashMap<String, Integer> levelMap;

    public ArrayList<String> hostNames;

    public volatile Object o1, o2, o3, o4, o5;

    public Crawler(ArrayList<String> urlSet, int maxDepth, int maxDoc, boolean multipleDomain, int numberTreads){
        db = new DB(DBVars.dbPort, DBVars.dbName, DBVars.dbUser, DBVars.dbPass);

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

        this.work(numberTreads);
    }

    public void work(int numberTreads){
        String oldState = checkState();

        // Load urls
        ArrayList<UrlEntity> urls = db.getUrls(db.selectTable("urls"));

        for(UrlEntity u : urls){
            if(u.getVisited()){
                this.visited.add(u.getUrl());
            } else {
                if(oldState.equals("Interrupted")){
                    this.que.add(u.getUrl());
                }
            }
        }

        // Load PageRank


        if (!oldState.equals("Interrupted")) {
            this.que = new LinkedList<>(urlSet);
        }

        // Collect all the hostnames from the initials urls
        this.hostNames = new ArrayList<>();
        for(int i=0; i < this.urlSet.size(); i++){
            String hostName = extractDomain(UrlCleaner.normalizeUrl(this.urlSet.get(i)));

            if(!this.hostNames.contains(hostName)){
                this.hostNames.add(hostName);
            }
        }

        for(String link : this.urlSet){
            this.levelMap.put(UrlCleaner.normalizeUrl(link), 1);
        }

        ArrayList<CrawlerThread> bots = new ArrayList<>();

        for(int i=0; i<numberTreads; i++){
            bots.add(new CrawlerThread(i, this));
        }

        for(CrawlerThread c : bots){
            try {
                c.getThread().join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        changeState("Finished");
        System.out.println("------> Crawling finished successfully");

        // Calculate the TF*IDF Score
        System.out.println("------> Starting to compute the TF*IDF Score");
        db.calculateTF_IDF();

        // Compute PageRank
        System.out.println("------> Compute PageRank");
        db.computePageRank();
    }

    private void changeState(String state) {
        db.insert_globalVar("crawlerState", state);
    }

    private String checkState() {
        ArrayList<GlobalVarEntity> varList = db.getGlobalVars(db.searchByAtt("globalvars", "name", "crawlerState"));

        if(varList.size() > 0){
            return varList.get(0).getContent();
        }
        return "Empty";
    }

    private static String extractDomain(String url) {
        URI uri = null;
        try {
            uri = new URI(url);
            String domain = uri.getHost();
            return domain.startsWith("www.") ? domain.substring(4) : domain;
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
