package DB.Entities;

public class GlobalVarEntity {
    public Integer varid;
    public String key;
    public String value;

    public GlobalVarEntity(Integer varid, String key, String value){
        this.varid = varid;
        this.key = key;
        this.value = value;
    }
}
