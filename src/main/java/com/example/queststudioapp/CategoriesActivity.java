package com.example.queststudioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CategoriesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<Category> categoryList;
    private FirebaseFirestore db;
    private String role;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        db = FirebaseFirestore.getInstance();
        SharedPreferences prefs = getSharedPreferences("QuestPrefs", MODE_PRIVATE);
        role = prefs.getString("userRole", "client");

        recyclerView = findViewById(R.id.categoriesRecyclerView);
        fabAdd = findViewById(R.id.btnAddCategoryFab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        categoryList = new ArrayList<>();

        boolean isAdmin = "admin".equals(role);
        adapter = new CategoryAdapter(categoryList, isAdmin, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(Category category) {
                // Переходим ко всем квестам этой категории
                Intent intent = new Intent(CategoriesActivity.this, AllQuestsActivity.class);
                intent.putExtra("category", category.getName());
                startActivity(intent);
            }

            @Override
            public void onDeleteClick(Category category) {
                new AlertDialog.Builder(CategoriesActivity.this)
                        .setTitle("Удалить категорию?")
                        .setMessage("Вы уверены, что хотите удалить категорию " + category.getName() + "?")
                        .setPositiveButton("Да", (dialog, which) -> {
                            db.collection("categories").document(category.getId()).delete()
                                    .addOnSuccessListener(aVoid -> loadCategories());
                        })
                        .setNegativeButton("Нет", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        if (isAdmin) {
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabAdd.setVisibility(View.GONE);
        }

        fabAdd.setOnClickListener(v -> showAddCategoryDialog());

        loadCategories();
    }

    private void loadCategories() {
        db.collection("categories").get().addOnSuccessListener(queryDocumentSnapshots -> {
            categoryList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Category cat = doc.toObject(Category.class);
                cat.setId(doc.getId());
                categoryList.add(cat);
            }
            // Если коллекция в БД пустая, создадим базовые категории для первого запуска
            if (categoryList.isEmpty()) {
                createDefaultCategories();
            } else {
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Новая категория");

        final EditText input = new EditText(this);
        input.setHint("Например, Хоррор");
        builder.setView(input);

        builder.setPositiveButton("Добавить", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                Map<String, Object> catMap = new HashMap<>();
                catMap.put("name", name);
                db.collection("categories").add(catMap).addOnSuccessListener(doc -> loadCategories());
            }
        });
        builder.setNegativeButton("Отмена", null);
        builder.show();
    }

    private void createDefaultCategories() {
        String[] defaults = {"Мистика", "Хоррор", "Приключения", "Детектив"};
        for (String s : defaults) {
            Map<String, Object> catMap = new HashMap<>();
            catMap.put("name", s);
            db.collection("categories").add(catMap);
        }
        // Перезагружаем список
        db.collection("categories").get().addOnSuccessListener(queryDocumentSnapshots -> {
            categoryList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Category cat = doc.toObject(Category.class);
                cat.setId(doc.getId());
                categoryList.add(cat);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
