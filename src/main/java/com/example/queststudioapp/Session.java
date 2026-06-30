package com.example.queststudioapp;
import com.google.firebase.Timestamp;

public class Session {
    public String sessionId;
    public String questId;
    public Timestamp date_time;
    public int price;
    public String color_code;
    public boolean isBooked;

    public Session() {}

    public Session(String sessionId, String questId, Timestamp date_time, int price, String color_code, boolean isBooked) {
        this.sessionId = sessionId;
        this.questId = questId;
        this.date_time = date_time;
        this.price = price;
        this.color_code = color_code;
        this.isBooked = isBooked;
    }
}
