package com.sakthisugars.salesandmarketing;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

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

import butterknife.InjectView;

public class Login extends AppCompatActivity {
    String value1;
    String username, password;
    ProgressDialog progressDialog;
    int PASSWORD_MIN_LENGTH = 1;
    int PASSWORD_MAX_LENGTH = 8;
    String val;
    String valueY;
    boolean PASSWORD_CONTAINS_SPECIAL_CHARACTERS = false;
    String _url = "http://192.168.1.28:900/android.svc/ValidateUser/";
    String LOGIN_URL;
    String RESET_PASSWORD_URL = "";
    Database_handler database_handler;
    SQLiteDatabase db;
    //Json data keywords
    private UserSessionManager session;
    private String NAME = "UserName";
    private String EMAIL = "email";
    private String PASSWORD = "Password";
    private String PARENT_EMPLOYEE = "parent_employee";
    private String IS_ADMIN = "is_admin";
    @InjectView(R.id.username) EditText name;
    @InjectView(R.id.password) EditText pass;
    @InjectView(R.id.login) ImageView login;
 //   @InjectView(R.id.forgot_password) TextView forgot_password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
      /*  final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // TODO: Your application init goes here.
                session.logoutUser();
            }
        }, 1*60*1000);*/
        session = new UserSessionManager(getApplicationContext());
        name = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.password);
        login = (ImageView) findViewById(R.id.login);
     // forgot_password = (TextView) findViewById(R.id.forgot_password);
        database_handler = new Database_handler(this, Database_handler.DATABASE_NAME, null, Database_handler.DATABASE_VERSION);
        db = database_handler.getWritableDatabase();
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = name.getText().toString();
                password = pass.getText().toString();

                if (username.length() == 0) {
                    Toast.makeText(Login.this, "Please enter the username", Toast.LENGTH_LONG).show();
                } else if (password.length() < PASSWORD_MIN_LENGTH) {
                    Toast.makeText(Login.this, "Password is too small, Please check", Toast.LENGTH_LONG).show();
                } else if (password.length() > PASSWORD_MAX_LENGTH) {
                    Toast.makeText(Login.this, "Password is too large, Please check", Toast.LENGTH_LONG).show();
                } else if (PASSWORD_CONTAINS_SPECIAL_CHARACTERS) {
                    for (int i = 0; i < password.length(); i++) {
                        char check = password.charAt(i);
                        if (!(32 < check && check < 48 || 57 < check && check < 65 || 90 < check && check < 97 || 123 < check && check < 127)) {
                            Toast.makeText(Login.this, "Password must contain special characters, Please check", Toast.LENGTH_LONG).show();
                        }
                    }
                } else {
                    Employee employee = database_handler.Search_employee(db, username);
                    new connect().execute("");


                    /* if (employee == null) {
                         new connect().execute(LOGIN_URL+username+"/"+password);
                    } else {
                        Employee.sname = employee.name;
                        Employee.semail = employee.email;
                        Employee.sparent_employee = employee.parent_employee;
                        Employee.sis_admin = employee.is_admin;
                        Employee.spassword = employee.password;
                        if (password.equals(employee.password)) {
                            Intent intent = new Intent(Login.this, Homepage.class);
                            finish();
                            startActivity(intent);
                        }
                    }
                    if (username.equals("srinath") && password.equals("12345678")) {
                        Employee.sname = "srinath";
                        Employee.spassword = "12345678";
                        Employee.sis_admin = 1;
                        Employee.sparent_employee = "Boss";
                        Employee.semail = "srnthsrdhrn1@gmail.com";
                        database_handler.Store_employee_data(db, "srinath", "12345678", 1, "srnthsrdhrn1@gmail.com", "Boss");
                        finish();
                        startActivity(new Intent(Login.this, Homepage.class));
                    } else {
                        if (username.equals("employee") && password.equals("123456")) {
                        Employee.sname = "srinath123";
                        Employee.spassword = "123456";
                        Employee.sis_admin = 0;
                        Employee.sparent_employee = "srinath";
                        Employee.semail = "srnthsrdhrn2@gmail.com";
                            finish();
                        startActivity(new Intent(Login.this,Transaction.class));
                    } else {
                        Toast.makeText(Login.this, "Incorrect username and password", Toast.LENGTH_LONG).show();
                    }
                }
                    */
                }
            }

        });

       /* forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                LinearLayout linearLayout = new LinearLayout(Login.this);
                linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                View view = View.inflate(Login.this, R.layout.forgot_password_dialog, linearLayout);
                builder.setTitle("Forgot Password")
                        .setView(view);
                final AlertDialog dialog = builder.create();
                dialog.show();
                Button confirm = (Button) view.findViewById(R.id.confirm);
                final EditText email = (EditText) view.findViewById(R.id.email_id);
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email_id = email.getText().toString();
                        //new connect().execute(RESET_PASSWORD_URL+email_id);
                        email.setText("");
                        Toast.makeText(Login.this, "Your password has been sent to your mail", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });


            }
        });*/


    }

    private class connect extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(Login.this);
            progressDialog.setTitle("Processing");
            progressDialog.setMessage("Please wait");
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String string = "";
           /* try {
                 URL url = new URL(LOGIN_URL);
                URLConnection e = url.openConnection();
                BufferedReader reader = new BufferedReader(new InputStreamReader(e.getInputStream()));
                String line =  reader.readLine();
                return  line;
            }catch (Exception var5) {
                Log.d("Common Json Exception", var5.toString());
                return "";
            }*/
            try {
                //URL url = new URL(params[0]);
                String Combine_txt = username + "/" + password;
                LOGIN_URL = _url + Combine_txt;
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
                            Toast.makeText(Login.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
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
                Log.v("The ResultJson", String.valueOf(jsonObject));

            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();

            if (jsonObject != null) {
                try {
                    //JSONObject jsonResultObject = new JSONObject(String.valueOf(jsonObject));
                  //  String value = jsonResultObject.getString("ValidateUserResult");
                    JSONObject jsonResultObject = new JSONObject(String.valueOf(jsonObject));
                      String value = jsonResultObject.getString("ValidateUserResult");
                    Log.v("OOOOOOOOOOOOOOOOOOOOOOO", value);

                    // JSONObject result = jsonObject.getJSONObject(String.valueOf(i));
                    // String value = jsonObject.getString("ValidateUserResult");
                    String result = value;//String.valueOf(value);
                    Log.v("reeesuuultttttttttttttt", result);
                    String a = "Invalid User";
                    String b ="User Name Does Not Exist...&N";

                    if (result.contains(a)) {
                        Toast.makeText(Login.this, "Invalid password", Toast.LENGTH_LONG).show();
                        pass.setText("");
                        pass.findFocus();

                    }
                    else if (result.contains(b)){
                        Toast.makeText(Login.this, "Invalid user name", Toast.LENGTH_LONG).show();
                        name.setText("");
                        name.findFocus();

                    }
                    else {

                        String str = "";
                        String[] array = value.split("&");
                        for (int i = 0; i < array.length; i++) {
                            //str += (array[j]) + "\n";
                            //if(array[j]=="Y")
                            String valuea = array[0];
                            valueY = array[1];
                        }


                        // char val=char.valueOf(valueY);
                        char val = valueY.charAt(0);
                        //Log.v("OOOOOOOOOOO111111111111", String.valueOf(val));
                    /*String name = jsonObject.getString(NAME);
                    String password=jsonObject.getString(PASSWORD);
                    String email=jsonObject.getString(EMAIL);
                    String parent_employee=jsonObject.getString(PARENT_EMPLOYEE);
                    int is_admin=jsonObject.getInt(IS_ADMIN);
                     Employee.sname = "srinath";
                        Employee.semail = "srnthsrdhrn1@gmail.com";
                        Employee.sparent_employee = "Boss";
                        Employee.sis_admin = 'Y';
                        // Employee.spassword=data.getString(PASSWORD);
                        //database_handler.Store_employee_data(db,name,password,is_admin,email,parent_employee);
                        finish();
                        // if(Employee.sis_admin=='Y')*/
                        Employee.sis_admin = val;
                        if (val == 'Y') {
                            session.createUserLoginSession(String.valueOf(username));

                            // Starting MainActivity
                            Intent i = new Intent(getApplicationContext(), Homepage.class);

                            //i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                           // i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            // Add new Flag to start new Activity
                          //  i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                          //  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);

                            finish();
                            //startActivity(new Intent(Login.this,Homepage.class));
                           /* Intent intent = new Intent(Login.this, Homepage.class);
                            intent.putExtra("userid", username);
                            startActivity(intent);*/
                            //  EditText editText = (EditText) findViewById(R.id.username);
                            //  String uname = editText.getText().toString();
                            //  intent.putExtra("userid", uname);
                            //  Intent intent1 = getIntent();
                            // String username = intent1.getStringExtra("userid");
                            // Log.v("OOOOOOOOOOOOvallllllll", String.valueOf(username));
                            // startActivity(intent);
                            // startActivity(new Intent(Login.this, Homepage.class));

                        } else {
                            session.createUserLoginSession(String.valueOf(username));

                            // Starting MainActivity
                            Intent i = new Intent(getApplicationContext(), Transaction.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            // Add new Flag to start new Activity
                            i.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            name.setText("");
                            pass.setText("");
                            name.findFocus();
                            ////   /.m n
                            startActivity(i);

                            finish();
                           // startActivity(new Intent(Login.this, Transaction.class));
                             /*Intent intent = new Intent(Login.this, Transaction.class);
                             intent.putExtra("userid", username);
                             startActivity(intent);*/
                        }
                    }
                } catch (JSONException e) {
                    Toast.makeText(Login.this, "Error Retrieving data, Please try again", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(Login.this, "Incorrect Username and Password or Connect to Internet", Toast.LENGTH_LONG).show();
            }
        }
    }


      /*@Override
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
                    String value = jsonResultObject.getString("ValidateUserResult");
                   Log.v("OOOOOOOOOOOOOOOOOOOOOOO", String.valueOf(value));

                    // JSONObject result = jsonObject.getJSONObject(String.valueOf(i));
                    // String value = jsonObject.getString("ValidateUserResult");
                    String a="Invalid User";
                    String b="";
                    if (value == b||value==a ) {

                        Toast.makeText(Login.this, "Invalid UserData, Please try again", Toast.LENGTH_LONG).show();
                    } else {
                        String str = "";
                        String[] array = value.split("&");
                        for (int i = 0; i < array.length; i++) {
                            //str += (array[j]) + "\n";
                            //if(array[j]=="Y")
                            String valuea = array[0];
                            valueY = array[1];

                        }
                    }
                        // char val=char.valueOf(valueY);
                        char val = valueY.charAt(0);
                        //Log.v("OOOOOOOOOOO111111111111", String.valueOf(val));
                    /*String name = jsonObject.getString(NAME);
                    String password=jsonObject.getString(PASSWORD);
                    String email=jsonObject.getString(EMAIL);
                    String parent_employee=jsonObject.getString(PARENT_EMPLOYEE);
                    int is_admin=jsonObject.getInt(IS_ADMIN);*/
                      /*  Employee.sname = "srinath";
                        Employee.semail = "srnthsrdhrn1@gmail.com";
                        Employee.sparent_employee = "Boss";
                        Employee.sis_admin = 'Y';
                        // Employee.spassword=data.getString(PASSWORD);
                        //database_handler.Store_employee_data(db,name,password,is_admin,email,parent_employee);
                        finish();
                        // if(Employee.sis_admin=='Y')*/
                       /* Employee.sis_admin = 'N';
                        if (val == 'Y') {
                            startActivity(new Intent(Login.this, Homepage.class));
                        } else
                            startActivity(new Intent(Login.this, Transaction.class));
                    }catch(JSONException e){
                        Toast.makeText(Login.this, "Error Retrieving data, Please try again", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(Login.this, "Incorrect Username or Password", Toast.LENGTH_LONG).show();
                }
            }
        }*/

        @Override
        protected void onDestroy() {
            if (progressDialog != null)
                if (progressDialog.isShowing())
                    progressDialog.dismiss();
            super.onDestroy();
        }

        boolean flag = true;

        @Override
        public void onBackPressed() {
            if (flag) {
                super.onBackPressed();
            } else {
                Toast.makeText(Login.this, "Press Back Again to Exit", Toast.LENGTH_LONG).show();
                flag = true;
            }
            }


    }

