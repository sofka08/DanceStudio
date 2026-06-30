package com.example.queststudioapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private List<Category> categories;
    private boolean isAdmin;
    private OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
        void onDeleteClick(Category category);
    }

    public CategoryAdapter(List<Category> categories, boolean isAdmin, OnCategoryClickListener listener) {
        this.categories = categories;
        this.isAdmin = isAdmin;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category cat = categories.get(position);
        holder.name.setText(cat.getName().toUpperCase());

        if (isAdmin) {
            holder.btnDelete.setVisibility(View.VISIBLE);
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> listener.onCategoryClick(cat));
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(cat));
    }

    @Override
    public int getItemCount() { return categories.size(); }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView btnDelete;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.categoryNameText);
            btnDelete = itemView.findViewById(R.id.btnDeleteCategory);
        }
    }
}
