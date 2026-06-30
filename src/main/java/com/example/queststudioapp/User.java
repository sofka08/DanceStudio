package com.example.queststudioapp; // проверь, чтобы это совпадало с твоим названием пакета

public class User {
    public String userId;
    public String fio;
    public String email;
    public String phone;
    public String role; // "admin" или "client"

    // Пустой конструктор нужен для Firebase
    public User() {}

    // Конструктор для создания пользователя
    public User(String userId, String fio, String email, String phone, String role) {
        this.userId = userId;
        this.fio = fio;
        this.email = email;
        this.phone = phone;
        this.role = role;
    }
}
