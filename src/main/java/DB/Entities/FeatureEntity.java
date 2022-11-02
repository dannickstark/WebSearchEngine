package DB.Entities;

public class FeatureEntity {
    public Integer featid;
    public Integer docid;
    public String term;
    public Integer term_frequency;

    public FeatureEntity(Integer featid, Integer docid, String term, Integer term_frequency){
        this.featid = featid;
        this.docid = docid;
        this.term = term;
        this.term_frequency = term_frequency;
    }
}
