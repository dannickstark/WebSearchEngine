package DB.Entities;

public class DocumentEntity {
    public Integer docid;
    public String url;

    public DocumentEntity(Integer docid, String url){
        this.docid = docid;
        this.url = url;
    }
}
