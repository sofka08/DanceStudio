package com.example.queststudioapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ActorAdapter extends RecyclerView.Adapter<ActorAdapter.ActorViewHolder> {

    private List<Actor> actorList;
    private OnActorActionListener listener;

    public interface OnActorActionListener {
        void onDelete(Actor actor);
        void onEdit(Actor actor);
    }

    public ActorAdapter(List<Actor> actorList, OnActorActionListener listener) {
        this.actorList = actorList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_actor, parent, false);
        return new ActorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActorViewHolder holder, int position) {
        Actor actor = actorList.get(position);
        holder.name.setText(actor.getName());
        holder.role.setText("Роль: " + actor.getRole());
        holder.contacts.setText("Контакты: " + actor.getContacts());

        Glide.with(holder.itemView.getContext())
                .load(actor.getImageUrl())
                .placeholder(R.drawable.placeholder_image)
                .into(holder.image);

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(actor));
        holder.btnEdit.setOnClickListener(v -> listener.onEdit(actor));
    }

    @Override
    public int getItemCount() { return actorList.size(); }

    public static class ActorViewHolder extends RecyclerView.ViewHolder {
        ImageView image, btnEdit, btnDelete;
        TextView name, role, contacts;

        public ActorViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.actorImage);
            name = itemView.findViewById(R.id.actorName);
            role = itemView.findViewById(R.id.actorRole);
            contacts = itemView.findViewById(R.id.actorContacts);
            btnEdit = itemView.findViewById(R.id.btnEditActor);
            btnDelete = itemView.findViewById(R.id.btnDeleteActor);
        }
    }
}
