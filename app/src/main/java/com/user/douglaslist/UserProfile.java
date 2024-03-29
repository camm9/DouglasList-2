package com.user.douglaslist;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.douglaslist.DabaseHelper;
import com.example.douglaslist.MainActivity;
import com.example.douglaslist.R;

public class UserProfile extends AppCompatActivity {
    SharedPreferences sp;
    DabaseHelper dabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        User userProfile = (User) getIntent().getSerializableExtra("userData");

        dabaseHelper = new DabaseHelper(this);

        //Check user session is valid
        SharedPreferences sp= getSharedPreferences("DouglasListUserSession", MODE_PRIVATE);
        Long storedTime = sp.getLong("session_expiration",0);
        Boolean session = UserLogin.checkSessionToken(storedTime);
        if (!session){
            UserLogin.removeSessionToken(sp);
            startActivity(new Intent(UserProfile.this, MainActivity.class));
        }


        TextView name = findViewById(R.id.tvProfileName);
        String fullName = userProfile.firstName + " " + userProfile.lastName;
        name.setText(fullName);

        TextView address = findViewById(R.id.tvProfileAddress);
        address.setText("City: " +userProfile.getAddress());

        TextView username = findViewById(R.id.tvProfileUsername);
        username.setText("Username: "+ userProfile.getUsername());

        //get profile image from Profile table
        ImageView profileImg = findViewById(R.id.ivProfileImg);
        String storedImgEncoded = dabaseHelper.returnProfileImage(userProfile.getEmail());
        byte[] decodeString = Base64.decode(storedImgEncoded,Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        profileImg.setImageBitmap(decodedBitmap);

        Button btnLogOut = findViewById(R.id.btnProfileLogout);
        btnLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserLogin.removeSessionToken(sp);
                Toast.makeText(UserProfile.this, "Signed Out Successfully!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(UserProfile.this, MainActivity.class));
            }
        });

        Button btnChangePW = findViewById(R.id.btnChangePW);
        btnChangePW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, ChangePassword.class);
                intent.putExtra("userData", userProfile);
                startActivity(intent);
            }
        });

        Button btnEditProfile = findViewById(R.id.btnEditProfile);
        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UserProfile.this, EditProfile.class);
                intent.putExtra("userData", userProfile);
                startActivity(intent);
            }
        });

    }
}