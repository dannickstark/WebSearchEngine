package DB.Entities;

public class LinkEntity {
    public Integer linkid;
    public Integer from_docid;
    public Integer to_docid;
    public String url;

    public LinkEntity(Integer linkid, Integer from_docid, Integer to_docid, String url){
        this.linkid = linkid;
        this.from_docid = from_docid;
        this.to_docid = to_docid;
        this.url = url;
    }
}
