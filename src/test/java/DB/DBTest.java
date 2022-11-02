package DB;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

class DBTest {
    public DB db = new DB("SearchEngine", "postgres", "UNIstark123@");

    @Test
    void connect() {
        Connection conn = db.getConnection();
        Assertions.assertNotNull(conn);
    }
}