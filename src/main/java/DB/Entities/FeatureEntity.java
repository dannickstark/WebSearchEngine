package DB.Entities;

public class FeatureEntity {
    public Integer featid;
    public Integer docid;
    public String term;
    public Double term_frequency;
    public Double tf;
    public Double idf;
    public Double score;

    public FeatureEntity(Integer featid, Integer docid, String term, Double term_frequency, Double tf, Double idf, Double score){
        this.featid = featid;
        this.docid = docid;
        this.term = term;
        this.term_frequency = term_frequency;
        this.tf = tf;
        this.idf = idf;
        this.score = score;
    }
}
