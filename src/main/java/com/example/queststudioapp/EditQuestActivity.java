package com.example.queststudioapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class EditQuestActivity extends AppCompatActivity {

    private EditText nameInp, descInp, categoryInp, diffInp, priceInp, imageInp;
    private Button btnSave;
    private TextView titlePage;
    private FirebaseFirestore db;
    private String questId = null; // Если null - добавляем новый, если есть - редактируем

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_quest);

        db = FirebaseFirestore.getInstance();

        titlePage = findViewById(R.id.editQuestTitle);
        nameInp = findViewById(R.id.editQuestName);
        descInp = findViewById(R.id.editQuestDescription);
        categoryInp = findViewById(R.id.editQuestCategory);
        diffInp = findViewById(R.id.editQuestDifficulty);
        priceInp = findViewById(R.id.editQuestPrice);
        imageInp = findViewById(R.id.editQuestImageUrl);
        btnSave = findViewById(R.id.btnSaveQuest);

        // Проверяем, передали ли нам ID для редактирования
        questId = getIntent().getStringExtra("quest_id");

        if (questId != null) {
            titlePage.setText("РЕДАКТИРОВАНИЕ");
            loadQuestData();
        } else {
            titlePage.setText("НОВЫЙ КВЕСТ");
        }

        btnSave.setOnClickListener(v -> saveQuest());
    }

    private void loadQuestData() {
        db.collection("quests").document(questId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                nameInp.setText(doc.getString("name"));
                descInp.setText(doc.getString("description"));
                categoryInp.setText(doc.getString("category"));
                diffInp.setText(doc.getString("difficulty"));
                imageInp.setText(doc.getString("imageUrl"));
                // Обработка цены (может быть числом или строкой)
                Object priceObj = doc.get("price");
                priceInp.setText(String.valueOf(priceObj != null ? priceObj : 0));
            }
        });
    }

    private void saveQuest() {
        String name = nameInp.getText().toString().trim();
        String desc = descInp.getText().toString().trim();
        String category = categoryInp.getText().toString().trim();
        String diff = diffInp.getText().toString().trim();
        String priceStr = priceInp.getText().toString().trim();
        String image = imageInp.getText().toString().trim();

        if (name.isEmpty() || desc.isEmpty() || category.isEmpty() || priceStr.isEmpty()) {
            Toast.makeText(this, "Заполните основные поля", Toast.LENGTH_SHORT).show();
            return;
        }

        double price = Double.parseDouble(priceStr);

        Map<String, Object> questMap = new HashMap<>();
        questMap.put("name", name);
        questMap.put("description", desc);
        questMap.put("category", category);
        questMap.put("difficulty", diff);
        questMap.put("price", price);
        questMap.put("imageUrl", image);

        if (questId == null) {
            // Создаем новый
            db.collection("quests").add(questMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Квест добавлен!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            // Обновляем существующий
            db.collection("quests").document(questId).set(questMap)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Данные обновлены!", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        }
    }
}
