package com.kijevigombooc.pirosfogo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    private ArrayList<Match> matches;
    private OnItemClickListener listener;

    public MatchAdapter(ArrayList<Match> matches){
        this.matches = matches;
    }

    @Override
    public MatchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_layout, parent, false);
        MatchViewHolder pvh = new MatchViewHolder(v, listener);
        return pvh;
    }

    @Override
    public void onBindViewHolder(@NonNull MatchViewHolder holder, int position) {
        holder.dateDisplay.setText(matches.get(position).getDate());
    }

    @Override
    public int getItemCount() {
        return matches.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }



    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public static class MatchViewHolder extends RecyclerView.ViewHolder{

        public TextView dateDisplay;
        public ImageView deleteButton;

        public MatchViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            dateDisplay = itemView.findViewById(R.id.dateDisplay);
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
