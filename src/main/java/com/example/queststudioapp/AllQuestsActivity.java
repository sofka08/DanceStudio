package com.example.queststudioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AllQuestsActivity extends AppCompatActivity implements QuestAdapter.OnQuestClickListener {

    private RecyclerView recyclerView;
    private QuestAdapter adapter;
    private List<Quest> questList;
    private FirebaseFirestore db;
    private String role;
    private String categoryFilter;
    private FloatingActionButton fabAdd;
    private EditText searchQuests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_quests);

        db = FirebaseFirestore.getInstance();
        categoryFilter = getIntent().getStringExtra("category");

        SharedPreferences prefs = getSharedPreferences("QuestPrefs", MODE_PRIVATE);
        role = prefs.getString("userRole", "client");

        recyclerView = findViewById(R.id.questsRecyclerView);
        fabAdd = findViewById(R.id.btnAddQuestFab);
        searchQuests = findViewById(R.id.searchQuests);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        questList = new ArrayList<>();
        adapter = new QuestAdapter(questList, this, this);
        recyclerView.setAdapter(adapter);

        // Показываем Плюс только админу
        if ("admin".equals(role)) {
            fabAdd.setVisibility(View.VISIBLE);
        } else {
            fabAdd.setVisibility(View.GONE);
        }

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(AllQuestsActivity.this, EditQuestActivity.class));
        });

        searchQuests.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        loadQuests();
    }

    private void loadQuests() {
        db.collection("quests").get().addOnSuccessListener(queryDocumentSnapshots -> {
            questList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Quest q = doc.toObject(Quest.class);
                q.setQuestId(doc.getId());

                if (categoryFilter == null || q.getCategory().equalsIgnoreCase(categoryFilter)) {
                    questList.add(q);
                }
            }
            adapter.updateList(questList);
        });
    }

    @Override
    public void onQuestClick(Quest quest) {
        Intent intent = new Intent(this, QuestDetailActivity.class);
        intent.putExtra("quest_id", quest.getQuestId());
        startActivity(intent);
    }

    @Override
    public void onDeleteClick(Quest quest) {
        new AlertDialog.Builder(this)
                .setTitle("Удалить квест?")
                .setMessage("Вы уверены, что хотите удалить квест " + quest.getName() + "?")
                .setPositiveButton("Да", (dialog, which) -> {
                    db.collection("quests").document(quest.getQuestId()).delete()
                            .addOnSuccessListener(aVoid -> loadQuests());
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    @Override
    public void onEditClick(Quest quest) {
        Intent intent = new Intent(this, EditQuestActivity.class);
        intent.putExtra("quest_id", quest.getQuestId());
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadQuests();
    }
}
