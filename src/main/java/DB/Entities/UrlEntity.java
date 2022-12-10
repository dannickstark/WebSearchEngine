package DB.Entities;

public class UrlEntity {
    private Integer urlid;
    private String url;
    private Boolean visited;

    public UrlEntity(Integer urlid, String url, Boolean visited){
        this.urlid = urlid;
        this.url = url;
        this.visited = visited;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getVisited() {
        return visited;
    }

    public Integer getUrlid() {
        return urlid;
    }
}
