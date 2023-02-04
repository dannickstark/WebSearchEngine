package DB.Entities;

import java.util.ArrayList;

public class ApiResponse {
    private ArrayList<SearchResult> resultList;
    private Query query;
    private ArrayList<StatsEntity> stat;
    private Integer cw;

    public ApiResponse(){

    }

    public ApiResponse(ArrayList<SearchResult> resultList, String query, Integer k, ArrayList<StatsEntity> stat, Integer cw){
        this.resultList = resultList;
        this.query = new Query(query, k);
        this.stat = stat;
        this.cw = cw;
    }

    public class Query {
        String query;
        Integer k;

        Query(String query, Integer k){
            this.query = query;
            this.k = k;
        }
    }

    public ArrayList<SearchResult> getResultList() {
        return resultList;
    }

    public Query getQuery() {
        return query;
    }

    public ArrayList<StatsEntity> getStat() {
        return stat;
    }
}
