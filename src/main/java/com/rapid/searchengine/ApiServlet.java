package com.rapid.searchengine;

import DB.*;
import DB.Entities.ApiResponse;
import DB.Entities.SearchResult;
import DB.Entities.StatsEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

@WebServlet(name = "api", value = "/api")
public class ApiServlet extends HttpServlet {
    DB db;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public void init() {
        db = new DB(DBVars.dbName, DBVars.dbUser, DBVars.dbPass);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("q");
        String k = request.getParameter("k");

        Integer max = null;
        if(k != null){
            max = Integer.parseInt(k);
        }

        Recolter rec = db.search(query, max);
        ArrayList<SearchResult> results = rec.results;
        ArrayList<StatsEntity> statsResults = rec.statsResults;

        Integer cw = db.getNumberOfTerms();

        ApiResponse apiR = new ApiResponse(results, query, max, statsResults, cw);
        String resultsJsonString = gson.toJson(apiR);

        PrintWriter out = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(resultsJsonString);
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
