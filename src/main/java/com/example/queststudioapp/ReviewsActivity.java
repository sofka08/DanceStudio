package com.example.queststudioapp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewsActivity extends AppCompatActivity {
    private FirebaseFirestore db;
    private String questId, userFio, userRole;
    private List<Review> reviewList;
    private ReviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);

        db = FirebaseFirestore.getInstance();
        questId = getIntent().getStringExtra("quest_id");

        SharedPreferences prefs = getSharedPreferences("QuestPrefs", MODE_PRIVATE);
        userFio = prefs.getString("userFio", "Аноним");
        userRole = prefs.getString("userRole", "client");

        RecyclerView rv = findViewById(R.id.reviewsRecyclerView);
        EditText et = findViewById(R.id.etReviewComment);
        RatingBar rb = findViewById(R.id.ratingBarInput);
        Button btn = findViewById(R.id.btnSendReview);

        // Админ не пишет отзывы
        if ("admin".equals(userRole)) {
            findViewById(R.id.layoutAddReview).setVisibility(View.GONE);
        }

        reviewList = new ArrayList<>();
        adapter = new ReviewAdapter(reviewList, "admin".equals(userRole), review -> {
            // ПОДТВЕРЖДЕНИЕ УДАЛЕНИЯ ОТЗЫВА
            new AlertDialog.Builder(this)
                    .setTitle("Удалить отзыв?")
                    .setMessage("Вы уверены, что хотите удалить этот отзыв?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        db.collection("reviews").document(review.getReviewId()).delete()
                                .addOnSuccessListener(aVoid -> loadReviews());
                    })
                    .setNegativeButton("Нет", null)
                    .show();
        });

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
        loadReviews();

        btn.setOnClickListener(v -> {
            String comment = et.getText().toString().trim();
            if (comment.isEmpty()) return;

            Map<String, Object> map = new HashMap<>();
            map.put("questId", questId);
            map.put("comment", comment);
            map.put("rating", rb.getRating());
            map.put("userName", userFio); // ИСПОЛЬЗУЕМ РЕАЛЬНОЕ ФИО ИЗ ПАМЯТИ

            db.collection("reviews").add(map).addOnSuccessListener(d -> {
                et.setText("");
                rb.setRating(5);
                loadReviews();
                Toast.makeText(this, "Отзыв отправлен!", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void loadReviews() {
        db.collection("reviews").whereEqualTo("questId", questId).get().addOnSuccessListener(queryDocumentSnapshots -> {
            reviewList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Review r = doc.toObject(Review.class);
                r.setReviewId(doc.getId());
                reviewList.add(r);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
