package com.example.queststudioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

public class QuestDetailActivity extends AppCompatActivity {

    private ImageView detailQuestImage;
    private TextView detailQuestName, detailQuestCategory, detailQuestDifficulty, detailQuestDescription;
    private Button btnBookQuest, btnGoToReviews;
    private FirebaseFirestore db;
    private String currentQuestId, currentQuestName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quest_detail);

        db = FirebaseFirestore.getInstance();
        currentQuestId = getIntent().getStringExtra("quest_id");

        detailQuestImage = findViewById(R.id.detailQuestImage);
        detailQuestName = findViewById(R.id.detailQuestName);
        detailQuestCategory = findViewById(R.id.detailQuestCategory);
        detailQuestDifficulty = findViewById(R.id.detailQuestDifficulty);
        detailQuestDescription = findViewById(R.id.detailQuestDescription);
        btnBookQuest = findViewById(R.id.btnBookQuest);
        btnGoToReviews = findViewById(R.id.btnGoToReviews);

        SharedPreferences prefs = getSharedPreferences("QuestPrefs", MODE_PRIVATE);
        String role = prefs.getString("userRole", "client");

        // УБИРАЕМ КНОПКУ ДЛЯ АДМИНА
        if ("admin".equals(role)) {
            btnBookQuest.setVisibility(View.GONE);
        }

        loadQuestDetails();

        btnBookQuest.setOnClickListener(v -> {
            Intent intent = new Intent(this, BookingActivity.class);
            intent.putExtra("quest_id", currentQuestId);
            intent.putExtra("quest_name", currentQuestName);
            startActivity(intent);
        });

        btnGoToReviews.setOnClickListener(v -> {
            Intent intent = new Intent(this, ReviewsActivity.class);
            intent.putExtra("quest_id", currentQuestId);
            startActivity(intent);
        });
    }

    private void loadQuestDetails() {
        db.collection("quests").document(currentQuestId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Quest quest = doc.toObject(Quest.class);
                currentQuestName = quest.getName();
                detailQuestName.setText(quest.getName());
                detailQuestDescription.setText(quest.getDescription());
                detailQuestCategory.setText("Категория: " + quest.getCategory());
                detailQuestDifficulty.setText("Сложность: " + quest.getDifficulty());
                Glide.with(this).load(quest.getImageUrl()).into(detailQuestImage);
            }
        });
    }
}
