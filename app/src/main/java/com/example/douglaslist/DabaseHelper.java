package com.example.douglaslist;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.user.douglaslist.User;

import java.io.ByteArrayOutputStream;

import at.favre.lib.crypto.bcrypt.BCrypt;


public class DabaseHelper extends SQLiteOpenHelper {

    final static String DATABASE_NAME = "Douglas.db";
    final static int DATABASE_VERSION = 3;

    final static String USER_TABLE = "User";
    final static String T1COL1 = "UserID"; //Primary Key
    final static String T1COL2 = "Email"; //Must be unique
    final static String T1COL3 = "FirstName";
    final static String T1COL4 = "LastName";
    final static String T1COL5 = "Username"; //Must be unique
    final static String T1COL6 = "CellPhone";

    final static String T1COL7 = "Address";

    final static String T1COL8 = "Password"; //Foreign Key

    final static String PROFILE_TABLE = "Profile";
    final static String PROFILE_COL1 = "Email";
    final static String PROFILE_COL2 = "ProfileImage";

    public DabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUserTable = "CREATE TABLE " + USER_TABLE +
                "(" + T1COL1 + " INTEGER PRIMARY KEY AUTOINCREMENT," + //UserID
                T1COL2 + " TEXT UNIQUE,"+ //Email
                T1COL3 + " TEXT,"+ //FirstName
                T1COL4 + " TEXT,"+ //LastName
                T1COL5 + " TEXT UNIQUE," + //Username
                T1COL6 + " TEXT," + //CellPhone
                T1COL7 + " TEXT,"+ //Address
                T1COL8 + " TEXT)"; //Password
        db.execSQL(createUserTable);

        String createProfileTable = "CREATE TABLE " + PROFILE_TABLE +
                "(" + PROFILE_COL1 + " TEXT PRIMARY KEY," + //Email
                PROFILE_COL2 + " TEXT)"; //ProfileImage
        db.execSQL(createProfileTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PROFILE_TABLE);
        onCreate(db);
    }

    //add new user to user table via the registration page
    public boolean addUser(User user){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(T1COL2, user.getEmail());
        values.put(T1COL3, user.getFirstName());
        values.put(T1COL4, user.getLastName());
        values.put(T1COL5, user.getUsername());
        values.put(T1COL6, user.getPhoneNumber());
        values.put(T1COL7, user.getAddress());
        values.put(T1COL8, user.getPassword());

        Log.d(DATABASE_NAME, "addUser: Adding " + user + "to " + USER_TABLE);
        long result = db.insert(USER_TABLE, null, values);
        if (result == -1) return false;
        else return true;
    }

    //check to see if there is already a user with that email in the registration page
    public boolean checkEmail(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE email = ?",
                new String[]{email});
        if (cursor.getCount() > 0)
            return true;
        else return false;
    }
    //check to see if there is already a user with that username in the registration page
    public boolean checkUsername(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + USER_TABLE + " WHERE username = ?",
                new String[]{username});
        if (cursor.getCount() > 0)
            return true;
        else return false;
    }

    //Return username for a given email
    public String returnUsername(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] storedUsername = {"Username"};
        String selection = "Email = ?";
        String[] selectionArgs = { String.valueOf(email)};
        Cursor cursor = db.query(USER_TABLE, storedUsername, selection,selectionArgs,null,null,null);
        if (cursor.moveToFirst()){
            String retrievedUsername= cursor.getString(cursor.getColumnIndexOrThrow("Username"));
            cursor.close();
            return retrievedUsername;
        } else{
            cursor.close();
            return null;
        }
    }
    //Update password -- to be used for ForgotPassword feature
    public boolean updatePassword(String email, String newPassword){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        //encrypt new generated password
        String bcryptPassword = BCrypt.withDefaults().hashToString(12,newPassword.toCharArray());
        values.put("Password", bcryptPassword);

        String selection = "Email = ?";
        String[] selectionArgs = { String.valueOf(email)};

        int rowsAffected = db.update(USER_TABLE, values,selection,selectionArgs);
        db.close();
        boolean updateResult = rowsAffected > 0;
        return updateResult;

    }


    //For login page, retrieve the stored password and use to compare with entered password
    public String checkHashedPassword(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] storedPassword = {"Password"};
        String selection = "Email = ?";
        String[] selectionArgs = { String.valueOf(email)};
        Cursor cursor = db.query(USER_TABLE, storedPassword, selection,selectionArgs,null,null,null);
        if (cursor.moveToFirst()){
            String retrievedPassword = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
            cursor.close();
            return retrievedPassword;
        } else{
            cursor.close();
            return null;
        }

    }

    //return User profile of logged in User
    public User returnUserProfile(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        User activeUser;

        String[] projection = null;
        String selection = "Email = ?";
        String[] selectionArgs = { String.valueOf(email) };
        Cursor cursor = db.query(USER_TABLE, projection, selection, selectionArgs,null,null,null);
        int id = 0;
        String firstName = "default";
        String lastName = "default";
        String username ="default";
        String cellPhone = "default";
        String address = "default";
        String password = "default";

        if (cursor.moveToFirst()){
            //retrieve all columns for row
            for (int i = 0; i < cursor.getColumnCount(); i++){
                String columnName = cursor.getColumnName(i);
                String columnValue = cursor.getString(i);

                //UserID Email FirstName LastName Username CellPhone Address Password

                if (columnName.equals("UserID")){
                    id = Integer.parseInt(columnValue);
                } else if (columnName.equals("FirstName")){
                    firstName = columnValue;
                } else if(columnName.equals("LastName")){
                    lastName = columnValue;
                } else if(columnName.equals("Username")){
                    username = columnValue;
                } else if (columnName.equals("CellPhone")){
                    cellPhone = columnValue;
                } else if (columnName.equals("Address")){
                    address = columnValue;
                } else if (columnName.equals("Password")){
                    password = columnValue;
                }
            }

        }
        cursor.close();
        //public User(int id, String Username, String firstName, String lastName, String address, String phoneNumber, String email, String password)
        activeUser = new User(id,username,firstName,lastName,address,cellPhone,email,password);

        return activeUser;
    }

    //update image to Profile table where user email is
    public boolean updateImage(Bitmap imageBitmap, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_COL2, bitmapToBase64(imageBitmap));

        String selection = "Email = ?";
        String[] selectionArgs = { String.valueOf(email)};

        int rowsAffected = db.update(PROFILE_TABLE, contentValues,selection,selectionArgs);
        db.close();
        boolean updateResult = rowsAffected > 0;
        return updateResult;
    }
    //inserts an image into Profile table
    public boolean insertImage(Bitmap imageBitmap, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PROFILE_COL1, email);
        contentValues.put(PROFILE_COL2, bitmapToBase64(imageBitmap));

        long result = db.insert(PROFILE_TABLE, null, contentValues);
        if (result == -1) return false;
        else return true;
    }

    //Helps import bitmap images to database
    private String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return Base64.encodeToString(byteArray, Base64.DEFAULT);
    }

    public String returnProfileImage(String email){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] storedImg = {"ProfileImage"};
        String selection = "Email = ?";
        String[] selectionArgs = { String.valueOf(email)};
        Cursor cursor = db.query(PROFILE_TABLE, storedImg, selection,selectionArgs,null,null,null);
        if (cursor.moveToFirst()){
            String retrievedImg = cursor.getString(cursor.getColumnIndexOrThrow("ProfileImage"));
            cursor.close();
            return retrievedImg;
        } else{
            cursor.close();
            return null;
        }

    }

}
