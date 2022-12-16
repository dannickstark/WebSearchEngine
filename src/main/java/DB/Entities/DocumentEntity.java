package DB.Entities;

import java.util.ArrayList;

public class DocumentEntity {
    private Integer docid;
    private String url;
    private String title;
    private String description;
    private ArrayList<String> terms;

    public DocumentEntity(Integer docid, String url, String title, String description){
        this.docid = docid;
        this.url = url;
        this.title = title;
        this.description = description;
    }

    public DocumentEntity(Integer docid, String url, String title, String description, ArrayList<String> terms){
        this.docid = docid;
        this.url = url;
        this.title = title;
        this.description = description;
        this.terms = terms;
    }

    public Integer getDocid() {
        return docid;
    }

    public String getDescription() {
        return description;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }
}