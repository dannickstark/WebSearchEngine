package DB.Entities;

public class SearchResult {
    public Integer docid;
    public Double agScore;

    public SearchResult(Integer docid, Double agScore){
        this.docid = docid;
        this.agScore = agScore;
    }
}
