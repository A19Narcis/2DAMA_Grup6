package com.example.projecte_2dam_grup6;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SignUp extends AppCompatActivity {

    private EditText nomRegister;
    private EditText cognomRegister;
    private EditText userRegister;
    private EditText passRegister;
    private EditText descRegister;
    private EditText emailRegister;
    private EditText locationRegister;
    private EditText edatRegister;
    private Button btnRegister;
    private Button btnAddImage;
    private Button btnStart;
    private TextView autoGmail;
    private TextView txtErrorRegister;

    private final String HOST = "http://192.168.251.66:3000/registerNewUser";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        nomRegister = findViewById(R.id.newNomtxt);
        cognomRegister = findViewById(R.id.newCognomtxt);
        userRegister = findViewById(R.id.newUsertxt);
        passRegister = findViewById(R.id.newPasstxt);
        descRegister = findViewById(R.id.newDescrtxt);
        emailRegister = findViewById(R.id.newEmailtxt);
        locationRegister = findViewById(R.id.newLocationtxt);
        edatRegister = findViewById(R.id.newEdattxt);
        btnRegister = findViewById(R.id.btnCreateUser);
        btnStart = findViewById(R.id.btnBackToStart);
        autoGmail = findViewById(R.id.txtAutoGmail);
        txtErrorRegister = findViewById(R.id.txtNoValidUserRegister);

        btnAddImage = findViewById(R.id.pickImageButton);
        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 3);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null){
            Uri selectedImage = data.getData();
            ImageView galleryImage = findViewById(R.id.userImage);
            galleryImage.setImageURI(selectedImage);
            galleryImage.setBackground(null);
        }
    }

    public void backToStart(View view){
        Intent intent = new Intent(this, IniciApp.class);
        startActivity(intent);
    }

    public void validarRegisterUser(View view){
        //Veure que tots els camps tenen informació
        boolean valid = true;
        if (nomRegister.getText().length() == 0 || cognomRegister.getText().length() == 0 || userRegister.getText().length() == 0
                || passRegister.getText().length() == 0 || descRegister.getText().length() == 0 || emailRegister.getText().length() == 0
                || locationRegister.getText().length() == 0){
            valid = false;
            Toast.makeText(this, R.string.omplirCamps, Toast.LENGTH_LONG).show();
        }

        for (int i = 0; i < descRegister.getText().length() && valid; i++) {
            char valorText = descRegister.getText().charAt(i);
            if (!isEmojiCharacter(valorText)){
                valid = false;
                Toast.makeText(this, R.string.emojiErrorSignUp, Toast.LENGTH_LONG).show();
            }
        }

        //Fer la connexió per afegir l'usuari
        if (valid){
            String json = "{\"email\":\""
                    + emailRegister.getText()+ "@gmail.com" + "\",\"nom\":\""
                    + nomRegister.getText() + "\",\"cognoms\":\""
                    + cognomRegister.getText() + "\",\"edad\":"
                    + edatRegister.getText() + ",\"ubicacio\":\""
                    + locationRegister.getText() + "\",\"user\": \""
                    + userRegister.getText() + "\",\"pass\":\""
                    + passRegister.getText() + "\",\"descripcio\": \""
                    + descRegister.getText().toString() + "\",\"rol\":\"user\"}";
            Log.d("JSON", json);
            new connexioRegisterUser().execute(HOST, json);
        }
    }

    private boolean isEmojiCharacter(char valorText) {
        return (valorText == 0x0) || (valorText == 0x9) || (valorText == 0xA) ||
                (valorText == 0xD) || ((valorText >= 0x20) && (valorText <= 0xD7FF)) ||
                ((valorText >= 0xE000) && (valorText <= 0xFFFD)) || ((valorText >= 0x10000)
                && (valorText <= 0x10FFFF));
    }

    private class connexioRegisterUser extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... strings) {
            String urlString = strings[0];
            String jsonInfo = strings[1];
            return validacioCreateUser(urlString, jsonInfo);
        }

        public String validacioCreateUser(String urlString, String json) {
            String result = "";
            try {
                URL myUrl = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) myUrl.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/json");

                writeStringToOutputStream(json, connection.getOutputStream());
                result = getStringFromInputStream(connection.getInputStream());
                Log.d("RESULT: ", result);
                int statusCode = connection.getResponseCode();
                connection.disconnect();

                Log.i("POST", "POST result: " + statusCode + " " + result);

            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(String s){
            System.out.println("VALOR S ---> " + s);
            super.onPostExecute(s);
            if (s.equals("false")) {
                txtErrorRegister.setVisibility(View.VISIBLE);
            } else if (s.equals("true")) {
                txtErrorRegister.setVisibility(View.INVISIBLE);
                Toast.makeText(SignUp.this, "Usuari creat correctament", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(SignUp.this, SignIn.class);
                startActivity(intent);
            }
        }



    }
    private static void writeStringToOutputStream(String json, OutputStream outputStream) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8); // API 19: StandardCharsets.UTF_8
        outputStream.write(bytes);
        outputStream.close();
    }

    private static String getStringFromInputStream(InputStream stream) throws IOException {
        InputStreamReader streamReader = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(streamReader);
        StringBuilder stringBuilder = new StringBuilder();
        String inputLine;
        while((inputLine = reader.readLine()) != null){
            stringBuilder.append(inputLine);
        }
        reader.close();
        streamReader.close();
        return stringBuilder.toString();
    }

}