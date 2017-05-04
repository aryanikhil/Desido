package com.example.aryanikhil2.desido.FragmentsMain;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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


import java.io.IOException;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.concurrent.ExecutionException;

/**
 * Created by root on 15/3/17.
 */

public class FragmentProfile extends android.support.v4.app.Fragment implements View.OnClickListener {

    public FragmentProfile() {


    }

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MyPrefs = "NikPrefs";
    TextView name, uname, upload, gender1, dob, mobile, membership;
    int uid1;
    String uid, name1, gender, yearOfBirth, careOf, location, villageTehsil, postOffice, district, state, postCode;
    ImageView pic;
    Button save, apply_semipro;
    public String username;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View profile = inflater.inflate(R.layout.activity_profile, container, false);


        name = (TextView) profile.findViewById(R.id.name);
        uname = (TextView) profile.findViewById(R.id.username);
        upload = (TextView) profile.findViewById(R.id.upload);
        pic = (ImageView) profile.findViewById(R.id.profilepic);
        gender1 = (TextView) profile.findViewById(R.id.gender);
        dob = (TextView) profile.findViewById(R.id.dob);
        mobile = (TextView) profile.findViewById(R.id.mobile);
        membership = (TextView) profile.findViewById(R.id.membership);
        save = (Button) profile.findViewById(R.id.save);
        apply_semipro = (Button) profile.findViewById(R.id.semipro);


        SharedPreferences sharedpreferences = getActivity().getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        uid1 = sharedpreferences.getInt("userid", -1);
        if (uid1 != -1) {
            // do for the profileT
            name.setText(sharedpreferences.getString("name", ""));
            uname.setText(sharedpreferences.getString("username", ""));
            username = sharedpreferences.getString("username", "");
            gender1.setText(sharedpreferences.getString("gender", ""));
            dob.setText(sharedpreferences.getString("dob", ""));

        } else {
            startActivity(new Intent(getActivity(), MainActivity.class));
            Toast.makeText(profile.getContext(), "Please login for Seeing your profile!!", Toast.LENGTH_LONG).show();
        }

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanQRCode();
            }
        });
        final Integer[] uuid = new Integer[1];

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                  uuid[0] = new Verify().execute(username).get();
                    Log.e("uuid",String.valueOf(uuid[0]));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                uploadId ui = new uploadId();

                ui.execute(sharedpreferences.getString("uid",""), sharedpreferences.getString("gender","").trim() ,
                        sharedpreferences.getString("location",""),sharedpreferences.getString("username",""));
                getActivity().finish();
                startActivity(new Intent(getActivity(),MainActivity.class));
            }
        });



        return profile;
    }


    private void scanQRCode() {

        IntentIntegrator integrator = new IntentIntegrator(getActivity()) {
            @Override
            protected void startActivityForResult(Intent intent, int code) {
                FragmentProfile.this.startActivityForResult(intent, code);
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
                startActivity(new Intent(getActivity(), MainActivity.class));
            } else {
                Log.e("Result", result.getContents());
                Toast.makeText(getContext(), result.getContents(), Toast.LENGTH_LONG).show();
                //processScannedData(result.getContents());
                try {
                    processScannedData(result.getContents());
                    FragmentProfile pf = new FragmentProfile();
                    getFragmentManager().beginTransaction().replace(R.id.profile_fragment,pf).commit();


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } else {
            onActivityResult(requestCode, resultCode, data);
        }
    }


    protected void processScannedData(String scanData) {

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
                if (eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("Rajdeol", "Start document");
                } else if (eventType == XmlPullParser.START_TAG) {
                    // extract data from tag
                    //uid
                    uid = parser.getAttributeValue(0);
                    Log.e("uid", uid);
                    //name
                    name1 = parser.getAttributeValue(1);
                    Log.e("name", name1);
                    //gender
                    gender = parser.getAttributeValue(2);
                    Log.e("gender", gender);
                    // year of birth
                    yearOfBirth = parser.getAttributeValue(3);
                    Log.e("year of birth", yearOfBirth);
                    // care of
                    careOf = parser.getAttributeValue(4);
                    Log.e("careof", careOf);
                    //location
                    location = parser.getAttributeValue(5);
                    Log.e("location", location);
                    // village Tehsil
                    villageTehsil = parser.getAttributeValue(6);
                    Log.e("village tehsil", villageTehsil);
                    // Post Office
                    postOffice = parser.getAttributeValue(7);
                    Log.e("postoffice", postOffice);
                    // district
                    district = parser.getAttributeValue(8);
                    Log.e("district", district);
                    // state
                    state = parser.getAttributeValue(9);
                    Log.e("state", state);
                    // Post Code
                    postCode = parser.getAttributeValue(10);
                    Log.e("postCode", postCode);
                } else if (eventType == XmlPullParser.END_TAG) {
                    Log.d("Rajdeol", "End tag " + parser.getName());
                } else if (eventType == XmlPullParser.TEXT) {
                    Log.d("Rajdeol", "Text " + parser.getText());
                }
                // update eventType
                eventType = parser.next();
            }


            // display the data on screen

            Log.e("shared preferneces", uid);

            sharedPreferences = getActivity().getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
            editor = sharedPreferences.edit();
            editor.putString("gender", gender);
            editor.putString("uid",uid);
            editor.putString("location",location);
            editor.putString("dob", yearOfBirth);
            editor.commit();



            apply_semipro.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }// EO function
}


class uploadId extends AsyncTask<String,Boolean,Boolean> {
    private ProgressDialog progressDialog;
    Context context;


    public Context getContext() {
        return context;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        //  this.progressDialog.dismiss();
        super.onPostExecute(aBoolean);
    }

    @Override
    protected Boolean doInBackground(String... strings) {

        try {
            Class.forName("org.postgresql.Driver");
            Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido", "student", "student");
            if (con == null) {
                Log.e("Connection status", "error");
            }

            PreparedStatement pstmt = con.prepareStatement("UPDATE users SET verify_id=?,gender=?,paddr=? WHERE username=?");
            pstmt.setString(1, strings[0]);
            pstmt.setString(2, strings[1]);
            pstmt.setString(3, strings[2]);
            pstmt.setString(4, strings[3]);
            if (pstmt.executeUpdate() != 0) {

                //Toast.makeText(getContext(),"Data updated Successfully on server..",Toast.LENGTH_LONG).show();
                Log.e("Data updation", "Data updation successful");

                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//        Toast.makeText(getContext(),"Data not updated on server..",Toast.LENGTH_LONG).show();
        Log.e("Data updation", "Data updation not successful");
        return false;
    }

    @Override
    protected void onPreExecute() {

        //this.progressDialog.setMessage("Uploading the ID...");
        //this.progressDialog.show();
        super.onPreExecute();
    }



}
 class Verify extends AsyncTask<String,Void,Integer>{
    public Integer doInBackground(String... Params){
        try {
            Class.forName("org.postgresql.Driver");
            // Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");
            Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");

            if(con==null){
                Log.e("Connection status","Error");
            }

            PreparedStatement pstmt = con.prepareStatement("SELECT uid FROM users WHERE username=?");
            pstmt.setString(1,Params[0]);
            ResultSet rs = pstmt.executeQuery();
            if(rs!=null){
                rs.next();
                return rs.getInt("uid");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
}