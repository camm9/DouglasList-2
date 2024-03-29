package com.example.douglaslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.user.douglaslist.UserLogin;
import com.user.douglaslist.UserRegistration;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button redirectLogin = findViewById(R.id.btnRedirectToLogin);
        Button redirectSignUp = findViewById(R.id.btnGetStarted);

        redirectLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (MainActivity.this, UserLogin.class));
            }
        });

        redirectSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent (MainActivity.this, UserRegistration.class));
            }
        });
    }
}