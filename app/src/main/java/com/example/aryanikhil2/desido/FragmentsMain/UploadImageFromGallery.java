package com.example.aryanikhil2.desido.FragmentsMain;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.aryanikhil2.desido.LogIn.LoginActivity;
import com.example.aryanikhil2.desido.UploadPostDetailsActivity;

import static android.app.Activity.RESULT_OK;

/**
 * Created by root on 20/3/17.
 */

public class UploadImageFromGallery extends Fragment {


    private static int RESULT_LOAD_IMG = 1;
    String imgDecodableString;
    int uid;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // Create intent to Open Image applications like Gallery, Google Photos
        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        uid = sharedpreferences.getInt("userid",-1);
        if(uid != -1) {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            // Start the Intent
            startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
        }
        else{
            Toast.makeText(getContext(),"Please login before posting!!",Toast.LENGTH_LONG).show();
        }



        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            // When an Image is picked
            if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
                    && null != data) {
                // Get the Image from data

                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };

                // Get the cursor
                Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                // Move to first row
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                imgDecodableString = cursor.getString(columnIndex);
                cursor.close();

                Intent uploadIntent = new Intent(getContext(), UploadPostDetailsActivity.class);
                uploadIntent.putExtra("location", imgDecodableString);
                uploadIntent.putExtra("userid", uid);
                startActivity(uploadIntent);

            } else {
                Toast.makeText(getActivity(), "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

}

