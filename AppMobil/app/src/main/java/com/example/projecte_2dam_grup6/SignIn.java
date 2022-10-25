package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

        txtPasswordSignIn = findViewById(R.id.loginUsertxt);
        txtPasswordSignIn = findViewById(R.id.loginPasstxt);
        buttonSignIn = findViewById(R.id.sendDataSignIn);

    }

    public void validarUserLogin(View view){
        new MostraTask().execute();
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
                URL url = new URL("http://localhost:3000/validarLogIn/" + txtUserSignIn + "/" + txtPasswordSignIn);
                con = (HttpURLConnection) url.openConnection();
                con.connect();

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
    }


}




















