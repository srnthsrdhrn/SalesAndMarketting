package com.sakthisugars.salesandmarketing;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class Transaction extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener{
    GPSTracker gpsTracker;
    LinearLayout linearLayout;
    Button start,start1;
    DrawerLayout drawer;
    Database_handler database_handler;
    SQLiteDatabase db;
    String userid;
    UserSessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_drawer);
        session = new UserSessionManager(getApplicationContext());
        Intent intentval = getIntent();
        userid = intentval.getStringExtra("userid");
        database_handler = new Database_handler(this,Database_handler.DATABASE_NAME,null,Database_handler.DATABASE_VERSION);
        db= database_handler.getWritableDatabase();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLUE));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLACK));
        navigationView.setBackgroundColor(Color.WHITE);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();
       // nav_Menu.findItem(R.id.transaction).setVisible(false);
        nav_Menu.findItem(R.id.logout).setVisible(false);
        //...........................................................................
        linearLayout = (LinearLayout) findViewById(R.id.linear_layout);
        start = (Button) findViewById(R.id.start);
        start1 = (Button)findViewById(R.id.start1);

        if(Employee.sis_admin=='N'){
            if(LocationService.START==1){
                finish();
                startActivity(new Intent(this,CustomerPage.class));
            }
            else{
                drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                gpsTracker = new GPSTracker(this);
                gpsTracker.getLocation();
                if (!gpsTracker.canGetLocation) {
                    gpsTracker.showSettingsAlert();
                } else {
                    int loc_lat = (int) (gpsTracker.location.getLatitude()* 10000);
                    int loc_long = (int) (gpsTracker.location.getLongitude() * 10000);
                    Location location = database_handler.getHome(gpsTracker.location);
                    if (location != null) {
                        int loc1_lat = (int) (location.getLatitude() * 10000);
                        int loc1_long = (int) (location.getLongitude() * 10000);
                        boolean flag=(loc_lat >=(loc1_lat-5)||loc_lat<=(loc1_lat+5)||loc_lat==loc1_lat) && (loc_long >= (loc1_long-5)||loc_long<=(loc1_long+5)||loc_long==loc1_long);
                        if (flag) {
                            start1.setEnabled(true);
                            start.setVisibility(View.INVISIBLE);
                            start1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Transaction.this.startService(new Intent(Transaction.this, LocationService.class));
                                    Transaction.this.finish();
                                    //startActivity(new Intent(Transaction.this, CustomerPage.class));
                                    Intent intent = new Intent(Transaction.this, CustomerPage.class);
                                    intent.putExtra("userid", userid);
                                    startActivity(intent);

                                }
                            });
                        } else {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                                start1.setBackgroundColor(Color.DKGRAY);
                            }
                            start.setEnabled(false);
                            start.setVisibility(View.INVISIBLE);
                            start1.setEnabled(false);
                            Toast.makeText(this, "Goto to Head Office and Login again to Start", Toast.LENGTH_LONG).show();
                            session.logoutUser();
                        }
                    } else {
                        start.setEnabled(false);
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            start1.setBackgroundColor(Color.DKGRAY);
                        }
                        start.setVisibility(View.INVISIBLE);
                        start1.setEnabled(false);
                        Toast.makeText(this, "Admin Must Login and Verify Head Office Location in Settings", Toast.LENGTH_LONG).show();
                        session.logoutUser();
                    }

                }
            }
        }else {
            toggle.syncState();
            stopService(new Intent(this, LocationService.class));
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            gpsTracker = new GPSTracker(this);
            gpsTracker.getLocation();
            if (!gpsTracker.canGetLocation) {
                gpsTracker.showSettingsAlert();
            } else{
                final Intent intent = new Intent(Transaction.this, MapsActivity.class);
                intent.putExtra("Latitude", gpsTracker.latitude);
                intent.putExtra("Longitude", gpsTracker.longitude);
                start1.setVisibility(View.INVISIBLE);
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(intent);
                    }
                });
            }
        }
        LocationService.userid = userid;
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
    @Override
    protected void onResume() {
        super.onResume();
        gpsTracker.getLocation();
        if(gpsTracker.location==null){
            gpsTracker.showSettingsAlert();
        }else {
            if (Employee.sis_admin == 'Y') {
                final Intent intent = new Intent(Transaction.this, MapsActivity.class);
                intent.putExtra("Latitude", gpsTracker.latitude);
                intent.putExtra("Longitude", gpsTracker.longitude);
                start.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(intent);
                    }
                });
            } else {
                int loc_lat = (int) (gpsTracker.latitude * 10000);
                int loc_long = (int) (gpsTracker.longitude * 10000);
                Location location = database_handler.getHome(gpsTracker.location);
                if (location != null) {
                    int loc1_lat = (int) (location.getLatitude() * 10000);
                    int loc1_long = (int) (location.getLongitude() * 10000);
                    if (loc_lat == loc1_lat && loc_long == loc1_long) {
                        start.setEnabled(false);
                        start.setVisibility(View.INVISIBLE);
                    } else {
                        start.setEnabled(false);
                        start.setVisibility(View.INVISIBLE);
                        start1.setEnabled(false);
                        Toast.makeText(this, "Goto to Head Office and Login again to Start", Toast.LENGTH_LONG).show();
                    }
                } else {
                    start.setEnabled(false);
                    start.setVisibility(View.INVISIBLE);
                    start1.setEnabled(false);
                    Toast.makeText(this, "Admin Must Login and Verify Head Office Location in Settings", Toast.LENGTH_LONG).show();
                }

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
                if (userid.contains("develop")) {
                    {
                        drawer.closeDrawer(GravityCompat.START);
                        String[] list = new String[]{"Item", "Scheme"};
                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                        builder.setTitle("Choose")
                                .setSingleChoiceItems(list, 2, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch (which) {
                                            case 0:
                                                dialog.dismiss();
                                                startActivity(new Intent(Transaction.this, Item.class));
                                                break;
                                            case 1:
                                                dialog.dismiss();
                                                startActivity(new Intent(Transaction.this, SchemeDesgin.class));
                                                break;
                                        }
                                    }
                                });
                        android.support.v7.app.AlertDialog dialog = builder.create();
                        dialog.show();
                    }
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                   // startActivity(new Intent(Transaction.this, Transaction.class));
                }
                break;
            }
            case R.id.settings: {
                if (userid.contains("develop")) {
                    Intent intent3 = new Intent(Transaction.this, com.sakthisugars.salesandmarketing.Settings.class);
                    intent3.putExtra("userid", userid);
                    startActivity(intent3);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                }
                // startActivity(new Intent(Transaction.this,Settings.class));
                break;
            }
          /* case R.id.report: {
                if (userid.contains("develop")) {
                    Intent intent1 = new Intent(Transaction.this, Report.class);
                    intent1.putExtra("userid", userid);
                    startActivity(intent1);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                    //  startActivity(new Intent(Transaction.this, Transaction.class));
                }
                //startActivity(new Intent(Transaction.this,Report.class));
                break;
            }*/
            case R.id.customer: {
                if (userid.contains("develop")) {
                    finish();
                    Intent intent1 = new Intent(Transaction.this, CustomerPage.class);
                    intent1.putExtra("userid", userid);
                    startActivity(intent1);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                    //  startActivity(new Intent(Transaction.this, Transaction.class));
                }
                //startActivity(new Intent(Transaction.this,Report.class));
                break;
            }
            case R.id.homepage: {
                if (userid.contains("develop")) {
                    finish();
                    Intent intent = new Intent(Transaction.this, Homepage.class);
                    intent.putExtra("userid", userid);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                    // startActivity(new Intent(Transaction.this, Transaction.class));
                }
                break;
                //startActivity(new Intent(Transaction.this,Homepage.class));
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    /**
     * Created by singapore on 24-06-2016.
     */

    public  class GPSTracker extends Service implements LocationListener {

        private final Context mContext;

        //Activity for requesting permission
        private Activity activity;

        //Request code for the permission
        int REQUEST_CODE= 100;
        // flag for GPS status
        boolean isGPSEnabled = false;

        // flag for network status
        boolean isNetworkEnabled = false;

        // flag for GPS status
        boolean canGetLocation = false;

        Location location; // location
        public double latitude; // latitude
        public double longitude; // longitude

        // The minimum distance to change Updates in meters
        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters

        // The minimum time between updates in milliseconds
        private static final long MIN_TIME_BW_UPDATES = 1000 * 60* 1; // 1 minute

        // Declaring a Location Manager
        protected LocationManager locationManager;

        public GPSTracker(Context context) {
            this.mContext = context;
            this.activity=Transaction.this;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(mContext,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                    turnGPSOn();
                    getLocation();
                    stopUsingGPS();
                }
                if(shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)){
                    Toast.makeText(mContext,"Location is not Available without the Permission",Toast.LENGTH_LONG).show();
                }
                ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);

            }else {
                try {
                    turnGPSOn();
                    getLocation();
                    stopUsingGPS();
                }catch (SecurityException e){
                    e.printStackTrace();

                }
            }

        }

        public void getLocation() throws  SecurityException {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (!isGPSEnabled && !isNetworkEnabled) {

            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }

                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }

                    }
                    stopUsingGPS();
                }
            }

        }


        /**
         * Stop using GPS listener Calling this function will stop using GPS in your
         * app.
         * */
        public void stopUsingGPS() throws SecurityException {
            if (locationManager != null) {

                locationManager.removeUpdates(GPSTracker.this);
                turnGPSOff();
            }
        }

        /**
         * Function to show settings alert dialog On pressing Settings button will
         * launch Settings Options
         * */
        public void showSettingsAlert() {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

            // Setting DialogHelp Title
            alertDialog.setTitle("GPS settings");

            // Setting DialogHelp Message
            alertDialog
                    .setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(
                                    Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mContext.startActivity(intent);
                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            start.setEnabled(false);
                        }
                    });

            // Showing Alert Message

            alertDialog.show();
        }

        @Override
        public void onLocationChanged(Location location){
            float bestAccuracy = -1f;
            if (location.getAccuracy() != 0.0f && (location.getAccuracy() < bestAccuracy) || bestAccuracy == -1f) {
                try {
                    locationManager.removeUpdates(this);
                }catch(SecurityException e){
                    e.printStackTrace();
                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
                }
            }
            bestAccuracy = location.getAccuracy();
        }

        @Override
        public void onProviderDisabled(String provider) {

        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public IBinder onBind(Intent arg0) {
            return null;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length>0){
            gpsTracker.getLocation();
        }else{
            Toast.makeText(this,"Location Service Denied",Toast.LENGTH_LONG).show();
        }

    }

    //turn on the gps
    public void turnGPSOn() {
        Intent intent = new Intent("android.location.GPS_ENABLED_CHANGE");
        intent.putExtra("enabled", true);
        this.sendBroadcast(intent);

        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        // if(!provider.contains("gps")){ //if gps is disabled
        final Intent poke = new Intent();
        poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
        poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
        poke.setData(Uri.parse("3"));
        this.sendBroadcast(poke);


        //}
    }

    // turn off the gps
    public void turnGPSOff() {
        String provider = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.sendBroadcast(poke);
        }
    }

}
