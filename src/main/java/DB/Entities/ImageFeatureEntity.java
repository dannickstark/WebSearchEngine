package DB.Entities;

public class ImageFeatureEntity {
    private Integer featid;
    private Integer docid;
    private Integer imageid;
    private String term;
    private Double score;

    public ImageFeatureEntity(Integer featid, Integer docid, Integer imageid, String term, Double score){
        this.featid = featid;
        this.docid = docid;
        this.imageid = imageid;
        this.term = term;
        this.score = score;
    }

    public Integer getImageid() {
        return imageid;
    }

    public void setImageid(Integer imageid) {
        this.imageid = imageid;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public Integer getDocid() {
        return docid;
    }

    public void setDocid(Integer docid) {
        this.docid = docid;
    }

    public Integer getFeatid() {
        return featid;
    }

    public void setFeatid(Integer featid) {
        this.featid = featid;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}
