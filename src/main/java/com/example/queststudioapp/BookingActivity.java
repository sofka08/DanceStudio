package com.example.queststudioapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingActivity extends AppCompatActivity {

    private TextView tvTitle;
    private Button btnSelectDate, btnConfirm;
    private Spinner spinnerTime;
    private EditText etPlayersCount;
    private FirebaseFirestore db;
    private String questId, questName, selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        db = FirebaseFirestore.getInstance();

        questId = getIntent().getStringExtra("quest_id");
        questName = getIntent().getStringExtra("quest_name");

        tvTitle = findViewById(R.id.tvBookingTitle);
        btnSelectDate = findViewById(R.id.btnSelectDate);
        spinnerTime = findViewById(R.id.spinnerTime);
        etPlayersCount = findViewById(R.id.etPlayersCount);
        btnConfirm = findViewById(R.id.btnConfirmBooking);

        if (questName != null) {
            tvTitle.setText("Бронирование квеста:\n" + questName);
        }

        btnSelectDate.setOnClickListener(v -> showDatePicker());

        // Заполняем время с новыми яркими стилями
        setupTimeSpinner();

        btnConfirm.setOnClickListener(v -> saveBooking());
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
            selectedDate = dayOfMonth + "." + (month1 + 1) + "." + year1;
            btnSelectDate.setText("Дата: " + selectedDate);
        }, year, month, day);

        datePickerDialog.show();
    }

    private void setupTimeSpinner() {
        List<String> timeList = new ArrayList<>();
        timeList.add("10:00");
        timeList.add("12:00");
        timeList.add("14:00");
        timeList.add("16:00");
        timeList.add("17:30");
        timeList.add("19:00");
        timeList.add("21:00");

        // ПОДКЛЮЧАЕМ НАШИ КРАСИВЫЕ ШАБЛОНЫ СПИСКА
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, timeList);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerTime.setAdapter(adapter);
    }

    private void saveBooking() {
        String players = etPlayersCount.getText().toString().trim();
        String time = spinnerTime.getSelectedItem().toString();

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Выберите дату!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (players.isEmpty()) {
            Toast.makeText(this, "Укажите количество игроков!", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = FirebaseAuth.getInstance().getUid();
        if (userId == null) {
            Toast.makeText(this, "Войдите в систему для бронирования!", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> bookingMap = new HashMap<>();
        bookingMap.put("userId", userId);
        bookingMap.put("questId", questId);
        bookingMap.put("questName", questName);
        bookingMap.put("date", selectedDate);
        bookingMap.put("time", time);
        bookingMap.put("numberOfPlayers", Integer.parseInt(players));
        bookingMap.put("status", "pending");

        db.collection("bookings").add(bookingMap).addOnSuccessListener(doc -> {
            Toast.makeText(this, "Квест успешно забронирован!", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}
