package DB;

import DB.Entities.*;
import Indexer.TextManipulator;
import org.checkerframework.checker.units.qual.A;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class DB {
    private Connection conn = null;
    private String dbName;

    private PageRank pr;

    public DB(String port, String dbName, String user, String pass) {
        TextManipulator.loadStopWords();
        TextManipulator.loadCountsWords();

        this.dbName = dbName;
        this.connect(port, user, pass);

        this.pr = new PageRank(this);
    }

    public void computePageRank(){
        this.pr.compute();
    }

    public Connection connect(String port, String user, String pass) {
        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:postgresql://localhost:" + port + "/" + this.dbName, user, pass);

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
    public Integer insert_document(String url, String title, String description, Boolean isInternal, String lang) {
        String escDescription = TextManipulator.escape(description);
        String query = String.format("insert into documents(url, title, description, internal, language) values('%s', '%s', '%s', %s, '%s');",
                url, title, escDescription, isInternal, lang
        );
        return executeUpdateQuery(query);
    }

    public Integer insert_image(String url, Integer docid) {
        String query = String.format("insert into images(url, docid) values('%s', %s);",
                url, docid
        );
        return executeUpdateQuery(query);
    }

    public Integer insert_document(String url, String title, String description, String[] terms, Boolean isInternal, String lang) {
        String query = String.format("insert into documents(url, title, description, terms, internal, language) values('%s', '%s', '%s', %s, %s, '%s');",
                url, title, description, terms, isInternal, lang
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

    public Integer insert_imagefeature(int docid, int imageid, String term, Double score) {
        String query = String.format(
                "insert into imagefeatures (docid, imageid, term, score) values(%s, %s, '%s', %s);",
                docid, imageid, term, score
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

    public void updateEntityByKey(String table_name, String keyName, Integer keyVal, String att, Object val) {
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
                        rs.getString("description"),
                        (ArrayList<String>) rs.getArray("terms")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<ImageEntity> getImages(ResultSet rs) {
        ArrayList<ImageEntity> result = new ArrayList<>();

        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new ImageEntity(
                        rs.getInt("imageid"),
                        rs.getInt("docid"),
                        rs.getString("url")
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

    public ArrayList<ImageFeatureEntity> getImageFeatures(ResultSet rs) {
        ArrayList<ImageFeatureEntity> result = new ArrayList<>();

        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new ImageFeatureEntity(
                        rs.getInt("featid"),
                        rs.getInt("docid"),
                        rs.getInt("imageid"),
                        rs.getString("term"),
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
                        result.size() + 1,
                        rs.getInt("docid"),
                        rs.getString("url"),
                        rs.getString("title"),
                        rs.getString("description"),
                        rs.getDouble("agScore"),
                        rs.getBoolean("internal")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<SearchImageResult> getSearchImageResult(ResultSet rs) {
        ArrayList<SearchImageResult> result = new ArrayList<>();
        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new SearchImageResult(
                        result.size() + 1,
                        rs.getInt("imageid"),
                        rs.getInt("docid"),
                        rs.getString("url"),
                        rs.getString("docurl"),
                        rs.getString("doctitle"),
                        rs.getDouble("agScore")
                ));
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public ArrayList<StatsEntity> getStats(ResultSet rs) {
        ArrayList<StatsEntity> result = new ArrayList<>();
        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.add(new StatsEntity(
                        rs.getString("term"),
                        rs.getInt("df")
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
                     	select term, COUNT(DISTINCT docid)::float docs
                     	from features
                     	GROUP BY term
                     ), totalDocs as (
                     	select COUNT(docid)
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

    public ResultSet conjunctiveSearch(String queryTerms, Object k, String lang){
        String query = String.format("""
                WITH docsTerms as (
                	select docid, array_agg(term)::text[] as terms
                	from features
                	GROUP BY docid
                )
                                
                select d.*, AVG(f.score) agScore
                from features f, docsTerms dt, documents d
                where dt.terms @> string_to_array('%s', ' ')
                    AND f.docid = dt.docid
                    AND f.docid = d.docid
                    AND f.term = ANY(string_to_array('%s', ' '))
                    AND d.language = '%s'
                GROUP BY f.docid, d.docid
                ORDER BY agScore desc
                LIMIT %s;
                """, queryTerms, queryTerms, lang, k);
        return executePutQuery(query);
    }

    public ResultSet conjunctiveImageSearch(String queryTerms, Object k, String lang){
        String query = String.format("""
                WITH imagesTerms as (
                	select imageid, array_agg(term)::text[] as terms
                	from featureimages
                	GROUP BY imageid
                )
                                
                select i.*, d.title as doctitle, d.url as docurl, AVG(f.score) agScore
                from imagefeatures f, imagesTerms it, documents d, images i
                where it.terms @> string_to_array('%s', ' ')
                    AND f.imageid = it.imageid
                    AND f.imageid = i.imageid
                    AND i.docid = d.docid
                    AND f.term ILIKE ANY(string_to_array('%s', ' '))
                    AND d.language = '%s'
                GROUP BY i.imageid, d.docid
                ORDER BY agScore desc
                LIMIT %s;
                """, queryTerms, queryTerms, lang, k);
        return executePutQuery(query);
    }

    public ResultSet conjunctiveSearch_new(String queryTerms, Object k, String lang){
        String query = String.format(""" 
                select d.*, AVG(f.score) agScore
                from features f, documents d
                where d.terms @> string_to_array('%s', ' ')
                    AND f.docid = d.docid
                    AND f.term ILIKE ANY(string_to_array('%s', ' '))
                    AND d.language = '%s'
                GROUP BY f.docid, d.docid
                ORDER BY agScore desc
                LIMIT %s;
                """, queryTerms, queryTerms, lang, k);
        return executePutQuery(query);
    }

    public ResultSet disjunctiveSearch(String queryTerms, Object k, String lang){
        String query = String.format("""
                select d.*, AVG(f.score) agScore
                from features f, documents d
                where f.term ILIKE ANY(string_to_array('%s', ' '))
                    AND f.docid = d.docid
                    AND d.language = '%s'
                GROUP BY f.docid, d.docid
                ORDER BY agScore desc
                LIMIT %s;
                """, queryTerms, lang, k);
        return executePutQuery(query);
    }

    public ResultSet disjunctiveImageSearch(String queryTerms, Object k, String lang){
        String query = String.format("""
                select i.*, d.title as doctitle, d.url as docurl, AVG(f.score) agScore
                from imagefeatures f, documents d, images i
                where f.term ILIKE ANY(string_to_array('%s', ' '))
                    AND f.imageid = i.imageid
                    AND i.docid = d.docid
                    AND d.language = '%s'
                GROUP BY i.imageid, d.docid
                ORDER BY agScore desc
                LIMIT %s;
                """, queryTerms, lang, k);
        return executePutQuery(query);
    }

    public ResultSet conjunctiveStats(String queryTerms, String lang){
        String query = String.format("""
                WITH docsTerms as (
                	select docid,  array_agg(term)::text[] as terms
                	from features
                	GROUP BY docid
                )
                                
                select f.term, SUM(f.docid) df
                from features f, docsTerms dt, documents d
                where dt.terms @> string_to_array('%s', ' ')
                    AND f.docid = dt.docid
                    AND f.term = ANY(string_to_array('%s', ' '))
                    AND d.docid = f.docid
                    AND d.language = '%s'
                GROUP BY f.term
                ORDER BY term;
                """, queryTerms, queryTerms, lang);
        return executePutQuery(query);
    }

    public ResultSet disjunctiveStats(String queryTerms, String lang){
        String query = String.format("""
                select f.term, SUM(f.docid) df
                from features f, documents d
                where f.term = ANY(string_to_array('%s', ' '))
                    AND d.language = '%s'
                GROUP BY f.term
                ORDER BY f.term;
                """, queryTerms, lang);
        return executePutQuery(query);
    }

    public Integer getNumberOfTerms(){
        String query = String.format("""
                Select COUNT(DISTINCT term)
                from features;
                """);
        ResultSet rs =  executePutQuery(query);

        try {
            if (!rs.next()) return 0;
            return rs.getInt("count");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Recolter search(String queryTerms, Integer k){
        return search(queryTerms, k, "en", "documents");
    }

    public Recolter search(String queryTerms, Integer k, String lang, String searchType){
        // to lowercase
        queryTerms = queryTerms.toLowerCase();

        // get and remove site operator
        String urlRegex = "^\\s*site:(https?:\\/\\/)?(www.)?([^\\s]*)\\s+(.+)";
        Pattern pattern = Pattern.compile(urlRegex);
        Matcher matcher = pattern.matcher(queryTerms);

        String siteUrl = null;
        if (matcher.find()){
            siteUrl = matcher.group(3);
            queryTerms = matcher.group(4);
        }

        // get and remove quotations
        String quoteRegex = "\\\"[^\\\"]*\\\"";
        Pattern pattern2 = Pattern.compile(quoteRegex);
        Matcher matcher2 = pattern2.matcher(queryTerms);

        String quotedQueries = "";
        String disQueryTerms = queryTerms;

        while (matcher2.find()){
            for (int i = 0; i <= matcher2.groupCount(); i++) {
                String quote = matcher2.group(i);
                quotedQueries += quote.replaceAll("\"", "") + " ";
                disQueryTerms = disQueryTerms.replaceAll(quote, "");
            }
        }

        quotedQueries = quotedQueries.trim();
        disQueryTerms = disQueryTerms.trim();

        ArrayList<SearchResult> resultsCon = new ArrayList<>();
        ArrayList<SearchResult> resultsDis = new ArrayList<>();
        ArrayList<SearchImageResult> imageResultsCon = new ArrayList<>();
        ArrayList<SearchImageResult> imageResultsDis = new ArrayList<>();
        ArrayList<StatsEntity> statsCon = null;
        ArrayList<StatsEntity> statsDis = null;

        // Search for conjunctive
        Recolter recCon = null;
        if(quotedQueries.length() > 0){
            recCon = searchNext(quotedQueries, queryTerms, "null", "conjunctive", lang, searchType);
            resultsCon = recCon.results;
            statsCon = recCon.statsResults;

            imageResultsCon = recCon.imageResults;
        }

        // Search for disjunctive
        Recolter recDis = null;
        if(disQueryTerms.length() > 0) {
            recDis = searchNext(disQueryTerms, queryTerms, "null", null, lang, searchType);
            resultsDis = recDis.results;
            statsDis = recDis.statsResults;
            imageResultsDis = recDis.imageResults;
        }

        // Merge the stats
        ArrayList<StatsEntity> finalStats = new ArrayList<>();

        if(searchType.equals("documents")){
            finalStats = mergeStats(statsCon, statsDis);
        }

        // Filter the lists
        if(siteUrl != null){
            String finalSiteUrl = siteUrl;
            if(searchType.equals("images")){
                imageResultsCon = (ArrayList<SearchImageResult>) imageResultsCon.stream()
                        .filter(res -> res.getUrl().contains(finalSiteUrl)).collect(Collectors.toList());
                imageResultsDis = (ArrayList<SearchImageResult>) imageResultsDis.stream()
                        .filter(res -> res.getUrl().contains(finalSiteUrl)).collect(Collectors.toList());
            } else {
                resultsCon = (ArrayList<SearchResult>) resultsCon.stream()
                        .filter(res -> res.getUrl().contains(finalSiteUrl)).collect(Collectors.toList());
                resultsDis = (ArrayList<SearchResult>) resultsDis.stream()
                        .filter(res -> res.getUrl().contains(finalSiteUrl)).collect(Collectors.toList());
            }
        }

        // Join the two lists
        ArrayList<SearchResult> results = new ArrayList<>();
        ArrayList<SearchImageResult> imagesResults = new ArrayList<>();

        if(searchType.equals("images")){
            imagesResults = joinImageResults(imageResultsCon, imageResultsDis);
        } else {
            results = joinResults(resultsCon, resultsDis);
        }

        // Get k first results
        if(searchType.equals("images")){
            ArrayList<SearchImageResult> finalResults = getKfirstImages(k, imagesResults);
            return new Recolter(finalResults);
        } else {
            ArrayList<SearchResult> finalResults = getKfirst(k, results);
            computeSnipet(finalResults, queryTerms);
            return new Recolter(finalResults, finalStats);
        }
    }

    private ArrayList<SearchImageResult> getKfirstImages(Integer k, ArrayList<SearchImageResult> imagesResults) {
        if(k == null) return imagesResults;
        if(k >= imagesResults.size()) return imagesResults;

        ArrayList<SearchImageResult> finalResults = new ArrayList<>();

        for(int i=0; i < k; i++){
            finalResults.add(imagesResults.get(i));
        }

        return finalResults;
    }

    private ArrayList<SearchResult> getKfirst(Integer k, ArrayList<SearchResult> results) {
        if(k == null) return results;
        if(k >= results.size()) return results;

        ArrayList<SearchResult> finalResults = new ArrayList<>();

        for(int i=0; i < k; i++){
            finalResults.add(results.get(i));
        }

        return finalResults;
    }

    private ArrayList<StatsEntity> mergeStats(ArrayList<StatsEntity> statsCon, ArrayList<StatsEntity> statsDis) {
        if(statsCon == null){
            if(statsDis == null) return  new ArrayList<>();
            return new ArrayList<>(statsDis);
        }

        if(statsDis == null){
            return new ArrayList<>(statsCon);
        }

        ArrayList<StatsEntity> finalStats = new ArrayList<>();
        for(StatsEntity stat : statsCon){

            for(StatsEntity stat2 : statsDis){
                if(stat2.getTerm().equals(stat.getTerm())){
                    stat.setDf(stat.getDf() + stat2.getDf());
                    break;
                }
            }

            finalStats.add(stat);
        }

        return finalStats;
    }

    private void computeSnipet(ArrayList<SearchResult> results, String queryTerms) {
        for(int i=0; i < results.size(); i++){
            SearchResult sr = results.get(i);
            String desc = sr.getDescription();
            List<String> words = TextManipulator.splitWords(queryTerms);

            // check if a word is present in the doc,
            // if not collect it
            ArrayList<String> cWords = new ArrayList<>();
            ArrayList<String> ncWords = new ArrayList<>();

            for(String word : words){
                int index = desc.toLowerCase().indexOf(word.toLowerCase());

                if(index != -1){
                    cWords.add(word);
                } else {
                    ncWords.add(word);
                }
            }

            // check if all the key word are in the doc
            String snippet;
            if(ncWords.size() > 0){
                snippet = findSmallSnippet(desc, cWords, 8);
            } else {
                snippet = findSnippet(desc, words, 30);
            }

            results.get(i).setDescription(snippet);
            results.get(i).setMissingTerms(ncWords, queryTerms);
        }
    }

    public static String findSmallSnippet(String bigText, List<String> keywords, int maxWords){
        ArrayList<String> result = new ArrayList<>();

        for(String word : keywords){
            ArrayList<String> dummyList = new ArrayList<>();
            dummyList.add(word);
            result.add(findSnippet(bigText, dummyList, maxWords));
        }

        return TextManipulator.joinWords(result, "...");
    }

    /**
     * Finds a snippet of text containing as many keywords as possible in a big text
     *
     * @param bigText the string in which the snippet has to be extracted
     * @param keywords the list of strings that are the keywords
     * @param maxWords the maximum number of words in the snippet
     * @return the snippet of text containing as many keywords as possible
     */
    public static String findSnippet(String bigText, List<String> keywords, int maxWords) {
        String[] words = bigText.split(" ");
        int bestStart = 0; // start index of the best snippet so far
        int bestEnd = 0; // end index of the best snippet so far
        int bestCount = 0; // number of keywords found in the best snippet so far
        ArrayList<String> foundedKeys = new ArrayList<>(); // keywords found in the current snippet
        keywords = TextManipulator.convertToLower(keywords);

        for (int i = 0; i < words.length; i++) {
            int start = i; // start index of the current snippet
            int end = Math.min(i + maxWords, words.length); // end index of the current snippet

            for (int j = start; j < end; j++) {
                for (String keyword : keywords) {
                    if(!foundedKeys.contains(keyword)){
                        if (words[j].toLowerCase().equals(keyword)) {
                            foundedKeys.add(keyword);
                        }
                    }
                }
            }

            if (foundedKeys.size() > bestCount) {
                bestStart = start;
                bestEnd = end;
                bestCount = foundedKeys.size();
            }

            if(foundedKeys.size() >= keywords.size()){
                break;
            }

            foundedKeys = new ArrayList<>();
        }
        StringBuilder snippetBuilder = new StringBuilder();
        for (int i = bestStart; i < bestEnd; i++) {
            snippetBuilder.append(words[i]);
            snippetBuilder.append(" ");
        }
        return TextManipulator.highlight(keywords, snippetBuilder.toString());
    }

    public ArrayList<SearchImageResult> joinImageResults(ArrayList<SearchImageResult> resultsCon, ArrayList<SearchImageResult> resultsDis){
        if(resultsCon.size() == 0) return resultsDis;
        if(resultsDis.size() == 0) return resultsCon;

        ArrayList<SearchImageResult> results = new ArrayList<>();

        for(SearchImageResult srCon : resultsCon){
            for(SearchImageResult srDis : resultsDis){
                if(srDis.getDocid() == srCon.getDocid()){
                    srCon.setScore(srDis.getScore() + srCon.getScore());
                    break;
                }
            }

            results.add(srCon);
        }

        return results;
    }

    public ArrayList<SearchResult> joinResults(ArrayList<SearchResult> resultsCon, ArrayList<SearchResult> resultsDis){
        if(resultsCon.size() == 0) return resultsDis;
        if(resultsDis.size() == 0) return resultsCon;

        ArrayList<SearchResult> results = new ArrayList<>();

        for(SearchResult srCon : resultsCon){
            for(SearchResult srDis : resultsDis){
                if(srDis.getDocid() == srCon.getDocid()){
                    srCon.setScore(srDis.getScore() + srCon.getScore());
                    break;
                }
            }

            results.add(srCon);
        }

        return results;
    }

    public Recolter searchNext(String queryTerms, String initialQt, Object k, String mode, String lang, String searchType){
        // split words
        List<String> words = TextManipulator.splitWords(queryTerms);
        // convert to lowercase
        words = TextManipulator.convertToLower(words);
        // remove stop words
        words = TextManipulator.removeStopWords(words, lang);
        // stemming
        List<String> stemmedWords = TextManipulator.stemming(words, lang);
        // construct new query
        String newQueryTerms = String.join(" ", stemmedWords);

        ArrayList<StatsEntity> statsResults;

        if(mode == "conjunctive"){
            if(searchType.equals("images")){
                ArrayList<SearchImageResult> results = getSearchImageResult(conjunctiveImageSearch(newQueryTerms, k, lang));
                return new Recolter(results);
            } else {
                ArrayList<SearchResult> results = getSearchResult(conjunctiveSearch(newQueryTerms, k, lang));
                statsResults = getStats(conjunctiveStats(newQueryTerms, lang));
                return new Recolter(results, statsResults);
            }
        } else {
            if(searchType.equals("images")){
                ArrayList<SearchImageResult> results = getSearchImageResult(disjunctiveImageSearch(newQueryTerms, k, lang));
                return new Recolter(results);
            } else {
                ArrayList<SearchResult> results = getSearchResult(disjunctiveSearch(newQueryTerms, k, lang));
                statsResults = getStats(disjunctiveStats(newQueryTerms, lang));
                return new Recolter(results, statsResults);
            }
        }
    }

    public HashMap<String, Integer> getTermsCounts(String lang, String type){
        String query;

        if(type.equals("images")){
            query = String.format("""
                SELECT f.term, COUNT(f.term) as nbOccurences
                FROM imagefeatures f, documents d
                WHERE f.docid = d.docid
                AND d.language = '%s'
                GROUP BY f.term;
                """, lang);
        } else {
            query = String.format("""
                SELECT f.term, COUNT(f.term) as nbOccurences
                FROM features f, documents d
                WHERE f.docid = d.docid
                AND d.language = '%s'
                GROUP BY f.term;
                """, lang);
        }

        ResultSet rs = executePutQuery(query);

        HashMap<String, Integer> result = new HashMap<>();
        if (rs == null)
            return result;

        try {
            while (rs.next()) {
                result.put(
                        rs.getString("term"),
                        rs.getInt("nbOccurences")
                );
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public String checkQuerySpelling(String query, String lang, String type){
        Boolean changed = false;

        // Get the list of terms in the db
        HashMap<String, Integer> dbWords = getTermsCounts(lang, type);

        // Split query in list of word
        List<String> qWords = TextManipulator.splitWords(query);

        // For each word compute the Levenshtein distance
        // Find the alternative for each word
        String newQuery = "";

        for(int i=0; i < qWords.size(); i++){
            String qWord = qWords.get(i);
            int qOcuurences = (dbWords.get(qWord) == null? 0 : dbWords.get(qWord));

            String mSimWord = qWord;
            int mLowerDist = 0;
            int mOcuurences = 0;

            // For each term in the DB
            for (String dbWord : dbWords.keySet()){
                if(dbWord != qWord){
                    int dist = TextManipulator.computeLevenshteinDistance(qWord, dbWord);

                    Boolean check1 = mLowerDist > 0 && dist < mLowerDist;
                    Boolean check2 = mLowerDist == 0;

                    if((check1 || check2) && dist < qWord.length()){
                        mLowerDist = dist;
                        mSimWord = dbWord;
                        mOcuurences = dbWords.get(dbWord);
                    }
                }
            }

            // Check if: A term occurs, but rarely, and there are much more frequently used
            //terms in the database that are very similar to the query term
            if(mSimWord != qWord && mOcuurences > qOcuurences){
                changed = true;
                newQuery += " " + mSimWord;
            } else {
                newQuery += " " + qWord;
            }
        }

        return (changed ? newQuery : query);
    }

}
