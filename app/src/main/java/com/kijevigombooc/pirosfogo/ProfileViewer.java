package com.kijevigombooc.pirosfogo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ProfileViewer extends AppCompatActivity
{
    RecyclerView profileHolder;
    ProfileAdapter adapter;
    ArrayList<Profile> profiles = new ArrayList<>();

    final int REQUEST_EDIT = 1;
    final int REQUEST_ADD = 2;
    int currentEdited = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_viewer);

        setResult(RESULT_CANCELED);

        loadProfiles();
        buildRecyclerView();
        buildFloatingButton();
    }

    void buildFloatingButton(){
        FloatingActionButton addProfile = findViewById(R.id.addProfile);
        addProfile.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                goToEdit(null);
            }
        });

    }

    void loadProfiles(){
        DBHelper helper = new DBHelper(this);
        profiles = helper.GetAllProfiles();
        for(Profile p : profiles){
            p.setImageBitmap(100, 100);
        }
    }

    void selectProfile(int position){
        Intent intent = new Intent();
        intent.putExtra("id", profiles.get(position).getId());
        setResult(RESULT_OK, intent);
        finish();
    }

    void buildRecyclerView(){
        profileHolder = findViewById(R.id.profileHolder);
        profileHolder.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        adapter = new ProfileAdapter(profiles);

        profileHolder.setAdapter(adapter);
        profileHolder.setLayoutManager(manager);
        adapter.setOnItemClickListener(new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(getIntent().getBooleanExtra("choose", false))
                    selectProfile(position);
            }

            @Override
            public void onEditClick(int position) {
                currentEdited = position;
                goToEdit(profiles.get(position).getId());
            }

            @Override
            public void onDeleteClick(int position) {
                deleteProfile(position);
                adapter.notifyItemRemoved(position);
            }
        });
    }

    void deleteProfile(int position){
        if(position < profiles.size() && position >= 0){
            DBHelper helper = new DBHelper(ProfileViewer.this);

            helper.removeProfile((int)(long)profiles.get(position).getId());
            profiles.remove(position);
        }
    }

    void goToEdit(Long id){
        Intent intent = new Intent(ProfileViewer.this, ProfileEdit.class);
        if(id != null){
            intent.putExtra("load", true);
            intent.putExtra("id", id);
            startActivityForResult(intent, REQUEST_EDIT);
        }
        else
            startActivityForResult(intent, REQUEST_ADD);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_EDIT && resultCode == RESULT_OK){
            DBHelper helper = new DBHelper(this);
            profiles.set(currentEdited, helper.getProfileByID(profiles.get(currentEdited).getId()));
            profiles.get(currentEdited).setImageBitmap(100,100);
            if(adapter != null)
                adapter.notifyItemChanged(currentEdited);
        }
        else if(requestCode == REQUEST_ADD && resultCode == RESULT_OK && data != null){
            DBHelper helper = new DBHelper(this);
            profiles.add(helper.getProfileByID(data.getLongExtra("id", -1)));
            profiles.get(profiles.size()-1).setImageBitmap(100,100);
            if(adapter != null)
                adapter.notifyItemInserted(profiles.size()-1);
        }
    }
}
