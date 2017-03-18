package com.example.aryanikhil2.desido.FragmentsMain;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryanikhil2.desido.LogIn.LoginActivity;
import com.example.aryanikhil2.desido.R;
import com.example.aryanikhil2.desido.UploadPostDetailsActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Nikhil on 02-07-2016.
 */
public class FragmentHome extends Fragment {
    ImageButton clickPic;
    Button uploadPic;
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    File photoFile = null;
    int uid;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View rootView =  inflater.inflate(R.layout.home_frag, container, false);

        clickPic = (ImageButton)rootView.findViewById(R.id.imageButton);
        uploadPic = (Button)rootView.findViewById(R.id.button2);
        uploadPic.setPaintFlags(uploadPic.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        clickPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedpreferences = getActivity().getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
                uid = sharedpreferences.getInt("userid",-1);
                if(uid != -1) {
                    dispatchTakePictureIntent();
                }
                else{
                    Toast.makeText(getContext(),"Please login before posting!!",Toast.LENGTH_LONG).show();
                }
            }
        });

        uploadPic.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                Toast.makeText(getContext(),"Working2",Toast.LENGTH_LONG).show();
            }
        });

        return rootView;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }

        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == RESULT_OK) {
            Intent uploadIntent = new Intent(getContext(), UploadPostDetailsActivity.class);
            uploadIntent.putExtra("location", mCurrentPhotoPath);
            uploadIntent.putExtra("userid", uid);
            startActivity(uploadIntent);
        }
        else if(photoFile.length() == 0) photoFile.delete();
    }
}
