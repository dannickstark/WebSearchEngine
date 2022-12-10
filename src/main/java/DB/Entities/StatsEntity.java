package DB.Entities;

public class StatsEntity {
    private Integer df;
    private String term;

    public StatsEntity(String term, Integer df){
        this.term = term;
        this.df = df;
    }

    public String getTerm() {
        return term;
    }

    public Integer getDf() {
        return df;
    }

    public void setDf(Integer df) {
        this.df = df;
    }
}
