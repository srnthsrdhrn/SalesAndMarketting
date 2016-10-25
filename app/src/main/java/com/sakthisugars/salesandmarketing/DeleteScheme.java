package com.sakthisugars.salesandmarketing;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Administrator on 7/22/2016.
 */
public class DeleteScheme extends AppCompatActivity {
    String schemename_url = "http://192.168.1.28:900/Android.svc/GetSchemeList/Develop/M";
    String delete_url = "http://192.168.1.28:900/Android.svc/SaveSchemeMaster?";
    ProgressDialog progressDialog;
    String LOGIN_URL;
    String rateofitem, itemid, qty, uomid, rateval, rate, shemeid;
    TextView txtitemcode, txtitemid, txtstockcount, txtuom, txtuomid;
    EditText txtname, txtdescription;
    Button  delete;
    String userid, Schemename, description, name;
    Spinner mySpinner, Schemeid_spn;
    public ArrayList<Schemetype> SchemetypeList = new ArrayList<Schemetype>();
    public ArrayList<Schemetype> SchemeldList = new ArrayList<Schemetype>();
    public ArrayList<String> ItemList = new ArrayList<String>();
    public ArrayList<String> Schemenamelist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schemedelete_drawer);
        delete = (Button) findViewById(R.id.delete);
        new fetchscheme().execute("");
        delete.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                new deletescheme().execute("");

            }

        });
    }
    private class fetchscheme extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(DeleteScheme.this);

        }

        @Override
        protected String doInBackground(String... params) {
            String string = "";

            try {

                LOGIN_URL = schemename_url;
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
                            Toast.makeText(DeleteScheme.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
                JSONArray jsonArray = new JSONArray(jsonObject.optString("GetSchemeListResult"));
                Log.i(Item.class.getName(),
                        "Number of entries00000000000 " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    Schemetype schemeidlist = new Schemetype();

                    schemeidlist.setSchemId(jsonObject.optString("SchemeUId"));
                    SchemeldList.add(schemeidlist);
                    Schemenamelist.add(jsonObject.optString("SchemeName"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Schemeid_spn = (Spinner) findViewById(R.id.delete_spn);
            Schemeid_spn.setAdapter(new ArrayAdapter<String>(DeleteScheme.this, android.R.layout.simple_spinner_dropdown_item, Schemenamelist));
            Schemeid_spn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0,
                                           View arg1, int position, long arg3) {
                    // TODO Auto-generated method stub
                    // delete.setVisibility(View.VISIBLE);
                    shemeid = SchemeldList.get(position).getSchemeId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }
    private class deletescheme extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            // Intent intent = getIntent();
            // userid = intent.getStringExtra("userid");
            String string = "";
            try {

                // String Combine_txt1 = "/"+itemid + "/" + qty + "/" + uomid + "/" + rateofitem;
                String Combine_txt = "UserId=Develop" + "&" + "Name=0" + "&" + "Description=0" + "&" + "Type=0" + "&" + "EntryType=D" + "&" + "SchemeId=" + shemeid;

                // UserId=Develop&Key=S&ItemId=1&Qty=111&UOMId=4&Rate=222;

                Log.v("The new URL is", String.valueOf(Combine_txt));
                LOGIN_URL = delete_url + Combine_txt;
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
                            Toast.makeText(DeleteScheme.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
                    String value = jsonResultObject.getString("SaveSchemeMasterResult");
                    Log.v("The Result", String.valueOf(value));

                    Toast.makeText(DeleteScheme.this, value, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(DeleteScheme.this, (CharSequence) e, Toast.LENGTH_LONG).show();
                }
            }
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


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }
    boolean flag = false;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
            }
        }



