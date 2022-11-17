package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InfoUser extends AppCompatActivity {

    private String infoUserFromMainPage;
   // private String userId, userName;



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
        //infoUserFromMainPage = intent.getStringExtra(PantallaPrincipal.EXTRA_MESSAGE);

        infoUserFromMainPage = intent.getStringExtra(PantallaPrincipal.EXTRA_MESSAGE);

        //Que usuario esta consultando

        //Consultar Id del Perfil en base de Datos

        //Retornar esos datos

        //Monstrar esos datos

        test = findViewById(R.id.textView);

        test.setText(infoUserFromMainPage);

    }
}