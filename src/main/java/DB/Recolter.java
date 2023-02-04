package DB;

import DB.Entities.SearchImageResult;
import DB.Entities.SearchResult;
import DB.Entities.StatsEntity;

import java.util.ArrayList;

public class Recolter {
    public ArrayList<SearchResult> results;
    public ArrayList<SearchImageResult> imageResults;
    public ArrayList<StatsEntity> statsResults;

    Recolter(ArrayList<SearchResult> results, ArrayList<StatsEntity> statsResults){
        this.results = results;
        this.statsResults = statsResults;
    }

    Recolter(ArrayList<SearchImageResult> imageResults){
        this.imageResults = imageResults;
    }
}