package com.example.queststudioapp;

import com.google.firebase.firestore.PropertyName;

public class Quest {
    private String questId;
    private String name;
    private String description;
    private String imageUrl;
    private String category;
    private String difficulty;
    private double price;
    private String ageLimit;

    public Quest() {}

    public String getQuestId() { return questId; }
    public void setQuestId(String questId) { this.questId = questId; }

    public String getName() { return name != null ? name : ""; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description != null ? description : ""; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category != null ? category : ""; }
    public void setCategory(String category) { this.category = category; }

    public String getDifficulty() { return difficulty != null ? difficulty : ""; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }

    public String getAgeLimit() { return ageLimit != null ? ageLimit : ""; }
    public void setAgeLimit(String ageLimit) { this.ageLimit = ageLimit; }

    // Безопасное получение цены
    public double getPrice() { return price; }
    public void setPrice(Object price) {
        if (price instanceof Double) {
            this.price = (Double) price;
        } else if (price instanceof Long) {
            this.price = ((Long) price).doubleValue();
        } else if (price instanceof String) {
            try {
                this.price = Double.parseDouble((String) price);
            } catch (Exception e) {
                this.price = 0.0;
            }
        }
    }
}
