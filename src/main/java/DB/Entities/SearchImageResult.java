package DB.Entities;

public class SearchImageResult extends ImageEntity{
    private Integer rank;
    private Double score;
    private String docUrl;
    private String docTitle;

    public SearchImageResult(Integer rank, Integer imageid, Integer docid, String url, String docUrl, String docTitle, Double score) {
        super(imageid, docid, url);
        this.rank = rank;
        this.score = score;
        this.docUrl = docUrl;
        this.docTitle = docTitle;
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

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getDocTitle() {
        return docTitle;
    }
}
