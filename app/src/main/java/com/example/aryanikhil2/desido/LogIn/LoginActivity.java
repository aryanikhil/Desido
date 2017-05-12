package com.example.aryanikhil2.desido.LogIn;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.aryanikhil2.desido.R;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class LoginActivity extends AppCompatActivity{
    EditText name,pass;
    Button signin,signup;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    public static final String MyPrefs = "NikPrefs";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);

        name = (EditText) findViewById(R.id.editText2);
        pass = (EditText) findViewById(R.id.editText10);
        signin = (Button) findViewById(R.id.button3);
        signup = (Button) findViewById(R.id.button4);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        /*
        delete = (Button)findViewById(R.id.button5);
        */
        signup.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                signin.setVisibility(View.GONE);
                signup.setVisibility(View.GONE);
                FragmentSignup fs = new FragmentSignup();
                getSupportFragmentManager().beginTransaction().replace(R.id.login_fragment,fs).commit();
            }
        });

        signin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Login signin", Toast.LENGTH_SHORT).show();
                String uName = name.getText().toString();
                String uPass = pass.getText().toString();
                Integer uId = null;
                try {
                    uId = new Verify().execute(uName, uPass).get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (uId != 0) {
                    sharedPreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
                    editor = sharedPreferences.edit();
                    editor.putInt("userid", uId);
                    String name = null;
                    try {
                        name = new GetName().execute(uId).get();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    editor.putString("name", name);
                    editor.putString("username", uName);
                    editor.commit();
                    Toast.makeText(getApplicationContext(), "Sign In Successful!!!", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(getApplicationContext(), "Username or Password Mismatch. Please try again.", Toast.LENGTH_LONG).show();
                }
            }
        });

  /*     delete.setOnClickListener(new OnClickListener() {
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
*/

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class Verify extends AsyncTask<String,Void,Integer>{
        public Integer doInBackground(String... Params){
            try {
                Class.forName("org.postgresql.Driver");
               Connection con = DriverManager.getConnection("jdbc:postgresql://10.0.2.2:5432/desido","postgres","5438");
                //Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");

                if(con==null){
                    Log.e("Connection status","Error");
                }

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
                //Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");

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
                //Connection con = DriverManager.getConnection("jdbc:postgresql://172.16.40.26:5432/student?currentSchema=desido","student","student");

                PreparedStatement pstmt = con.prepareStatement("SELECT name FROM users WHERE uid=?");
                pstmt.setInt(1,Params[0]);
                ResultSet rs = pstmt.executeQuery();
                rs.next();
               /* Log.e("string",rs.getString(1)+ rs.getString(2)+rs.getString(3));
               sharedPreferences = getSharedPreferences(MyPrefs, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                editor.putString("gender",rs.getString(2));
                editor.putString("dob",rs.getString(3));*/
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
