package DB.Entities;

import java.util.ArrayList;

public class ApiResponse {
    private ArrayList<SearchResult> resultList;
    private Query query;

    public ApiResponse(ArrayList<SearchResult> resultList, String query, Integer k){
        this.resultList = resultList;
        this.query = new Query(query, k);
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
}
