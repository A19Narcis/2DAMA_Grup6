package com.example.projecte_2dam_grup6;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UploadProduct extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.projecte_2dam_grup6.extra.MESSAGE";

    private Button btnBack;
    public AutoCompleteTextView autoCompleteProduteCat;
    ArrayAdapter<String> adapterCat;
    private EditText titolProd;
    private EditText descProd;
    private EditText preuProd;
    private AutoCompleteTextView catProd;
    private Button addProduct;

    private String server_path;
    private String addProduct_path;

    private String dadesUserLogIn;

    private String json;

    private ImageView imageView;

    private boolean prodImageExists = true;
    Bitmap mBitmap;
    FloatingActionButton fabCamera;
    Uri picUri;
    ApiService apiService;
    private final static int IMAGE_RESULT = 200;

    Boolean ready = false;
    int id_user_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        LinearLayout linearLayout = findViewById(R.id.linearLayout);
        AnimationDrawable animationDrawable = (AnimationDrawable) linearLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2500);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();

        //GET DADES USER LOGIN
        Intent intent = getIntent();
        dadesUserLogIn = intent.getStringExtra(PantallaPrincipal.EXTRA_MESSAGE);

        try {
            JSONArray jsonArray = new JSONArray(dadesUserLogIn);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            id_user_login = jsonObject.optInt("id");
        } catch (JSONException e){
            e.printStackTrace();
        }

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnBack = findViewById(R.id.btnBack);
        autoCompleteProduteCat = findViewById(R.id.idAutoCategoria);
        titolProd = findViewById(R.id.newProductTitol);
        descProd = findViewById(R.id.newProductDesc);
        preuProd = findViewById(R.id.newProductPreu);
        catProd = findViewById(R.id.idAutoCategoria);
        addProduct = findViewById(R.id.btnAddProduct);
        imageView = findViewById(R.id.productImage);

        //Llegir les dades del fitxer JSON en ASSETS
        try {
            JSONObject obj_settings = new JSONObject(loadJSONFromAsset());
            server_path = obj_settings.getString("server");
            addProduct_path = obj_settings.getString("addNewProduct");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String[] cat_prod = new String[4];
        cat_prod[0] = getResources().getString(R.string.opt_upload_Juguete);
        cat_prod[1] = getResources().getString(R.string.opt_upload_Herramienta);
        cat_prod[2] = getResources().getString(R.string.opt_upload_Mueble);
        cat_prod[3] = getResources().getString(R.string.opt_upload_Ropa);

        //Drop Down MENU (Categories)
        adapterCat = new ArrayAdapter<String>(this, R.layout.list_cat_prod, cat_prod);
        autoCompleteProduteCat.setAdapter(adapterCat);
        autoCompleteProduteCat.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String categoria = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(UploadProduct.this, "Categoria: "+ categoria, Toast.LENGTH_SHORT);
            }
        });

        fabCamera = findViewById(R.id.fab_image);

        fabCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        initRetrofitClient();
    }

    public void selectImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        iniciIntentImage.launch(intent);
    }

    ActivityResultLauncher<Intent> iniciIntentImage = registerForActivityResult(
            new ActivityResultContracts
                    .StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    if (data != null  && data.getData() != null) {
                        Uri selectedImageUri = data.getData();
                        mBitmap = null;
                        try {
                            mBitmap = MediaStore.Images.Media.getBitmap(
                                    this.getContentResolver(),
                                    selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageView.setImageBitmap(mBitmap);
                    }
                }
            });


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
                prodImageExists = true;
                if (ready){
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
                    byte[] bitmapdata = bos.toByteArray();


                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bitmapdata);
                    fos.flush();
                    fos.close();

                    RequestBody reqFile = RequestBody.create(MediaType.parse("image/*"), file);
                    MultipartBody.Part body = MultipartBody.Part.createFormData("myFile", file.getName(), reqFile);
                    RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "myFile");

                    Call<ResponseBody> req = apiService.postImageProd(body, name);
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
            } else {
                prodImageExists = false;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void backToStart(View view){
        super.onBackPressed();
    }



    public void validacioAddProducte(View view){
        boolean valid = true;
        //CAP TEXT SENSE VALOR
        String clearDesc = descProd.getText().toString();
        clearDesc = clearDesc.replace('\n', ' ');

        String clearCat = catProd.getText().toString();

        Log.d("CATEGORIA", "categoria: " + catProd.getText());

        if (titolProd.getText().length() == 0
                || (preuProd.getText().length() == 0 || preuProd.getText().toString().startsWith(".")
                || preuProd.getText().toString().endsWith(".") || clearCat.equals("Categoria") || clearCat.equals("Category"))
                || clearDesc.length() == 0){
            valid = false;
        }

        //NO EMOJIS EN LA DESCRIPCIO
        for (int i = 0; i < descProd.getText().length() && valid; i++) {
            char valorText = descProd.getText().charAt(i);
            if (!isEmojiCharacter(valorText)){
                valid = false;
                Toast.makeText(this, R.string.emojiErrorSignUp, Toast.LENGTH_LONG).show();
            }
        }

        if (valid){
            multipartImageUpload();
            Log.d("IMAGE_PROD", "image exists? -> " + prodImageExists);
            if (!prodImageExists){
                Toast.makeText(this, R.string.msg_imageNeeded, Toast.LENGTH_LONG).show();
            } else {
                json = "{\"id_usu\":" + id_user_login
                        + ",\"nom\":\"" + titolProd.getText()
                        + "\",\"preu\":" + preuProd.getText()
                        + ",\"categoria\":\"" + catProd.getText()
                        + "\",\"descripcion\":\"" + descProd.getText() + "\"}";
                Log.d("JSON", "Prodcute: " + json);

                //CONNEXIO SERVER
                final String HOST = server_path + addProduct_path;
                new connexioAddProducte().execute(HOST, json);
            }
        } else {
            Toast.makeText(this, R.string.omplirCamps, Toast.LENGTH_LONG).show();
        }

    }

    private class connexioAddProducte extends AsyncTask<String, Void, String> {
        @Override
        public String doInBackground(String... strings) {
            String urlString = strings[0];
            String jsonInfo = strings[1];
            return validacioNewProduct(urlString, jsonInfo);
        }

        public String validacioNewProduct(String urlString, String json) {
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
            ready = true;
            multipartImageUpload();
            Toast.makeText(UploadProduct.this, R.string.msg_addNewProd, Toast.LENGTH_SHORT).show();
            //goBack();
            Intent intent = new Intent(UploadProduct.this, PantallaPrincipal.class);
            intent.putExtra(EXTRA_MESSAGE, dadesUserLogIn);
            startActivity(intent);
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