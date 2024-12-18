package com.example;

import com.google.gson.Gson;
import com.google.cloud.datastore.Entity;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/ldf")
public class LdfServlet extends HttpServlet {
    private final DatastoreManager datastoreManager = new DatastoreManager();

    
    @Override
protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    response.setContentType("application/json");
    response.setCharacterEncoding("UTF-8");
    Gson gson = new Gson();

    Map<String, Object> res = new HashMap<>();
    StringBuilder sb = new StringBuilder();
    String line;

    // Lire les données JSON du corps de la requête
    try (BufferedReader reader = request.getReader()) {
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
    }

    // Parser le JSON en Map
    Map<String, Object> jsonData = gson.fromJson(sb.toString(), Map.class);

    // Récupérer les paramètres en s'assurant du bon type
    String subject = jsonData.get("subject") != null ? jsonData.get("subject").toString() : null;
    String predicate = jsonData.get("predicate") != null ? jsonData.get("predicate").toString() : null;
    String object = jsonData.get("object") != null ? jsonData.get("object").toString() : null;
    String graph = jsonData.get("graph") != null ? jsonData.get("graph").toString() : null;

    System.out.println("subject: " + subject);

    // Gestion des paramètres numériques
    int page = 1; // Default page
    try {
        if (jsonData.get("page") != null) {
            page = ((Double) jsonData.get("page")).intValue(); // Convertir un Double en int
        }
    } catch (ClassCastException | NumberFormatException e) {
        page = 1; // Valeur par défaut en cas d'erreur
    }

    int pageSize = 10; // Default page size

    // Appeler DatastoreManager pour récupérer les quads
    List<Entity> quads = datastoreManager.getFilteredQuads(subject, predicate, object, graph, page, pageSize);
    List<Map<String, String>> formattedResults = new ArrayList<>();
    for (Entity quad : quads) {
        Map<String, String> result = new HashMap<>();
        result.put("subject", quad.getString("subject"));
        result.put("predicate", quad.getString("predicate"));
        result.put("object", quad.getString("object"));
        result.put("graph", quad.getString("graph"));
        formattedResults.add(result);
    }
    // Construire la réponse
    if (formattedResults.isEmpty()) {
        res.put("message", "No matching quads found.");
    } else {
        res.put("results", formattedResults);
        res.put("page", page);
        res.put("pageSize", pageSize);
    }

    res.put("executionTimeMs", System.currentTimeMillis());

    // Retourner la réponse en JSON
    response.getWriter().println(gson.toJson(res));
}
}