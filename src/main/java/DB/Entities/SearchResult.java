package DB.Entities;

public class SearchResult extends DocumentEntity{
    private Integer rank;
    private Double score;

    public SearchResult(Integer rank, Integer docid, String url, String title, String description, Double score, Boolean internal){
        super(docid, url, title, description, internal);
        this.rank = rank;
        this.score = score;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getRank() {
        return rank;
    }
}
