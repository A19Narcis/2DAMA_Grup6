package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignIn extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.projecte_2dam_grup6.extra.MESSAGE";

    private EditText txtUserSignIn;
    private EditText txtPasswordSignIn;
    private TextView textErrorDades;
    private Button buttonSignIn;
    private Button btnStart;

    private String server_path;
    private String loginValidate_path;

    private CheckBox checkBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtUserSignIn = findViewById(R.id.loginUsertxt);
        txtPasswordSignIn = findViewById(R.id.loginPasstxt);
        buttonSignIn = findViewById(R.id.sendDataSignIn);
        textErrorDades = findViewById(R.id.txtNoValidUser);
        btnStart = findViewById(R.id.btnBackToStart);
        checkBox = findViewById(R.id.checkPass);

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //Veure la contrasenya
                    txtPasswordSignIn.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //Veure la contrasenya en punts
                    txtPasswordSignIn.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

    }

    @Override
    public void finish(){
        Intent intent = new Intent(SignIn.this, IniciApp.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void backToStart(View view){
        Intent intent = new Intent(SignIn.this, IniciApp.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    public void validarUserLogin(View view){
        boolean valid = true;
        String userValue = txtUserSignIn.getText().toString();
        String passValue = txtPasswordSignIn.getText().toString();

        if (userValue.length() == 0 | passValue.length() == 0){
            Toast.makeText(SignIn.this, R.string.omplirCamps, Toast.LENGTH_LONG).show();
            valid = false;
        }

        if (valid){
            new validarLoginUser().execute();
        }
        
    }


    private class validarLoginUser extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            return veureResultat();
        }

        private String veureResultat(){
            //Llegir les dades del fitxer JSON en ASSETS
            try {
                JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
                server_path = obj_settings.getString("server");
                loginValidate_path = obj_settings.getString("loginUsuari_GET");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url_server = server_path + loginValidate_path + "/" + txtUserSignIn.getText() + "/" + txtPasswordSignIn.getText();
            Log.d("url_server", url_server);
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String validacioUsuari = null;

            try {
                Uri builtURI = Uri.parse(url_server).buildUpon().build();
                URL requestURL = new URL(builtURI.toString());

                urlConnection = (HttpURLConnection) requestURL.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Get the InputStream.
                InputStream inputStream = urlConnection.getInputStream();

                // Create a buffered reader from that input stream.
                reader = new BufferedReader(new InputStreamReader(inputStream));

                // Use a StringBuilder to hold the incoming response.
                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                if (builder.length() == 0) {
                    // Stream was empty. No point in parsing.
                    return null;
                }
                validacioUsuari = builder.toString();

                Log.d("Mostra", validacioUsuari);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return validacioUsuari;
        }

        @Override
        protected void onPostExecute(String s){
            System.out.println("VALOR S +----> " + s);
            super.onPostExecute(s);
            if (s.equals("false")){
                textErrorDades.setVisibility(View.VISIBLE);
            } else {
                Log.d("USER", "user: " + s);
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    int banUser = jsonObject.getInt("ban");

                    if (banUser == 1){
                        textErrorDades.setVisibility(View.VISIBLE);
                    } else {
                        textErrorDades.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent(SignIn.this, PantallaPrincipal.class);
                        Toast.makeText(SignIn.this, getString(R.string.welcome_msg) + " " + txtUserSignIn.getText().toString(), Toast.LENGTH_SHORT).show();
                        intent.putExtra(EXTRA_MESSAGE, s);
                        startActivity(intent);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("settingsApp.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

}




















