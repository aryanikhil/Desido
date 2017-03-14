package com.example.aryanikhil2.desido.LogIn;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aryanikhil2.desido.R;

import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Created by Nikhil on 25-07-2016.
 */
public class FragmentSignup extends Fragment {
    EditText name,uName,pass,cPass,mobile,email,address;
    Button reg;

    public FragmentSignup(){

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.signup_frag, container, false);
        name = (EditText)view.findViewById(R.id.editText4);
        uName = (EditText)view.findViewById(R.id.editText5);
        pass = (EditText)view.findViewById(R.id.editText6);
        cPass = (EditText)view.findViewById(R.id.editText8);
        email = (EditText)view.findViewById(R.id.editText9);
        reg = (Button)view.findViewById(R.id.button);

        reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass1 = pass.getText().toString();
                String pass2 = cPass.getText().toString();

                if(pass1.equals(pass2)){
                    if(hasContent(name) && hasContent(uName) && hasContent(pass) && hasContent(cPass) && hasContent(email) ){
                        Boolean validated = null;
                        try{
                            validated = new Validate().execute(uName.getText().toString()).get();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        if(validated){
                            Integer created = null;
                            try{
                                created = new CreateNew().execute(name.getText().toString(),uName.getText().toString(),email.getText().toString(),pass.getText().toString()).get();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            if(created!=0) {
                                Toast.makeText(getActivity(), "Sign Up Successful!! Please Log In...", Toast.LENGTH_LONG).show();
                                getActivity().finish();
                            }
                            else{
                                Toast.makeText(getActivity(), "Sorry!!! Sign Up Unsuccessful!!", Toast.LENGTH_LONG).show();
                            }
                        }
                        else Toast.makeText(getActivity(),"Sorry username already taken. Please try again.",Toast.LENGTH_LONG).show();
                    }
                    else Toast.makeText(getActivity(),"Data Entered Incorrectly. Please check again.",Toast.LENGTH_LONG).show();
                }
                else Toast.makeText(getActivity(),"Passwords mismatch. Please check again.",Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }
    public boolean hasContent(EditText et){
        if(et.getText().toString().trim().length()>0) return true;
        else{
            et.setError("Fill This Field");
            return false;
        }
    }

    class Validate extends AsyncTask<String,Void,Boolean> {

        public Boolean doInBackground(String... params){
            try {
                Class.forName("org.postgresql.Driver");
                Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");

                PreparedStatement pstmt = con.prepareStatement("SELECT uid FROM users WHERE username=?");
                pstmt.setString(1,params[0]);
                ResultSet rs = pstmt.executeQuery();
                if(!rs.next()){
                    return true;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }


        public void onPostExecute(Boolean res){
        }
    }

    class CreateNew extends AsyncTask<String,Void,Integer>{

        public Integer doInBackground(String... Params){
            try {
                Class.forName("org.postgresql.Driver");
                Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");

                PreparedStatement pstmt = con.prepareStatement("INSERT INTO users(name,username,email,pass) VALUES(?,?,?,?)");
                pstmt.setString(1,Params[0]);
                pstmt.setString(2,Params[1]);
                pstmt.setString(3,Params[2]);
                pstmt.setString(4,Params[3]);
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
