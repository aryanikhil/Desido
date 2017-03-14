package com.example.aryanikhil2.desido;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aryanikhil2.desido.FragmentsMain.FragmentHome;
import com.example.aryanikhil2.desido.LogIn.LoginActivity;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
        TextView name;
        TextView uName;
        NavigationView navigationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentHome FH = new FragmentHome();
        getSupportFragmentManager().beginTransaction().replace(R.id.relView,FH).commit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        name = (TextView)header.findViewById(R.id.nav_name);
        uName = (TextView)header.findViewById(R.id.nav_username);
        SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        name.setText(sharedPreferences.getString("name","Welcome Guest"));
        uName.setText(sharedPreferences.getString("username",null));
    }

    public void onResume(){
        super.onResume();

        SharedPreferences sharedpreferences = getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
        name.setText(sharedpreferences.getString("name","Welcome Guest"));
        uName.setText(sharedpreferences.getString("username",null));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }else if(id == R.id.action_logout){
            SharedPreferences sharedPreferences = getSharedPreferences(LoginActivity.MyPrefs, Context.MODE_PRIVATE);
            int uId = sharedPreferences.getInt("userid",0);
            if(uId!=0) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
                name.setText(sharedPreferences.getString("name", "Welcome Guest"));
                uName.setText(sharedPreferences.getString("username", null));
                Toast.makeText(this, "Successfully Logged Out!!!", Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(this, "You Are Already Logged Out!!!", Toast.LENGTH_LONG).show();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            FragmentHome FH = new FragmentHome();
            getSupportFragmentManager().beginTransaction().replace(R.id.relView,FH).commit();
        } else if (id == R.id.nav_feeds) {

        } else if (id == R.id.nav_hireus) {

        } else if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_login){
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_help) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
