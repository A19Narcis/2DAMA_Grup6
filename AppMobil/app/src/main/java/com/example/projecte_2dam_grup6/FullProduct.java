package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FullProduct extends AppCompatActivity {

    private String server_path;
    private String getSingleProductPath;

    private Button btnBack;
    private String id_prod_selec;

    private TextView titleProd;
    private String url_imgProd;
    private TextView descProd;
    private TextView nomUserProd;
    private String url_imgUser;
    private TextView preuProd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_product);

        Intent intent = getIntent();
        id_prod_selec = intent.getStringExtra("INFO_PROD");
        Toast.makeText(this, id_prod_selec, Toast.LENGTH_SHORT).show();


        titleProd = findViewById(R.id.textViewNomProducte);
        descProd = findViewById(R.id.textViewDescripcioProducte);
        nomUserProd = findViewById(R.id.nomUserProduct);
        preuProd = findViewById(R.id.preuProducte);

        getInfoProducte(); //Agafar les dades del producte

    }

    private void getInfoProducte() {
        new getInfoProductInfo().execute();
    }

    private class getInfoProductInfo extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return veureResultatProducte();
        }

        private String veureResultatProducte() {
            //Llegir les dades del fitxer JSON en ASSETS
            try {
                JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
                server_path = obj_settings.getString("server");
                getSingleProductPath = obj_settings.getString("dadesSingleProducte");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url_server = server_path + getSingleProductPath + "/" + id_prod_selec;
            Log.d("url_server", url_server);
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String dadesEnText = null;

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
                dadesEnText = builder.toString();

                Log.d("Mostra", dadesEnText);

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
            return dadesEnText;
        }


        @Override
        protected void onPostExecute(String s){
            super.onPostExecute(s);
            Log.d("Producte", "***: " + s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                url_imgProd = jsonObject.optString("path_prod");
                url_imgUser = jsonObject.optString("path_user");
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }




    public void goBack(View v){
        Intent intent = new Intent(this, PantallaPrincipal.class);
        startActivity(intent);
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