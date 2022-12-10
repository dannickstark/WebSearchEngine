package com.rapid.searchengine;

import java.io.*;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;

import DB.*;
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

        long startTime = System.nanoTime();
        Recolter rec = db.search(query, 20);
        ArrayList<SearchResult> results = rec.results;
        long estimatedTime = System.nanoTime() - startTime;
        long elapsedTime = estimatedTime / 1000000000;

        request.setAttribute("query", query);
        request.setAttribute("results", results);
        request.setAttribute("elapsedTime", elapsedTime);
        this.getServletContext().getRequestDispatcher("/Search.jsp").forward(request, response);
    }

    public void destroy() {
    }
}