package com.example.queststudioapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class AdminActorsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ActorAdapter adapter;
    private List<Actor> actorList;
    private FirebaseFirestore db;
    private FloatingActionButton fabAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_actors);

        db = FirebaseFirestore.getInstance();
        recyclerView = findViewById(R.id.actorsRecyclerView);
        fabAdd = findViewById(R.id.btnAddActorFab);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        actorList = new ArrayList<>();

        // ИСПРАВЛЕНО: Вместо стрелочки пишем четко OnDelete и OnEdit
        adapter = new ActorAdapter(actorList, new ActorAdapter.OnActorActionListener() {
            @Override
            public void onDelete(Actor actor) {
                db.collection("actors").document(actor.getActorId())
                        .delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(AdminActorsActivity.this, "Актер удален", Toast.LENGTH_SHORT).show();
                            loadActors();
                        });
            }

            @Override
            public void onEdit(Actor actor) {
                Intent intent = new Intent(AdminActorsActivity.this, EditActorActivity.class);
                intent.putExtra("actor_id", actor.getActorId());
                startActivity(intent);
            }
        });

        recyclerView.setAdapter(adapter);

        fabAdd.setOnClickListener(v -> {
            startActivity(new Intent(AdminActorsActivity.this, EditActorActivity.class));
        });

        loadActors();
    }

    private void loadActors() {
        db.collection("actors").get().addOnSuccessListener(queryDocumentSnapshots -> {
            actorList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Actor actor = doc.toObject(Actor.class);
                actor.setActorId(doc.getId());
                actorList.add(actor);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadActors();
    }
}
