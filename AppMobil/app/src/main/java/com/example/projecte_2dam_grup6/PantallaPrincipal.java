package com.example.projecte_2dam_grup6;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.projecte_2dam_grup6.databinding.ActivityPantallaPrincipalBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PantallaPrincipal extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPantallaPrincipalBinding binding;

    private String server_path;
    private String loginValidate_path;

    private String dadesUserLogIn;

    //Valors DADES USER
    private String email;
    private String nom;
    private String cognoms;
    private String ubicacio;
    private String rol;
    private String desc;
    private String dataN;

    Bitmap bitmapImage;
    String path_decodeImage;
    ImageView navUserImage;
    CardView card;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getinfoUser();

        //GET DADES USER LOGIN
        Intent intent = getIntent();
        dadesUserLogIn = intent.getStringExtra(SignIn.EXTRA_MESSAGE);

        binding = ActivityPantallaPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarPantallaPrincipal.toolbar);
        binding.appBarPantallaPrincipal.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PantallaPrincipal.this, UploadProduct.class);
                startActivity(intent);
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = (TextView) headerView.findViewById(R.id.userLogIn);
        navUserImage = (ImageView) headerView.findViewById(R.id.imageUserLogIn);
        card = (CardView) headerView.findViewById(R.id.view2);
        card.setBackgroundColor(Color.TRANSPARENT);
        card.setCardElevation(0);
        //navImageView.setImageBitmap(bitmapImage);
        navUsername.setText(dadesUserLogIn);



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home).setOpenableLayout(drawer).build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_pantalla_principal);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void getinfoUser() {
        new getDadesUserLogIn().execute();
    }

    private class getDadesUserLogIn extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return veureResultat();
        }

        private String veureResultat(){
            //Llegir les dades del fitxer JSON en ASSETS
            try {
                JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
                server_path = obj_settings.getString("server");
                loginValidate_path = obj_settings.getString("dadesUserLogIn");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url_server = server_path + loginValidate_path + "/" + dadesUserLogIn;
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
            System.out.println("VALOR S ---> " + s);
            super.onPostExecute(s);
            Log.d("INFO", "User: " + s);
            try {
                JSONArray jsonArray = new JSONArray(s);
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                email = jsonObject.optString("email");
                nom = jsonObject.optString("nom");
                cognoms = jsonObject.optString("cognoms");
                ubicacio = jsonObject.optString("ubicacio");
                rol = jsonObject.optString("rol");
                desc = jsonObject.optString("descripcio");
                dataN = jsonObject.optString("data_naixament");
                new getImageUserLogIn().execute();
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    //GET IMAGE USER
    private class getImageUserLogIn extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return veureResultat();
        }

        private String veureResultat(){
            //Llegir les dades del fitxer JSON en ASSETS
            try {
                JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
                server_path = obj_settings.getString("server");
                loginValidate_path = obj_settings.getString("imageUserLogin");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url_server = server_path + loginValidate_path + "/" + dadesUserLogIn;
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
            System.out.println("VALOR S ---> " + s);
            super.onPostExecute(s);
            Log.d("INFO", "Image USER: " + s);
            StrictMode.ThreadPolicy gfgPolicy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(gfgPolicy);
            path_decodeImage = s;
            InputStream in =null;
            Bitmap bmp = null;
            ImageView iv = navUserImage;
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pantalla_principal, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_pantalla_principal);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}