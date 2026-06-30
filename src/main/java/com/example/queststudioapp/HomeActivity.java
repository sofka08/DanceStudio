package com.example.queststudioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class HomeActivity extends AppCompatActivity {

    private TextView userNameTitle;
    private Button btnAllQuests, btnCategories, btnMyBookings, btnAdminActors, btnLogout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userNameTitle = findViewById(R.id.userNameTitle);
        btnAllQuests = findViewById(R.id.btnAllQuests);
        btnCategories = findViewById(R.id.btnCategories);
        btnMyBookings = findViewById(R.id.btnMyBookings);
        btnAdminActors = findViewById(R.id.btnAdminActors);
        btnLogout = findViewById(R.id.btnLogout);

        SharedPreferences prefs = getSharedPreferences("QuestPrefs", MODE_PRIVATE);
        boolean isGuest = prefs.getBoolean("isGuest", false);

        if (isGuest) {
            userNameTitle.setText("ГОСТЕВОЙ ДОСТУП");
            btnMyBookings.setVisibility(View.GONE);
            btnAdminActors.setVisibility(View.GONE);
            prefs.edit().putString("userRole", "client").putString("userFio", "Гость").apply();
        } else {
            loadUserData();
        }

        btnAllQuests.setOnClickListener(v -> startActivity(new Intent(this, AllQuestsActivity.class)));
        btnCategories.setOnClickListener(v -> startActivity(new Intent(this, CategoriesActivity.class)));
        btnMyBookings.setOnClickListener(v -> startActivity(new Intent(this, MyBookingsActivity.class)));
        btnAdminActors.setOnClickListener(v -> startActivity(new Intent(this, AdminActorsActivity.class)));

        btnLogout.setOnClickListener(v -> {
            mAuth.signOut();
            getSharedPreferences("QuestPrefs", MODE_PRIVATE).edit().clear().apply();
            startActivity(new Intent(HomeActivity.this, MainActivity.class));
            finish();
        });
    }

    private void loadUserData() {
        if (mAuth.getCurrentUser() == null) return;
        String uid = mAuth.getCurrentUser().getUid();
        db.collection("users").document(uid).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                String name = doc.getString("fio");
                String role = doc.getString("role");
                userNameTitle.setText("ПРИВЕТ, " + (name != null ? name.toUpperCase() : "ПОЛЬЗОВАТЕЛЬ"));

                SharedPreferences.Editor editor = getSharedPreferences("QuestPrefs", MODE_PRIVATE).edit();
                editor.putString("userRole", role != null ? role : "client");
                editor.putString("userFio", name != null ? name : "Пользователь"); // СОХРАНЯЕМ ФИО
                editor.apply();

                if ("admin".equals(role)) {
                    btnAdminActors.setVisibility(View.VISIBLE);
                    btnMyBookings.setText("ВСЕ БРОНИРОВАНИЯ");
                } else {
                    btnAdminActors.setVisibility(View.GONE);
                    btnMyBookings.setText("МОИ БРОНИРОВАНИЯ");
                }
            }
        });
    }
}
