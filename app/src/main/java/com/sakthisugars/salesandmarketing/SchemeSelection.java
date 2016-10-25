package com.sakthisugars.salesandmarketing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

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
import java.util.List;

public class SchemeSelection extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private List<Scheme_data> content_list = new ArrayList<>();
    private RecyclerView recyclerView;
    private Scheme_data_adapter Adapter;
    ProgressDialog progressDialog;
    Button regular_sales, selected_scheme;
    // String Scheme_url= "http://192.168.1.28:900/Android.svc/GetSchemeList/Develop/M";
    //  String Scheme_url="http://192.168.1.28:900/Android.svc/SaveSchemeDetail?UserId=DEVELOP&SchemeName=0&Description=0&Type=0&ParentId=0&ItemId=0&Rate=0&PerDiscount=0&Discount=0&FreeItem=0&Key=C&SchemeDetId=0&SchemeUId=0";
    String Scheme_url = "http://192.168.1.28:900//Android.svc/SaveSchemeDetail?UserId=DEVELOP&SchemeName=111&Description=111&Type=0&ParentId=0&ItemId=0&Rate=0&PerDiscount=0&Discount=0&FreeItem=0&Key=C&SchemeDetId=0&SchemeUId=0";
    //String Scheme_url="http://192.168.1.28:900/Android.svc/SaveSchemeDetail?UserId=DEVELOP&SchemeName=0&Description=0&Type=0&ParentId=0&ItemId=0&Rate=0&PerDiscount=0&Discount=0&FreeItem=0&Key=C&SchemeDetId=0&SchemeUId=0";
    //String Scheme_url1="http://192.168.1.28:900/Android.svc/SaveSchemeDetail?UserId=DEVELOP&SchemeName=0&Description=0&Type=0&ParentId=0&ItemId=0&Rate=0&PerDiscount=0&Discount=0&FreeItem=0&Key=C&SchemeDetId=0&SchemeUId=24";
    String LOGIN_URL, parentitemid, parentitemname, qtyvalue;
    ArrayAdapter<String> list_view_main_adapter;
    String name;
    DrawerLayout drawer;
    Database_handler database_handler;
    SQLiteDatabase db;
    String userid;
    TextView uname;
    Button payment;
    UserSessionManager session;
    ArrayList<String> payment_data = new ArrayList<>();
    final Context context = this;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheme_selection_drawer);
        // ActionBar actionBar = getActionBar();
        // actionBar.setDisplayHomeAsUpEnabled(true);
        session = new UserSessionManager(getApplicationContext());
        // Intent intent = getIntent();
        //  userid = intent.getStringExtra("userid");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        uname = (TextView) findViewById(R.id.uname);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (session.checkLogin())
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
//........................................................................................
        new fetchitemname().execute("");
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        Adapter = new Scheme_data_adapter(content_list,this);
        //list_view_main_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice, (List<String>) Adapter);
        // recyclerView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        // recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(Adapter);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                final CardView view1 = (CardView) view;
                final Scheme_data feedItem = content_list.get(position);
                if(feedItem.isSelected()){
                    feedItem.setSelected(false);
                    view1.setCardBackgroundColor(getResources().getColor(android.R.color.background_light));
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(SchemeSelection.this);
                    LinearLayout linearLayout = new LinearLayout(SchemeSelection.this);
                    linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                    final View dialogview = LayoutInflater.from(SchemeSelection.this).inflate(R.layout.scheme_qty_selection, linearLayout, false);
                    builder.setView(dialogview);
                    builder.setTitle("Select Quantity");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            feedItem.setSelected(true);
                            EditText paymnt = (EditText) dialogview.findViewById(R.id.quantity);
                            String pymt_data = feedItem.getScheme_name() + "~" + paymnt.getText().toString() + "~" + feedItem.getRate() * Integer.parseInt(paymnt.getText().toString());
                            payment_data.add(pymt_data);
                            view1.setCardBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();


                    // public void onClick (AdapterView<?> arg0, View arg1, int position, long arg3){
                    // Scheme_data feedItem =new  Scheme_data();
                    // Scheme_data_adapter.MyViewHolder holder = (Scheme_data_adapter.MyViewHolder) view.getTag();
                    // int positions = holder.getPosition();
                    // Toast.makeText(SchemeSelection.this, "Please enter is" + position, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
        regular_sales = (Button) findViewById(R.id.sales);
        regular_sales.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Intent intent1 = new Intent(SchemeSelection.this, Regular_Sales.class);
                intent1.putExtra("userid", userid);
                startActivity(intent1);
            }
        });
        payment = (Button) findViewById(R.id.payment);
        payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SchemeSelection.this,Sales_Calculation.class);
                intent.putExtra("schemes",true);
                intent.putExtra("userid", userid);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("Scheme Data",payment_data);
                intent.putExtra("Scheme List",bundle);
                startActivity(intent);
            }
        });
        // prepareSchemeData("one by two","Computer-2","Laptop -1",5,1);
       /* selected_scheme = (Button) findViewById(R.id.select);
        selected_scheme.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String data = "";
                List<Student> stList = ((CardViewDataAdapter) Adapter)
                        .getStudentist();

                for (int i = 0; i < stList.size(); i++) {
                    Student singleStudent = stList.get(i);
                    if (singleStudent.isSelected() == true) {

                        data = data + "\n" + singleStudent.getName().toString();
      /*
       * Toast.makeText( CardViewActivity.this, " " +
       * singleStudent.getName() + " " +
       * singleStudent.getEmailId() + " " +
       * singleStudent.isSelected(),
       * Toast.LENGTH_SHORT).show();
       */
                  /*  }

                }

                Toast.makeText(SchemeSelection.this,
                        "Selected Students: \n" + data, Toast.LENGTH_LONG)
                        .show();
            }
        });*/
        uname.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // dialog.show();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                alertDialogBuilder
                        .setCancelable(false)
                        .setMessage("Are you sure you want to Logout")
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // get user input and set it to result
                                        // edit text
                                        session.logoutUser();
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    boolean flag = false;

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (flag) {
                super.onBackPressed();
            } else {
                Toast.makeText(this, "Press Back Again to Exit", Toast.LENGTH_LONG).show();
                flag = true;
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
                    Intent intent2 = new Intent(SchemeSelection.this, Homepage.class);
                    intent2.putExtra("userid", userid);
                    startActivity(intent2);
                } else {
                    Toast.makeText(this, "Authorised people only", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case R.id.customer: {
                Intent intent2 = new Intent(SchemeSelection.this, CustomerPage.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            }
            case R.id.home: {
                Intent intent2 = new Intent(SchemeSelection.this, Home.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;
            }
            case R.id.marker: {
                Intent intent2 = new Intent(SchemeSelection.this, Location_Find.class);
                intent2.putExtra("userid", userid);
                startActivity(intent2);
                break;

            }
            case R.id.logout:
               /* Intent intent = new Intent(SchemeSelection.this, Login.class);
                startActivity(intent);
                intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;*/
                session.logoutUser();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    void gotodetails() {
        Intent intent = new Intent(getApplicationContext(), Scheme_data.class);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("SchemeSelection Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    /*  public void prepareSchemeData(String name,String main_items, String offer_items, int discount_value, int discount_type) {
            Scheme_data content = new Scheme_data();
            content.setScheme_name(name);
            content.setMain_items(main_items);
            content.setOffer_items(offer_items);
            content.setDiscount_amt(discount_value);
            content.setDiscount_value_type(discount_type);
            content_list.add(content);
            Adapter.notifyDataSetChanged();
        }*/
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);

    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onDoubleTapEvent(MotionEvent e) {
                    return false;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }

    private class fetchitemname extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(SchemeSelection.this);

        }

        @Override
        protected String doInBackground(String... params) {
            String string = "";

            try {

                LOGIN_URL = Scheme_url;
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
                            Toast.makeText(SchemeSelection.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
                    //  ItemDetails info = new ItemDetails();

                    // info.setCode(jsonObject.optString("ItemCode"));
                    // info.setItemId(jsonObject.optString("ItemId"));
                    Scheme_data content = new Scheme_data();
                    content.setScheme_name(jsonObject.optString("SchemeName"));
                    content.setScheme_id(jsonObject.optString("SchemeUId"));
                    content.setMain_items(jsonObject.optString("ParentName"));
                    content.setOffer_items(jsonObject.optString("ItemName"));
                    content.setDiscount_amt(jsonObject.optInt("ValueDiscount"));
                    content.setPer_Discount_amt(jsonObject.optInt("PercentDiscount"));
                    content.setfree_item(jsonObject.optString("FreeItem"));
                    content.setRate(jsonObject.optInt("Rate"));
                    content_list.add(content);
                    Adapter.notifyDataSetChanged();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //  spinner_main = (Spinner) findViewById(R.id.spinner_main);
          /*  ArrayAdapter<String> adapter = new ArrayAdapter<String>(Regular_Sales.this, android.R.layout.simple_spinner_item, Itemnamelist);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            popupSpinner.setAdapter(adapter);
            popupSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0,
                                           View arg1, int position, long arg3) {
                    // TODO Auto-generated method stub
                    parentitemname= Itemname.get(position).getName();
                    parentitemid = Itemname.get(position).getItemId();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });*/

        }

    }
    // public boolean onOptionsItemSelected(MenuItem item){
    //  finish();
    //  return true;
    //}
 /*  @Override
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
