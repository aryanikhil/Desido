package com.example.aryanikhil2.desido.LogIn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aryanikhil2.desido.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginActivity extends AppCompatActivity {
    EditText name,pass;
    Button logIn,signUp,delete;
    Intent intent;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MyPrefs = "NikPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        name = (EditText)findViewById(R.id.editText);
        pass = (EditText)findViewById(R.id.editText2);
        logIn = (Button)findViewById(R.id.button3);
        signUp = (Button)findViewById(R.id.button4);
        delete = (Button)findViewById(R.id.button5);

        signUp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentSignup FS = new FragmentSignup();
                getSupportFragmentManager().beginTransaction().replace(R.id.rel_login,FS).commit();
            }
        });

        logIn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = name.getText().toString();
                String uPass = pass.getText().toString();
                Integer uId = null;
                try{
                    uId = new Verify().execute(uName,uPass).get();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(uId!=0){
                    sharedPreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putInt("userid",uId);
                    String name = null;
                    try{
                        name = new GetName().execute(uId).get();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    editor.putString("name",name);
                    editor.putString("username",uName);
                    editor.commit();
                    Toast.makeText(getApplicationContext(),"Sing In Successful!!!", Toast.LENGTH_LONG).show();
                    finish();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Username or Password Mismatch. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

        delete.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String uName = name.getText().toString();
                String uPass = pass.getText().toString();
                Integer uId = null;
                try{
                    uId = new Verify().execute(uName,uPass).get();
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(uId!=0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);

                    final Integer finalUId = uId;
                    builder.setMessage("Are You Sure You Want To Delete Your Account?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog, int id){
                                Integer c = null;
                                try{
                                    c = new Delete().execute(finalUId).get();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                if(c>0) Toast.makeText(getApplicationContext(),"Account Deleted Successfully!!", Toast.LENGTH_LONG).show();
                                else Toast.makeText(getApplicationContext(),"Sorry Unable To Delete Account!!", Toast.LENGTH_LONG).show();
                            }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.setTitle("Delete Account??");
                    alert.show();

                }
                else{
                    Toast.makeText(getApplicationContext(),"Username or Password Mismatch. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    class Verify extends AsyncTask<String,Void,Integer>{
        public Integer doInBackground(String... Params){
            try {
                Class.forName("org.postgresql.Driver");
                Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");

                PreparedStatement pstmt = con.prepareStatement("SELECT uid FROM users WHERE username=? AND pass=?");
                pstmt.setString(1,Params[0]);
                pstmt.setString(2,Params[1]);
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

    class Delete extends AsyncTask<Integer,Void,Integer>{
        public Integer doInBackground(Integer... Params){
            try {
                Class.forName("org.postgresql.Driver");
                Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");

                PreparedStatement pstmt = con.prepareStatement("DELETE FROM users WHERE uid=?");
                pstmt.setInt(1,Params[0]);
                int count = pstmt.executeUpdate();
                return count;
            }catch (Exception e){
                e.printStackTrace();
            }
            return 0;
        }

        public void onPostExecute(Integer res){
        }
    }

    class GetName extends AsyncTask<Integer,Void,String>{
        public String doInBackground(Integer... Params){
            try {
                Class.forName("org.postgresql.Driver");
                Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");

                PreparedStatement pstmt = con.prepareStatement("SELECT name FROM users WHERE uid=?");
                pstmt.setInt(1,Params[0]);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
                return rs.getString("name");
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        public void onPostExecute(String res){
        }
    }
}
