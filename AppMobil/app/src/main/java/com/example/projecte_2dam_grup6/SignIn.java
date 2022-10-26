package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SignIn extends AppCompatActivity {

    private EditText txtUserSignIn;
    private EditText txtPasswordSignIn;
    private Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtUserSignIn = (EditText) findViewById(R.id.loginUsertxt);
        txtPasswordSignIn = (EditText) findViewById(R.id.loginPasstxt);
        buttonSignIn = findViewById(R.id.sendDataSignIn);
    }

    public void validarUserLogin(View view){

        String userValue = txtUserSignIn.getText().toString();
        String passValue = txtPasswordSignIn.getText().toString();

        if (userValue.length() == 0 | passValue.length() == 0){
            Toast.makeText(SignIn.this, "Omple tots els camps", Toast.LENGTH_LONG).show();
        }


        new MostraTask().execute();
    }


    private class MostraTask extends AsyncTask<String, Void, String>{

        HttpURLConnection con;
        @Override
        protected String doInBackground(String... strings) {
            return mostra();
        }

        private String mostra() {
            String bufferStr = "";
            String linea = "";

            try {
                URL url = new URL("http://192.168.194.66:3000/validarLogIn/" + txtUserSignIn.getText() + "/" + txtPasswordSignIn.getText());
                System.out.println("USUARIO PARA LOGIN -> " + txtUserSignIn.getText());
                System.out.println("CPNTRASENYA USUARI -> " + txtPasswordSignIn.getText());
                con = (HttpURLConnection) url.openConnection();
                con.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                while ((bufferStr = reader.readLine()) != null){
                    linea = bufferStr;

                    Log.d("Mostra", bufferStr);
                    Log.d("Mostra", linea);
                }
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println( linea);

            return linea;
        }

        @Override
        protected void onPostExecute(String s){
            System.out.println("VALOR S ---> " + s);
            super.onPostExecute(s);
            if (s.equals("false")){
                Toast.makeText(SignIn.this, "Mal hecho", Toast.LENGTH_SHORT).show();
            } else if (s.equals("true")){
                Toast.makeText(SignIn.this, "Welcome", Toast.LENGTH_SHORT).show();
                //Intent intent = new Intent(this, PantallaPrincipal.class);
                //startActivity(intent);
            }
        }
    }
}




















