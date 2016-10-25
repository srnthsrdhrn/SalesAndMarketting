package com.sakthisugars.salesandmarketing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.HashMap;

public class Item extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    DrawerLayout drawer;
    ProgressDialog progressDialog;
    String Itemlist_url = "http://192.168.1.28:900/Android.svc/GetItemList/Develop/I";
    String save_url1 = "http://192.168.1.28:900/Android.svc/SaveItemStock/Develop/S";
    String save_url = " http://192.168.1.28:900/Android.svc/SaveItemStock?";
    String LOGIN_URL;
    TextView uom,uname;
    String rateofitem, itemid, qty, uomid, rate;
    TextView txtitemcode, txtitemid, txtstockcount, txtuom, txtuomid;
    EditText txtrate, txtqty;
    ImageView save, cancel;
    String username,userid;
    Spinner mySpinner;
    UserSessionManager session;
    public ArrayList<ItemDetails> ItemDetailsList = new ArrayList<ItemDetails>();
    public ArrayList<String> ItemList = new ArrayList<String>();
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.item_master);
        session = new UserSessionManager(getApplicationContext());
       // Intent intentval = getIntent();
       // userid = intentval.getStringExtra("userid");
        save = (ImageView) findViewById(R.id.save);
        cancel = (ImageView) findViewById(R.id.cancel);
        uom = (TextView) findViewById(R.id.uom);
        txtqty = (EditText) findViewById(R.id.qty);
        txtrate = (EditText) findViewById(R.id.rate);
        //txtitemcode = (TextView) findViewById(R.id.itemcode);
        //  txtitemid = (TextView) findViewById(R.id.itemid);
        // txtstockcount = (TextView) findViewById(R.id.stockcount);
        //  txtuom = (TextView) findViewById(R.id.uom);
        // txtuomid = (TextView) findViewById(R.id.uomid);
        // rate = (EditText) findViewById(R.id.rate);
       // ............................meniu item........................

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uname=(TextView)findViewById(R.id.uname);
        if(session.checkLogin())
            finish();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get name
        userid = user.get(UserSessionManager.KEY_NAME);
        uname.setText(String.valueOf(userid));
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
        //..................................manu item....................................

        new connect().execute("");
        save.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                //itemid=txtitemid.getText().toString();
                // rate = String.valueOf(txtrate.getText());
               /* rate = txtrate.getText().toString();
                final int item_rate = Integer.parseInt(rate);
                qty = txtqty.getText().toString();
                final int item_qty = Integer.parseInt(qty);
                if (qty!="" && item_qty > 0) {
                    if (rate!="" && item_rate > 0) {
                        new save().execute("");
                        txtqty.setText("");
                        txtrate.setText("");
                    } else
                        Toast.makeText(Item.this, "Please enter the Qty", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(Item.this, "Please enter the Rate", Toast.LENGTH_LONG).show();

                }*/
                rate = txtrate.getText().toString();
              //  final int item_rate = Integer.parseInt(rate);
                qty = txtqty.getText().toString();
               // final int item_qty = Integer.parseInt(qty);
                if(qty.length()==0){

                    Toast.makeText(Item.this, "Please enter the Qty", Toast.LENGTH_LONG).show();
                }
                else if(rate.length()==0){
                    Toast.makeText(Item.this, "Please enter the Rate", Toast.LENGTH_LONG).show();
                }else {
                    int item_rate = Integer.parseInt(rate);
                    int item_qty = Integer.parseInt(qty);
                    if(item_rate<=0){
                        Toast.makeText(Item.this, "Please enter Non_Zero the Rate", Toast.LENGTH_LONG).show();
                    }
                    else if(item_qty<=0){
                        Toast.makeText(Item.this, "Please enter Non_Zero the Qty", Toast.LENGTH_LONG).show();
                    }
                    else{
                        new save().execute("");
                        txtqty.setText("");
                        txtrate.setText("");
                        txtqty.findFocus();
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtqty.setText("");
                txtrate.setText("");
                txtqty.findFocus();
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
   /* @Override
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
    }*/


    private class connect extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Item.this);

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
                            Toast.makeText(Item.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
                JSONArray jsonArray = new JSONArray(jsonObject.optString("GetItemListResult"));
                Log.i(Item.class.getName(),
                        "Number of entries00000000000 " + jsonArray.length());

                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    ItemDetails info = new ItemDetails();

                    info.setCode(jsonObject.optString("ItemCode"));
                    info.setItemId(jsonObject.optString("ItemId"));
                    info.setStockCount(jsonObject.optString("StockCount"));
                    info.setUOM(jsonObject.optString("UOM"));
                    info.setUOMId(jsonObject.optString("UOMId"));
                    ItemDetailsList.add(info);
                    ItemList.add(jsonObject.optString("ItemName"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            mySpinner = (Spinner) findViewById(R.id.my_spinner);
            mySpinner.setAdapter(new ArrayAdapter<String>(Item.this, android.R.layout.simple_spinner_dropdown_item, ItemList));
            mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0,
                                           View arg1, int position, long arg3) {
                    // TODO Auto-generated method stub
                    // Locate the textviews in activity_main.xml
                    // txtitemcode = (TextView) findViewById(R.id.itemcode);
                    //txtitemid = (TextView) findViewById(R.id.itemid);
                    //txtstockcount = (TextView) findViewById(R.id.stockcount);
                    // txtuom = (TextView) findViewById(R.id.uom);
                    // txtuomid = (TextView) findViewById(R.id.uomid);

                    // Set the text followed by the position
                    // txtitemcode.setText(ItemDetailsList.get(position).getCode());
                    // txtitemid.setText(ItemDetailsList.get(position).getItemId());
                    itemid = ItemDetailsList.get(position).getItemId();
                    //   txtstockcount.setText(ItemDetailsList.get(position).getStockCount());
                    uom.setText(ItemDetailsList.get(position).getUOM());
                    // txtuomid.setText(ItemDetailsList.get(position).getUOMId());
                    uomid = ItemDetailsList.get(position).getUOMId();
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
            username = intent.getStringExtra("userid");
            String string = "";
            try {

                // String Combine_txt1 = "/"+itemid + "/" + qty + "/" + uomid + "/" + rateofitem;
                String Combine_txt = "UserId=" + username + "&" + "Key=S" + "&" + "ItemId=" + itemid + "&" + "Qty=" + qty + "&" + "UOMId=" + uomid + "&" + "Rate=" + rate;
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
                            Toast.makeText(Item.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
                    String value = jsonResultObject.getString("SaveItemStockResult");
                    Log.v("The Result", String.valueOf(value));
                    Toast.makeText(Item.this, value, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(Item.this, (CharSequence) e, Toast.LENGTH_LONG).show();
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
            case R.id.master: {
                drawer.closeDrawer(GravityCompat.START);
                String[] list = new String[]{"Item Stock Details","Scheme Configuration","EmployeeConfiguration"};
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose")
                        .setSingleChoiceItems(list, 3, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        dialog.dismiss();
                                        //startActivity(new Intent(Homepage.this, Item.class));
                                        Intent intent = new Intent(Item.this, Item.class);
                                        intent.putExtra("userid", userid);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        dialog.dismiss();
                                        // startActivity(new Intent(Homepage.this,SchemeDesgin.class));
                                        Intent intent1 = new Intent(Item.this, SchemeDesgin.class);
                                        intent1.putExtra("userid", userid);
                                        startActivity(intent1);
                                        //startActivity(new Intent(Homepage.this,SchemeDesgin.class));
                                        break;
                                    case 2:
                                        dialog.dismiss();
                                        // startActivity(new Intent(Homepage.this,EmployeeDetails.class));
                                        Intent intent2 = new Intent(Item.this, EmployeeDetails.class);
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
                Intent intent1 = new Intent(Item.this, Homepage.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
                //startActivity(new Intent(Homepage.this,Report.class));
                break;
            case R.id.customer:
                Intent intent2 = new Intent(Item.this, CustomerPage.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            case R.id.settings:
                Intent intent3 = new Intent(Item.this, Settings.class);
                intent3.putExtra("userid", userid);
                startActivity(intent3);
                // startActivity(new Intent(Homepage.this,Settings.class));
                break;
            case R.id.marker:
                Intent intent5 = new Intent(Item.this, Location_Find.class);
                intent5.putExtra("userid", userid);
                startActivity(intent5);
                break;
            case R.id.logout:
                session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}




