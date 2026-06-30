package com.example.queststudioapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText fioInp, phoneInp, emailInp, passInp;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        fioInp = findViewById(R.id.regFio);
        phoneInp = findViewById(R.id.regPhone);
        emailInp = findViewById(R.id.regEmail);
        passInp = findViewById(R.id.regPass);
        registerBtn = findViewById(R.id.registerBtn);

        registerBtn.setOnClickListener(v -> {
            String email = emailInp.getText().toString().trim();
            String pass = passInp.getText().toString().trim();
            String fio = fioInp.getText().toString().trim();
            String phone = phoneInp.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty() || fio.isEmpty() || phone.isEmpty()) {
                Toast.makeText(this, "Заполните ВСЕ поля", Toast.LENGTH_SHORT).show();
                return;
            }

            if (pass.length() < 6) {
                Toast.makeText(this, "Пароль слишком короткий (мин. 6 симв.)", Toast.LENGTH_SHORT).show();
                return;
            }

            Toast.makeText(this, "Проверка связи с сервером...", Toast.LENGTH_SHORT).show();

            // Создаем пользователя в Auth
            mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String uid = mAuth.getCurrentUser().getUid();

                    // Сохраняем в Firestore
                    Map<String, Object> user = new HashMap<>();
                    user.put("fio", fio);
                    user.put("phone", phone);
                    user.put("email", email);
                    user.put("role", "client");

                    db.collection("users").document(uid)
                            .set(user)
                            .addOnSuccessListener(aVoid -> {
                                // ВОТ ТЕПЕРЬ ВСЁ ХОРОШО
                                Toast.makeText(this, "РЕГИСТРАЦИЯ УСПЕШНА!", Toast.LENGTH_LONG).show();
                                finish();
                            })
                            .addOnFailureListener(e -> {
                                // Если база данных запретила запись (например, правила не настроены)
                                Toast.makeText(this, "ОШИБКА БАЗЫ: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            });

                } else {
                    // Если такой email уже есть в системе (ты его уже создала в прошлый раз)
                    Toast.makeText(this, "ОШИБКА: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
