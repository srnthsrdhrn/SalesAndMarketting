package com.sakthisugars.salesandmarketing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
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

/**
 * Created by Administrator on 7/26/2016.
 */
public class Regular_Sales extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Button add,pay;
    ListView itemlist;
    EditText qty;
    TextView uname;
    Spinner popupSpinner;
    String qty_value;
    String text,offeritem="";
    ProgressDialog progressDialog;
    public ArrayList<ItemDetails> ItemnameDetails = new ArrayList<ItemDetails>();
    public ArrayList<String> Itemnamelist = new ArrayList<String>();
    String Itemname_url = "http://192.168.1.28:900/Android.svc/GetItemList/Develop/I";
    String LOGIN_URL;
    String parentitemid,parentitemname,qtyvalue,rate,parentitemrate,stockcount;
    int totamount;
    ArrayList<String> list_main;
    ArrayList<String> selectedItem = new ArrayList<String>();
    ArrayAdapter<String> list_view_main_adapter;
    DrawerLayout drawer;
    Database_handler database_handler;
    SQLiteDatabase db;
    String userid;
    UserSessionManager session;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.regular_sales_drawer);
       // ActionBar actionBar = getActionBar();
        //actionBar.setDisplayHomeAsUpEnabled(true);
        session = new UserSessionManager(getApplicationContext());
        Intent intent = getIntent();
        userid = intent.getStringExtra("userid");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uname=(TextView)findViewById(R.id.uname);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(session.checkLogin())
            finish();

        // get user data from session
        HashMap<String, String> user = session.getUserDetails();

        // get name
        userid = user.get(UserSessionManager.KEY_NAME);
        uname.setText(String.valueOf(userid));
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLUE));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLACK));
        navigationView.setBackgroundColor(Color.WHITE);
        navigationView.setNavigationItemSelectedListener(this);
