package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SignUp extends AppCompatActivity {

    private EditText nomRegister;
    private EditText cognomRegister;
    private EditText userRegister;
    private EditText passRegister;
    private EditText descRegister;
    private EditText emailRegister;
    private EditText locationRegister;
    private EditText edatRegister;
    private Button btnRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        nomRegister = findViewById(R.id.newNomtxt);
        cognomRegister = findViewById(R.id.newCognomtxt);
        userRegister = findViewById(R.id.newUsertxt);
        passRegister = findViewById(R.id.newPasstxt);
        descRegister = findViewById(R.id.newDescrtxt);
        emailRegister = findViewById(R.id.newEmailtxt);
        locationRegister = findViewById(R.id.newLocationtxt);
        edatRegister = findViewById(R.id.newEdattxt);

        nomRegister = findViewById(R.id.newNomtxt);
    }

    public void validarRegisterUser(View view){
        //Veure que tots els camps tenen informació
        boolean valid = true;
        if (nomRegister.getText().length() == 0 || cognomRegister.getText().length() == 0 || userRegister.getText().length() == 0
                || passRegister.getText().length() == 0 || descRegister.getText().length() == 0 || emailRegister.getText().length() == 0
                || locationRegister.getText().length() == 0){
            valid = false;
            Toast.makeText(this, R.string.omplirCamps, Toast.LENGTH_LONG).show();
        }

        //Fer la connexió per afegir l'usuari
        if (valid){
            new connexioRegisterUser().execute();
        }
    }

    private class connexioRegisterUser extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            return veureResultat();
        }

        private String veureResultat(){
            String url_server = "http://192.168.244.66:3000/registerNewUser/";
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String validacioUsuari = null;

            try {
                Uri builtURI = Uri.parse(url_server).buildUpon().build();
                URL requestURL = new URL(builtURI.toString());

                urlConnection = (HttpURLConnection) requestURL.openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setDoOutput(true);

                //Dades del BODY
                //USUARI -> email, nom, cognoms, edat, ubicacio, user, pass, descripcio, rol
                String jsonInputString = "{email:'" + emailRegister.getText() +"',nom:'" + nomRegister.getText() + "',cognoms:'" + cognomRegister.getText() + "',edad:" + edatRegister.getText() + ",ubicacio:'" + locationRegister.getText() + "',user: '" + userRegister.getText() + "',pass:'" + passRegister.getText() + "',descripcio: '" + descRegister.getText() + "',rol:'user'}";
                Log.d("JSON",  jsonInputString);


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
            if (s.equals("false")){
            } else if (s.equals("true")){
                //Intent intent = new Intent(this, PantallaPrincipal.class);
                //startActivity(intent);
            }
        }
    }

}