package DB.Entities;

public class LinkEntity {
    private Integer linkid;
    private Integer from_docid;
    private Integer to_docid;
    private String url;

    public LinkEntity(Integer linkid, Integer from_docid, Integer to_docid, String url){
        this.linkid = linkid;
        this.from_docid = from_docid;
        this.to_docid = to_docid;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public Integer getFrom_docid() {
        return from_docid;
    }

    public Integer getLinkid() {
        return linkid;
    }

    public Integer getTo_docid() {
        return to_docid;
    }
}
