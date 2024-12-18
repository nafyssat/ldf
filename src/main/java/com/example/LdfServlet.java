package com.example;

import com.google.gson.Gson;
import com.google.cloud.datastore.Entity;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ldf")
public class LdfServlet extends HttpServlet {
    private final DatastoreManager datastoreManager = new DatastoreManager();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        Gson gson = new Gson();
        Map<String, Object> res = new HashMap<>();

        String subject = request.getParameter("subject");
        String predicate = request.getParameter("predicate");
        String object = request.getParameter("object");
        String graph = request.getParameter("graph");

        int page = 1; // Default page
        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            page = 1;
        }

        int pageSize = 10; // Default page size

        // Fetch results using the DatastoreManager
        List<Entity> quads = datastoreManager.getFilteredQuads(subject, predicate, object, graph, page, pageSize);

        if (quads.isEmpty()) {
            res.put("message", "No matching quads found.");
        } else {
            res.put("results", quads);
            res.put("page", page);
            res.put("pageSize", pageSize);
        }

        res.put("executionTimeMs", System.currentTimeMillis());
        response.getWriter().println(gson.toJson(res));
    }
    @Override
protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String action = request.getParameter("action");

    if ("insertTestData".equals(action)) {
        datastoreManager.insertTestData();
        response.getWriter().println("Test data inserted successfully.");
    } else {
        response.getWriter().println("No action specified.");
    }
}
}