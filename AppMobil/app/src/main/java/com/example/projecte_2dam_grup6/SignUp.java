package com.example.projecte_2dam_grup6;


import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignUp extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback {


    private static final int REQUEST_LOCATION_PERMISSION = 1;
    public static final float INITIAL_ZOOM = 12f;
    private GoogleMap mMap;

    private int contadorFlags = 0;

    ApiService apiService;
    private EditText nomRegister;
    private EditText cognomRegister;
    private EditText userRegister;
    private EditText passRegister;
    private EditText descRegister;
    public EditText emailRegister;
    private TextView locationRegister;
    private Button edatRegister;
    private Button btnRegister;
    private Button btnStart;
    private TextView autoGmail;
    private TextView txtErrorRegister;
    private String dateUser;
    private boolean validDate = true;
    private TextView showDateContainer;
    Bitmap mBitmap;
    FloatingActionButton fabCamera;
    Uri picUri;
    private final static int IMAGE_RESULT = 200;
    private byte[] bitmapdata;

    private String server_path;
    private String register_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        // Obtain the SupportMapFragment and get notified when the map is ready
        // to be used.
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
            register_path = obj_settings.getString("registerUser_POST");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        nomRegister = findViewById(R.id.newNomtxt);
        cognomRegister = findViewById(R.id.newCognomtxt);
        userRegister = findViewById(R.id.newUsertxt);
        passRegister = findViewById(R.id.newPasstxt);
        descRegister = findViewById(R.id.newDescrtxt);
        emailRegister = findViewById(R.id.newEmailtxt);
        locationRegister = findViewById(R.id.newLocationtxt);
        edatRegister = findViewById(R.id.btnPickerEdat);
        btnRegister = findViewById(R.id.btnCreateUser);
        btnStart = findViewById(R.id.btnBackToStart);
        autoGmail = findViewById(R.id.txtAutoGmail);
        txtErrorRegister = findViewById(R.id.txtNoValidUserRegister);
        showDateContainer = findViewById(R.id.viewUserInputDate);

        fabCamera = findViewById(R.id.fab);
        fabCamera.setOnClickListener(this);

        initRetrofitClient();
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
        LatLng home = new LatLng(41.376063, 2.108777);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home, INITIAL_ZOOM));

        // Add a ground overlay 100 meters in width to the home location.
        GroundOverlayOptions homeOverlay = new GroundOverlayOptions()
                .image(BitmapDescriptorFactory.fromResource(R.drawable.logo_rounded_hd))
                .position(home, 100);

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
                        locationRegister.setText("" + marker.getPosition().latitude + " " + "" + marker.getPosition().longitude);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab:
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
                break;
        }
    }

    public Intent getPickImageChooserIntent() {

        Uri outputFileUri = getCaptureImageOutputUri();

        List<Intent> allIntents = new ArrayList<>();
        PackageManager packageManager = getPackageManager();

        Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            if (outputFileUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            }
            allIntents.add(intent);
        }


        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        List<ResolveInfo> listGallery = packageManager.queryIntentActivities(galleryIntent, 0);
        for (ResolveInfo res : listGallery) {
            Intent intent = new Intent(galleryIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            allIntents.add(intent);
        }



        Intent mainIntent = allIntents.get(allIntents.size() - 1);
        for (Intent intent : allIntents) {
            if (intent.getComponent().getClassName().equals("com.android.documentsui.DocumentsActivity")) {
                mainIntent = intent;
                break;
            }
        }
        allIntents.remove(mainIntent);

        Intent chooserIntent = Intent.createChooser(mainIntent, "Select source");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, allIntents.toArray(new Parcelable[allIntents.size()]));

        return chooserIntent;
    }

    private Uri getCaptureImageOutputUri() {
        Uri outputFileUri = null;
        File getImage = getExternalFilesDir("");
        if (getImage != null) {
            outputFileUri = Uri.fromFile(new File(getImage.getPath(), "image.png"));
        }
        return outputFileUri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SignUp.RESULT_OK) {
            ImageView imageView = findViewById(R.id.userImage);
            if (requestCode == IMAGE_RESULT) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    mBitmap = BitmapFactory.decodeFile(filePath);
                    imageView.setImageBitmap(mBitmap);
                }
            }
        }
    }

    private String getImageFromFilePath(Intent data) {
        boolean isCamera = data == null || data.getData() == null;

        if (isCamera) return getCaptureImageOutputUri().getPath();
        else return getPathFromURI(data.getData());

    }

    public String getImageFilePath(Intent data) {
        return getImageFromFilePath(data);
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable("pic_uri", picUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        picUri = savedInstanceState.getParcelable("pic_uri");
    }

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        apiService = new Retrofit.Builder().baseUrl(server_path).client(client).build().create(ApiService.class);
    }

    private void multipartImageUpload() {
        try {
            File filesDir = getApplicationContext().getFilesDir();
            Log.d("File img PATH", "PATH: " + filesDir.toString());

            File file = new File(filesDir, "image" + ".png");
            OutputStream os;
            if (mBitmap != null){
                try {

                    os = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                    os.close();
                } catch (Exception e) {
                    Log.e(getClass().getSimpleName(), "Error writing bitmap", e);
                }

                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
                bitmapdata = bos.toByteArray();


                FileOutputStream fos = new FileOutputStream(file);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();


                RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), reqFile);
                RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "myFile");

                Call<ResponseBody> req = apiService.postImage(body, name);
                req.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.e("Upload", String.valueOf(response.body()));
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.d("Error onFailure", "Error onFailure: ");
                    }

                });
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showDatePicker(View view){
        DialogFragment newFragment = new DatePickerFragment();
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
        try {
            Date dataAvuiFormatted = new SimpleDateFormat("dd/MM/yyyy").parse(dataActual);
            Date dataUserFormatted = new SimpleDateFormat("dd/MM/yyyy").parse(dateUser);

            showDateContainer.setText(dateUser);

            if (dataAvuiFormatted.toInstant().isBefore(dataUserFormatted.toInstant())){
                validDate = false;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return cal;
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
                || locationRegister.getText().length() == 0 || showDateContainer.getText().length() == 0){
            valid = false;
            Toast.makeText(this, R.string.omplirCamps, Toast.LENGTH_LONG).show();
        }

        if (!validDate){
            Toast.makeText(this, R.string.errDataValue, Toast.LENGTH_LONG).show();
        }

        for (int i = 0; i < descRegister.getText().length() && valid; i++) {
            char valorText = descRegister.getText().charAt(i);
            if (!isEmojiCharacter(valorText)){
                valid = false;
                Toast.makeText(this, R.string.emojiErrorSignUp, Toast.LENGTH_LONG).show();
            }
        }

        //Fer la connexió per afegir l'usuari
        if (valid && validDate){

            String clerDesc = descRegister.getText().toString();
            clerDesc = clerDesc.replace('\n', ' ');
            descRegister.setText(clerDesc);

            String json = "{\"email\":\""
                    + emailRegister.getText()+ "@gmail.com" + "\",\"nom\":\""
                    + nomRegister.getText() + "\",\"cognoms\":\""
                    + cognomRegister.getText() + "\",\"edad\":\""
                    + dateUser + "\",\"ubicacio\":\""
                    + locationRegister.getText().toString() + "\",\"user\": \""
                    + userRegister.getText() + "\",\"pass\":\""
                    + passRegister.getText() + "\",\"descripcio\": \""
                    + descRegister.getText().toString() + "\",\"rol\":\"user\"}";
            Log.d("JSON", json);

            final String HOST = server_path + register_path;
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
                multipartImageUpload();
                Toast.makeText(SignUp.this, R.string.msg_addUser, Toast.LENGTH_SHORT).show();
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