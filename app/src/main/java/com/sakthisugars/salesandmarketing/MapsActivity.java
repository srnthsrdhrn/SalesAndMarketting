package com.sakthisugars.salesandmarketing;

import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private float ZOOM_LEVEL= 18.0f;//if the number increases, the zoom also increases
    private FloatingActionButton fab;
    private  LatLng home;
    Database_handler database_handler;
    SQLiteDatabase db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        database_handler= new Database_handler(this,Database_handler.DATABASE_NAME,null,Database_handler.DATABASE_VERSION);
        db=database_handler.getWritableDatabase();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        fab= (FloatingActionButton) findViewById(R.id.my_location);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home,ZOOM_LEVEL));
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        double lat = getIntent().getExtras().getDouble("Latitude");
        double log = getIntent().getExtras().getDouble("Longitude");
        // Add a marker in Sydney and move the camera
        home = new LatLng(lat, log);
        Location location = new Location("dummy provider");
        Location[] locations = database_handler.getLocation(db, location);
        if(locations!=null){
            for (int i = 0; i < locations.length; i++) {
                Location location1 = locations[i];
                Date date = new Date(location1.getTime());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                SimpleDateFormat dateFormat1 = new SimpleDateFormat("KK:mm a",Locale.US);
                String date1="Date: "+dateFormat.format(date);
                String time= "Time: "+dateFormat1.format(date);
                Marker marker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(location1.getLatitude(), location1.getLongitude()))
                        .title(date1)
                        .snippet(time));
                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        marker.showInfoWindow();
                        return false;
                    }
                });
            }
        }
        Marker marker = mMap.addMarker(new MarkerOptions().position(home)
                .title("Head Office")
                .snippet("This is the Head office Location"));
        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return false;
            }
        });
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(home,ZOOM_LEVEL));
    }

}
