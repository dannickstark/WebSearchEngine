package DB.Entities;

public class DocumentEntity {
    private Integer docid;
    private String url;
    private String title;
    private String description;

    public DocumentEntity(Integer docid, String url, String title, String description){
        this.docid = docid;
        this.url = url;
        this.title = title;
        this.description = description;
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