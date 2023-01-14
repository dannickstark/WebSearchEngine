import DB.*;
import DB.Entities.SearchResult;

import java.util.ArrayList;
import java.util.Scanner;

public class CLI {
    DB db;

    CLI(){
        db = new DB(DBVars.dbPort, DBVars.dbName, DBVars.dbUser, DBVars.dbPass);
        showWelcome();

        work();
    }

    public void work(){
        Scanner in = new Scanner(System.in);
        String query;

        askJob();
        query = in.nextLine();

        switch (query){
            case "Q":
                return;
            case "S":
                askQuery();
                query = in.nextLine();

                Recolter rec = db.search(query, 5);
                ArrayList<SearchResult> results = rec.results;

                for(int i=0; i < results.size(); i++){
                    SearchResult res = results.get(i);
                    showResult(i, res);
                }

                work();
                break;
            default:
                work();
        }
    }

    public void showWelcome(){
        System.out.println("========================================");
        System.out.println("========================================");
        System.out.println("==[ Welcome to " + ConsoleColors.PURPLE_BOLD + "'RAPID'" + ConsoleColors.RESET + " search engine ]==");
        System.out.println("========================================");
        System.out.println("========================================\n");
    }

    public void askJob(){
        System.out.println("What did you want to do now ?");
        System.out.print("-> Search something (S)");
        System.out.print("-> Quit (Q)");
        System.out.print("===> ");
    }

    public void askQuery(){
        System.out.println("What did you want to search ?");
        System.out.print("===> ");
    }

    public void showResult(int index, SearchResult result){
        System.out.printf("[%s] - %s - (Score: %s)%n", (index + 1), result.getUrl(), result.getScore());
    }

    public static void main(String[] args){
        CLI cli = new CLI();
    }
}
