package com.kijevigombooc.pirosfogo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private ArrayList<Profile> profiles;
    private OnItemClickListener listener;

    public ProfileAdapter(ArrayList<Profile> profiles){
        this.profiles = profiles;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_layout, parent, false);
        ProfileViewHolder pvh = new ProfileViewHolder(v, listener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull ProfileViewHolder holder, int position) {
        holder.nameDisplay.setText(profiles.get(position).getName());
        holder.profileImageDisplay.setImageBitmap(profiles.get(position).getImageBitmap());
    }

    @Override
    public int getItemCount() {
        return profiles.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }



    public interface OnItemClickListener{
        void onItemClick(int position);
        void onEditClick(int position);
        void onDeleteClick(int position);
    }

    public static class ProfileViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImageDisplay;
        public TextView nameDisplay;
        public ImageView editButton;
        public ImageView deleteButton;

        public ProfileViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            nameDisplay = itemView.findViewById(R.id.profileName);
            profileImageDisplay = itemView.findViewById(R.id.profileImage);
            editButton = itemView.findViewById(R.id.editButton);
            deleteButton = itemView.findViewById(R.id.deleteButton);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION);
                        listener.onItemClick(position);
                    }
                }
            });

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) ;
                        listener.onEditClick(position);
                    }
                }
            });

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        if(listener != null) {
                            int position = getAdapterPosition();
                            if (position != RecyclerView.NO_POSITION) ;
                            listener.onDeleteClick(position);
                        }
                }
            });
        }
    }
}
