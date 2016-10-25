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
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
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

public class SchemeDesgin extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawer;
    Button add_item_main, add_item_offer;
    RadioButton percent, amount;
    EditText txtname, txtdescription, qty_main, qty_offer,txtperdiscount,txtamt_discount,txtfree_item,value_offer;
    TextView uname;
    ListView listView_main, listView_offer;
    Spinner spinner_main, spinner_offer,mySpinner;
    ImageView save, cancel;
    Button update,delete;
    Database_handler database_handler;
    SQLiteDatabase db;
    String result;
    String userid;
    ProgressDialog progressDialog;
    String Itemname_url = "http://192.168.1.28:900/Android.svc/GetItemList/Develop/I";
    String Itemlist_url = "http://192.168.1.28:900/Android.svc/GetSchemeTypeList?UserId=Develop&Key=T";
    public ArrayList<Schemetype> SchemetypeList = new ArrayList<Schemetype>();
    public ArrayList<String> ItemList = new ArrayList<String>();
    String save_url = "http://192.168.1.28:900/Android.svc/SaveSchemeDetail?";
   // String delete_url = "http://192.168.1.28:900/Android.svc/SaveSchemeMaster?";
    String LOGIN_URL,offeritem="",mainitem="",username;
    String  itemid,parentitemid,offeritemid,offertype,Schemename, description,per_discount,discount_amt,freeitems;
    int perdisount_amt,itemamt;
   // ItemDetails[] itemDetails;
    public ArrayList<ItemDetails> Itemname = new ArrayList<ItemDetails>();
    public ArrayList<String> Itemnamelist = new ArrayList<String>();
    ArrayList<ItemDetails> item_list;
    ArrayList<String> list_main;
   // ArrayList<String> list_main_id;
    ArrayAdapter<String> list_view_main_adapter;
    ArrayList<String> list_offer;
   // ArrayList<String> list_offer_id;
    ArrayAdapter<String> list_view_offer_adapter;
    ArrayList<String> main_item;
    ArrayList<String> offer_item;
    ArrayAdapter<String> view_mainitem_adapter;
    ArrayAdapter<String> view_offer_adapter;
    UserSessionManager session;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheme_master);
        Intent intentval = getIntent();
        userid = intentval.getStringExtra("userid");
        session = new UserSessionManager(getApplicationContext());
        //Initializing Database
        database_handler = new Database_handler(this, Database_handler.DATABASE_NAME, null, Database_handler.DATABASE_VERSION);
        db = database_handler.getWritableDatabase();
        new fetchtype().execute("");
        //Initializing views
        add_item_main = (Button) findViewById(R.id.add_item_main);
        add_item_offer = (Button) findViewById(R.id.add_item_offer);
       // percent = (RadioButton) findViewById(R.id.percent);
       // amount = (RadioButton) findViewById(R.id.amount);
       // if (percent.isChecked()) {
          // offertype.contains("1");
         //   amount.setChecked(false);
          //  percent.setChecked(false);
       // }  if (amount.isChecked()){
           // offertype.contains("0");
           // percent.setChecked(false);
      //  }
       // value_offer = (EditText) findViewById(R.id.value);
        txtname = (EditText) findViewById(R.id.scheme_name);
        txtdescription = (EditText) findViewById(R.id.description);
        txtfree_item = (EditText) findViewById(R.id.free_items);
        txtperdiscount = (EditText) findViewById(R.id.per_discount);
        txtamt_discount = (EditText) findViewById(R.id.discount_amount);
        qty_main = (EditText) findViewById(R.id.quantity_main);
        qty_main.setText("0");
        qty_offer = (EditText) findViewById(R.id.quantity_offer);
        qty_offer.setText("0");
        listView_main = (ListView) findViewById(R.id.listView);
        listView_offer = (ListView) findViewById(R.id.listView_offer);
        spinner_main = (Spinner) findViewById(R.id.spinner_main);
        spinner_offer = (Spinner) findViewById(R.id.spinner_offer);
       // scheme_id = (EditText) findViewById(R.id.scheme_id);
        //Setting adapter for spinners
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.item_stocks, R.layout.list_view_row);
        spinner_main.setAdapter(adapter);
        spinner_offer.setAdapter(adapter);

                //initializing views for the footer buttons
        save = (ImageView) findViewById(R.id.save);
        update = (Button) findViewById(R.id.update);
        cancel = (ImageView) findViewById(R.id.cancel);
        delete = (Button) findViewById(R.id.delete);
       // ........................................menu item........................................
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
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemTextColor(ColorStateList.valueOf(Color.BLUE));
        navigationView.setItemIconTintList(ColorStateList.valueOf(Color.BLACK));
        navigationView.setBackgroundColor(Color.WHITE);
        navigationView.setNavigationItemSelectedListener(this);
     //................................manu item..........................................................
        new fetchitemname().execute("");
        save.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                try {
                Schemename = txtname.getText().toString();
                description = txtdescription.getText().toString();
                per_discount = txtperdiscount.getText().toString();
               // Log.v("The new Perdis is", per_discount);
                //final int per_dis = Integer.parseInt(per_discount);
                discount_amt = txtamt_discount.getText().toString();
              //  final int dis_amt = Integer.parseInt(discount_amt);
               // Log.v("The new amount is", discount_amt);
              // freeitems = txtfree_item.getText().toString();
               // if (Schemename.length()==0 || description.length()==0 || per_discount.length()==0 || discount_amt.length()==0 || freeitems .length()==0) {
                  //  Toast.makeText(SchemeDesgin.this, "Fill the Details Properly", Toast.LENGTH_LONG).show();
               // } else {
                    final int per_dis = Integer.parseInt(per_discount);
                    final int dis_amt = Integer.parseInt(discount_amt);
                    if (per_dis > 0) {
                        if (dis_amt == 0) {
                            new save().execute("");
                        } else {
                            Toast.makeText(SchemeDesgin.this, "Please enter 0 into dicount_amt", Toast.LENGTH_LONG).show();
                        }

                    } else if (dis_amt > 0) {
                        new save().execute("");
                    } else {
                        Toast.makeText(SchemeDesgin.this, "Please enter non zero into dicount_amt", Toast.LENGTH_LONG).show();
                    }
               // }
            }
                catch (NumberFormatException e){
                  e.printStackTrace();
                    //Toast.makeText(SchemeDesgin.this, (CharSequence) e, Toast.LENGTH_LONG).show();
                }
        }

        });
        cancel.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {
                list_view_main_adapter.clear();
                list_view_offer_adapter.clear();
                txtname.setText("");
                txtamt_discount.setText("");
                txtdescription.setText("");
                txtfree_item.setText("");
                txtperdiscount.setText("");
                txtname.findFocus();
            }

        });
        //Setting adapter for the listView_main
        list_main = new ArrayList<>();
         list_view_main_adapter= new ArrayAdapter<String>(this, R.layout.list_view_row, list_main);
        listView_main.setAdapter(list_view_main_adapter);
        //..................................add main_item name into listview.............
        main_item = new ArrayList<>();
        view_mainitem_adapter= new ArrayAdapter<String>(this, R.layout.list_view_row, main_item);
        listView_main.setAdapter(view_mainitem_adapter);
        //................................................................................
        //Setting adapter for the listView_offer
        list_offer = new ArrayList<>();
        list_view_offer_adapter = new ArrayAdapter<String>(this, R.layout.list_view_row, list_offer);
        listView_offer.setAdapter(list_view_offer_adapter);
        //..................................add main_item name into listview.............
        offer_item = new ArrayList<>();
        view_offer_adapter= new ArrayAdapter<String>(this, R.layout.list_view_row, offer_item);
        listView_offer.setAdapter(view_offer_adapter);
        //................................................................................

        //setting on click listener for the add item button in the main section in the top
        add_item_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = spinner_main.getSelectedItem().toString();
                try {
                    int quantity = Integer.parseInt(qty_main.getText().toString());
                    if(quantity >0){
                        String text_itemname = item + "-" + quantity;
                        String text= parentitemid + "-" + quantity;
                        String check_quantity;
                        for(int i=0;i<list_main.size();i++){
                            String text_item=list_main.get(i);
                            String check="";
                            for(int j=0;j<text_item.length();j++){
                                if(text_item.charAt(j)=='-'){
                                    check=text_item.substring(0,j);
                                    check_quantity=text_item.substring(j+1);
                                    int qty=Integer.parseInt(check_quantity);
                                    if(check.equals(item)){
                                        quantity+=qty;
                                        text= item + "-"+ quantity;
                                        break;
                                    }
                                }
                            }


                        }
                        list_main.add(text);
                        //list_main_id.add(text_id);
                        list_view_main_adapter.notifyDataSetChanged();
                        //.................add parent name...............
                        main_item.add(text_itemname);
                        view_mainitem_adapter.notifyDataSetChanged();
                        //........................................
                        qty_main.setText("0");
                    }
                    else {
                        Toast.makeText(SchemeDesgin.this, "Please Enter a Quantity", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(SchemeDesgin.this, "Please Enter a Quantity", Toast.LENGTH_SHORT).show();

                }
            }
        });

        //Onclick listener for the add item button in the offer section in the bottom
        add_item_offer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String offer_items = spinner_offer.getSelectedItem().toString();
                try {
                    int quantity = Integer.parseInt(qty_offer.getText().toString());
                    if(quantity >0){
                        String text_offer = offer_items + "-" + quantity;
                        String text= offeritemid + "-" + quantity;
                        list_offer.add(text);
                        // list_offer_id.add(text_id);
                        list_view_offer_adapter.notifyDataSetChanged();
                        //.................add offer name...............
                        offer_item.add(text_offer);
                        view_offer_adapter.notifyDataSetChanged();
                        //........................................
                        qty_offer.setText("0");
                    }
                    else {
                        Toast.makeText(SchemeDesgin.this, "Please Enter Quantity", Toast.LENGTH_SHORT).show();
                    }

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    Toast.makeText(SchemeDesgin.this, "Please Enter Quantity", Toast.LENGTH_SHORT).show();
                }
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

    }/*
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scheme_data scheme_data = new Scheme_data();
                for (int i = 0; i < list_main.size(); i++) {
                    scheme_data.main_items += list_main.get(i) + ",";
                    list_main.remove(i);
                }
                for (int i = 0; i < list_offer.size(); i++) {
                    scheme_data.offer_items += list_offer.get(i) + ",";
                    list_offer.remove(i);
                }
                scheme_data.discount_value = Integer.parseInt(value_offer.getText().toString());
                value_offer.setText("");
                if (percent.isChecked()) {
                    scheme_data.discount_value_type = 1;
                    percent.setChecked(false);
                } else {
                    scheme_data.discount_value_type = 0;
                    amount.setChecked(false);
                }
                int id = Integer.parseInt(scheme_id.getText().toString());
                database_handler.Scheme_update(db, scheme_data, id);
            }
        });


        //Checking while the value is entered in the scheme id field
        scheme_id.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = scheme_id.getText().toString();
                if (!text.equals("")) {
                    try {
                        long id = Integer.parseInt(text);
                        Scheme_data scheme_data = database_handler.Scheme_retrieve(db, id);
                        if (scheme_data != null) {
                            String[] scheme_main_items = string_to_item(scheme_data.main_items);
                            for (int i = 0; i < scheme_main_items.length; i++) {
                                list_main.add(i, scheme_main_items[i]);
                            }
                            list_view_main_adapter.notifyDataSetChanged();
                            String[] scheme_offer_items = string_to_item(scheme_data.offer_items);
                            for (int i = 0; i < scheme_offer_items.length; i++) {
                                list_offer.add(i, scheme_offer_items[i]);
                            }
                            list_view_offer_adapter.notifyDataSetChanged();
                            value_offer.setText(scheme_data.discount_value);
                            if (scheme_data.discount_value_type == 1) {
                                percent.setChecked(true);
                            } else {
                                amount.setChecked(true);
                            }
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Scheme_data scheme_data = new Scheme_data();
                for (int i = 0; i < list_main.size(); i++) {
                    scheme_data.main_items += list_main.get(i) + ",";
                }
                list_main.clear();
                list_view_main_adapter.notifyDataSetChanged();
                for (int i = 0; i < list_offer.size(); i++) {
                    scheme_data.offer_items += list_offer.get(i) + ",";
                }
                list_offer.clear();
                list_view_offer_adapter.notifyDataSetChanged();
                scheme_data.discount_value = Integer.parseInt(value_offer.getText().toString());
                value_offer.setText("");
                if (percent.isChecked()) {
                    scheme_data.discount_value_type = 1;
                    percent.setChecked(false);
                } else {
                    scheme_data.discount_value_type = 0;
                    amount.setChecked(false);
                }
                long id = database_handler.Scheme_insertions(db, scheme_data);
                Toast.makeText(Scheme.this, "Scheme ID is " + id, Toast.LENGTH_LONG).show();
            }
        });
    }
    public String[] string_to_item(String s){
        int count = 0;
        for( int i=0; i<s.length(); i++ ) {
            if( s.charAt(i) == ',' ) {
                count++;
            }
        }
        String[] items = new String[count];
        int item_counter=0;
        int occurrence_counter=0;
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)==','){
                items[item_counter]=s.substring(occurrence_counter+1,i-1);
                occurrence_counter++;
            }
        }
        return items;
    }*/
    private class fetch_item_list extends AsyncTask<String,Void,String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray jsonArray= jsonObject.getJSONArray("GetItemListResult");
                item_list = new ArrayList<>();
                for(int i=0;i<jsonArray.length();i++){
                    JSONObject item= jsonArray.getJSONObject(i);
                    int Item_id= Integer.parseInt(item.getString("ItemId"));
                    String Item_name= item.getString("ItemName");
                    ItemDetails itemDetails= new ItemDetails();
                    itemDetails.setName(Item_name);
                    itemDetails.setItemId(Item_id+"");
                    item_list.add(i,itemDetails);
                }
                ArrayList<String> list= new ArrayList<>();
                for(int i=0;i<item_list.size();i++){
                    list.add(i,item_list.get(i).getName());
                }
                spinner_main.setAdapter(new ArrayAdapter<String>(SchemeDesgin.this, android.R.layout.simple_spinner_dropdown_item, list));
                spinner_main.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                        itemid = item_list.get(position).getItemId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                spinner_offer.setAdapter(new ArrayAdapter<String>(SchemeDesgin.this, android.R.layout.simple_spinner_dropdown_item, list));
                spinner_offer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                        itemid = item_list.get(position).getItemId();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        @Override
        protected String doInBackground(String... params) {
            String string = "";

            try {
                URL url = new URL(params[0]);
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
                            Toast.makeText(SchemeDesgin.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
    }
    private class fetchtype extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SchemeDesgin.this);

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
                            Toast.makeText(SchemeDesgin.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
            mySpinner.setAdapter(new ArrayAdapter<String>(SchemeDesgin.this, android.R.layout.simple_spinner_dropdown_item, ItemList));
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
    private String main_item_list;
    private String offer_item_list;
    private class save extends AsyncTask<String, Integer, String> {
        String main_list="";
        String offer_list="";
        @Override
        protected void onPreExecute() {
           /* for (String m : list_main)
            {
                mainitem += m + ",";
            }*/
           for(int i=0;i<list_main.size();i++) {
                        main_list+= list_main.get(i)+",";
                        mainitem=String.valueOf(main_list);
            }

            for(int i=0;i<list_offer.size();i++) {
                offer_list+= list_offer.get(i)+",";
               offeritem=String.valueOf(offer_list);
            }
           /* for (String off : list_offer)
            {
                offeritem += off + ",";
            }*/
        }


        @Override
        protected String doInBackground(String... params) {
         Intent intent = getIntent();
            username = intent.getStringExtra("userid");
            Log.v("The new Username is", String.valueOf(username));
            String string = "";
            try {

                // String Combine_txt1 = "/"+itemid + "/" + qty + "/" + uomid + "/" + rateofitem;
                String Combine_txt = "UserId=" + username + "&" + "SchemeName=" + Schemename + "&" + "Description=" + description + "&" + "Type=" + itemid + "&" + "ParentId="+mainitem+ "&" + "ItemId="+offeritem+"&Rate=12.89&"+"PerDiscount="+per_discount+"&Discount="+discount_amt+"&FreeItem="+freeitems+"&Key=I&SchemeDetId=0";

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
                            Toast.makeText(SchemeDesgin.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
                JSONArray jsonArray = new JSONArray(jsonObject.optString("SaveSchemeDetailResult"));
                Log.i(Item.class.getName(),
                        "Number of entries00000000000 " + jsonArray.length());
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    ItemDetails info = new ItemDetails();
                  result=jsonObject.optString("Message");
                    Toast.makeText(SchemeDesgin.this, result, Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
           /* JSONObject jsonObject = null;
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
                    String value = jsonResultObject.getString("SaveSchemeDetailResult");
                    Log.v("The Result", String.valueOf(value));

                    Toast.makeText(SchemeDesgin.this, value, Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    Toast.makeText(SchemeDesgin.this, (CharSequence) e, Toast.LENGTH_LONG).show();
                }
            }*/
        }
    }
    private class  fetchitemname extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SchemeDesgin.this);

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
                            Toast.makeText(SchemeDesgin.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
                    //  info.setStockCount(jsonObject.optString("StockCount"));
                    //info.setUOM(jsonObject.optString("UOM"));
                    // info.setUOMId(jsonObject.optString("UOMId"));
                    Itemname.add(info);
                    Itemnamelist.add(jsonObject.optString("ItemName"));

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
          //  spinner_main = (Spinner) findViewById(R.id.spinner_main);
            spinner_main.setAdapter(new ArrayAdapter<String>(SchemeDesgin.this, android.R.layout.simple_spinner_dropdown_item, Itemnamelist));
            spinner_main.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                    parentitemid = Itemname.get(position).getItemId();
                    //   txtstockcount.setText(ItemDetailsList.get(position).getStockCount());
                    //uom.setText(ItemDetailsList.get(position).getUOM());
                    // txtuomid.setText(ItemDetailsList.get(position).getUOMId());
                    // uomid = ItemDetailsList.get(position).getUOMId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
            spinner_offer.setAdapter(new ArrayAdapter<String>(SchemeDesgin.this, android.R.layout.simple_spinner_dropdown_item, Itemnamelist));
            spinner_offer.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

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
                    offeritemid = Itemname.get(position).getItemId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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
                        .setSingleChoiceItems(list,3, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        dialog.dismiss();
                                        //startActivity(new Intent(Homepage.this, Item.class));
                                        Intent intent = new Intent(SchemeDesgin.this, Item.class);
                                        intent.putExtra("userid", userid);
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        dialog.dismiss();
                                        // startActivity(new Intent(Homepage.this,SchemeDesgin.class));
                                        Intent intent1 = new Intent(SchemeDesgin.this, SchemeDesgin.class);
                                        intent1.putExtra("userid", userid);
                                        startActivity(intent1);
                                        //startActivity(new Intent(Homepage.this,SchemeDesgin.class));
                                        break;
                                    case 2:
                                        dialog.dismiss();
                                        // startActivity(new Intent(Homepage.this,EmployeeDetails.class));
                                        Intent intent2 = new Intent(SchemeDesgin.this, EmployeeDetails.class);
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
                Intent intent1 = new Intent(SchemeDesgin.this,Homepage.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
                //startActivity(new Intent(Homepage.this,Report.class));
                break;
            case R.id.customer:
                Intent intent2 = new Intent(SchemeDesgin.this,CustomerPage.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            case R.id.settings:
                Intent intent3 = new Intent(SchemeDesgin.this,Settings.class);
                intent3.putExtra("userid", userid);
                startActivity(intent3);
                // startActivity(new Intent(Homepage.this,Settings.class));
                break;
            case R.id.marker:
                Intent intent5 = new Intent(SchemeDesgin.this,Location_Find.class);
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
    }
