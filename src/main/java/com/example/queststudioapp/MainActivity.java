package com.example.queststudioapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private EditText emailLogin, passwordLogin;
    private Button loginBtn, registerBtn, guestLoginBtn;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private static final String PREFS_NAME = "QuestPrefs";
    private static final String IS_GUEST_KEY = "isGuest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        loginBtn = findViewById(R.id.loginBtn);
        registerBtn = findViewById(R.id.registerBtn);
        guestLoginBtn = findViewById(R.id.guestLoginBtn);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            checkUserStatusAndGoHome();
        }

        loginBtn.setOnClickListener(v -> loginUser());
        registerBtn.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, RegisterActivity.class)));
        guestLoginBtn.setOnClickListener(v -> {
            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean(IS_GUEST_KEY, true);
            editor.putBoolean("isAdmin", false);
            editor.apply();

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.putExtra("is_guest", true);
            startActivity(intent);
            finish();
        });
    }

    private void loginUser() {
        String email = emailLogin.getText().toString().trim();
        String pass = passwordLogin.getText().toString().trim();

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Введите email и пароль", Toast.LENGTH_SHORT).show();
            return;
        }

        loginBtn.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        loginBtn.setEnabled(true);

                        if (task.isSuccessful()) {
                            // ИСПРАВЛЕНО ЗДЕСЬ: убрано лишнее "Get"
                            SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean(IS_GUEST_KEY, false);

                            // Логика админа
                            if (email.equalsIgnoreCase("admin@mail.ru")) {
                                editor.putBoolean("isAdmin", true);
                            } else {
                                editor.putBoolean("isAdmin", false);
                            }
                            editor.apply();

                            Toast.makeText(MainActivity.this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                            checkUserStatusAndGoHome();
                        } else {
                            Toast.makeText(MainActivity.this, "Ошибка: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void checkUserStatusAndGoHome() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isGuest = prefs.getBoolean(IS_GUEST_KEY, false);
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        intent.putExtra("is_guest", isGuest);
        startActivity(intent);
        finish();
    }
}
