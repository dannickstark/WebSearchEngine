package DB;

import java.sql.SQLException;

public class Migrator {
    public static void main(String[] args){
        try {
            DB db = new DB(DBVars.dbPort, DBVars.dbName, DBVars.dbUser, DBVars.dbPass);
            Migration mi = new Migration(db);
            mi.migrate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
