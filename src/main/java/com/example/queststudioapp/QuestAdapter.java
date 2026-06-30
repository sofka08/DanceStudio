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
import com.bumptech.glide.Glide;
import java.util.ArrayList;
import java.util.List;

public class QuestAdapter extends RecyclerView.Adapter<QuestAdapter.QuestViewHolder> {

    private List<Quest> questList;
    private List<Quest> fullList;
    private OnQuestClickListener listener;
    private boolean isAdmin;

    public interface OnQuestClickListener {
        void onQuestClick(Quest quest);
        void onDeleteClick(Quest quest);
        void onEditClick(Quest quest);
    }

    public QuestAdapter(List<Quest> questList, OnQuestClickListener listener, Context context) {
        this.questList = questList;
        this.fullList = new ArrayList<>(questList);
        this.listener = listener;
        SharedPreferences prefs = context.getSharedPreferences("QuestPrefs", Context.MODE_PRIVATE);
        this.isAdmin = "admin".equals(prefs.getString("userRole", "client"));
    }

    @NonNull
    @Override
    public QuestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_quest, parent, false);
        return new QuestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestViewHolder holder, int position) {
        Quest quest = questList.get(position);
        holder.name.setText(quest.getName());
        holder.price.setText(quest.getPrice() + " руб.");
        holder.description.setText(quest.getDescription());

        // Показываем кнопки управления только админу
        if (isAdmin) {
            holder.adminTools.setVisibility(View.VISIBLE);
        } else {
            holder.adminTools.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(quest.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.img);

        holder.itemView.setOnClickListener(v -> listener.onQuestClick(quest));
        holder.btnEdit.setOnClickListener(v -> listener.onEditClick(quest));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(quest));
    }

    @Override
    public int getItemCount() { return questList.size(); }

    public void updateList(List<Quest> newList) {
        this.questList = newList;
        this.fullList = new ArrayList<>(newList);
        notifyDataSetChanged();
    }

    public void filter(String text) {
        List<Quest> filtered = new ArrayList<>();
        for (Quest q : fullList) {
            if (q.getName().toLowerCase().contains(text.toLowerCase())) {
                filtered.add(q);
            }
        }
        questList = filtered;
        notifyDataSetChanged();
    }

    public static class QuestViewHolder extends RecyclerView.ViewHolder {
        ImageView img, btnEdit, btnDelete;
        TextView name, price, description;
        View adminTools;

        public QuestViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.questImage);
            name = itemView.findViewById(R.id.questName);
            price = itemView.findViewById(R.id.questPrice);
            description = itemView.findViewById(R.id.questDescription);
            adminTools = itemView.findViewById(R.id.adminQuestTools);
            btnEdit = itemView.findViewById(R.id.btnEditQuest);
            btnDelete = itemView.findViewById(R.id.btnDeleteQuest);
        }
    }
}
