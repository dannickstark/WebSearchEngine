package DB;

import DB.Entities.DocumentEntity;
import DB.Entities.FeatureEntity;
import DB.Entities.GlobalVarEntity;
import DB.Entities.LinkEntity;

import java.sql.*;
import java.util.ArrayList;

public class DB {
    private Connection conn = null;
    private String dbName;

    public DB(String dbName, String user, String pass) {
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
                }
                else {
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
    public void truncateTable(String tableName){
        Statement statement;
        try{
            statement = conn.createStatement();
            int result = statement.executeUpdate("TRUNCATE " + tableName + " CASCADE");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
    // =========== INSERT
    public Integer insert_document(String url) {
        String query = String.format("insert into documents(url) values('%s');", url);
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

    // =========== SELECT
    public ResultSet selectTable(String table_name) {
        String query = String.format("select * from %s", table_name);
        return executePutQuery(query);
    }

    public ResultSet searchByAtt(String table_name, String att, int val){
        String query=String.format("select * from %s where %s = %s",table_name, att, val);
        return executePutQuery(query);
    }

    public ResultSet searchByAtt(String table_name, String att, String val){
        String query=String.format("select * from %s where %s = '%s'",table_name, att, val);
        return executePutQuery(query);
    }

    // ====================================== UPDATE
    public void updateEntityByKey(String table_name, String keyName, String keyVal, String att,String val){
        String query=String.format("update %s set %s='%s' where %s='%s'",table_name,att,val,keyName,keyVal);
        executeUpdateQuery(query);
    }

    public void updateEntityByKey(String table_name, String keyName, String keyVal, String att,int val){
        String query=String.format("update %s set %s=%s where %s='%s'",table_name,att,val,keyName,keyVal);
        executeUpdateQuery(query);
    }
    public void updateEntityByKey(String table_name, String keyName, int keyVal, String att,String val){
        String query=String.format("update %s set %s='%s' where %s=%s",table_name,att,val,keyName,keyVal);
        executeUpdateQuery(query);
    }

    public void updateEntityByKey(String table_name, String keyName, int keyVal, String att,int val){
        String query=String.format("update %s set %s=%s where %s=%s",table_name,att,val,keyName,keyVal);
        executeUpdateQuery(query);
    }

    // ======================================= CONVERT
    public ArrayList<DocumentEntity> getDocuments(ResultSet rs){
        ArrayList<DocumentEntity> result = new ArrayList<>();

        if(rs == null)
            return result;

        try {
            while(rs.next()){
                result.add(new DocumentEntity(
                        rs.getInt("docid"),
                        rs.getString("url")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<FeatureEntity> getFeatures(ResultSet rs){
        ArrayList<FeatureEntity> result = new ArrayList<>();

        if(rs == null)
            return result;

        try {
            while(rs.next()){
                result.add(new FeatureEntity(
                        rs.getInt("featid"),
                        rs.getInt("docid"),
                        rs.getString("term"),
                        rs.getInt("term_frequency")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<LinkEntity> getLinks(ResultSet rs){
        ArrayList<LinkEntity> result = new ArrayList<>();

        if(rs == null)
            return result;

        try {
            while(rs.next()){
                result.add(new LinkEntity(
                        rs.getInt("linkid"),
                        rs.getInt("from_docid"),
                        rs.getInt("to_docid")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<GlobalVarEntity> getGlobalVars(ResultSet rs){
        ArrayList<GlobalVarEntity> result = new ArrayList<>();

        if(rs == null)
            return result;

        try {
            while(rs.next()){
                result.add(new GlobalVarEntity(
                        rs.getInt("varid"),
                        rs.getString("key"),
                        rs.getString("value")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }


}
