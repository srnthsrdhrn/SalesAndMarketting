package com.sakthisugars.salesandmarketing;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by Administrator on 7/19/2016.
 */
public class GpsDetails extends Activity {
    ProgressDialog progressDialog;
    String save_url = "http://192.168.1.28:900/Android.svc/SaveEmployeeStartOver?";
    String LOGIN_URL;
    double Latitude;
    double Longitude;
    String userid,lat,lang,tim;
    private String FILE_NAME = "home_data.txt";
    Context context;

    //  Bundle bundle = getIntent().getExtras();
    // userid = bundle.getString("userid");
    public Location getHome(Location location) {
        String data = readFromFile();
        String[] values = string_to_item(data);
        if (values[0] != null && values[1] != null) {
            if (!values[0].equals("") && !values[1].equals("")) {
                location.setLatitude(Double.parseDouble(values[0]));
                location.setLongitude(Double.parseDouble(values[1]));
                return location;
            } else
                return null;
        } else
            return null;
    }

    public void setHome(Location location) {
        String data = "Latitude;" + location.getLatitude() + ";Longitude;" + location.getLongitude();
        writeToFile(data);
        //new save().execute("");

        // writeToFile(data);
    }

    private void writeToFile(String data) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    private String readFromFile() {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(FILE_NAME);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    private String[] string_to_item(String s) {
        String[] items = new String[2];
        int check1 = 0, check2 = 0;
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == ';') {
                if (check1 == 0) {
                    check1 = i;
                } else {
                    if (check2 == 0) {
                        check2 = i;
                        items[count] = s.substring(check1 + 1, i);
                        count++;
                    } else {
                        items[count] = s.substring(i + 1);
                        count++;
                        break;
                    }
                }
            }
        }
        return items;
    }

    private class save extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            //Bundle bundle = getIntent().getExtras();
            // userid = bundle.getString("userid");
            String string = "";
            try {
                String Combine_txt = "UserId=" + "develop" + "&" + "Longitude=" + lang + "&" + "Latitude=" + lat;
                //String Combine_txt1 = "/"+itemid + "/" + qty + "/" + uomid + "/" + rateofitem;
                Log.v("The new URL is half", String.valueOf(Combine_txt));
                LOGIN_URL = save_url + Combine_txt;
                URL url = new URL(LOGIN_URL);
                Log.v("The new URL is", String.valueOf(url));
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(10000);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

                    if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            stringBuilder.append("\n");
                        }
                        string = stringBuilder.toString();
                        reader.close();
                        inputStream.close();
                    }
                    httpURLConnection.disconnect();
                } catch (SocketTimeoutException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GpsDetails.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return string;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                Log.v("The new URL is", String.valueOf(jsonObject));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
            if (jsonObject != null) {
                try {
                    JSONObject jsonResultObject = new JSONObject(String.valueOf(jsonObject));
                    String value = jsonResultObject.getString("SaveEmployeeStartOverResult");
                    Log.v("The Result", String.valueOf(value));

                    Toast.makeText(GpsDetails.this, value, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(GpsDetails.this, (CharSequence) e, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    boolean flag = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    public void storeLocation(String latitude, String longitude, String time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(tim, time);
        contentValues.put(lat, latitude);
        contentValues.put(lang, longitude);

        new save().execute("");
    }
}
