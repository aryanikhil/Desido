package com.example.aryanikhil2.desido.FragmentsMain;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryanikhil2.desido.LogIn.LoginActivity;
import com.example.aryanikhil2.desido.MainActivity;
import com.example.aryanikhil2.desido.R;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.function.LongFunction;

import static com.example.aryanikhil2.desido.R.id.gender;


/**
 * Created by root on 15/3/17.
 */

public class ProfileActivity extends android.support.v4.app.Fragment implements View.OnClickListener {

    public ProfileActivity(){


    }
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MyPrefs = "NikPrefs";
    TextView name,uname,upload,gender1,dob,mobile,membership;
    int uid1;
    String uid,name1,gender,yearOfBirth,careOf,location,villageTehsil,postOffice,district,state,postCode;
    ImageView pic;
public String username;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profile =  inflater.inflate(R.layout.activity_profile, container, false);


        name = (TextView) profile.findViewById(R.id.name);
        uname   = (TextView)profile.findViewById(R.id.username);
        upload = (TextView)profile.findViewById(R.id.upload);
        pic = (ImageView) profile.findViewById(R.id.profilepic);
        gender1 =(TextView) profile.findViewById(R.id.gender);
        dob = (TextView) profile.findViewById(R.id.dob);
        mobile = (TextView) profile.findViewById(R.id.mobile);
        membership=(TextView) profile.findViewById(R.id.membership);

        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        uid1 = sharedpreferences.getInt("userid",-1);
        if(uid1 != -1) {
            // do for the profileT
            name.setText(sharedpreferences.getString("name", ""));
            uname.setText(sharedpreferences.getString("username", ""));
            username = sharedpreferences.getString("username", "");
            gender1.setText(sharedpreferences.getString("gender", ""));
            dob.setText(sharedpreferences.getString("dob", ""));

        }
        else{
            startActivity(new Intent(getActivity(),MainActivity.class));
            Toast.makeText(profile.getContext(),"Please login for Seeing your profile!!",Toast.LENGTH_LONG).show();
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode();
            }
        });


        return profile;
    }


    private void scanQRCode(){

        IntentIntegrator integrator = new IntentIntegrator(getActivity()){
            @Override
            protected void startActivityForResult(Intent intent, int code) {
                ProfileActivity.this.startActivityForResult(intent, code);
            }
        };
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan QR Code");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();

    }
    @Override
    public void onClick(View view) {





    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(getContext(), "You cancelled the scanning", Toast.LENGTH_LONG).show();
                try {
                    String text = getStringFromFile("/home/sahil/AndroidStudioProjects/Desido/app/src/main/res/values/aadhar.txt");
                    processScannedData(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                Log.e("Result",result.getContents());
                Toast.makeText(getContext(), result.getContents(), Toast.LENGTH_LONG).show();
                //processScannedData(result.getContents());
                try {
                    processScannedData(getStringFromFile(result.getContents()));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            onActivityResult(requestCode, resultCode, data);
        }
    }



    protected void processScannedData(String scanData){

        XmlPullParserFactory pullParserFactory;
        try {
            // init the parserfactory
            pullParserFactory = XmlPullParserFactory.newInstance();
            // get the parser
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(new StringReader(scanData));
            // parse the XML
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("Rajdeol","Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    // extract data from tag
                    //uid
                    uid = parser.getAttributeValue(0);
                    Log.e("uid",uid);
                    //name
                    name1 = parser.getAttributeValue(1);
                    Log.e("name",name1);
                    //gender
                    gender = parser.getAttributeValue(2);
                    Log.e("gender",gender);
                    // year of birth
                    yearOfBirth = parser.getAttributeValue(3);
                    Log.e("year of birth",yearOfBirth);
                    // care of
                    careOf = parser.getAttributeValue(4);
                    Log.e("careof",careOf);

                    location =parser.getAttributeName(5);
                    //location
                    Log.e("location",location);

                    // village Tehsil
                    villageTehsil = parser.getAttributeValue(6);
                    Log.e("village tehsil",villageTehsil);
                    // Post Office
                    postOffice = parser.getAttributeValue(7);
                    Log.e("postoffice",postOffice);
                    // district
                    district = parser.getAttributeValue(8);
                    Log.e("district",district);
                    // state
                    state = parser.getAttributeValue(9);
                    Log.e("state",state);
                    // Post Code
                    postCode = parser.getAttributeValue(10);
                    Log.e("postCode",postCode);
                } else if(eventType == XmlPullParser.END_TAG) {
                    Log.d("Rajdeol","End tag "+parser.getName());
                } else if(eventType == XmlPullParser.TEXT) {
                    Log.d("Rajdeol","Text "+parser.getText());}
                // update eventType
                eventType = parser.next();
            }


            // display the data on screen

            uploadId ui = new uploadId();
           ui.execute(uid,gender,yearOfBirth,location,username);
            //ui.execute(21321,"M",1997,"ghar","sahil ayank");

            Log.e("shared preferneces",uid);

            sharedPreferences = getActivity().getSharedPreferences(MyPrefs,Context.MODE_PRIVATE);
            editor  = sharedPreferences.edit();
            editor.putString("gender",gender);
            editor.putString("dob",yearOfBirth);
            editor.commit();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// EO function




    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

    public static String getStringFromFile (String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
}


class uploadId extends AsyncTask<String,Boolean,Boolean>{
  private ProgressDialog progressDialog;
    Context context;


    public Context getContext() {
        return context;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
       this.progressDialog.dismiss();
        super.onPostExecute(aBoolean);
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");
            if(con==null){
                Log.e("Connection status","error");
            }

            PreparedStatement pstmt = con.prepareStatement("INSERT INTO users(verify_id,gender,dob,paddr) VALUES(?,?,?,?) where username=?");


            pstmt.setInt(1, Integer.parseInt(strings[0]));
            pstmt.setString(2,strings[1]);
            pstmt.setString(3,strings[2]);
            pstmt.setString(4,strings[3]);
            pstmt.setString(5,strings[4]);
            pstmt.executeUpdate();
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onPreExecute() {

        this.progressDialog.setMessage("Uploading the ID...");
        this.progressDialog.show();
        super.onPreExecute();
    }



}
