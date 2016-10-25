package com.sakthisugars.salesandmarketing;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by singapore on 25-06-2016.
 */
public class NetworkCheck extends BroadcastReceiver {
    Database_handler database_handler;
    SQLiteDatabase db;
    public static boolean Connected = false;

    public NetworkCheck() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        database_handler = new Database_handler(context, Database_handler.DATABASE_NAME, null, Database_handler.DATABASE_VERSION);
        db = database_handler.getWritableDatabase();
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                Connected = true;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                Connected = true;
        } else
            Connected = false;

        if (Connected) {
            if(LocationService.userid!=null) {
                //Updating locations
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                Location[] locations = database_handler.getLocation(db, location);
                for (Location location1 : locations) {
                    Date date = new Date(location1.getTime());
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US);//2016-08-09%2010:45:01.253
                    if (Connected) {
                        new ServerSync().execute(context.getResources().getString(R.string.LocationUpdateUrl) + "?UserId=" + LocationService.userid + "&Longitude=" + location1.getLongitude() + "&Latitude=" + location1.getLatitude() + "&EntryDateTime=" + simpleDateFormat.format(date).replace(" ", "%20"));
                        location1.setLatitude(23456);
                        location1.setLongitude(23456);
                    } else
                        break;
                }

                for (Location location1 : locations)
                    if (location1.getLatitude() == 23456 && location1.getLongitude() == 23456)
                        database_handler.removeLocation(db, location1);
            }
         }
        else {

        }
    }
    public static class ServerSync extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... params) {
            HttpURLConnection conn;
            try {
                URL url = new URL(params[0]);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                conn.getInputStream();
                conn.disconnect();

            } catch (MalformedURLException e) {
                Log.d("Rating Upload","URL Malformed");
                e.printStackTrace();
            } catch (IOException e) {
                Log.d("Rating Upload","IOException");
                e.printStackTrace();
            }
            return null;
        }
    }

}
