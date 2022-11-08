package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

public class UploadProduct extends AppCompatActivity {

    private Button btnBack;
    String[] cat_prod = {"Joguina", "Eina", "Roba", "Moble"};
    private AutoCompleteTextView autoCompleteProduteCat;
    private ArrayAdapter<String> adapterCat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_product);

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        btnBack = findViewById(R.id.btnBack);
        autoCompleteProduteCat = findViewById(R.id.idAutoCategoria);
        adapterCat = new ArrayAdapter<String>(this, R.layout.list_cat_prod);
        autoCompleteProduteCat.setAdapter(adapterCat);
        autoCompleteProduteCat.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String categoria = adapterView.getItemAtPosition(i).toString();
            }
        });

    }

    public void backToStart(View view){
        Intent intent = new Intent(this, PantallaPrincipal.class);
        startActivity(intent);
    }
}