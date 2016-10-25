package com.sakthisugars.salesandmarketing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by Administrator on 7/25/2016.
 */
public class Payment_Type extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
Button cod,cards;
    TextView tv,uname;
    DrawerLayout drawer;
    Database_handler database_handler;
    SQLiteDatabase db;
    String userid;
    UserSessionManager session;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_type_drawer);
       // ActionBar actionBar = getActionBar();
       // actionBar.setDisplayHomeAsUpEnabled(true);
        session = new UserSessionManager(getApplicationContext());
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uname=(TextView)findViewById(R.id.uname);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(session.checkLogin())
            finish();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get name
        userid = user.get(UserSessionManager.KEY_NAME);
        uname.setText(String.valueOf(userid));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLUE));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLACK));
        navigationView.setBackgroundColor(Color.WHITE);
        navigationView.setNavigationItemSelectedListener(this);
        //...................................................................
        Intent intentval = getIntent();
        userid = intentval.getStringExtra("userid");
        cod = (Button) findViewById(R.id.cod);
        tv=(TextView)findViewById(R.id.txt);
        cards = (Button) findViewById(R.id.cards);
        cod.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(Payment_Type.this, Payment_Approval.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
        cards.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent1 = new Intent(Payment_Type.this, Payment_Type.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
        uname.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // dialog.show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder
                        .setCancelable(false)
                        .setMessage("Are you sure you want to Logout")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        session.logoutUser();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
    }
    boolean flag = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(flag){
                super.onBackPressed();
            }else{
                Toast.makeText(this, "Press Back Again to Exit", Toast.LENGTH_LONG).show();
                flag=true;
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.Admin: {
                if (userid.contains("develop")) {
                    Intent intent2 = new Intent(Payment_Type.this, Homepage.class);
                    intent2.putExtra("userid", userid);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.customer: {
                Intent intent2 = new Intent(Payment_Type.this, CustomerPage.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            }
            case R.id.home: {
                Intent intent2 = new Intent(Payment_Type.this, Home.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            }
            case R.id.marker: {
                Intent intent2 = new Intent(Payment_Type.this, Location_Find.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;

            }
            case R.id.logout:
              /*  Intent intent = new Intent(Payment_Type.this, Login.class);
                startActivity(intent);
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                session.logoutUser();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
  //  public boolean onOptionsItemSelected(MenuItem item){
    //    finish();
     //   return true;
 //   }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.back:
                this.finish();
                System.exit(0);
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/

}

