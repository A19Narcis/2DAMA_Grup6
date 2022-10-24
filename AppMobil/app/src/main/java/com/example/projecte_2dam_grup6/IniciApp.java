package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class IniciApp extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inici_app);

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}