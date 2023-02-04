package DB.Entities;

import java.util.ArrayList;

public class SearchResult extends DocumentEntity{
    private Integer rank;
    private Double score;
    private ArrayList<MissingTerm> missingTerms;

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

    public ArrayList<MissingTerm> getMissingTerms() {
        return missingTerms;
    }

    public void setMissingTerms(ArrayList<String> missingTerms, String query) {
        this.missingTerms = new ArrayList<>();
        for(String term : missingTerms){
            String possibleQ = query.replace(term, "\"" + term + "\"");
            this.missingTerms.add(new MissingTerm(term, possibleQ));
        }
    }
}
