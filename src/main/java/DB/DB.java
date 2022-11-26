package DB;

import DB.Entities.*;
import Indexer.TextManipulator;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB {
    private Connection conn = null;
    private String dbName;

    public DB(String dbName, String user, String pass) {
        TextManipulator.loadStopWords();

        this.dbName = dbName;
        this.connect(user, pass);
    }

    public Connection connect(String user, String pass) {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/" + this.dbName, user, pass);

            if (conn != null) {
                System.out.println("Connection to DB established !");
            } else {
                System.out.println("Connection failed !");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return conn;
    }

    public Connection getConnection() {
        return conn;
    }

    public boolean checkIfTableExist(String tableName) throws SQLException {
        DatabaseMetaData meta = conn.getMetaData();
        ResultSet resultSet = meta.getTables(null, null, tableName, new String[]{"TABLE"});

        return resultSet.next();
    }

    public Integer executeUpdateQuery(String query) {
        Statement statement;
        try {
            statement = conn.createStatement();

            int affectedRows = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
            if (affectedRows == 0) {
                return null;
            }

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                } else {
                    throw new SQLException("Operation failed, no ID obtained.");
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public ResultSet executePutQuery(String query) {
        Statement statement;
        try {
            statement = conn.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    // =================================================== Help functions
    // =========== TRUNCATE
    public void truncateTable(String tableName) {
        try {
            if (checkIfTableExist(tableName)) {
                Statement statement;
                try {
                    statement = conn.createStatement();
                    int result = statement.executeUpdate("TRUNCATE " + tableName + " CASCADE");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    // =========== DROP
    public void deleteTable(String table_name) {
        Statement statement;
        try {
            String query = String.format("drop table %s", table_name);
            statement = conn.createStatement();
            statement.executeUpdate(query);
            System.out.println("Table Deleted");
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    // =========== INSERT
    public Integer insert_document(String url, String title, String description) {
        String query = String.format("insert into documents(url, title, description) values('%s', '%s', '%s');",
                url, title, description
        );
        return executeUpdateQuery(query);
    }

    public Integer insert_feature(int docid, String term, int term_frequency) {
        String query = String.format(
                "insert into features (docid, term, term_frequency) values(%s, '%s', %s);",
                docid, term, term_frequency
        );

        return executeUpdateQuery(query);
    }

    public Integer insert_link(int from_docid, String url) {
        String query = String.format(
                "insert into links (from_docid, url) values(%s, '%s');",
                from_docid, url
        );
        return executeUpdateQuery(query);
    }

    public Integer insert_link(int from_docid, int to_docid, String url) {
        String query = String.format(
                "insert into links (from_docid, to_docid, url) values(%s, %s, '%s');",
                from_docid, to_docid, url
        );
        return executeUpdateQuery(query);
    }

    public Integer insert_globalVar(String name, String content) {
        String query = String.format(
                "insert into globalvars (name, content) values('%s', '%s');",
                name, content
        );
        return executeUpdateQuery(query);
    }

    public Integer insert_url(String url, Boolean visited) {
        String vis = (visited ? "TRUE" : "FALSE");
        String query = String.format(
                "insert into urls (url, visited) values('%s', %s);",
                url, vis
        );
        return executeUpdateQuery(query);
    }

    // =========== SELECT
    public ResultSet selectTable(String table_name) {
        String query = String.format("select * from %s", table_name);
        return executePutQuery(query);
    }

    public ResultSet searchByAtt(String table_name, String att, Object val) {
        String query = String.format("select * from %s where %s = %s", table_name, att, val);
        return executePutQuery(query);
    }

    public ResultSet searchByAtt(String table_name, String att, String val) {
        String query = String.format("select * from %s where %s = '%s'", table_name, att, val);
        return executePutQuery(query);
    }

    public ResultSet searchByAtt_(String table_name, String att, String val) {
        String query = String.format("select * from %s where %s %s", table_name, att, val);
        return executePutQuery(query);
    }

    // ====================================== UPDATE
    public void updateEntityByKey_(String table_name, String keyName, String keyVal, String att, String val) {
        String query = String.format("update %s set %s=%s where %s=%s", table_name, att, val, keyName, keyVal);
        executeUpdateQuery(query);
    }

    public void updateEntityByKey(String table_name, String keyName, String keyVal, String att, String val) {
        String query = String.format("update %s set %s='%s' where %s='%s'", table_name, att, val, keyName, keyVal);
        executeUpdateQuery(query);
    }

    public void updateEntityByKey(String table_name, String keyName, String keyVal, String att, Object val) {
        String query = String.format("update %s set %s=%s where %s='%s'", table_name, att, val, keyName, keyVal);
        executeUpdateQuery(query);
    }

    public void updateEntityByKey(String table_name, String keyName, Object keyVal, String att, String val) {
        String query = String.format("update %s set %s='%s' where %s=%s", table_name, att, val, keyName, keyVal);
        executeUpdateQuery(query);
    }

    public void updateEntityByKey(String table_name, String keyName, Object keyVal, String att, Object val) {
        String query = String.format("update %s set %s=%s where %s=%s", table_name, att, val, keyName, keyVal);
        executeUpdateQuery(query);
    }

    // ======================================= CONVERT
    public ArrayList<DocumentEntity> getDocuments(ResultSet rs) {
        ArrayList<DocumentEntity> result = new ArrayList<>();

        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new DocumentEntity(
                        rs.getInt("docid"),
                        rs.getString("url"),
                        rs.getString("title"),
                        rs.getString("description")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<FeatureEntity> getFeatures(ResultSet rs) {
        ArrayList<FeatureEntity> result = new ArrayList<>();

        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new FeatureEntity(
                        rs.getInt("featid"),
                        rs.getInt("docid"),
                        rs.getString("term"),
                        rs.getDouble("term_frequency"),
                        rs.getDouble("tf"),
                        rs.getDouble("idf"),
                        rs.getDouble("score")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<LinkEntity> getLinks(ResultSet rs) {
        ArrayList<LinkEntity> result = new ArrayList<>();

        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new LinkEntity(
                        rs.getInt("linkid"),
                        rs.getInt("from_docid"),
                        rs.getInt("to_docid"),
                        rs.getString("url")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<GlobalVarEntity> getGlobalVars(ResultSet rs) {
        ArrayList<GlobalVarEntity> result = new ArrayList<>();
        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new GlobalVarEntity(
                        rs.getInt("varid"),
                        rs.getString("name"),
                        rs.getString("content")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<UrlEntity> getUrls(ResultSet rs) {
        ArrayList<UrlEntity> result = new ArrayList<>();
        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new UrlEntity(
                        rs.getInt("urlid"),
                        rs.getString("url"),
                        rs.getBoolean("visited")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<SearchResult> getSearchResult(ResultSet rs) {
        ArrayList<SearchResult> result = new ArrayList<>();
        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new SearchResult(
                        rs.getInt("docid"),
                        rs.getDouble("agScore")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    // =================== HELP FUNCTIONS
    public void addUrl(String url) {
        insert_url(url, false);
    }

    public void updateUrl(String url) {
        var urls = getUrls(searchByAtt("urls", "url", url));
        if (urls.size() > 0) {
            updateEntityByKey_("urls", "url", "'" + url + "'", "visited", "TRUE");
        } else {
            insert_url(url, true);
        }
    }

    public void calculateTF_IDF() {
        String query = String.format("""
                     WITH tfs as (
                     	select term, 1 + LOG(term_frequency) tf
                     	from features
                     ), docfs as (
                     	select term, COUNT(DISTINCT docid) docs
                     	from features
                     	GROUP BY term
                     	ORDER BY term
                     ), totalDocs as (
                     	select COUNT(DISTINCT docid)
                     	from documents
                     ), idfs as (
                     	select f.term, LOG(ttds.count / df.docs) idf
                     	from features f, docfs df, totalDocs ttds
                     	WHERE f.term = df.term
                     )
                     
                     update features
                     set tf = tfs.tf, idf = idfs.idf, score = tfs.tf * idfs.idf
                     FROM tfs, idfs
                     WHERE features.term = tfs.term
                     	AND features.term = idfs.term;
                """);
        executeUpdateQuery(query);
    }

    public ResultSet conjunctiveSearch(String queryTerms, int k){
        String query = String.format("""
                WITH docsTerms as (
                	select docid,  array_agg(term)::text[] as terms
                	from features
                	GROUP BY docid
                )
                                
                select f.docid, SUM(f.score) agScore
                from features f, docsTerms dt
                where f.docid = dt.docid
                	AND dt.terms @> string_to_array('%s', ' ')
                GROUP BY f.docid
                ORDER BY agScore
                LIMIT %s;
                """, queryTerms, k);
        return executePutQuery(query);
    }

    public ResultSet disjunctiveSearch(String queryTerms, int k){
        String query = String.format("""
                select docid, SUM(score) agScore
                from features
                where term = ANY(string_to_array('%s', ' '))
                GROUP BY docid
                ORDER BY agScore
                LIMIT %s;
                """, queryTerms, k);
        return executePutQuery(query);
    }

    public ArrayList<SearchResult> search(String queryTerms, int k, String mode){
        // split words
        List<String> words = TextManipulator.splitWords(queryTerms);
        // convert to lowercase
        words = TextManipulator.convertToLower(words);
        // remove stop words
        words = TextManipulator.removeStopWords(words);
        // stemming
        List<String> stemmedWords = TextManipulator.stemming(words);
        // construct new query
        String newQueryTerms = String.join(" ", stemmedWords);

        if(mode == "conjunctive"){
            return getSearchResult(conjunctiveSearch(newQueryTerms, k));
        }
        return getSearchResult(disjunctiveSearch(newQueryTerms, k));
    }
}
