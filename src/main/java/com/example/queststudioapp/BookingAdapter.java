package com.example.queststudioapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class BookingAdapter extends RecyclerView.Adapter<BookingAdapter.BookingViewHolder> {

    private List<Booking> bookingList;
    private OnBookingActionListener listener;
    private boolean isAdmin;

    public interface OnBookingActionListener {
        void onDelete(Booking booking);
        void onConfirm(Booking booking);
    }

    public BookingAdapter(List<Booking> bookingList, Context context, OnBookingActionListener listener) {
        this.bookingList = bookingList;
        this.listener = listener;
        SharedPreferences prefs = context.getSharedPreferences("QuestPrefs", Context.MODE_PRIVATE);
        this.isAdmin = "admin".equals(prefs.getString("userRole", "client"));
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_booking, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        Booking b = bookingList.get(position);
        holder.questName.setText("Квест: " + b.getQuestName());
        holder.dateTime.setText(b.getDate() + " в " + b.getTime() + " | " + b.getNumberOfPlayers() + " чел.");

        // ИСПРАВЛЕНО ЗДЕСЬ: заменили getPlayersList() на getNumberOfPlayers()
        holder.players.setText("Игроки: " + b.getNumberOfPlayers() + " чел.");

        // Красивое отображение статуса
        String statusText = b.getStatus();
        if ("pending".equals(statusText)) statusText = "Ожидает";
        if ("confirmed".equals(statusText)) statusText = "Подтверждено";
        holder.status.setText("Статус: " + statusText);

        if (isAdmin) {
            holder.adminTools.setVisibility(View.VISIBLE);
            // Если уже подтверждено, скрываем кнопку галочки
            holder.btnConfirm.setVisibility("confirmed".equals(b.getStatus()) ? View.GONE : View.VISIBLE);
        } else {
            holder.adminTools.setVisibility(View.GONE);
        }

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(b));
        holder.btnConfirm.setOnClickListener(v -> listener.onConfirm(b));
    }

    @Override
    public int getItemCount() { return bookingList.size(); }

    public static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView questName, dateTime, players, status;
        View adminTools;
        ImageView btnConfirm, btnDelete;

        public BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            questName = itemView.findViewById(R.id.bookingItemQuestName);
            dateTime = itemView.findViewById(R.id.bookingItemDateTime);
            players = itemView.findViewById(R.id.bookingItemPlayers);
            status = itemView.findViewById(R.id.bookingItemStatus);
            adminTools = itemView.findViewById(R.id.adminBookingTools);
            btnConfirm = itemView.findViewById(R.id.btnConfirmBooking);
            btnDelete = itemView.findViewById(R.id.btnDeleteBooking);
        }
    }
}
