package DB.Entities;

public class SearchResult {
    private Integer docid;
    private Double agScore;

    public SearchResult(Integer docid, Double agScore){
        this.docid = docid;
        this.agScore = agScore;
    }

    public Integer getDocid() {
        return docid;
    }

    public Double getAgScore() {
        return agScore;
    }
}
