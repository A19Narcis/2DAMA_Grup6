package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class InfoUser extends AppCompatActivity {

    private String infoUserFromMainPage;


    private TextView test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        infoUserFromMainPage = intent.getStringExtra(PantallaPrincipal.EXTRA_MESSAGE);

        test = findViewById(R.id.textView);

        test.setText(infoUserFromMainPage);

    }
}