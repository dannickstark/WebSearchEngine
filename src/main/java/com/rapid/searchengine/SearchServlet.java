package com.rapid.searchengine;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import DB.*;
import DB.Entities.SearchResult;
import com.google.common.util.concurrent.RateLimiter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

@WebServlet(name = "searchServlet", value = "/search")
public class SearchServlet extends HttpServlet {
    DB db;
    // Map to store rate limiters keyed by IP address
    private static final Map<String, RateLimiter> rateLimiters = new ConcurrentHashMap<>();

    public void init() {
        db = new DB(DBVars.dbPort, DBVars.dbName, DBVars.dbUser, DBVars.dbPass);
        db.computePageRank();
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String query = request.getParameter("q");
        String ipAddress = request.getRemoteAddr();
        String lang = request.getParameter("language");

        RateLimiter rateLimiter = rateLimiters.get(ipAddress);

        if (rateLimiter == null) {
            // Create a new RateLimiter for this IP address with a rate of 1 request per second
            rateLimiter = RateLimiter.create(1.0);
            rateLimiters.put(ipAddress, rateLimiter);
        }

        // Acquire a permit from the RateLimiter, blocking if necessary
        if (rateLimiter.tryAcquire()) {
            // Check if the request come within the same network
            Boolean isInNetwork = Utilities.isSameNetwork(request);

            if(lang == null){
                lang = getLanguage(request);
            }

            long startTime = System.nanoTime();
            Recolter rec = db.search(query, 20, lang);
            ArrayList<SearchResult> results = rec.results;
            long estimatedTime = System.nanoTime() - startTime;
            long elapsedTime = estimatedTime / 1000000000;


            String alternativeQ = db.checkQuerySpelling(query);
            if(alternativeQ != query){
                request.setAttribute("alternativeQ", alternativeQ);
            }

            request.setAttribute("query", query);
            request.setAttribute("language", lang);
            request.setAttribute("results", results);
            request.setAttribute("elapsedTime", elapsedTime);
            request.setAttribute("isInNetwork", isInNetwork);
            this.getServletContext().getRequestDispatcher("/Search.jsp").forward(request, response);
        } else {
            // Rate limit exceeded, return error response
            response.sendError(429, "Rate limit exceeded");
        }
    }

    private String getLanguage(HttpServletRequest request) {
        Locale browserLocale = request.getLocale();
        String language = browserLocale.getLanguage();
        String country = browserLocale.getCountry();

        return language;
    }

    public void destroy() {
    }
}