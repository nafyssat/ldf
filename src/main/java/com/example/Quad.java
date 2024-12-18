package com.example;
public class Quad {
    private String subject;
    private String predicate;
    private String object;
    private String graph;

    public Quad(String subject, String predicate, String object, String graph) {
        this.subject = subject;
        this.predicate = predicate;
        this.object = object;
        this.graph = graph;
    }
    public String getSubject() { return subject; }
    public String getPredicate() { return predicate; }
    public String getObject() { return object; }
    public String getGraph() { return graph; }
}
