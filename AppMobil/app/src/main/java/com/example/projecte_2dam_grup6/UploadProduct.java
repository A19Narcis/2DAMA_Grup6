package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

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

public class UploadProduct extends AppCompatActivity implements View.OnClickListener {

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
        fabCamera.setOnClickListener(this);

        initRetrofitClient();
    }

    private void initRetrofitClient() {
        OkHttpClient client = new OkHttpClient.Builder().build();

        apiService = new Retrofit.Builder().baseUrl(server_path).client(client).build().create(ApiService.class);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fab_image:
                startActivityForResult(getPickImageChooserIntent(), IMAGE_RESULT);
                break;
        }
    }

    private void multipartImageUpload() {
        try {
            File filesDir = getApplicationContext().getFilesDir();
            Log.d("File img PATH", "PATH: " + filesDir.toString());

            File file = new File(filesDir, "image" + ".png");
            OutputStream os;
            if (mBitmap != null){
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
        if (titolProd.getText().length() == 0
                || (preuProd.getText().length() == 0 || preuProd.getText().toString().startsWith("."))
                || (catProd.getText().length() == 0 || catProd.getText().equals("Categoria"))
                || descProd.getText().length() == 0){
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
                String json = "[{\"id_usu\":" + id_user_login
                        + ",\"nom\":\"" + titolProd.getText()
                        + "\",\"preu\":" + preuProd.getText()
                        + ",\"categoria\":\"" + catProd.getText()
                        + "\",\"descripcion\":\"" + descProd.getText() + "\"}]";
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
            goBack();
        }
    }

    private void goBack(){
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SignUp.RESULT_OK) {
            ImageView imageView = findViewById(R.id.productImage);
            if (requestCode == IMAGE_RESULT) {
                String filePath = getImageFilePath(data);
                if (filePath != null) {
                    mBitmap = BitmapFactory.decodeFile(filePath);
                    imageView.setImageBitmap(mBitmap);
                }
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

}