package com.user.douglaslist;

import static com.user.douglaslist.UserRegistration.isValidEmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.douglaslist.MainActivity;
import com.user.douglaslist.UserRegistration;
import com.example.douglaslist.DabaseHelper;
import com.example.douglaslist.R;

import java.security.SecureRandom;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class UserLogin extends AppCompatActivity {
    DabaseHelper dabaseHelper;
    SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        dabaseHelper = new DabaseHelper(this);

        EditText email = findViewById(R.id.editTxtEmailLogin);
        EditText password = findViewById(R.id.editTxtPasswordLogin);


        Button btnLogin = findViewById(R.id.btnLogin);

        Button btnForgot = findViewById(R.id.btnForgotPassword);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTxt = email.getText().toString();
                String passTxt = password.getText().toString();

                boolean validEmail = isValidEmail(emailTxt);

                if (emailTxt.equals("") || passTxt.equals("")){
                    Toast.makeText(UserLogin.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }

                if (!validEmail) {
                    email.setError("Enter correct email format");
                }else if (!dabaseHelper.checkEmail(emailTxt)){
                    Toast.makeText(UserLogin.this, "Invalid credentials" , Toast.LENGTH_SHORT).show();
                }else {
                    String retrievedPassword = dabaseHelper.checkHashedPassword(emailTxt);
                    BCrypt.Result result = BCrypt.verifyer().verify(passTxt.toCharArray(), retrievedPassword);

                    if (result.verified == true){
                        Toast.makeText(UserLogin.this, "Sign in successful!" , Toast.LENGTH_SHORT).show();
                        // return User object of logged in user
                        User userProfile = dabaseHelper.returnUserProfile(emailTxt);
                        // Store session token
                        String token = generateSessionToken();
                        saveSessionToken(token);
                        // send user to next page
                        Intent intent = new Intent(UserLogin.this, UserProfile.class);
                        intent.putExtra("userData", userProfile);
                        startActivity(intent);

                    } else {
                        Toast.makeText(UserLogin.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnForgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(UserLogin.this,ForgotPassword.class));
            }
        });
    }


    private String generateSessionToken() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);
        return randomBytes.toString();
    }

    private void saveSessionToken(String token){
        sp = getSharedPreferences("DouglasListUserSession", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        String encryptedToken = BCrypt.withDefaults().hashToString(12, token.toCharArray());
        editor.putString("token",encryptedToken);
        //Create an expiration time on session token
        long expirationTime = System.currentTimeMillis() + (30*60*1000);
        editor.putLong("session_expiration", expirationTime);
        editor.apply();
    }

    private String getSessionToken() {
        sp = getSharedPreferences("DouglasListUserSession", MODE_PRIVATE);
        return sp.getString("token", null);
    }

    public static Boolean checkSessionToken(Long storedTime){
        Long currentTime = System.currentTimeMillis();
        if (currentTime > storedTime){
            return false;
        } else return true;
    }

    public static void removeSessionToken(SharedPreferences sp){
        sp.edit().remove("token").apply();
    }

    /* Example of of how to check session token for timer expiration
    * sp = getSharedPreferences("DouglasListUserSession", MODE_PRIVATE);
                Long storedTime = sp.getLong("session_expiration",0);
                Boolean session = UserLogin.checkSessionToken(storedTime);
                if (!session){
                    UserLogin.removeSessionToken(sp);
                    startActivity(new Intent(ForgotPassword.this, MainActivity.class));
                }
    *
    * */

}