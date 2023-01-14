package DB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Migration {
    private DB db;

    public Migration(DB db){
        this.db = db;
    }

    public void migrate() throws SQLException {
        //resetTables();
        dropTables();

        createTable_documents() ;
        createTable_features();
        createTable_links();
        createTable_globalVars();
        createTable_urls();
    }

    public void resetTables(){
        db.truncateTable("links");
        db.truncateTable("features");
        db.truncateTable("documents");
        db.truncateTable("globalvars");
        db.truncateTable("urls");
    }

    public void dropTables(){
        db.deleteTable("links");
        db.deleteTable("features");
        db.deleteTable("documents");
        db.deleteTable("globalvars");
        db.deleteTable("urls");
    }

    public Integer createTable_documents() throws SQLException {
        if(!db.checkIfTableExist("documents")){
            String query="""
                create table documents (
                    docid SERIAL, 
                    url varchar(200) UNIQUE NOT NULL, 
                    title varchar(200),
                    description varchar(200),
                    terms text[],
                    crawled_on_date date NOT NULL DEFAULT CURRENT_DATE, 
                    pagerank double precision,
                    internal BOOLEAN DEFAULT FALSE,
                    language varchar(200),
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
                            tf double precision,
                            idf double precision,
                            score double precision,
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

    public Integer createTable_urls() throws SQLException {
        if(!db.checkIfTableExist("urls")){
            String query="""
                        create table urls (
                            urlid SERIAL, 
                            url varchar(200) UNIQUE NOT NULL, 
                            visited BOOLEAN NOT NULL DEFAULT TRUE,
                            primary key(urlid)
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
                            name varchar(200) NOT NULL, 
                            content varchar(200) NOT NULL, 
                            primary key(varid)
                        );
                    """;

            return db.executeUpdateQuery(query);
        }
        return null;
    }
}
