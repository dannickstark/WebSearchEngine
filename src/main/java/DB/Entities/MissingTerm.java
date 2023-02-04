package DB.Entities;

public class MissingTerm{
    private String term;
    private String alternativeQuery;

    MissingTerm(String term, String alternativeQuery){
        this.term = term;
        this.alternativeQuery = alternativeQuery;
    }

    public String getTerm() {
        return term;
    }

    public String getAlternativeQuery() {
        return alternativeQuery;
    }
}
