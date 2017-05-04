package com.example.aryanikhil2.desido;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.Date;

/**
 * Created by arya.nikhil2 on 14-03-2017.
 */

public class UploadPostDetailsActivity extends AppCompatActivity {
    EditText title, desc;
    ImageView image;
    Button upload,cancel;
    Spinner category;
    int uid;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_uploadpost);

        category = (Spinner)findViewById(R.id.spinner);
        title = (EditText)findViewById(R.id.editText15);
        desc = (EditText)findViewById(R.id.editText16);
        image = (ImageView)findViewById(R.id.imageView6);
        upload = (Button)findViewById(R.id.button9);
        cancel = (Button)findViewById(R.id.button8);
        progressDialog = new ProgressDialog(this);

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

        File imgFile = new File(getIntent().getStringExtra("location"));
        uid = getIntent().getIntExtra("userid", -1);

        if(imgFile.exists()){

            Log.e("image","image exists");
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            image.setImageBitmap(myBitmap);
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imgFile.delete();
                finish();
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer created = null;
                if(hasContent(title) && hasContent(desc)){
                    try {
                        progressDialog.setMessage("Uploading Image..");;
                        progressDialog.show();
                        created = new CreateNewPost().execute(category.getSelectedItem().toString(), title.getText().toString(), desc.getText().toString(), imgFile.getAbsolutePath().toString()).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if(created!=0) {
                        Toast.makeText(getApplicationContext(), "Posted Successfully!!", Toast.LENGTH_LONG).show();
                        finish();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Sorry!! Couldn't Post!! Please Try Again!!", Toast.LENGTH_LONG).show();
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

    class CreateNewPost extends AsyncTask<String,Void,Integer> {

        @Override
        protected void onPreExecute() {

        }

        public Integer doInBackground(String... Params){
            try {
                Class.forName("org.postgresql.Driver");
                Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");
                //Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");

                if(con==null){
                    Log.e("Connection status","error");
                }

                PreparedStatement pstmt = con.prepareStatement("INSERT INTO posts(uid,pic,title,info,category,timepost) VALUES(?,?,?,?,?,?)");

                pstmt.setInt(1,uid);
                File file = new File(Params[3]);
                FileInputStream fis = new FileInputStream(file);
                //org.apache.commons.io.FileUtils.copyInputStreamToFile(fis, file);
                pstmt.setBinaryStream(2,fis,file.length());
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
            progressDialog.dismiss();
        }
    }
}