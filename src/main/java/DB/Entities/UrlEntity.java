package DB.Entities;

public class UrlEntity {
    public Integer urlid;
    public String url;
    public Boolean visited;

    public UrlEntity(Integer urlid, String url, Boolean visited){
        this.urlid = urlid;
        this.url = url;
        this.visited = visited;
    }
}
