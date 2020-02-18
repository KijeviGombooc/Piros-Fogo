package com.kijevigombooc.pirosfogo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class RulesActivity extends AppCompatActivity {


    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rules);


        button = findViewById(R.id.detailedRulesButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToDetailed();
            }
        });
    }

    void goToDetailed(){
        Intent intent = new Intent(this, DetailedSettingsActivity.class);

        startActivity(intent);
    }
}
