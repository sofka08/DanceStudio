package com.example.queststudioapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private List<Review> reviewList;
    private boolean isAdmin;
    private OnReviewDeleteListener deleteListener;

    public interface OnReviewDeleteListener {
        void onDelete(Review review);
    }

    public ReviewAdapter(List<Review> reviewList, boolean isAdmin, OnReviewDeleteListener deleteListener) {
        this.reviewList = reviewList;
        this.isAdmin = isAdmin;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new ReviewViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review r = reviewList.get(position);
        holder.userName.setText(r.getUserName());
        holder.comment.setText(r.getComment());
        holder.ratingBar.setRating(r.getRating());

        if (isAdmin) {
            holder.btnDelete.setVisibility(View.VISIBLE);
            holder.btnDelete.setOnClickListener(v -> deleteListener.onDelete(r));
        } else {
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() { return reviewList.size(); }

    public static class ReviewViewHolder extends RecyclerView.ViewHolder {
        TextView userName, comment;
        RatingBar ratingBar;
        Button btnDelete;

        public ReviewViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.reviewUserName);
            comment = itemView.findViewById(R.id.reviewComment);
            ratingBar = itemView.findViewById(R.id.reviewRating);
            btnDelete = itemView.findViewById(R.id.btnDeleteReview);
        }
    }
}
