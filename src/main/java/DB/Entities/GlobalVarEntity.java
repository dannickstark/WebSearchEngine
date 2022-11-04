package DB.Entities;

public class GlobalVarEntity {
    public Integer varid;
    public String name;
    public String content;

    public GlobalVarEntity(Integer varid, String name, String content){
        this.varid = varid;
        this.name = name;
        this.content = content;
    }
}
