package DB.Entities;

public class GlobalVarEntity {
    private Integer varid;
    private String name;
    private String content;

    public GlobalVarEntity(Integer varid, String name, String content){
        this.varid = varid;
        this.name = name;
        this.content = content;
    }

    public Integer getVarid() {
        return varid;
    }

    public String getContent() {
        return content;
    }

    public String getName() {
        return name;
    }
}
