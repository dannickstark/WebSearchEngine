package DB.Entities;

public class SearchResult extends DocumentEntity{
    private Double agScore;

    public SearchResult(Integer docid, String url, String title, String description, Double agScore){
        super(docid, url, title, description);
        this.agScore = agScore;
    }

    public Double getAgScore() {
        return agScore;
    }

    public void setAgScore(Double agScore) {
        this.agScore = agScore;
    }
}
