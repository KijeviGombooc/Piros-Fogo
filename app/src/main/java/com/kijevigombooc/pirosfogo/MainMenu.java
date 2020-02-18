package com.kijevigombooc.pirosfogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button startGame = findViewById(R.id.startGame);
        Button loadGame = findViewById(R.id.loadGame);
        Button profiles = findViewById(R.id.profiles);
        Button rules = findViewById(R.id.rules);

        startGame.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainMenu.this, Game.class);
                intent.putExtra("load", false);
                startActivity(intent);
            }
        });

        loadGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, MatchViewer.class);
                startActivity(intent);
            }
        });

        profiles.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(MainMenu.this, ProfileViewer.class);
                startActivity(intent);
            }
        });
        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenu.this, RulesActivity.class);
                startActivity(intent);
            }
        });
    }
}
