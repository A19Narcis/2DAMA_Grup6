package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
    private String path_decodeProd;
    private TextView descProd;
    private TextView nomUserProd;
    private String url_imageUser;
    private TextView preuProd;

    private String path_decodeImage;

    private ImageView userImageInProduct;
    private ImageView imageViewProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_product);

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Intent intent = getIntent();
        id_prod_selec = intent.getStringExtra("INFO_PROD");


        titleProd = findViewById(R.id.textViewNomProducte);
        descProd = findViewById(R.id.textViewDescripcioProducte);
        nomUserProd = findViewById(R.id.nomUserProduct);
        preuProd = findViewById(R.id.preuProducte);
        userImageInProduct = findViewById(R.id.cardViewUserImageProducte);
        imageViewProduct = findViewById(R.id.imageViewProducte);

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
                titleProd.setText(jsonObject.optString("nom"));
                preuProd.setText(jsonObject.optString("preu") + " €");
                descProd.setText(jsonObject.optString("descripcion"));
                nomUserProd.setText(jsonObject.optString("user"));
                path_decodeProd = jsonObject.optString("path_prod");

                StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(gfgPolicy);
                InputStream in =null;
                Bitmap bmp = null;
                ImageView iv = imageViewProduct;
                int responseCode = -1;
                try{
                    URL url = new URL(path_decodeProd);//LINK IMG SERVER
                    Log.d("PATH", "URL: " + url);
                    HttpURLConnection con = (HttpURLConnection)url.openConnection();
                    con.setDoInput(true);
                    con.connect();
                    responseCode = con.getResponseCode();
                    if(responseCode == HttpURLConnection.HTTP_OK)
                    {
                        //download
                        in = con.getInputStream();
                        bmp = BitmapFactory.decodeStream(in);
                        in.close();
                        iv.setImageBitmap(bmp);
                    }

                }
                catch(Exception ex){
                    Log.e("Exception",ex.toString());
                }

                accessImageUser();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }



    private void accessImageUser() {
        new getImageUser().execute();
    }

    private class getImageUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return getPathImageUser();
        }

        private String getPathImageUser(){
            //Llegir les dades del fitxer JSON en ASSETS
            try {
                JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
                url_imageUser = obj_settings.getString("imageUserLogin");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url_server = server_path + url_imageUser + "/" + nomUserProd.getText().toString();
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
            super.onPostExecute(s);
            StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
            path_decodeImage = s;
            InputStream in =null;
            Bitmap bmp = null;
            ImageView iv = userImageInProduct;
            int responseCode = -1;
            try{
                URL url = new URL(path_decodeImage);//LINK IMG SERVER
                Log.d("PATH", "URL: " + url);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setDoInput(true);
                con.connect();
                responseCode = con.getResponseCode();
                if(responseCode == HttpURLConnection.HTTP_OK)
                {
                    //download
                    in = con.getInputStream();
                    bmp = BitmapFactory.decodeStream(in);
                    in.close();
                    iv.setImageBitmap(bmp);
                }

            }
            catch(Exception ex){
                Log.e("Exception",ex.toString());
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