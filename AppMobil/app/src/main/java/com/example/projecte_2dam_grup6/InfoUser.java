package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;

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
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class InfoUser extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 12f;

    private String infoUserFromMainPage;
    private String rol_str;
    private String idUserStr;
    private String path_decodeImage;
    private String server_path;
    private String getImagePath;
    private String[] location_values;

    private TextView infoNom;
    private TextView infoCog;
    private TextView infoUser;
    private TextView infoPass;
    private TextView infoEmail;
    private TextView infoDesc;
    private TextView infoData;
    private TextView infoLocation;
    private TextView infoRol;

    private Button accessButton;
    private Button backToStart;

    private CheckBox checkBox;

    private CircleImageView circleImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_user);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        //Llegir les dades del fitxer JSON en ASSETS
        try {
            JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
            server_path = obj_settings.getString("server");
            getImagePath = obj_settings.getString("imageUserLogin");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Intent intent = getIntent();
        infoUserFromMainPage = intent.getStringExtra(PantallaPrincipal.EXTRA_MESSAGE);

        accessButton = findViewById(R.id.btnEnterPeticio);
        backToStart = findViewById(R.id.btnBackToStart);

        infoNom = findViewById(R.id.infoNom);
        infoCog = findViewById(R.id.infoCognom);
        infoUser = findViewById(R.id.infoUsername);
        infoPass = findViewById(R.id.infoPass);
        infoEmail = findViewById(R.id.infoEmail);
        infoDesc = findViewById(R.id.infoDesc);
        infoData = findViewById(R.id.infoData);
        infoLocation = findViewById(R.id.infoLocation);
        infoRol = findViewById(R.id.infoRol);
        checkBox = findViewById(R.id.checkPass);

        circleImageView = findViewById(R.id.infoUserImage);


        //Separa json
        try {
            //Que usuario esta consultando
            JSONArray jsonArray = new JSONArray(infoUserFromMainPage);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            Log.d("Pantalla_USER", "Pantalla user personal: " + jsonObject);
            infoNom.setText(jsonObject.optString("nom"));
            infoCog.setText(jsonObject.optString("cognoms"));
            infoUser.setText(jsonObject.optString("user"));
            infoPass.setText(jsonObject.optString("pass"));
            infoEmail.setText(jsonObject.optString("email"));
            infoDesc.setText(jsonObject.optString("descripcio"));
            infoData.setText(jsonObject.optString("data_naixament"));
            infoLocation.setText(jsonObject.optString("ubicacio"));
            rol_str = jsonObject.optString("rol");
            idUserStr = jsonObject.optString("id");
            infoRol.setText(rol_str);
            //infoRol.setText(rol_str);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        location_values = infoLocation.getText().toString().split(" "); //Per ficar la localitzacio de l'usuari com a predeterminada

        //No veure el boto si ja es artista
        if (!rol_str.equals("user")){
            accessButton.setVisibility(View.GONE);
        }

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //Veure la contrasenya
                    infoPass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //Veure la contrasenya en punts
                    infoPass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        getImageUser();

    }

    private void getImageUser() {
        new getDecodedImageUserLogIn().execute();
    }

    //GET IMAGE USER
    private class getDecodedImageUserLogIn extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return veureResultat();
        }

        private String veureResultat(){
            String url_server = server_path + getImagePath + "/" + idUserStr;
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
            ImageView iv = circleImageView;
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

    public void backToStart(View view){
        super.onBackPressed();
    }

    public void accessPeticiones(View view){
        Intent intent = new Intent(InfoUser.this, PeticioArtista.class);
        intent.putExtra("ID_USUARI", idUserStr);
        startActivity(intent);
    }


    /**
     * Triggered when the map is ready to be used.
     *
     * @param googleMap The GoogleMap object representing the Google Map.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Pan the camera to your home address (in this case, Google HQ).
        LatLng home = new LatLng(Double.parseDouble(location_values[0]), Double.parseDouble(location_values[1]));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, INITIAL_ZOOM));

        // Add a ground overlay 100 meters in width to the home location.
        GroundOverlayOptions homeOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.logo_rounded_hd))
                .position(home, 100);

        mMap.addGroundOverlay(homeOverlay);

        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        // Set a long click listener for the map;
        setPoiClick(mMap); // Set a click listener for points of interest.
        setMapStyle(mMap); // Set the custom map style.
        enableMyLocation(mMap); // Enable location tracking.
        // Enable going into StreetView by clicking on an InfoWindow from a
        // point of interest.
    }

    /**
     * Adds a marker when a place of interest (POI) is clicked with the name of
     * the POI and immediately shows the info window.
     *
     * @param map The GoogleMap to attach the listener to.
     */
    private void setPoiClick(final GoogleMap map) {
        map.setOnPoiClickListener(new GoogleMap.OnPoiClickListener() {
            @Override
            public void onPoiClick(PointOfInterest poi) {
                Marker poiMarker = map.addMarker(new MarkerOptions()
                        .position(poi.latLng)
                        .title(poi.name));
                poiMarker.showInfoWindow();
                poiMarker.setTag(getString(R.string.poi));
            }
        });
    }

    /**
     * Loads a style from the map_style.json file to style the Google Map. Log
     * the errors if the loading fails.
     *
     * @param map The GoogleMap object to style.
     */
    private void setMapStyle(GoogleMap map) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = map.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.map_style));

            if (!success) {
                Log.e("TAG", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("ERR", "Can't find style. Error: ", e);
        }
    }

    /**
     * Checks for location permissions, and requests them if they are missing.
     * Otherwise, enables the location layer.
     */
    private void enableMyLocation(GoogleMap map) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            map.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

}