package com.example.projecte_2dam_grup6;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SignIn extends AppCompatActivity {

    private EditText txtUserSignIn;
    private EditText txtPasswordSignIn;
    private Button buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Hide title bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        txtPasswordSignIn = findViewById(R.id.loginUsertxt);
        txtPasswordSignIn = findViewById(R.id.loginPasstxt);
        buttonSignIn = findViewById(R.id.sendDataSignIn);

    }

}