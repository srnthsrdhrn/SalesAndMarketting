package com.sakthisugars.salesandmarketing;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by user on 9/7/2016.
 */
public class Unit__MapLocations extends MapsActivity implements OnMapReadyCallback {
    GPSTracker gpsTracker;
    private int mInterval;
    private Handler mHandler;
    private GoogleMap mMap;
    float loc_lat,loc_long;
//    double lat = getIntent().getExtras().getDouble("Latitude");
   // double log = getIntent().getExtras().getDouble("Longitude");
    // Add a marker in Sydney and move the camera
   // home = new LatLng(lat, log);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_locations);
        ActivityCompat.requestPermissions(Unit__MapLocations.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        gpsTracker = new GPSTracker(Unit__MapLocations.this);
        gpsTracker.getLocation();
        if (!gpsTracker.canGetLocation) {
            gpsTracker.showSettingsAlert();
        } else{
            loc_lat = (float) (gpsTracker.location.getLatitude());
            loc_long = (float) (gpsTracker.location.getLongitude());
           // txtlongtitude.setText(String.valueOf(loc_long));
          //  txtlatitude.setText(String.valueOf(loc_lat));
        }
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.maps);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        Bundle b = getIntent().getExtras();
        double lat = b.getDouble("Latitude");
        double log = b.getDouble("Longitude");
        //LatLng sydny = new LatLng(-34, 151);
        LatLng destination = new LatLng(lat, log);
        LatLng location=new LatLng(loc_lat,loc_long);
        mMap.addPolyline(new PolylineOptions().add(location,destination));
       // mMap.addMarker(new MarkerOptions().position(destination).title("Location"));
      //  mMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
      /*  if (checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);*/
        // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydny,18));//too zoom place
    }
    // map.moveCamera(CameraUpdateFactory.newLatLngZoom(
    // new LatLng(-18.142, 178.431), 2));

    // ..................Polylines are useful for marking paths and routes on the map.
    // map.addPolyline(new PolylineOptions().geodesic(true)
    //.add(new LatLng(-33.866, 151.195))  // Sydney
    // .add(new LatLng(-18.142, 178.431))  // Fiji
    // .add(new LatLng(21.291, -157.821))  // Hawaii
    // .add(new LatLng(37.423, -122.091))  // Mountain View

    //  );
    //  map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
    // }
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
            this.activity=Unit__MapLocations.this;
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
                            Toast.makeText(Unit__MapLocations.this,"Cannot Identify Location without Location Permission",Toast.LENGTH_LONG).show();
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
    @Override
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
    }
}
