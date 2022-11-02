package DB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Migration {
    private DB db;

    public Migration(DB db){
        this.db = db;
    }

    public void migrate() throws SQLException {
        resetTable();

        createTable_documents() ;
        createTable_features();
        createTable_links();
        createTable_globalVars();
    }

    public void resetTable(){
        db.truncateTable("links");
        db.truncateTable("features");
        db.truncateTable("documents");
        db.truncateTable("globalvars");
    }

    public Integer createTable_documents() throws SQLException {
        if(!db.checkIfTableExist("documents")){
            String query="""
                create table documents (
                    docid SERIAL, 
                    url varchar(200) UNIQUE NOT NULL, 
                    crawled_on_date date NOT NULL DEFAULT CURRENT_DATE, 
                    primary key(docid)
                );
            """;

            return db.executeUpdateQuery(query);
        }
        return null;
    }

    public Integer createTable_features() throws SQLException {
        if(!db.checkIfTableExist("features")){
            String query="""
                        create table features (
                            featid SERIAL, 
                            docid integer NOT NULL, 
                            term varchar(200) NOT NULL, 
                            term_frequency integer DEFAULT 1,
                            primary key(featid),
                            foreign key(docid) references documents
                        );
                    """;

            return db.executeUpdateQuery(query);
        }
        return null;
    }

    public Integer createTable_links() throws SQLException {
        if(!db.checkIfTableExist("links")){
            String query="""
                        create table links (
                            linkid SERIAL, 
                            from_docid integer NOT NULL, 
                            to_docid integer, 
                            url varchar(200) NOT NULL, 
                            primary key(linkid),
                            foreign key(from_docid) references documents,
                            foreign key(to_docid) references documents
                        );
                    """;

            return db.executeUpdateQuery(query);
        }
        return null;
    }

    public Integer createTable_globalVars() throws SQLException {
        if(!db.checkIfTableExist("globalvars")){
            String query="""
                        create table globalvars (
                            varid SERIAL, 
                            key varchar(200) NOT NULL, 
                            value varchar(200) NOT NULL, 
                            primary key(varid)
                        );
                    """;

            return db.executeUpdateQuery(query);
        }
        return null;
    }
}
