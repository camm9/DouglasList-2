package com.user.douglaslist;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.douglaslist.R;

public class EditProfile extends AppCompatActivity {
    ImageView ivImage;
    Button btnSave;
    Button btnUpload;
    ActivityResultLauncher<Intent> resultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        User userProfile = (User) getIntent().getSerializableExtra("userData");

        btnUpload = findViewById(R.id.btnUploadPhoto);
        btnSave = findViewById(R.id.btnSavePhoto);

        ivImage = findViewById(R.id.ivImageUpload);

        registerResult();
        btnUpload.setOnClickListener(view -> pickImage());
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
                            Toast.makeText(EditProfile.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

    }

}