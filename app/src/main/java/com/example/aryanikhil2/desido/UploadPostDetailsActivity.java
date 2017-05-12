package com.example.aryanikhil2.desido;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.uploadcare.android.library.api.UploadcareClient;
import com.uploadcare.android.library.api.UploadcareFile;
import com.uploadcare.android.library.callbacks.UploadcareFileCallback;
import com.uploadcare.android.library.exceptions.UploadcareApiException;
import com.uploadcare.android.library.upload.FileUploader;
import com.uploadcare.android.library.upload.Uploader;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by arya.nikhil2 on 14-03-2017.
 */

public class UploadPostDetailsActivity extends AppCompatActivity {
    EditText title, desc;
    ImageView image;
    Button upload,cancel;
    Spinner category;
    int uid;
    View uploadLayout;
    TextView statusTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);

        uploadLayout = findViewById(R.id.uploadLayout);
        category = (Spinner)findViewById(R.id.spinner);
        title = (EditText)findViewById(R.id.editText15);
        desc = (EditText)findViewById(R.id.editText16);
        image = (ImageView)findViewById(R.id.imageView6);
        upload = (Button)findViewById(R.id.button9);
        cancel = (Button)findViewById(R.id.button8);
        statusTextView = (TextView) findViewById(R.id.status);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categoryOfPost, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String item = parent.getItemAtPosition(pos).toString();
                Toast.makeText(parent.getContext(), "Selected: " + item, Toast.LENGTH_LONG).show();
            }

            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        Uri fileUri = null;
        String path = getIntent().getStringExtra("path");
        uid = getIntent().getIntExtra("userid", -1);
        UploadcareClient client = new UploadcareClient(MainActivity.PUBLICKEY, MainActivity.PRIVATEKEY);

        if(!path.equals(null)) {
            Bitmap myBitmap = BitmapFactory.decodeFile(path);
            image.setImageBitmap(myBitmap);
            fileUri = Uri.fromFile(new File(path));
        }else{
            Log.e("File Path", "Path Error -> NULL");
        }
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getIntent().getExtras().get("location").toString().equals("camera")) {
                    File img = new File(path);
                    img.delete();
                }
                finish();
            }
        });

        Uri finalFileUri = fileUri;
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final Integer[] created = {null};
                if(hasContent(title) && hasContent(desc)){
                    try {
                        if(!Uri.EMPTY.equals(finalFileUri)){
                            //Log.e("Image URL","URL Exists and is " + finalFileUri.toString());
                            showProgressOrResult(true, "Uploading...");
                            Uploader uploader = new FileUploader(client, finalFileUri, getBaseContext()).store(true);
                            uploader.uploadAsync(new UploadcareFileCallback() {
                                @Override
                                public void onFailure(UploadcareApiException e) {
                                    showProgressOrResult(false,
                                            e.getLocalizedMessage());
                                }
                                @Override
                                public void onSuccess(UploadcareFile file) {
                                    Log.e("File Id", file.getFileId().toString());
                                    try {
                                        created[0] = new CreateNewPost().execute(category.getSelectedItem().toString(), title.getText().toString(), desc.getText().toString(), file.getFileId().toString()).get();
                                        showProgressOrResult(false, "");
                                        if(created[0] != 0) {
                                            Toast.makeText(getApplicationContext(), "Posted Successfully!!", Toast.LENGTH_LONG).show();
                                            finish();
                                        }
                                        else{
                                            Toast.makeText(getApplicationContext(), "Sorry!! Couldn't Post!! Please Try Again!!", Toast.LENGTH_LONG).show();
                                        }
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    } catch (ExecutionException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public boolean hasContent(EditText et){
        if(et.getText().toString().trim().length()>0) return true;
        else{
            et.setError("Fill This Field");
            return false;
        }
    }

    private void showProgressOrResult(boolean progress, String message) {
        if (progress) {
            uploadLayout.setVisibility(View.GONE);
            statusTextView.setVisibility(View.VISIBLE);
            statusTextView.setText(message);
        } else {
            uploadLayout.setVisibility(View.VISIBLE);
            statusTextView.setVisibility(View.GONE);
        }
    }
    class CreateNewPost extends AsyncTask<String,Void,Integer> {

        @Override
        protected void onPreExecute() {

        }

        public Integer doInBackground(String... Params){
            try {
                Class.forName("org.postgresql.Driver");
                //Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");
                Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");
                if(con==null){
                    Log.e("Connection status","error");
                }

                PreparedStatement pstmt = con.prepareStatement("INSERT INTO posts(uid,pic,title,info,category,timepost) VALUES(?,?,?,?,?,?)");

                pstmt.setInt(1,uid);
                pstmt.setString(2,Params[3]);
                pstmt.setString(3,Params[1]);
                pstmt.setString(4,Params[2]);
                pstmt.setString(5,Params[0]);
                pstmt.setTimestamp(6,new Timestamp(new Date().getTime()));
                return pstmt.executeUpdate();
            }catch (Exception e){
                e.printStackTrace();
            }
            return 0;
        }

        public void onPostExecute(Integer res){

        }
    }
}