package DB.Entities;

public class FeatureEntity {
    private Integer featid;
    private Integer docid;
    private String term;
    private Double term_frequency;
    private Double tf;
    private Double idf;
    private Double score;

    public FeatureEntity(Integer featid, Integer docid, String term, Double term_frequency, Double tf, Double idf, Double score){
        this.featid = featid;
        this.docid = docid;
        this.term = term;
        this.term_frequency = term_frequency;
        this.tf = tf;
        this.idf = idf;
        this.score = score;
    }

    public Integer getDocid() {
        return docid;
    }

    public Double getIdf() {
        return idf;
    }

    public Double getScore() {
        return score;
    }

    public Double getTerm_frequency() {
        return term_frequency;
    }

    public Double getTf() {
        return tf;
    }

    public Integer getFeatid() {
        return featid;
    }

    public String getTerm() {
        return term;
    }
}
