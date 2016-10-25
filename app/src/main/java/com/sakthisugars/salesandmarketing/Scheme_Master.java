package com.sakthisugars.salesandmarketing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
 * Created by Administrator on 7/21/2016.
 */
public class Scheme_Master extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    Database_handler database_handler;
    SQLiteDatabase db;
    ProgressDialog progressDialog;
    String Itemlist_url = "http://192.168.1.28:900/Android.svc/GetSchemeTypeList?UserId=Develop&Key=T";
    String schemename_url = "http://192.168.1.28:900/Android.svc/GetSchemeList/Develop/M";
    String save_url = "http://192.168.1.28:900/Android.svc/SaveSchemeMaster?";
    String delete_url = "http://192.168.1.28:900/Android.svc/SaveSchemeMaster?";
    String LOGIN_URL;
    TextView uom;
    String rateofitem, itemid, qty, uomid, rateval, rate, shemeid;
    TextView txtitemcode, txtitemid, txtstockcount, txtuom, txtuomid;
    EditText txtname, txtdescription;
    Button save, cancel, update, delete;
    String userid, Schemename, description, name;
    Spinner mySpinner, Schemeid_spn;
    public ArrayList<Schemetype> SchemetypeList = new ArrayList<Schemetype>();
    public ArrayList<Schemetype> SchemeldList = new ArrayList<Schemetype>();
    public ArrayList<String> ItemList = new ArrayList<String>();
    public ArrayList<String> Schemenamelist = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheme_master);
        Intent intentval = getIntent();
        userid = intentval.getStringExtra("userid");
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
       // nav_Menu.findItem(R.id.settings).setVisible(false);
        /********************************************************************************/

        //Database initializations
        database_handler = new Database_handler(this, Database_handler.DATABASE_NAME, null, Database_handler.DATABASE_VERSION);
        db = database_handler.getWritableDatabase();
        save = (Button) findViewById(R.id.save);
        delete = (Button) findViewById(R.id.delete);
        update = (Button) findViewById(R.id.update);
        cancel = (Button) findViewById(R.id.cancel);
        txtdescription = (EditText) findViewById(R.id.description);
        txtname = (EditText) findViewById(R.id.scheme_name);

        // delete.setVisibility(View.INVISIBLE);
        new fetchtype().execute("");
        //new fetchscheme().execute("");
        save.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                Schemename = txtname.getText().toString();
                description = txtdescription.getText().toString();
                new save().execute("");

            }

        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                txtname.setText("");
                txtdescription.setText("");
            }
        });
        // delete.setOnClickListener(new View.OnClickListener() {

        //  @Override

        //  public void onClick(View v) {
        //    new deletescheme().execute("");
        // }
        // });

    }

    private class fetchtype extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Scheme_Master.this);

        }

        @Override
        protected String doInBackground(String... params) {
            String string = "";

            try {

                LOGIN_URL = Itemlist_url;
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
                            Toast.makeText(Scheme_Master.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
                JSONArray jsonArray = new JSONArray(jsonObject.optString("GetSchemeTypeListResult"));
                Log.i(Item.class.getName(),
                        "Number of entries00000000000 " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    Schemetype info = new Schemetype();

                    info.settypeid(jsonObject.optString("TypeId"));
                    SchemetypeList.add(info);
                    ItemList.add(jsonObject.optString("TypeName"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mySpinner = (Spinner) findViewById(R.id.Scheme_spinner);
            mySpinner.setAdapter(new ArrayAdapter<String>(Scheme_Master.this, android.R.layout.simple_spinner_dropdown_item, ItemList));
            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0,
                                           View arg1, int position, long arg3) {
                    // TODO Auto-generated method stub
                    itemid = SchemetypeList.get(position).gettypeid();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }
    }

    private class fetchscheme extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Scheme_Master.this);

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
                            Toast.makeText(Scheme_Master.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
            //Schemeid_spn = (Spinner) findViewById(R.id.spn_shm_id);
            Schemeid_spn.setAdapter(new ArrayAdapter<String>(Scheme_Master.this, android.R.layout.simple_spinner_dropdown_item, Schemenamelist));
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

    private class save extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected String doInBackground(String... params) {
            Intent intent = getIntent();
            userid = intent.getStringExtra("userid");
            String string = "";
            try {

                // String Combine_txt1 = "/"+itemid + "/" + qty + "/" + uomid + "/" + rateofitem;
                String Combine_txt = "UserId=" + "Develop" + "&" + "Name=" + Schemename + "&" + "Description=" + description + "&" + "Type=" + itemid + "&" + "EntryType=I" + "&" + "SchemeId=0";

                // UserId=Develop&Key=S&ItemId=1&Qty=111&UOMId=4&Rate=222;

                Log.v("The new URL is", String.valueOf(Combine_txt));
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
                            Toast.makeText(Scheme_Master.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(Scheme_Master.this, value, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(Scheme_Master.this, (CharSequence) e, Toast.LENGTH_LONG).show();
                }
            }
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
                            Toast.makeText(Scheme_Master.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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

                    Toast.makeText(Scheme_Master.this, value, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(Scheme_Master.this, (CharSequence) e, Toast.LENGTH_LONG).show();
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
            case R.id.homepage:
                finish();
                Intent intent = new Intent(Scheme_Master.this, Homepage.class);
                intent.putExtra("userid", userid);
                startActivity(intent);
               // startActivity(new Intent(Scheme_Master.this,Homepage.class));
                break;
            case R.id.delete:
                finish();
                Intent intent1 = new Intent(Scheme_Master.this, Homepage.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
               // startActivity(new Intent(Scheme_Master.this,DeleteScheme.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
