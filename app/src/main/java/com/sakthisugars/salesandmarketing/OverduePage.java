package com.sakthisugars.salesandmarketing;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class OverduePage extends AppCompatActivity {
    ProgressDialog progressDialog;
    ListView listView;
    String[] list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overdue_page);
        list = new String[]{"You have an overdue of Rs.10000 to be paid"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,R.layout.list_view_row,list);
        listView= (ListView) findViewById(R.id.overdue_list);

    }
    private class connect extends AsyncTask<String,Integer,String> {

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(OverduePage.this);
            progressDialog.setTitle("Processing");
            progressDialog.setMessage("Please wait");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setMax(100);
            progressDialog.show();
            super.onPreExecute();
        }
        @Override
        protected String doInBackground(String... params) {
            String string="";
            try {
                URL url = new URL(params[0]);
                try {
                    HttpURLConnection httpURLConnection= (HttpURLConnection)url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.setConnectTimeout(10000);
                    httpURLConnection.setReadTimeout(10000);
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    if(httpURLConnection.getResponseCode()==HttpURLConnection.HTTP_OK) {
                        StringBuilder stringBuilder = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            stringBuilder.append(line);
                            stringBuilder.append("\n");
                            publishProgress(stringBuilder.length(),httpURLConnection.getContentLength());
                        }
                        string = stringBuilder.toString();
                        reader.close();
                        inputStream.close();
                    }
                    httpURLConnection.disconnect();
                }catch (SocketTimeoutException e){
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OverduePage.this, "Slow Internet Connection \n Check your connection and Try Again", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }catch (IOException e) {
                    e.printStackTrace();
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            return string;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            float data_received=values[0];
            float total=values[1];
            float progress=data_received/total*100;
            progressDialog.setProgress((int)progress);
            super.onProgressUpdate(values);
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            progressDialog.dismiss();
        }
    }
    @Override
    protected void onDestroy() {
        if(progressDialog!=null)
            if(progressDialog.isShowing())
                progressDialog.dismiss();
        super.onDestroy();
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

}
