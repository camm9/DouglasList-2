package com.user.douglaslist;

import static com.user.douglaslist.UserRegistration.isValidEmail;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglaslist.DabaseHelper;
import com.example.douglaslist.MainActivity;
import com.example.douglaslist.R;

import java.security.SecureRandom;

public class ForgotPassword extends AppCompatActivity {
    SharedPreferences sp;
    DabaseHelper dabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        dabaseHelper = new DabaseHelper(this);

        EditText email = findViewById(R.id.editTxtEmailForgot);
        EditText username = findViewById(R.id.editTxtUsernameForgot);
        Button btnGeneratePassword = findViewById(R.id.btnGeneratePassword);
        TextView newPassword = findViewById(R.id.tvNewPassword);

        btnGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailTxt = email.getText().toString();
                String usernameTxt = username.getText().toString();
                boolean validEmail = dabaseHelper.checkEmail(emailTxt);
                boolean validUsername = dabaseHelper.checkUsername(usernameTxt);

                if(!validEmail || !validUsername){
                    Toast.makeText(ForgotPassword.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                } else if (validEmail || validUsername){
                    String retrievedUsername = dabaseHelper.returnUsername(emailTxt);
                    if (retrievedUsername.equals(usernameTxt)){
                        String generatedPassword = generatePassword();
                        newPassword.setText("Your new password is:\n\n"+ generatedPassword + "\n\n Please login and change password");
                        boolean updateResult = dabaseHelper.updatePassword(emailTxt, generatedPassword);

                        if (updateResult){
                            Toast.makeText(ForgotPassword.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ForgotPassword.this, "ERROR: Password not updated", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }

    public String generatePassword(){
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        int length = 8;

        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String nums = "0123456789";
        String specialChar = "!@#$%^&+=";

        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(specialChar.charAt(random.nextInt(specialChar.length())));
        password.append(nums.charAt(random.nextInt(nums.length())));
        password.append(uppercase.charAt(random.nextInt(lowercase.length())));

        for (int i = 4; i<length; i++){
            String allChars = lowercase + nums + specialChar + uppercase;
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }


        return password.toString();
    }
}