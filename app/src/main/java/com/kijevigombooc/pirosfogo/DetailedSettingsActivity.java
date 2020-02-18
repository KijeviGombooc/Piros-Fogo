package com.kijevigombooc.pirosfogo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.LeadingMarginSpan;
import android.widget.TextView;

public class DetailedSettingsActivity extends AppCompatActivity {


    TextView first;
    TextView second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_settings);
    }
}
