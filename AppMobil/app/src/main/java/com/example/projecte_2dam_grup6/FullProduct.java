package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FullProduct extends AppCompatActivity {

    private String server_path;
    private String getSingleProductPath;
    private String addLikePath;
    private String remLikePath;

    private String id_prod_selec;
    private String id_usuari;
    private String id_image_producte;
    private String id_usuari_prod;

    private TextView titleProd;
    private String path_decodeProd;
    private TextView descProd;
    private TextView nomUserProd;
    private String url_imageUser;
    private TextView preuProd;

    private String path_decodeImage;

    private ImageView userImageInProduct;
    private ImageView imageViewProduct;

    private Button likeBtn;
    private Button dislikeBtn;
    private Button goBack;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_product);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        try {
            JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
            server_path = obj_settings.getString("server");
            getSingleProductPath = obj_settings.getString("dadesSingleProducte");
            url_imageUser = obj_settings.getString("imageUserLogin");
            addLikePath = obj_settings.getString("addNewLike");
            remLikePath = obj_settings.getString("removeLike");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        id_prod_selec = intent.getStringExtra("INFO_PROD");
        id_usuari = intent.getStringExtra("ID_USUARI_LOGIN");


        titleProd = findViewById(R.id.textViewNomProducte);
        descProd = findViewById(R.id.textViewDescripcioProducte);
        nomUserProd = findViewById(R.id.nomUserProduct);
        preuProd = findViewById(R.id.preuProducte);
        userImageInProduct = findViewById(R.id.cardViewUserImageProducte);
        imageViewProduct = findViewById(R.id.imageViewProducte);
        likeBtn = findViewById(R.id.btn_like);
        dislikeBtn = findViewById(R.id.btn_dontLike);
        goBack = findViewById(R.id.btnBackToStart);

        getInfoProducte(); //Agafar les dades del producte

    }


    public void backToStart(View view){
        super.onBackPressed();
    }


    public void afegirMeGusta (View view){
        String json = "{\"id_usuari\":\"" + id_usuari +
                "\",\"id_producte\":\"" + id_prod_selec +
                "\",\"id_image_prod\":\"" + id_image_producte + "\"}";
        Log.d("PRODUTCTE LIKE", json);

        final String HOST = server_path + addLikePath;
        new connectionAddLikeProduct().execute(HOST, json);
    }

    public void removeMeGusta(View view) {
        String json = "{\"id_usuari\":\"" + id_usuari +
                "\",\"id_producte\":\"" + id_prod_selec +
                "\",\"id_image_prod\":\"" + id_image_producte + "\"}";
        Log.d("PRODUTCTE DISLIKE", json);

        final String HOST = server_path + remLikePath;
        new connectionRemLikeProduct().execute(HOST, json);
    }

    private class connectionRemLikeProduct extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];
            String jsonInfo = strings[1];
            return validacioRemLikeProduct(urlString, jsonInfo);
        }

        public String validacioRemLikeProduct(String urlString, String json) {
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
                Toast.makeText(FullProduct.this, R.string.rem_prodNotInList, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FullProduct.this, R.string.rem_product_msg, Toast.LENGTH_SHORT).show();
            }
        }

    }


    private class connectionAddLikeProduct extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings) {
            String urlString = strings[0];
            String jsonInfo = strings[1];
            return validacioAddLikeProduct(urlString, jsonInfo);
        }

        public String validacioAddLikeProduct(String urlString, String json) {
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
                Toast.makeText(FullProduct.this, R.string.add_prodAlreadyInList, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(FullProduct.this, R.string.add_product_msg, Toast.LENGTH_SHORT).show();
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
                nomUserProd.setText("@" +jsonObject.optString("user"));
                path_decodeProd = jsonObject.optString("path_prod");
                id_image_producte = jsonObject.optString("id_image");
                id_usuari_prod = jsonObject.optString("id");

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
            String url_server = server_path + url_imageUser + "/" + id_usuari_prod;
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