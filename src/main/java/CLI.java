import DB.*;
import DB.Entities.DocumentEntity;
import DB.Entities.FeatureEntity;
import DB.Entities.SearchResult;

import java.util.ArrayList;
import java.util.Scanner;

public class CLI {
    DB db;

    CLI(){
        db = new DB(DBVars.dbName, DBVars.dbUser, DBVars.dbPass);
        showWelcome();

        work();
    }

    public void work(){
        askQuery();

        Scanner in = new Scanner(System.in);
        String query = in.nextLine();

        ArrayList<SearchResult> results = db.search(query, 5, null);

        for(int i=0; i < results.size(); i++){
            SearchResult res = results.get(i);
            showResult(i, res);
        }
    }

    public void showWelcome(){
        System.out.println("========================================");
        System.out.println("========================================");
        System.out.println("==[ Welcome to " + ConsoleColors.PURPLE_BOLD + "'RAPID'" + ConsoleColors.RESET + " search engine ]==");
        System.out.println("========================================");
        System.out.println("========================================\n");
    }

    public void askQuery(){
        System.out.println("What did you want to search ?");
        System.out.print("===> ");
    }

    public void showResult(int index, SearchResult result){
        ArrayList<DocumentEntity> docs = db.getDocuments(db.searchByAtt("documents", "docid", result.docid));

        if(docs.size() > 0){
            System.out.printf("[%s] - %s - (Score: %s)%n", (index + 1), docs.get(0).url, result.agScore);
        }
    }

    public static void main(String[] args){
        CLI cli = new CLI();
    }
}
