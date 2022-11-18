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
    private String nickNameUserStr, userRolStr, idUserStr, emailUserStr, dataUserStr, ubicacioUserStr, descripcioUserStr, imageUserStr;
    JSONArray jsonArray = null;


    private TextView nomUserText, rolUserText, idUserText, emailUserText, dataUserText, ubicacioUserText, descripcioUserText;

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
            imageUserStr = jsonObject.optString("id_image");
            nickNameUserStr = jsonObject.optString("user");
            userRolStr = jsonObject.optString("rol");
            idUserStr = jsonObject.optString("id");
            emailUserStr = jsonObject.optString("email");
            dataUserStr = jsonObject.optString("data_naixament");
            ubicacioUserStr = jsonObject.optString("ubicacio");
            descripcioUserStr = jsonObject.optString("descripcio");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Consultar Id del Perfil en base de Datos

        //Retornar esos datos

        //Monstrar esos datos

        nomUserText = findViewById(R.id.nomUser);

        nomUserText.setText(nickNameUserStr);

        rolUserText = findViewById(R.id.rolUserMod);

        rolUserText.setText(userRolStr);

        idUserText = findViewById(R.id.idUserMod);

        idUserText.setText(idUserStr);

        emailUserText = findViewById(R.id.emailUserMod);

        emailUserText.setText(emailUserStr);

        dataUserText = findViewById(R.id.dataUserMod);

        dataUserText.setText(dataUserStr);

        dataUserText = findViewById(R.id.dataUserMod);

        dataUserText.setText(dataUserStr);

        ubicacioUserText = findViewById(R.id.ubicacioUserMod);

        ubicacioUserText.setText(ubicacioUserStr);

    }
}