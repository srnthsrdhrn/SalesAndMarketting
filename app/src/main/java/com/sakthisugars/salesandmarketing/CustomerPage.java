package com.sakthisugars.salesandmarketing;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.HashMap;

public class CustomerPage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button overdue, new_purchase;
    DrawerLayout drawer;
    Database_handler database_handler;
    SQLiteDatabase db;
    String userid;
    TextView uname;
    UserSessionManager session;
    final Context context = this;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_drawer_page);
        session = new UserSessionManager(getApplicationContext());
        // ActionBar actionBar = getActionBar();
      //getSupportActionBar().setDisplayShowHomeEnabled(true);
      //  getSupportActionBar().setDisplayShowHomeEnabled(true);
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uname=(TextView)findViewById(R.id.uname);
       // uname.setText(String.valueOf(userid));
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLUE));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLACK));
        navigationView.setBackgroundColor(Color.WHITE);
        navigationView.setNavigationItemSelectedListener(this);

        // Menu nav_Menu = navigationView.getMenu();
        //  nav_Menu.findItem(R.id.customer).setVisible(false);
        // nav_Menu.findItem(R.id.master).setVisible(false);
        // ...............................................................................
        //  overdue= (Button) findViewById(R.id.overdue);
        new_purchase = (Button) findViewById(R.id.new_purchase);
        if(session.checkLogin())
            finish();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get name
        userid = user.get(UserSessionManager.KEY_NAME);
        uname.setText(String.valueOf(userid));
        new_purchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  startActivity(new Intent(CustomerPage.this,SchemeSelection.class));
                finish();
                Intent intent = new Intent(CustomerPage.this, SchemeSelection.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
            }
        });
        // overdue.setOnClickListener(new View.OnClickListener() {
        //  @Override
        //  public void onClick(View v) {
        //     startActivity(new Intent(CustomerPage.this,OverduePage.class));
        //   }
        // });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
        super.onBackPressed();
       // Intent intent = new Intent(Intent.ACTION_MAIN);
       // intent.addCategory(Intent.CATEGORY_HOME);
       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
       // startActivity(intent);
      //  finish();
      //  System.exit(0);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(flag){
                super.onBackPressed();
            }else{
                Toast.makeText(this,"Press Back Again to Exit",Toast.LENGTH_LONG).show();
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
           /* case R.id.master: {
                if(userid.contains("develop")) {
                drawer.closeDrawer(GravityCompat.START);
                String[] list = new String[]{"Item", "Scheme_Master", "Employee Details"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose")
                        .setSingleChoiceItems(list,3, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        dialog.dismiss();
                                        //startActivity(new Intent(Homepage.this, Item.class));
                                        Intent intent = new Intent(CustomerPage.this, Item.class);
                                        intent.putExtra("userid", userid);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        dialog.dismiss();
                                        // startActivity(new Intent(Homepage.this,SchemeDesgin.class));
                                        Intent intent1 = new Intent(CustomerPage.this, SchemeDesgin.class);
                                        intent1.putExtra("userid", userid);
                                        startActivity(intent1);
                                        //startActivity(new Intent(Homepage.this,SchemeDesgin.class));
                                        break;
                                    case 2:
                                        dialog.dismiss();
                                        // startActivity(new Intent(Homepage.this,EmployeeDetails.class));
                                        Intent intent2 = new Intent(CustomerPage.this, EmployeeDetails.class);
                                        intent2.putExtra("userid", userid);
                                        startActivity(intent2);
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            else {
                Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                   // startActivity(new Intent(CustomerPage.this, CustomerPage.class));
            }
            }
            break;
            case R.id.transaction: {

                if (userid.contains("develop")) {
                    Intent intent2 = new Intent(CustomerPage.this, Transaction.class);
                    intent2.putExtra("userid", userid);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                    // startActivity(new Intent(CustomerPage.this, CustomerPage.class));
                }

                break;
            }

            case R.id.report: {

                if (userid.contains("develop")) {
                    Intent intent2 = new Intent(CustomerPage.this, Report.class);
                    intent2.putExtra("userid", userid);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                    //  startActivity(new Intent(CustomerPage.this, CustomerPage.class));
                }
                break;
            }
            case R.id.settings: {
                if (userid.contains("develop")) {
                    Intent intent2 = new Intent(CustomerPage.this, Settings.class);
                    intent2.putExtra("userid", userid);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                }
                break;
            }*/
            case R.id.Admin: {
                if (userid.contains("develop")) {
                    Intent intent2 = new Intent(CustomerPage.this, Homepage.class);
                    intent2.putExtra("userid", userid);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.customer: {
                Intent intent2 = new Intent(CustomerPage.this, CustomerPage.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            }
            case R.id.home: {
                Intent intent2 = new Intent(CustomerPage.this, Home.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            }
            case R.id.marker: {
                Intent intent2 = new Intent(CustomerPage.this, Location_Find.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;

            }
            case R.id.logout:
                session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
           case R.id.logout_click:
                Intent intent4 = new Intent(Homepage.this, Login.class);
                startActivity(intent4);
                intent4 = new Intent(Intent.ACTION_MAIN);
                intent4.addCategory(Intent.CATEGORY_HOME);
                intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent4);
                break;
            case R.id.back:
                this.finish();
                System.exit(0);
        }
        // default:
        return super.onOptionsItemSelected(item);
    }*/
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CustomerPage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.sakthisugars.salesandmarketing/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "CustomerPage Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app URL is correct.
                Uri.parse("android-app://com.sakthisugars.salesandmarketing/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}
