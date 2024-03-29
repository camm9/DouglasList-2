package com.user.douglaslist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.douglaslist.DabaseHelper;
import com.example.douglaslist.R;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

public class EditProfile extends AppCompatActivity {
    ImageView ivImage;
    Button btnSave;
    Button btnUpload;
    ActivityResultLauncher<Intent> resultLauncher;

    DabaseHelper dabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        User userProfile = (User) getIntent().getSerializableExtra("userData");
        String email = userProfile.getEmail();

        dabaseHelper = new DabaseHelper(EditProfile.this);

        //get profile image from Profile table
        ivImage = findViewById(R.id.ivImageUpload);
        String storedImgEncoded = dabaseHelper.returnProfileImage(email);
        byte[] decodeString = Base64.decode(storedImgEncoded,Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodeString, 0, decodeString.length);
        ivImage.setImageBitmap(decodedBitmap);

        btnUpload = findViewById(R.id.btnUploadPhoto);
        btnSave = findViewById(R.id.btnSavePhoto);

        //Retrieve image from phone
        registerResult();
        btnUpload.setOnClickListener(view -> pickImage());

        //Save image to Profile table in database
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable)  ivImage.getDrawable();
                Bitmap bitmap = bitmapDrawable.getBitmap();


                boolean imgSize = checkImgSize(bitmap);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                String imgString = Base64.encodeToString(byteArray, Base64.DEFAULT);
                byte[] imgBytes = imgString.getBytes(StandardCharsets.UTF_8);
                long byteSize = imgBytes.length;

                //imgSizeTxt.setText("Image size is: "+ byteSize);

                if (imgSize){
                    boolean imgSaved = dabaseHelper.updateImage(bitmap, email);
                    if (imgSaved) {
                        Toast.makeText(EditProfile.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditProfile.this, UserProfile.class);
                        intent.putExtra("userData", userProfile);
                        startActivity(intent);
                    } else {
                        Toast.makeText(EditProfile.this, "ERROR: Image Not Saved!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(EditProfile.this, "ERROR: Image is too big!", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void pickImage(){
        Intent intent = new Intent(MediaStore.ACTION_PICK_IMAGES);
        resultLauncher.launch(intent);
    }

    private void registerResult() {
        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();
                            ivImage.setImageURI(imageUri);
                        }catch (Exception e){
                            Toast.makeText(EditProfile.this, "ERROR: Image could not be retrieved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

    private boolean checkImgSize(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        String imgString = Base64.encodeToString(byteArray, Base64.DEFAULT);
        byte[] imgBytes = imgString.getBytes(StandardCharsets.UTF_8);
        long byteSize = imgBytes.length;
        long maxSizeAllowed = 1_300_000;
        boolean result = byteSize < maxSizeAllowed;
        return result;
    }

}