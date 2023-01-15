package com.rapid.searchengine;

import DB.*;
import DB.Entities.ApiResponse;
import DB.Entities.SearchResult;
import DB.Entities.StatsEntity;
import com.google.common.util.concurrent.RateLimiter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//@WebServlet(name = "json", value = "/json")
public class ApiServlet extends HttpServlet {
    DB db;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    // Map to store rate limiters keyed by IP address
    private static final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    public void init() {
        db = new DB(DBVars.dbPort, DBVars.dbName, DBVars.dbUser, DBVars.dbPass);
        db.computePageRank();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String query = request.getParameter("query");
        String k = request.getParameter("k");
        String lang = request.getParameter("language");
        String ipAddress = request.getRemoteAddr();

        RateLimiter rateLimiter = rateLimiters.get(ipAddress);

        if (rateLimiter == null) {
            // Create a new RateLimiter for this IP address with a rate of 1 request per second
            rateLimiter = RateLimiter.create(1.0);
            rateLimiters.put(ipAddress, rateLimiter);
        }

        // Acquire a permit from the RateLimiter, blocking if necessary
        if (rateLimiter.tryAcquire()) {
            // Handle request
            // Check if the request come within the same network
            Boolean isInNetwork = Utilities.isSameNetwork(request);

            Integer max = null;
            if(k != null){
                max = Integer.parseInt(k);
            }

            Recolter rec = db.search(query, max, lang);
            ArrayList<SearchResult> results = rec.results;
            ArrayList<StatsEntity> statsResults = rec.statsResults;

            Integer cw = db.getNumberOfTerms();

            // Remove confidentials documents
            if(!isInNetwork){
                ArrayList<SearchResult> collect = new ArrayList<>();

                for(int i=0; i < results.size(); i++){
                    SearchResult current = results.get(i);
                    if(!current.getInternal()){
                        collect.add(current);
                    }
                }

                results = collect;
            }

            ApiResponse apiR = new ApiResponse(results, query, max, statsResults, cw);
            String resultsJsonString = gson.toJson(apiR);

            PrintWriter out = response.getWriter();
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(resultsJsonString);
            out.flush();
        } else {
            // Rate limit exceeded, return error response
            response.sendError(429, "Rate limit exceeded");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
