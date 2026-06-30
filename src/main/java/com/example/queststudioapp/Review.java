package com.example.queststudioapp;

public class Review {
    private String reviewId;
    private String questId;
    private String userName;
    private String comment;
    private float rating;

    public Review() {} // Для Firebase

    public Review(String reviewId, String questId, String userName, String comment, float rating) {
        this.reviewId = reviewId;
        this.questId = questId;
        this.userName = userName;
        this.comment = comment;
        this.rating = rating;
    }

    public String getReviewId() { return reviewId; }
    public void setReviewId(String reviewId) { this.reviewId = reviewId; }
    public String getQuestId() { return questId; }
    public String getUserName() { return userName; }
    public String getComment() { return comment; }
    public float getRating() { return rating; }
}
