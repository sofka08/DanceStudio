package com.example.queststudioapp;

import java.io.Serializable;

public class Actor implements Serializable {
    private String actorId;
    private String name;
    private String role;
    private String contacts;
    private String imageUrl;

    public Actor() {
        // Пустой конструктор для Firebase
    }

    // Конструктор на 5 полей (тот самый, который требует EditActorActivity)
    public Actor(String actorId, String name, String role, String contacts, String imageUrl) {
        this.actorId = actorId;
        this.name = name;
        this.role = role;
        this.contacts = contacts;
        this.imageUrl = imageUrl;
    }

    public String getActorId() { return actorId; }
    public void setActorId(String actorId) { this.actorId = actorId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getContacts() { return contacts; }
    public void setContacts(String contacts) { this.contacts = contacts; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}
