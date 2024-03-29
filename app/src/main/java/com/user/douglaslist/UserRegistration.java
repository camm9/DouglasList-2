package com.user.douglaslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.douglaslist.DabaseHelper;
import com.example.douglaslist.MainActivity;
import com.example.douglaslist.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class UserRegistration extends AppCompatActivity {
    DabaseHelper dabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_registration);
        dabaseHelper = new DabaseHelper(this);
        Button signUpUser = findViewById(R.id.btnSignUp);


        signUpUser.setOnClickListener(new View.OnClickListener() {
            boolean isInserted;
            @Override
            public void onClick(View v) {
                EditText username = findViewById(R.id.edTxtSignUpUsername);
                EditText password = findViewById(R.id.edTxtSignUpPassword);
                EditText fName = findViewById(R.id.edTxtSignUpFirstName);
                EditText lName = findViewById(R.id.edTxtSignUpLastName);
                EditText address = findViewById(R.id.edTxtSignUpAddress);
                EditText email = findViewById(R.id.edTxtSignUpEmail);

                EditText phone = findViewById(R.id.edTxtSignUpPhoneNumber);
                phone.addTextChangedListener(new PhoneNumberFormattingTextWatcher("CA"));


                //Sanitize input formats

                boolean validPassword = isValidPassword(password.getText().toString());
                boolean validEmail = isValidEmail(email.getText().toString());

                if (username.getText().toString().equals("")) {
                    username.setError("Enter a unique username");

                }else if (dabaseHelper.checkUsername(username.getText().toString())){
                    username.setError("This username is in use already. Pick a different username ");
                }
                else if (password.getText().toString().equals("")) {
                    password.setError("Create a password");
                } else if (!validPassword) {
                    password.setError("Create a password with 8+ characters, 1 digit, 1 uppercase letter, " +
                            "& 1 special character");
                } else if (password.getText().toString().length() < 8) {
                    password.setError("Create a password with 8+ characters");
                } else if (fName.getText().toString().equals("")) {
                    fName.setError("Enter your first name");
                } else if (lName.getText().toString().equals("")) {
                    lName.setError("Enter your last name");
                } else if (address.getText().toString().equals("")) {
                    address.setError("Enter your city");
                } else if (phone.length() > 10 | phone.length() < 10){
                    phone.setError("Enter a local 10-digit number");
                } else if (email.getText().toString().equals("")){
                    email.setError("Enter your email");
                } else if (!validEmail){
                    email.setError("Enter correct email format");
                } else if (dabaseHelper.checkEmail(email.getText().toString())){
                    email.setError("This email is in use already. Pick a new email ");
                }
                else {
                    //encrypt password with Bcrypt
                    String passTxt = password.getText().toString();
                    String bcryptPassword = BCrypt.withDefaults().hashToString(12,passTxt.toCharArray());

                    isInserted = dabaseHelper.addUser(new User
                            (username.getText().toString(),
                                    fName.getText().toString(),
                                    lName.getText().toString(),
                                    address.getText().toString(),
                                    phone.getText().toString(),
                                    email.getText().toString(),
                                    bcryptPassword)
                    );

                    if(isInserted) {
                        Toast.makeText(UserRegistration.this, "User added", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UserRegistration.this, MainActivity.class));
                    }else {
                        Toast.makeText(UserRegistration.this, "User not added", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });



    }


    /*
    the password contains at least one digit, one lowercase letter, one uppercase
    letter, one special character, and is at least 8 characters long.
    */
    public static boolean isValidPassword (String password){
        final String passwordPattern = ("^" +
                "(?=.*[A-Z])"+           // at least 1 uppercase
                "(?=.*[a-z])"+           // at least 1 lowercase
                "(?=.*[!@#$%^&+=])" +     // at least 1 special character
                "(?=\\S+$)" +            // no white spaces
                ".{8,}" +                // at least 8 characters
                "$");
        Pattern pattern = Pattern.compile(passwordPattern);
        Matcher matcher = pattern.matcher(password);
        boolean result = matcher.matches();
        return result;
    }

    public static boolean isValidEmail(String email){
        final boolean emailPattern = Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return emailPattern;
    }
}