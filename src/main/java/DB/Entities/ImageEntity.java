package DB.Entities;

import java.util.ArrayList;

public class ImageEntity {
    private Integer imageid;
    private Integer docid;
    private String url;

    public ImageEntity(Integer imageid, Integer docid, String url){
        this.imageid = imageid;
        this.docid = docid;
        this.url = url;
    }

    public Integer getDocid() {
        return docid;
    }

    public Integer getImageid() {
        return imageid;
    }

    public String getUrl() {
        return url;
    }
}
