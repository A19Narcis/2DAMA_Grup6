package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InfoUser extends AppCompatActivity {

    private String infoUserFromMainPage;
    private String userName, userRol, idUser;
    JSONArray jsonArray = null;


    private TextView nomUserText, rolUserText, idUserText;

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

        //Separa json
        try {
            //Que usuario esta consultando
            jsonArray = new JSONArray(infoUserFromMainPage);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            userName = jsonObject.optString("user");
            userRol = jsonObject.optString("rol");
            idUser = jsonObject.optString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Consultar Id del Perfil en base de Datos

        //Retornar esos datos

        //Monstrar esos datos

        nomUserText = findViewById(R.id.nomUser);

        nomUserText.setText(userName);

        rolUserText = findViewById(R.id.rolUserMod);

        rolUserText.setText(userRol);

        idUserText = findViewById(R.id.idUserMod);

        idUserText.setText(idUser);



    }
}