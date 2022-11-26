package com.rapid.searchengine;

import java.io.*;
import java.util.ArrayList;

import DB.*;
import DB.Entities.DocumentEntity;
import DB.Entities.SearchResult;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "searchServlet", value = "/search")
public class SearchServlet extends HttpServlet {
    DB db;

    public void init() {
        db = new DB(DBVars.dbName, DBVars.dbUser, DBVars.dbPass);
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String query = request.getParameter("q");

        ArrayList<SearchResult> results = db.search(query, 20, null);
        ArrayList<DocumentEntity> resultsDoc = new ArrayList<>();

        for(int i=0; i < results.size(); i++){
            SearchResult res = results.get(i);
            ArrayList<DocumentEntity> docs = db.getDocuments(db.searchByAtt("documents", "docid", res.getDocid()));

            if(docs.size() > 0){
                resultsDoc.add(docs.get(0));
            }
        }

        request.setAttribute("results", resultsDoc);
        this.getServletContext().getRequestDispatcher("/Search.jsp").forward(request, response);
    }

    public void destroy() {
    }
}