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

public class EditActorActivity extends AppCompatActivity {

    private EditText nameInp, roleInp, contactInp, imageInp;
    private Button btnSave;
    private FirebaseFirestore db;
    private String actorId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_actor);

        db = FirebaseFirestore.getInstance();

        // Связываем переменные с ID из XML
        nameInp = findViewById(R.id.etActorName);
        roleInp = findViewById(R.id.etActorRole);
        contactInp = findViewById(R.id.etActorContact);
        imageInp = findViewById(R.id.etActorImage);
        btnSave = findViewById(R.id.btnSaveActor);

        actorId = getIntent().getStringExtra("actor_id");

        if (actorId != null) {
            ((TextView)findViewById(R.id.editTitle)).setText("РЕДАКТИРОВАНИЕ");
            loadActorData();
        }

        btnSave.setOnClickListener(v -> saveActor());
    }

    private void loadActorData() {
        db.collection("actors").document(actorId).get().addOnSuccessListener(doc -> {
            if (doc.exists()) {
                Actor actor = doc.toObject(Actor.class);
                if (actor != null) {
                    nameInp.setText(actor.getName());
                    roleInp.setText(actor.getRole());
                    contactInp.setText(actor.getContacts());
                    imageInp.setText(actor.getImageUrl());
                }
            }
        });
    }

    private void saveActor() {
        String name = nameInp.getText().toString().trim();
        String role = roleInp.getText().toString().trim();
        String contact = contactInp.getText().toString().trim();
        String image = imageInp.getText().toString().trim();

        if (name.isEmpty() || role.isEmpty()) {
            Toast.makeText(this, "Заполните имя и роль", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> actorMap = new HashMap<>();
        actorMap.put("name", name);
        actorMap.put("role", role);
        actorMap.put("contacts", contact);
        actorMap.put("imageUrl", image);

        if (actorId == null) {
            db.collection("actors").add(actorMap).addOnSuccessListener(doc -> finish());
        } else {
            db.collection("actors").document(actorId).update(actorMap).addOnSuccessListener(doc -> finish());
        }
    }
}
