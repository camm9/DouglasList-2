package com.user.douglaslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.douglaslist.DabaseHelper;
import com.example.douglaslist.MainActivity;
import com.example.douglaslist.R;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class ChangePassword extends AppCompatActivity {
    SharedPreferences sp;
    DabaseHelper dabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        dabaseHelper = new DabaseHelper(ChangePassword.this);


        User userProfile = (User) getIntent().getSerializableExtra("userData");
        String emailTxt = userProfile.getEmail();
        String retrievedPassword = userProfile.getPassword();

        //Check user session token
       sp = getSharedPreferences("DouglasListUserSession", MODE_PRIVATE);
        Long storedTime = sp.getLong("session_expiration",0);
        Boolean session = UserLogin.checkSessionToken(storedTime);
        if (!session){
            UserLogin.removeSessionToken(sp);
            startActivity(new Intent(ChangePassword.this, MainActivity.class));
        }

        Button btnUpdatePW = findViewById(R.id.btnCompletePWUpdate);

        btnUpdatePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText oldPW = findViewById(R.id.currentPW);
                String passTxt = oldPW.getText().toString();
                EditText newPW = findViewById(R.id.newPW);
                String newPWtxt = newPW.getText().toString();
                boolean validPassword = UserRegistration.isValidPassword(newPWtxt);

                BCrypt.Result result = BCrypt.verifyer().verify(passTxt.toCharArray(), retrievedPassword);

                if (passTxt.equals("") || newPWtxt.equals("")){
                    oldPW.setError("Please enter all fields");
                    newPW.setError("Please enter all fields");
                }

                if (!validPassword) {
                    newPW.setError("Create a password with 8+ characters, 1 digit, 1 uppercase letter, " +
                            "& 1 special character");
                } else if (newPWtxt.length() < 8) {
                    newPW.setError("Create a password with 8+ characters");
                } else if (validPassword && passTxt.equals(newPWtxt)){
                    Toast.makeText(ChangePassword.this, "New password must be different to current password" , Toast.LENGTH_SHORT).show();
                } else {
                    if (!result.verified){
                        Toast.makeText(ChangePassword.this, "Invalid Credentials" , Toast.LENGTH_SHORT).show();
                    } else {
                        boolean resultPW = dabaseHelper.updatePassword(emailTxt, newPWtxt);
                        if (resultPW){
                            Toast.makeText(ChangePassword.this, "Password change successful!" , Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangePassword.this, UserProfile.class);
                            intent.putExtra("userData", userProfile);
                            startActivity(intent);
                        } else {
                            Toast.makeText(ChangePassword.this, "ERROR: Did not update" , Toast.LENGTH_SHORT).show();
                        }
                    }
                }


            }
        });


    }
}