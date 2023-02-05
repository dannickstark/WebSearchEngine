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
        createTable_images() ;
        createTable_features();
        createTable_imageFeatures();
        createTable_links();
        createTable_globalVars();
        createTable_urls();

        System.out.println("------> END OF MIGRATION");
        System.exit(0);
    }

    public void resetTables(){
        db.truncateTable("links");
        db.truncateTable("features");
        db.truncateTable("imagefeatures");
        db.truncateTable("images");
        db.truncateTable("documents");
        db.truncateTable("globalvars");
        db.truncateTable("urls");
    }

    public void dropTables(){
        db.deleteTable("links");
        db.deleteTable("features");
        db.deleteTable("imagefeatures");
        db.deleteTable("images");
        db.deleteTable("documents");
        db.deleteTable("globalvars");
        db.deleteTable("urls");
    }

    public Integer createTable_documents() throws SQLException {
        if(!db.checkIfTableExist("documents")){
            String query="""
                create table documents (
                    docid SERIAL, 
                    url varchar(500) UNIQUE NOT NULL, 
                    title varchar(200),
                    description TEXT,
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

    public Integer createTable_images() throws SQLException {
        if(!db.checkIfTableExist("images")){
            String query="""
                create table images (
                    imageid SERIAL,
                    url varchar(500),
                    docid integer NOT NULL,
                    foreign key(docid) references documents,
                    primary key(imageid)
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

    public Integer createTable_imageFeatures() throws SQLException {
        if(!db.checkIfTableExist("imagefeatures")){
            String query="""
                        create table imagefeatures (
                            featid SERIAL, 
                            docid integer NOT NULL, 
                            imageid integer NOT NULL, 
                            term varchar(200) NOT NULL, 
                            score double precision,
                            primary key(featid),
                            foreign key(docid) references documents,
                            foreign key(imageid) references images
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
                            url varchar(500) NOT NULL, 
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
                            url varchar(500) UNIQUE NOT NULL, 
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
