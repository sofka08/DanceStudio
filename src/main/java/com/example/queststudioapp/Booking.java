package com.example.queststudioapp;

import com.google.firebase.firestore.ServerTimestamp;
import java.util.Date;

public class Booking {
    private String bookingId; // ID самого документа бронирования
    private String userId;
    private String questId;
    private String questName;
    private String date;
    private String time;
    private int numberOfPlayers;
    private String status; // Например: "pending", "confirmed", "cancelled"
    private Date createdAt;

    public Booking() {
        // Пустой конструктор нужен для Firebase
    }

    public Booking(String bookingId, String userId, String questId, String questName, String date, String time, int numberOfPlayers, String status, Date createdAt) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.questId = questId;
        this.questName = questName;
        this.date = date;
        this.time = time;
        this.numberOfPlayers = numberOfPlayers;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры для всех полей
    public String getBookingId() {
        return bookingId;
    }

    public void setBookingId(String bookingId) {
        this.bookingId = bookingId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getQuestId() {
        return questId;
    }

    public void setQuestId(String questId) {
        this.questId = questId;
    }

    public String getQuestName() {
        return questName;
    }

    public void setQuestName(String questName) {
        this.questName = questName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int numberOfPlayers) {
        this.numberOfPlayers = numberOfPlayers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @ServerTimestamp // Эта аннотация автоматически заполняет время на сервере
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
