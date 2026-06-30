package com.example.queststudioapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class MyBookingsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private BookingAdapter adapter;
    private List<Booking> bookingList;
    private FirebaseFirestore db;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_bookings);

        db = FirebaseFirestore.getInstance();
        userRole = getSharedPreferences("QuestPrefs", MODE_PRIVATE).getString("userRole", "client");

        recyclerView = findViewById(R.id.bookingsRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        bookingList = new ArrayList<>();

        adapter = new BookingAdapter(bookingList, this, new BookingAdapter.OnBookingActionListener() {
            @Override
            public void onDelete(Booking booking) {
                new AlertDialog.Builder(MyBookingsActivity.this)
                        .setTitle("Удалить бронь?")
                        .setMessage("Это действие нельзя отменить.")
                        .setPositiveButton("Удалить", (dialog, which) -> {
                            db.collection("bookings").document(booking.getBookingId()).delete()
                                    .addOnSuccessListener(aVoid -> loadBookings());
                        })
                        .setNegativeButton("Отмена", null).show();
            }

            @Override
            public void onConfirm(Booking booking) {
                // ПОДТВЕРЖДЕНИЕ БРОНИРОВАНИЯ
                new AlertDialog.Builder(MyBookingsActivity.this)
                        .setTitle("Подтвердить бронь?")
                        .setMessage("Клиент увидит, что бронь подтверждена.")
                        .setPositiveButton("Да, подтвердить", (dialog, which) -> {
                            db.collection("bookings").document(booking.getBookingId())
                                    .update("status", "confirmed")
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(MyBookingsActivity.this, "Подтверждено!", Toast.LENGTH_SHORT).show();
                                        loadBookings();
                                    });
                        })
                        .setNegativeButton("Нет", null).show();
            }
        });

        recyclerView.setAdapter(adapter);
        loadBookings();
    }

    private void loadBookings() {
        Query query;
        if ("admin".equals(userRole)) {
            query = db.collection("bookings");
        } else {
            query = db.collection("bookings").whereEqualTo("userId", FirebaseAuth.getInstance().getUid());
        }

        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            bookingList.clear();
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Booking b = doc.toObject(Booking.class);
                b.setBookingId(doc.getId());
                bookingList.add(b);
            }
            adapter.notifyDataSetChanged();
        });
    }
}
