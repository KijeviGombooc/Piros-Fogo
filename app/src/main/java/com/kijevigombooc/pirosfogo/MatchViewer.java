package com.kijevigombooc.pirosfogo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class MatchViewer extends AppCompatActivity {

    RecyclerView matchHolder;
    MatchAdapter adapter;
    ArrayList<Match> matches = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_viewer);

        loadMatches();
        buildRecyclerView();
    }

    void buildRecyclerView(){

        matchHolder = findViewById(R.id.matchHolder);
        matchHolder.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new MatchAdapter(matches);

        matchHolder.setAdapter(adapter);
        matchHolder.setLayoutManager(manager);

        adapter.setOnItemClickListener(new MatchAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {
                startMatch(matches.get(position).getId());
            }

            @Override
            public void onDeleteClick(int position) {
                deleteMatch(position);
                adapter.notifyItemRemoved(position);
            }
        });
    }

    private void deleteMatch(int position){
        if(position < matches.size() && position >= 0){
            DBHelper helper = new DBHelper(MatchViewer.this);

            helper.removeMatch(matches.get(position).getId());
            matches.remove(position);
        }
    }

    private void loadMatches() {

        DBHelper helper = new DBHelper(this);
        matches = helper.getAllMatchesDatesWithIDs();
    }

    void startMatch(long id){
        Intent intent = new Intent(this, Game.class);
        intent.putExtra("load", true);
        intent.putExtra("matchID", id);
        startActivity(intent);
    }
}
