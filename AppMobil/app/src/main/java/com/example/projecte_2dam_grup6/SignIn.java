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

        txtUserSignIn = findViewById(R.id.loginUsertxt);
        txtPasswordSignIn = findViewById(R.id.loginPasstxt);
        buttonSignIn = findViewById(R.id.sendDataSignIn);
    }

    public void validarUserLogin(View view){
        new MostraTask().execute();
        //Intent intent = new Intent(this, PantallaPrincipal.class);
        //startActivity(intent);
    }


    private class MostraTask extends AsyncTask<String, Void, String>{

        HttpURLConnection con;
        @Override
        protected String doInBackground(String... strings) {
            mostra();
            return null;
        }

        private void mostra() {
            try {
                URL url = new URL("http://192.168.1.34:3000/validarLogIn/" + txtUserSignIn.getText() + "/" + txtPasswordSignIn.getText());
                System.out.println("USEEEEEEEEEEEEER " + txtUserSignIn.getText());
                System.out.println("PAAAAAAAAAAAAAAS " + txtPasswordSignIn.getText());
                con = (HttpURLConnection) url.openConnection();
                System.out.println("COOOOOOOOOOOOOOON --> "  + con);

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String linea;
                while ((linea = reader.readLine()) != null){
                    Log.d("Mostra", linea);
                }
            } catch (MalformedURLException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);

            try {
                JSONObject jsonObject = new JSONObject(s);
                System.out.println("Object --> " + jsonObject);
                JSONArray itemsArray = jsonObject.getJSONArray("PERSONES");

                int i = 0;
                String user = null;
                String pass = null;

                while (i < itemsArray.length() && (user == null && pass == null)){
                    JSONObject persona = itemsArray.getJSONObject(i);
                    JSONObject persInfo = persona.getJSONObject("persona");

                    try {
                        user = persInfo.getString("nom");
                        pass = persInfo.getString("pass");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    i++;
                }

                if (user != null && pass != null){
                    Toast.makeText(SignIn.this, "Bien hecho", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SignIn.this, "Mal hecho", Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}




















