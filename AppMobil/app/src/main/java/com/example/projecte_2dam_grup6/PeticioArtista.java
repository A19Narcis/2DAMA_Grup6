package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PeticioArtista extends AppCompatActivity {

    private String server_path;
    private String addPet_path;

    private Button btnSend;
    private TextView textPeticio;

    private String arrIntent;
    private String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_peticio_artista);


        Intent intent = getIntent();
        arrIntent = intent.getStringExtra(PantallaPrincipal.EXTRA_MESSAGE);

        Log.d("Dades_USER", arrIntent);

        try {
            JSONArray jsonArray = new JSONArray(arrIntent);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            id_user = jsonObject.optString("id");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Llegir les dades del fitxer JSON en ASSETS
        try {
            JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
            server_path = obj_settings.getString("server");
            addPet_path = obj_settings.getString("addNewPeticio");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        btnSend = findViewById(R.id.btnPeticio);
        textPeticio = findViewById(R.id.peticioUsuari);

    }

    public void startPeticio(View view){

        String json = "{\"id_usu\":\"" + id_user + "\",\"peticion\":\""
                + textPeticio.getText().toString() + "\"}";

        final String HOST = server_path + addPet_path;
        new sendPeticio().execute(HOST, json);
    }

    public class sendPeticio extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            return readPeticio(strings[0], strings[1]);
        }

        private String readPeticio(String urlString, String json) {
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
            super.onPostExecute(s);
            Toast.makeText(PeticioArtista.this, "Petició enviada!", Toast.LENGTH_SHORT).show();
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