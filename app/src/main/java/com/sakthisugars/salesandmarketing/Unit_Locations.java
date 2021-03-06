package com.sakthisugars.salesandmarketing;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by user on 9/7/2016.
 */
public class Unit_Locations extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    Button sakthinagar,modakurichi,sivagaigai,dhenkanal;
    String userid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unit_location);
        Intent intentval = getIntent();
        userid = intentval.getStringExtra("userid");
        sakthinagar=(Button)findViewById(R.id.sakthinagar);
        dhenkanal=(Button)findViewById(R.id.dhenkanal);
        sivagaigai=(Button)findViewById(R.id.sivagangai);
        // ................................manu............................
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLUE));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLUE));
        navigationView.setBackgroundColor(Color.WHITE);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();
      //  nav_Menu.findItem(R.id.report).setVisible(false);
        nav_Menu.findItem(R.id.logout).setVisible(false);
        //....................................manu...........................
        sakthinagar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double latitude=11.474621;
                double longitude=77.571972;
                Intent intent = new Intent(Unit_Locations.this, Unit__MapLocations.class);
                intent.putExtra("Latitude",latitude);
                intent.putExtra("Longitude",longitude);
                startActivity(intent);
              //  startActivity(new Intent(Unit_Locations.this,Unit__MapLocations.class));
            }
        });
        sivagaigai.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double latitude=9.832708;
                double longitude=78.362879 ;
                Intent intent = new Intent(Unit_Locations.this, Unit__MapLocations.class);
                intent.putExtra("Latitude",latitude);
                intent.putExtra("Longitude",longitude);
                startActivity(intent);
               // startActivity(new Intent(Unit_Locations.this,Unit__MapLocations.class));
            }
        });
        dhenkanal.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                double latitude=20.673306;
                double longitude=85.569509;
               /* Intent intent = new Intent(Unit_Locations.this, Unit__MapLocations.class);
                intent.putExtra("Latitude",latitude);
                intent.putExtra("Longitude",longitude);
                startActivity(intent);
                //startActivity(new Intent(Unit_Locations.this,Unit__MapLocations.class));*/
                Intent yourInent = new Intent(Unit_Locations.this, Unit__MapLocations.class);
                Bundle b = new Bundle();
                b.putDouble("Latitude", latitude);
                b.putDouble("Longitude", longitude);
                yourInent.putExtras(b);
                startActivity(yourInent);
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
            case R.id.master: {
                drawer.closeDrawer(GravityCompat.START);
                String[] list = new String[]{"Item", "Scheme"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Choose")
                        .setSingleChoiceItems(list,2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        dialog.dismiss();
                                        startActivity(new Intent(Unit_Locations.this, Item.class));
                                        break;
                                    case 1:
                                        dialog.dismiss();
                                        startActivity(new Intent(Unit_Locations.this,SchemeDesgin.class));
                                        break;
                                }
                            }
                        });
                android.support.v7.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
            break;
            case R.id.settings:
                Intent intent2 = new Intent(Unit_Locations.this, Settings.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            case R.id.transaction:
                Intent intent1 = new Intent(Unit_Locations.this, Transaction.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
                // startActivity(new Intent(Report.this,Transaction.class));
                break;
            case R.id.homepage:
                Intent intent = new Intent(Unit_Locations.this, Homepage.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
                break;
            case R.id.customer:
                Intent intent3 = new Intent(Unit_Locations.this, CustomerPage.class);
                intent3.putExtra("userid", userid);
                startActivity(intent3);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
