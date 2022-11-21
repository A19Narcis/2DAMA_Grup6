package com.example.projecte_2dam_grup6;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditUser extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_MESSAGE = "com.example.projecte_2dam_grup6.extra.MESSAGE";

    private GoogleMap mMap;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 14f;

    private String arrInfoUser;
    private String server_path;
    private String getImagePath;
    private String dateUser;
    private String path_decodeImage;
    private String editUser_path;
    private String data_defautl;
    private String jsonNovesDades;
    private String rolUser;

    private String id_usuari;
    private String nomUser;
    private String cogUser;
    private String username;
    private String userPass;
    private String userEmail;
    private String userDesc;
    private String userDataN;
    private String userLocation;
    private String[] location_values;

    private int contadorFlags = 0;

    private CircleImageView circleImageView;

    private boolean validDate;

    private EditText editNom;
    private EditText editCognom;
    private EditText editUsername;
    private EditText editPassword;
    private EditText editEmail;
    private EditText editDesc;

    private TextView editDataNaix;
    private TextView editLocation;

    private CheckBox checkBox;

    private Button updateUserBTN;
    private Button cancelUpdateBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, mapFragment).commit();
        mapFragment.getMapAsync(this);

        circleImageView = findViewById(R.id.imageViewUser);

        editNom = findViewById(R.id.editNom);
        editCognom = findViewById(R.id.editCognom);
        editUsername = findViewById(R.id.editUsername);
        editPassword = findViewById(R.id.editPassword);
        editEmail = findViewById(R.id.editEmail);
        editDesc = findViewById(R.id.editDesc);

        editDataNaix = findViewById(R.id.editDataNaix);
        editLocation = findViewById(R.id.editLocation);

        checkBox = findViewById(R.id.checkPass);

        updateUserBTN = findViewById(R.id.btnUpdateUser);
        cancelUpdateBTN = findViewById(R.id.btnCancelUpdate);

        Intent intent = getIntent();
        arrInfoUser = intent.getStringExtra(PantallaPrincipal.EXTRA_MESSAGE);

        //Llegir les dades del fitxer JSON en ASSETS
        try {
            JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
            server_path = obj_settings.getString("server");
            getImagePath = obj_settings.getString("imageUserLogin");
            editUser_path = obj_settings.getString("editUser");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            JSONArray jsonArray = new JSONArray(arrInfoUser);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            Log.d("Usuari", "DADES: " + jsonObject);
            nomUser = jsonObject.optString("nom");
            cogUser = jsonObject.optString("cognoms");
            username = jsonObject.optString("user");
            userPass = jsonObject.optString("pass");
            userEmail = jsonObject.optString("email");
            userDesc = jsonObject.optString("descripcio");
            userDataN = jsonObject.optString("data_naixament");
            userLocation = jsonObject.optString("ubicacio");
            id_usuari = jsonObject.optString("id");
            rolUser = jsonObject.optString("rol");
        } catch (JSONException e){
            e.printStackTrace();
        }

        location_values = userLocation.split(" "); //Per ficar la localitzacio de l'usuari com a predeterminada

        String[] email_text = userEmail.split("@");

        editNom.setText(nomUser);
        editCognom.setText(cogUser);
        editUsername.setText(username);
        editPassword.setText(userPass);
        editEmail.setText(email_text[0]);
        editDesc.setText(userDesc);
        editDataNaix.setText(userDataN);
        editLocation.setText(userLocation);

        data_defautl = editDataNaix.getText().toString();


        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked){
                    //Veure la contrasenya
                    editPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    //Veure la contrasenya en punts
                    editPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        getImageUser();
    }


    public void validarUpdateUser(View view){
        //Veure que tots els camps tenen informació
        boolean valid = true;

        //Veure que el text no son espais
        String val_editNom = editNom.getText().toString().replace(" ", "");
        String val_editCog = editCognom.getText().toString().replace(" ", "");
        String val_editUse = editUsername.getText().toString().replace(" ", "");
        String val_editPas = editPassword.getText().toString().replace(" ", "");
        String val_editDes = editDesc.getText().toString().replace(" ", "");
        String val_editEma = editEmail.getText().toString().replace(" ", "");

        if (val_editNom.length() == 0 || val_editCog.length() == 0 || val_editUse.length() == 0
                || val_editPas.length() == 0 || val_editDes.length() == 0 || val_editEma.length() == 0
                || editLocation.getText().length() == 0 || editDataNaix.getText().length() == 0){
            valid = false;
            Toast.makeText(this, R.string.omplirCamps, Toast.LENGTH_LONG).show();
        }

        if (!validDate){
            if (data_defautl.equals(editDataNaix.getText().toString())){
                validDate = true;
            } else {
                Toast.makeText(this, R.string.errDataValue, Toast.LENGTH_LONG).show();
            }
        }

        for (int i = 0; i < editDesc.getText().length() && valid; i++) {
            char valorText = editDesc.getText().charAt(i);
            if (!isEmojiCharacter(valorText)){
                valid = false;
                Toast.makeText(this, R.string.emojiErrorSignUp, Toast.LENGTH_LONG).show();
            }
        }

        //Fer la connexió per editar
        if (valid && validDate){
            AlertDialog.Builder alertaLogOut = new AlertDialog.Builder(EditUser.this);
            alertaLogOut.setTitle(R.string.alert_editUser_title_update);
            alertaLogOut.setMessage(R.string.text_update_editUser);
            alertaLogOut.setPositiveButton(R.string.pos_option_EditUser, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String clerDesc = editDesc.getText().toString();
                    clerDesc = clerDesc.replace('\n', ' ');
                    editDesc.setText(clerDesc);

                    jsonNovesDades = "{\"email\":\""
                            + editEmail.getText()+ "@gmail.com" + "\",\"nom\":\""
                            + editNom.getText() + "\",\"cognoms\":\""
                            + editCognom.getText() + "\",\"data_naixament\":\""
                            + editDataNaix.getText().toString() + "\",\"ubicacio\":\""
                            + editLocation.getText().toString() + "\",\"user\": \""
                            + editUsername.getText() + "\",\"pass\":\""
                            + editPassword.getText() + "\",\"descripcio\": \""
                            + editDesc.getText().toString() + "\",\"id\":\""
                            + id_usuari + "\",\"rol\":\""
                            + rolUser + "\"}";
                    Log.d("JSON", jsonNovesDades);

                    final String HOST = server_path + editUser_path + "/" + id_usuari;
                    new connexioEditUser().execute(HOST, jsonNovesDades);
                }
            });
            alertaLogOut.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Es queda a la pantalla
                }
            });
            alertaLogOut.show();
        }
    }

    private class connexioEditUser extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... strings) {
            String urlString = strings[0];
            String jsonInfo = strings[1];
            return validacioEditUser(urlString, jsonInfo);
        }

        public String validacioEditUser(String urlString, String json) {
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
                Toast.makeText(EditUser.this, R.string.errRegisterText, Toast.LENGTH_SHORT).show();
            } else if (s.equals("true")) {
                Toast.makeText(EditUser.this, R.string.toast_msg_update, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(EditUser.this, PantallaPrincipal.class);
                intent.putExtra(EXTRA_MESSAGE, "["+jsonNovesDades+"]");
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


    private boolean isEmojiCharacter(char valorText) {
        return (valorText == 0x0) || (valorText == 0x9) || (valorText == 0xA) ||
                (valorText == 0xD) || ((valorText >= 0x20) && (valorText <= 0xD7FF)) ||
                ((valorText >= 0xE000) && (valorText <= 0xFFFD)) || ((valorText >= 0x10000)
                && (valorText <= 0x10FFFF));
    }

    public void cancelActionEditUser(View view){
        AlertDialog.Builder alertaLogOut = new AlertDialog.Builder(EditUser.this);
        alertaLogOut.setTitle(R.string.title_cancel_editUser);
        alertaLogOut.setMessage(R.string.text_cancel_editUser);
        alertaLogOut.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goBack();
            }
        });
        alertaLogOut.setNegativeButton(R.string.cancel_option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Es queda a la pantalla
            }
        });
        alertaLogOut.show();
    }

    private void goBack(){
        super.onBackPressed();
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
                .image(BitmapDescriptorFactory.fromResource(R.drawable.maps_mark))
                .position(home, 200);

        mMap.addGroundOverlay(homeOverlay);

        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        setMapLongClick(mMap); // Set a long click listener for the map;
        setPoiClick(mMap); // Set a click listener for points of interest.
        setMapStyle(mMap); // Set the custom map style.
        enableMyLocation(mMap); // Enable location tracking.
        // Enable going into StreetView by clicking on an InfoWindow from a
        // point of interest.
        setInfoWindowClickToPanorama(mMap);
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

    /**
     * Adds a blue marker to the map when the user long clicks on it.
     *
     * @param map The GoogleMap to attach the listener to.
     */
    private void setMapLongClick(final GoogleMap map) {

        // Add a blue marker to the map when the user performs a long click.
        map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                Log.d("FLAGS", "cotadorTheGrefg: " + contadorFlags);
                String snippet = String.format(Locale.getDefault(),
                        getString(R.string.lat_long_snippet),
                        latLng.latitude,
                        latLng.longitude);

                if (contadorFlags == 0){
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.dropped_pin))
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)));

                    contadorFlags++;
                } else if (contadorFlags == 1){
                    map.clear();
                    contadorFlags--;
                    map.addMarker(new MarkerOptions()
                            .position(latLng)
                            .title(getString(R.string.dropped_pin))
                            .snippet(snippet)
                            .icon(BitmapDescriptorFactory.defaultMarker
                                    (BitmapDescriptorFactory.HUE_BLUE)));
                    contadorFlags++;
                    Log.d("FLAGS", "estamos aqui: " + contadorFlags);
                }

            }
        });
    }

    /**
     * Starts a Street View panorama when an info window containing the poi tag
     * is clicked.
     *
     * @param map The GoogleMap to set the listener to.
     */
    private void setInfoWindowClickToPanorama(GoogleMap map) {
        map.setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        editLocation.setText("" + marker.getPosition().latitude + " " + "" + marker.getPosition().longitude);
                        // Check the tag
                        if (marker.getTag() == "poi") {

                            // Set the position to the position of the marker
                            StreetViewPanoramaOptions options =
                                    new StreetViewPanoramaOptions().position(
                                            marker.getPosition());

                            SupportStreetViewPanoramaFragment streetViewFragment
                                    = SupportStreetViewPanoramaFragment
                                    .newInstance(options);

                            // Replace the fragment and add it to the backstack
                            getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.fragment_container, streetViewFragment)
                                    .addToBackStack(null).commit();
                        }
                    }
                });
    }



    public void showDatePicker(View view){ //No entra de primeras porque ya hay una seleccionada por defecto LA DEL USUARIO
        DialogFragment newFragment = new DatePickerFragmentEdit();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void processDatePickerResult(int year, int month, int day) {
        String month_string = Integer.toString(month+1);
        String day_string = Integer.toString(day);
        String year_string = Integer.toString(year);
        dateUser = (day_string + "/" + month_string + "/" + year_string);
        String dataActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());

        validDate = true;

        Log.d("DATE_TAG", "dataAvui: " + dataActual);
        Log.d("DATE_TAG", "dataUser: " + dateUser);

        try {
            Date dataAvuiFormatted = new SimpleDateFormat("dd/MM/yyyy").parse(dataActual);
            Date dataUserFormatted = new SimpleDateFormat("dd/MM/yyyy").parse(dateUser);

            editDataNaix.setText(dateUser);

            if (dataAvuiFormatted.toInstant().isBefore(dataUserFormatted.toInstant())){
                validDate = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
            String url_server = server_path + getImagePath + "/" + id_usuari;
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

}