//..................................................................................................
        add = (Button) findViewById(R.id.sales);
        pay = (Button) findViewById(R.id.pay);
        itemlist = (ListView) findViewById(R.id.list_item);
     /*  // .........................................................
        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_multiple_choice, list_main);
        itemlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        itemlist.setAdapter(adapter);
       // ..................................................*/

        list_main = new ArrayList<>();
        // list_view_main_adapter = new ArrayAdapter<String>(this, R.layout.list_view_row, list_main);
        list_view_main_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, list_main);
        itemlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        itemlist.setAdapter(list_view_main_adapter);

        add.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = layoutInflater.inflate(R.layout.activity_popup_salesitems, null);
                final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
                popupWindow.setFocusable(true);
                Button selected_items = (Button) popupView.findViewById(R.id.select);
                Button cancel = (Button) popupView.findViewById(R.id.btnDismiss);
                qty = (EditText) popupView.findViewById(R.id.qty);
                qty_value = qty.getText().toString();
                popupSpinner = (Spinner) popupView.findViewById(R.id.popup_spn_itemname);
                new fetchitemname().execute("");
                selected_items.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        try {
                            qtyvalue = qty.getText().toString();
                            final int dis_amt = Integer.parseInt(qtyvalue);
                            if (dis_amt == 0) {
                                Toast.makeText(Regular_Sales.this, "Please enter the Quantity of Items", Toast.LENGTH_LONG).show();
                            } else {
                                //qtyvalue = qty.getText().toString();
                                // if (qtyvalue.length() == 0) {
                                //  Toast.makeText(Regular_Sales.this, "Please enter the Quantity of Items", Toast.LENGTH_LONG).show();
                                // } else {
                                String item = popupSpinner.getSelectedItem().toString();
                                qtyvalue = qty.getText().toString();
                                rate = parentitemrate;
                                int qtyval = Integer.parseInt(qtyvalue);
                                int val = Integer.parseInt(rate);
                                int count = Integer.parseInt(stockcount);
                                if (qtyval <= count) {
                                    totamount = val * qtyval;
                                    text = item + "~" + qtyvalue + "~" + totamount;
                                    list_main.add(text);
                                    list_view_main_adapter.notifyDataSetChanged();
                                    qty.setText("0");
                                } else {
                                    Toast.makeText(Regular_Sales.this, "Out of stock", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                            Toast.makeText(Regular_Sales.this, "Please Enter Quantity", Toast.LENGTH_SHORT).show();
                        }
                    }

                    // }
                });

                cancel.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });


                popupWindow.showAsDropDown(add, 50, -30);
            }
        });
       /* itemlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String item = ((TextView) view).getText().toString();
                Intent calculation = new Intent(Regular_Sales.this, Sales_Calculation.class);
                calculation.putExtra("Purchase_item", item);
                startActivity(calculation);
            }

        });*/

        pay.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                SparseBooleanArray checked = itemlist.getCheckedItemPositions();
                ArrayList<String> selectedItems = new ArrayList<String>();
                for (int i = 0; i < checked.size(); i++) {
                    // Item position in adapter
                    int position = checked.keyAt(i);
                    // Add sport if it is checked i.e.) == TRUE!
                    if (checked.valueAt(i))
                        selectedItems.add(list_view_main_adapter.getItem(position));
                }
                // onPreExecute();
                // for (String off : selectedItems) {
                // offeritem += off + ",";
                // }
                //   Toast.makeText(Regular_Sales.this, offeritem, Toast.LENGTH_LONG).show();

                String[] outputStrArr = new String[selectedItems.size()];

                for (int i = 0; i < selectedItems.size(); i++) {
                    outputStrArr[i] = selectedItems.get(i);
                }
                 // Bundle b = new Bundle();
                    // b.putStringArray("",outputStrArr);
                    //   Log.v("The new URL is", String.valueOf(b));
                Intent intent = new Intent(getApplicationContext(),
                        Sales_Calculation.class);
                // Create a bundle object
                Bundle b = new Bundle();
                b.putStringArray("selectedItems", outputStrArr);
                // Add the bundle to the intent.
                intent.putExtras(b);
                intent.putExtra("schemes",false);
                intent.putExtra("userid", userid);
                // start the ResultActivity
                startActivity(intent);

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
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.Admin: {
                if (userid.contains("develop")) {
                    Intent intent2 = new Intent(Regular_Sales.this, Homepage.class);
                    intent2.putExtra("userid", userid);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.customer: {
                Intent intent2 = new Intent(Regular_Sales.this, CustomerPage.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            }
            case R.id.home: {
                Intent intent2 = new Intent(Regular_Sales.this, Home.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            }
            case R.id.marker: {
                Intent intent2 = new Intent(Regular_Sales.this, Location_Find.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;

            }
            case R.id.logout:
               /* Intent intent = new Intent(Regular_Sales.this, Login.class);
                startActivity(intent);
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*/
                session.logoutUser();
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
   /* protected void onPreExecute() {
     SparseBooleanArray checked = itemlist.getCheckedItemPositions();
        for (int i = 0; i < checked.size(); i++) {
            // Item position in adapter
            int position = checked.keyAt(i);
            // Add sport if it is checked i.e.) == TRUE!
            if (checked.valueAt(i))
                selectedItems.add(list_view_main_adapter.getItem(position));
        }
    }*/

    private class  fetchitemname extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Regular_Sales.this);

        }

        @Override
        protected String doInBackground(String... params) {
            String string = "";

            try {

                LOGIN_URL = Itemname_url;
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
                            Toast.makeText(Regular_Sales.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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

                    // info.setCode(jsonObject.optString("ItemCode"));
                    info.setItemId(jsonObject.optString("ItemId"));
                    info.setName(jsonObject.optString("ItemName"));
                    info.setRate(jsonObject.optString("Rate"));
                    info.setStockCount(jsonObject.optString("StockCount"));
                    //info.setUOM(jsonObject.optString("UOM"));
                    // info.setUOMId(jsonObject.optString("UOMId"));
                    ItemnameDetails.add(info);
                    Itemnamelist.add(jsonObject.optString("ItemName"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //  spinner_main = (Spinner) findViewById(R.id.spinner_main);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(Regular_Sales.this, android.R.layout.simple_spinner_item, Itemnamelist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            popupSpinner.setAdapter(adapter);
            popupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0,
                                           View arg1, int position, long arg3) {
                    // TODO Auto-generated method stub
                    parentitemname= ItemnameDetails.get(position).getName();
                    parentitemid = ItemnameDetails.get(position).getItemId();
                    parentitemrate=ItemnameDetails.get(position).getRate();
                    stockcount=ItemnameDetails.get(position).getStockCount();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

    }
   // public boolean onOptionsItemSelected(MenuItem item){
     //   finish();
       // return true;
    //}
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
}

