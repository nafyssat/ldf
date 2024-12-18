// Updated DatastoreManager.java
package com.example;

import com.google.cloud.datastore.*;

import java.util.ArrayList;
import java.util.List;


public class DatastoreManager {
    private final Datastore datastore;

    public DatastoreManager() {
    this.datastore=DatastoreOptions.newBuilder()

        .setProjectId("tinyldf-datastore")
        .build()
        .getService();    
}

    public void insertQuad(String subject, String predicate, String object, String graph) {
        if (subject == null || predicate == null || object == null || graph == null) {
            throw new IllegalArgumentException("All parameters (subject, predicate, object, graph) must be non-null.");
        }

        com.google.cloud.datastore.Key key = datastore.newKeyFactory()
                .setKind("Quad")
                .newKey(subject + ":" + predicate);

        Entity quad = Entity.newBuilder(key)
                .set("subject", subject)
                .set("predicate", predicate)
                .set("object", object)
                .set("graph", graph)
                .build();

        datastore.put(quad);
        System.out.println("Inserted entity: " + quad);
    }

    public List<Entity> getFilteredQuads(String subject, String predicate, String object, String graph, int page, int pageSize) {
        // Return empty list if all parameters are null or empty
        if ((subject == null || subject.trim().isEmpty()) &&
            (predicate == null || predicate.trim().isEmpty()) &&
            (object == null || object.trim().isEmpty()) &&
            (graph == null || graph.trim().isEmpty())) {
                System.out.println("pas de chance tout est vide");
            return new ArrayList<>();
        }

        List<Entity> results = new ArrayList<>();

        if (subject != null && !subject.trim().isEmpty()) {
            System.out.println("on a trouvé un sujet "+subject);

            Query<Entity> subjectQuery = Query.newEntityQueryBuilder()
                    .setKind("Quad")
                    .setFilter(StructuredQuery.PropertyFilter.eq("subject", subject))
                    .setLimit(pageSize)
                    .setOffset((page - 1) * pageSize)
                    .build();
            datastore.run(subjectQuery).forEachRemaining(results::add);
            System.out.println(results+"result");
        }

        if (predicate != null && !predicate.trim().isEmpty()) {
            Query<Entity> predicateQuery = Query.newEntityQueryBuilder()
                    .setKind("Quad")
                    .setFilter(StructuredQuery.PropertyFilter.eq("predicate", predicate))
                    .setLimit(pageSize)
                    .setOffset((page - 1) * pageSize)
                    .build();
            datastore.run(predicateQuery).forEachRemaining(results::add);
        }

        if (object != null && !object.trim().isEmpty()) {
            Query<Entity> objectQuery = Query.newEntityQueryBuilder()
                    .setKind("Quad")
                    .setFilter(StructuredQuery.PropertyFilter.eq("object", object))
                    .setLimit(pageSize)
                    .setOffset((page - 1) * pageSize)
                    .build();
            datastore.run(objectQuery).forEachRemaining(results::add);
        }

        if (graph != null && !graph.trim().isEmpty()) {
            Query<Entity> graphQuery = Query.newEntityQueryBuilder()
                    .setKind("Quad")
                    .setFilter(StructuredQuery.PropertyFilter.eq("graph", graph))
                    .setLimit(pageSize)
                    .setOffset((page - 1) * pageSize)
                    .build();
            datastore.run(graphQuery).forEachRemaining(results::add);
        }

        return results;
    }
    public void insertTestData() {
    String[][] testData = {
        {"Person1", "knows", "Person2", "Graph1"},
        {"Person3", "likes", "Person4", "Graph2"},
        {"Person5", "dislikes", "Person6", "Graph1"},
        {"Person7", "works_with", "Person8", "Graph3"},
        {"Person9", "friends_with", "Person10", "Graph2"},
        {"Person11", "colleague_of", "Person12", "Graph3"},
        {"Person13", "related_to", "Person14", "Graph1"},
        {"Person15", "mentor_of", "Person16", "Graph4"},
        {"Person17", "neighbor_of", "Person18", "Graph4"},
        {"Person19", "knows", "Person20", "Graph5"}
    };

    for (String[] quad : testData) {
        insertQuad(quad[0], quad[1], quad[2], quad[3]);
    }

    System.out.println("Test data inserted successfully.");
}

}
