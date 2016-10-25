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
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    Database_handler database_handler;
    SQLiteDatabase db;
    String userid;
    TextView uname;
    UserSessionManager session;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        session = new UserSessionManager(getApplicationContext());
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


        Menu nav_Menu = navigationView.getMenu();
       // nav_Menu.findItem(R.id.homepage).setVisible(false);
        /*******************************************************************************************************************/
        database_handler = new Database_handler(this,Database_handler.DATABASE_NAME,null,Database_handler.DATABASE_VERSION);
        db=database_handler.getWritableDatabase();
        //Erase all records at the end of the day
        /*Calendar calendar = Calendar.getInstance();
        if(calendar.get(Calendar.HOUR_OF_DAY)==17){
            database_handler.Erase_All_Records(db);
        }*/
        if(session.checkLogin())
            finish();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get name
        userid = user.get(UserSessionManager.KEY_NAME);
        uname.setText(Html.fromHtml(userid));
        //uname.setText(String.valueOf(name));
        // get email
       // String email = user.get(UserSessionManager.KEY_EMAIL);

        // Show user data on activity
       // lblName.setText(Html.fromHtml("Name: <b>" + name + "</b>"));
      //  lblEmail.setText(Html.fromHtml("Email: <b>" + email + "</b>"));
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
            case R.id.master: {
                drawer.closeDrawer(GravityCompat.START);
                String[] list = new String[]{"Item Stock Details","Scheme Configuration","EmployeeConfiguration"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose")
                        .setSingleChoiceItems(list,3, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        dialog.dismiss();
                                        //startActivity(new Intent(Homepage.this, Item.class));
                                        Intent intent = new Intent(Homepage.this, Item.class);
                                       // intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                      //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                      //  intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent.putExtra("userid", userid);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        dialog.dismiss();
                                       // startActivity(new Intent(Homepage.this,SchemeDesgin.class));

                                        Intent intent1 = new Intent(Homepage.this, SchemeDesgin.class);
                                       // intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                      //  intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                      //  intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                      //  intent1.putExtra("userid", userid);
                                        startActivity(intent1);
                                        //startActivity(new Intent(Homepage.this,SchemeDesgin.class));
                                        break;
                                    case 2:
                                        dialog.dismiss();
                                       // startActivity(new Intent(Homepage.this,EmployeeDetails.class));

                                        Intent intent2 = new Intent(Homepage.this, EmployeeDetails.class);
                                       // intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                                       // intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                       // intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        intent2.putExtra("userid", userid);
                                        startActivity(intent2);
                                }
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            break;
          /* case R.id.transaction:
                Intent intent = new Intent(Homepage.this,Transaction.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
               // startActivity(new Intent(Homepage.this,Transaction.class));
                break;*/
         case R.id.homepage:

                Intent intent1 = new Intent(Homepage.this,Homepage.class);
            // intent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
           //  intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
           //  intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
                //startActivity(new Intent(Homepage.this,Report.class));
                break;
            case R.id.customer:

                Intent intent2 = new Intent(Homepage.this,CustomerPage.class);
                //intent2.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
              //  intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
               // intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
              //  intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            case R.id.settings:

                Intent intent3 = new Intent(Homepage.this,Settings.class);
               // intent3.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
              //  intent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              //  intent3.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
               // intent3.putExtra("userid", userid);
                startActivity(intent3);
               // startActivity(new Intent(Homepage.this,Settings.class));
                break;
            case R.id.marker:

                Intent intent5 = new Intent(Homepage.this,Location_Find.class);
               // intent5.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
               // intent5.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              //  intent5.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
              //  intent5.putExtra("userid", userid);
                startActivity(intent5);
                break;
          case R.id.logout:
              session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*@Override
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


}
