package com.sakthisugars.salesandmarketing;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;

public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawer;
    Button set_time,closing_time_set,save;
    TimePickerDialog timePickerDialog,tpd;
    SQLiteDatabase db;
    EditText MasterPassword;
    Database_handler database_handler;
    Button set_location;
    GPSTracker gpsTracker;
    EditText time;
    TextView uname;
    String userid;
    private int mInterval;
    private Handler mHandler;
    UserSessionManager session;
    final Context context = this;
    public Settings(){

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_drawer);
        session = new UserSessionManager(getApplicationContext());
        Intent intentval = getIntent();
        userid = intentval.getStringExtra("userid");
        uname=(TextView)findViewById(R.id.uname);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if(session.checkLogin())
            finish();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get name
        userid = user.get(UserSessionManager.KEY_NAME);
        uname.setText(Html.fromHtml(userid));
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
       // nav_Menu.findItem(R.id.settings).setVisible(false);
          //nav_Menu.findItem(R.id.logout).setVisible(false);
        /********************************************************************************/
        startService(new Intent(this,GPSTracker.class));
        //Database initializations
        database_handler = new Database_handler(this,Database_handler.DATABASE_NAME,null,Database_handler.DATABASE_VERSION);
        db=database_handler.getWritableDatabase();
        time= (EditText) findViewById(R.id.closing_time);
        set_time = (Button) findViewById(R.id.set_time);
        closing_time_set= (Button) findViewById(R.id.closing_time_set);
        set_location= (Button) findViewById(R.id.head_office_selection);
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                database_handler.ChangeSettings(db, Database_handler.SETTINGS_LOCATION_CHECK_TIMER,hourOfDay+ ","+minute);
            }
        },0,0,true);



        save = (Button) findViewById(R.id.save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text =MasterPassword.getText().toString();
                database_handler.ChangeSettings(db,Database_handler.SETTINGS_MASTER_PASSWORD,text);
            }
        });

        tpd = new TimePickerDialog(Settings.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String timer = hourOfDay+";"+ minute;
                database_handler.ChangeSettings(db, Database_handler.SETTINGS_REPORTING_TIME, timer);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                calendar.set(Calendar.MILLISECOND,0);
                Intent intent= new Intent(Settings.this,Notification.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(Settings.this,100,intent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager manager= (AlarmManager)getSystemService(ALARM_SERVICE);
                manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY, pendingIntent);
                Toast.makeText(Settings.this, "Reporting Time Updated", Toast.LENGTH_LONG).show();
            }
        },0,0,false);

        closing_time_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tpd.show();
            }
        });
        set_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCompat.requestPermissions(Settings.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
                gpsTracker = new GPSTracker(Settings.this);
                gpsTracker.getLocation();
                if (!gpsTracker.canGetLocation) {
                    gpsTracker.showSettingsAlert();
                } else{
                    database_handler.setHome(gpsTracker.location);
                    //gps_details.setHome(gpsTracker.location);
                    Toast.makeText(Settings.this,"Head Office Location Updated", Toast.LENGTH_LONG).show();
                }
            }
        });

        set_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog.show();
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
                Toast.makeText(this,"Press Back Again to Exit",Toast.LENGTH_LONG).show();
                flag=true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flag=false;
                    }
                },3000);
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
                                        Intent intent = new Intent(Settings.this, Item.class);
                                        intent.putExtra("userid", userid);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        dialog.dismiss();
                                        // startActivity(new Intent(Homepage.this,SchemeDesgin.class));
                                        Intent intent1 = new Intent(Settings.this, SchemeDesgin.class);
                                        intent1.putExtra("userid", userid);
                                        startActivity(intent1);
                                        //startActivity(new Intent(Homepage.this,SchemeDesgin.class));
                                        break;
                                    case 2:
                                        dialog.dismiss();
                                        // startActivity(new Intent(Homepage.this,EmployeeDetails.class));
                                        Intent intent2 = new Intent(Settings.this, EmployeeDetails.class);
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
                Intent intent1 = new Intent(Settings.this,Homepage.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
                //startActivity(new Intent(Homepage.this,Report.class));
                break;
            case R.id.customer:
                Intent intent2 = new Intent(Settings.this,CustomerPage.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            case R.id.settings:
                Intent intent3 = new Intent(Settings.this,Settings.class);
                intent3.putExtra("userid", userid);
                startActivity(intent3);
                // startActivity(new Intent(Homepage.this,Settings.class));
                break;
            case R.id.marker:
                Intent intent5 = new Intent(Settings.this,Location_Find.class);
                intent5.putExtra("userid", userid);
                startActivity(intent5);
                break;
            case R.id.logout:
                session.logoutUser();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public  class GPSTracker extends Service implements LocationListener {
        private Context mContext;

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

        @Override
        public void onCreate() {
            super.onCreate();
            mHandler = new Handler();
            startRepeatingTask();
            String time = database_handler.GetSettings(db,Database_handler.SETTINGS_LOCATION_CHECK_TIMER);
            String [] times= time.split(",");
            int hour=Integer.parseInt(times[0]);
            int minute=Integer.parseInt(times[1]);
            mInterval=minute*60*1000+hour*60*60*1000;
        }
        Runnable mStatusChecker = new Runnable() {
            @Override
            public void run() {
                try {
                    gpsTracker.getLocation();//this function can change value of mInterval.
                } finally {
                    // 100% guarantee that this always happens, even if
                    // your update method throws an exception
                    mHandler.postDelayed(mStatusChecker, mInterval);
                }
            }
        };

        void startRepeatingTask() {
            mStatusChecker.run();
        }

        void stopRepeatingTask() {
            mHandler.removeCallbacks(mStatusChecker);
        }

        public GPSTracker(Context context) {
            mContext = context;
            this.activity=Settings.this;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(ContextCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
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
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
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
            android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(mContext);

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
                                    android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            mContext.startActivity(intent);
                        }
                    });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Toast.makeText(Settings.this,"Cannot Identify Location without Location Permission",Toast.LENGTH_LONG).show();
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

        String provider = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.contains("gps")){ //if gps is disabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.sendBroadcast(poke);


        }
    }

    // turn off the gps
    public void turnGPSOff() {
        String provider = android.provider.Settings.Secure.getString(getContentResolver(), android.provider.Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.contains("gps")){ //if gps is enabled
            final Intent poke = new Intent();
            poke.setClassName("com.android.settings", "com.android.settings.widget.SettingsAppWidgetProvider");
            poke.addCategory(Intent.CATEGORY_ALTERNATIVE);
            poke.setData(Uri.parse("3"));
            this.sendBroadcast(poke);
        }
    }

}
