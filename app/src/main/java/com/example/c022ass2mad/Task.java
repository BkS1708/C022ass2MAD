package com.example.c022ass2mad;

public class Task {
    private String id;
    private String name;        // Task Name (new)
    private String description; // Task Description (new)
    private String priority;

    public Task() {}

    public Task(String id, String name, String description, String priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priority = priority;
    }

    // Getters & Setters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getPriority() { return priority; }

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setPriority(String priority) { this.priority = priority; }
}


