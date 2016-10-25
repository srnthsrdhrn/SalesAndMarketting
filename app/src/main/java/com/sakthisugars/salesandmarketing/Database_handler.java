package com.sakthisugars.salesandmarketing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by singapore on 22-06-2016.
 */
public class Database_handler extends SQLiteOpenHelper {
    public static String DATABASE_NAME = "sales.db";
    public static int DATABASE_VERSION = 4;

    //Employee table details
    private String EMPLOYEE_DATA_TABLE = "employee";
    private String EMPLOYEE_PRIMARY_KEY = "id";
    private String EMPLOYEE_NAME = "username";
    private String EMPLOYEE_IS_ADMIN = "is_admin";
    private String EMPLOYEE_EMAIL = "email";
    private String PARENT_EMPLOYEE = "parent_employee";
    private String EMPLOYEE_PASSWORD = "password";
    private String EMPLOYEE_DATA_CREATE = " CREATE TABLE IF NOT EXISTS " + EMPLOYEE_DATA_TABLE + "(" + EMPLOYEE_PRIMARY_KEY + " INTEGER " +
            "PRIMARY KEY AUTOINCREMENT," + EMPLOYEE_NAME + " TEXT NOT NULL," + EMPLOYEE_IS_ADMIN + " INT NOT NULL ,"
            + EMPLOYEE_EMAIL + " TEXT NOT NULL," + PARENT_EMPLOYEE + " TEXT NOT NULL, " + EMPLOYEE_PASSWORD + " TEXT NOT NULL )";

    //Schemes Table details
    //The scheme table will relate with he Item table
    //All items in the list will only be ids referring to the item table
    //eg:" 1-2;2-8;
    //refers to item number 1 with a quantity of two and item number 2 with a quantity of eight
    //discount value type is stored as integer
    //1-percentage and 2-amount
    private String SCHEMES_DATA_TABLE = "scheme";
    private String SCHEME_PRIMARY_KEY = "scheme_id";
    private String SCHEME_MAIN_ITEMS = "main_items";
    private String SCHEME_OFFER_ITEMS = "offer_items";
    private String SCHEME_DISCOUNT_VALUE = "value";
    private String SCHEME_DISCOUNT_VALUE_TYPE = "type";
    private String SCHEMES_DATA_CREATE = "CREATE TABLE IF NOT EXISTS " + SCHEMES_DATA_TABLE + "( " + SCHEME_PRIMARY_KEY
            + " INTEGER PRIMARY KEY , " + SCHEME_MAIN_ITEMS + " TEXT NOT NULL, " + SCHEME_OFFER_ITEMS + " TEXT NOT NULL, " +
            SCHEME_DISCOUNT_VALUE + " INTEGER NOT NULL, " + SCHEME_DISCOUNT_VALUE_TYPE + " INTEGER NOT NULL )";

    //Employee Tracker Table
    private String TRACKER_DATA_TABLE = "tracker";
    private String TRACKER_PRIMARY_KEY = "time";
    private String TRACKER_LONGITUDE = "longitude";
    private String TRACKER_LATITUDE = "latitude";
    private String TRACKER_DATA_CREATE = "CREATE TABLE IF NOT EXISTS " + TRACKER_DATA_TABLE + "( " + TRACKER_PRIMARY_KEY +
            " TEXT PRIMARY KEY, " + TRACKER_LATITUDE + " TEXT NOT NULL, " + TRACKER_LONGITUDE + " TEXT NOT NULL )";

    // Settings Table to store the User_preferences
    private String SETTINGS_DATA_TABLE = "settings";
    private String SETTINGS_PRIMARY_KEY = "id";
    private String SETTINGS_NAME = "settings_name";
    private String SETTINGS_VALUE = "settings_value";
    private String SETTINGS_TABLE_CREATE = " CREATE TABLE IF NOT EXISTS " + SETTINGS_DATA_TABLE + "( " + SETTINGS_PRIMARY_KEY +
            " INTEGER PRIMARY KEY AUTOINCREMENT , " + SETTINGS_NAME + " TEXT NOT NULL, " + SETTINGS_VALUE + " TEXT NOT NULL )";
    public static String SETTINGS_LOCATION_CHECK_TIMER="timer";
    public static String SETTINGS_REPORTING_TIME="reporting_time";
    public static String SETTINGS_MASTER_PASSWORD="master_password";
    // Separate file to store Home Location
    private String FILE_NAME = "home_data.txt";
    Context context;

    public Database_handler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(EMPLOYEE_DATA_CREATE);
        db.execSQL(SCHEMES_DATA_CREATE);
        db.execSQL(TRACKER_DATA_CREATE);
        db.execSQL(SETTINGS_TABLE_CREATE);
        //Settings table pre creation of settings
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTINGS_LOCATION_CHECK_TIMER,"0");
        contentValues.put(SETTINGS_REPORTING_TIME,"17");
        contentValues.put(SETTINGS_MASTER_PASSWORD,"");
        db.insert(SETTINGS_DATA_TABLE,null,contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        while (oldVersion != newVersion) {
            switch (oldVersion) {
                case 2:
                    db.execSQL(SCHEMES_DATA_CREATE);
                    oldVersion++;
                    break;
                case 3:
                    db.execSQL(TRACKER_DATA_CREATE);
                    oldVersion++;
                    break;
                case 4:
                    db.execSQL(SETTINGS_TABLE_CREATE);
                    //Settings table pre creation of settings
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(SETTINGS_LOCATION_CHECK_TIMER,"0");
                    contentValues.put(SETTINGS_REPORTING_TIME,"17");
                    db.insert(SETTINGS_DATA_TABLE,null,contentValues);
                    oldVersion++;
                case 5:
                    contentValues = new ContentValues();
                    contentValues.put(SETTINGS_MASTER_PASSWORD,"");
                    db.insert(SETTINGS_DATA_TABLE,null,contentValues);
                    oldVersion++;
            }
        }
    }

    /**
     * to be called at the end of each day after updating all the data to the server
     */
    public void Erase_All_Records(SQLiteDatabase db) {
        db.delete(EMPLOYEE_DATA_TABLE, null, null);
        db.delete(SCHEMES_DATA_TABLE, null, null);
        db.delete(TRACKER_DATA_TABLE, null, null);
    }

    /**
     * Employee table Manipulations
     */
    public void Store_employee_data(SQLiteDatabase db, String name, String password, int is_admin, String email, String parent_employee) {
        ContentValues c = new ContentValues();
        c.put(EMPLOYEE_NAME, name);
        c.put(EMPLOYEE_IS_ADMIN, is_admin);
        c.put(EMPLOYEE_EMAIL, email);
        c.put(PARENT_EMPLOYEE, parent_employee);
        c.put(EMPLOYEE_PASSWORD, password);
        db.insert(EMPLOYEE_DATA_TABLE, null, c);
    }

    public Employee Search_employee(SQLiteDatabase db, String username) {
        Cursor c = db.rawQuery("SELECT * FROM " + EMPLOYEE_DATA_TABLE + " WHERE " + EMPLOYEE_NAME + "=?", new String[]{username});
        Employee employee = new Employee();
        if (c.moveToFirst()) {
            employee.name = c.getString(c.getColumnIndex(EMPLOYEE_NAME));
            employee.password = c.getString(c.getColumnIndex(EMPLOYEE_PASSWORD));
            employee.is_admin = c.getInt(c.getColumnIndex(EMPLOYEE_IS_ADMIN));
            employee.parent_employee = c.getString(c.getColumnIndex(PARENT_EMPLOYEE));
            employee.email = c.getString(c.getColumnIndex(EMPLOYEE_EMAIL));
            c.close();
            return employee;
        } else {
            c.close();
            return null;
        }
    }

    public Employee[] Retrieve_employee_data(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT * FROM " + EMPLOYEE_DATA_TABLE, null);
        if (c.moveToFirst()) {
            Employee[] employees = new Employee[c.getCount()];
            for (int i = 0; i < c.getCount(); i++) {
                employees[i] = new Employee();
                employees[i].name = c.getString(c.getColumnIndex(EMPLOYEE_NAME));
                employees[i].email = c.getString(c.getColumnIndex(EMPLOYEE_EMAIL));
                employees[i].password = c.getString(c.getColumnIndex(EMPLOYEE_PASSWORD));
                employees[i].parent_employee = c.getString(c.getColumnIndex(PARENT_EMPLOYEE));
                employees[i].is_admin = c.getInt(c.getColumnIndex(EMPLOYEE_IS_ADMIN));
                c.moveToNext();
            }
            c.close();
            return employees;
        } else
            c.close();
        return null;
    }

    /**
     * Scheme Table Manipulations
     */
    public long Scheme_insertions(SQLiteDatabase db, Scheme_data schemeData) {
        ContentValues c = new ContentValues();
        c.put(SCHEME_MAIN_ITEMS, schemeData.getMain_items());
        c.put(SCHEME_OFFER_ITEMS, schemeData.getOffer_items());
        c.put(SCHEME_DISCOUNT_VALUE, schemeData.getDiscount_amt());
        c.put(SCHEME_DISCOUNT_VALUE_TYPE, schemeData.getDiscount_value_type());
        return db.insert(SCHEMES_DATA_TABLE, null, c);
    }

    public void Scheme_update(SQLiteDatabase db, Scheme_data schemeData, long id) {
        ContentValues c = new ContentValues();
        c.put(SCHEME_MAIN_ITEMS, schemeData.getMain_items());
        c.put(SCHEME_OFFER_ITEMS, schemeData.getOffer_items());
        c.put(SCHEME_DISCOUNT_VALUE, schemeData.getDiscount_amt());
        c.put(SCHEME_DISCOUNT_VALUE_TYPE, schemeData.getDiscount_value_type());
        db.update(SCHEMES_DATA_TABLE, c, SCHEME_PRIMARY_KEY + "=" + id, null);
    }

    public Scheme_data Scheme_retrieve(SQLiteDatabase db, long Scheme_id) {
        Cursor c = db.rawQuery("SELECT *  FROM " + SCHEMES_DATA_TABLE + " WHERE " + SCHEME_PRIMARY_KEY + "=?", new String[]{Scheme_id + ""});
        if (c.moveToFirst()) {
            Scheme_data scheme_data = new Scheme_data();
            scheme_data.setMain_items(c.getString(c.getColumnIndex(SCHEME_MAIN_ITEMS)));
            scheme_data.setOffer_items(c.getString(c.getColumnIndex(SCHEME_OFFER_ITEMS)));
            scheme_data.setDiscount_amt(c.getInt(c.getColumnIndex(SCHEME_DISCOUNT_VALUE)));
            scheme_data.setDiscount_value_type(c.getInt(c.getColumnIndex(SCHEME_DISCOUNT_VALUE_TYPE)));
            c.close();
            return scheme_data;
        } else {
            c.close();
            return null;
        }
    }

    public String[] Scheme_retrieve_all(SQLiteDatabase db) {
        Cursor c = db.rawQuery("SELECT *  FROM " + SCHEMES_DATA_TABLE, null);
        if (c.moveToFirst()) {
            String[] id_list = new String[c.getCount()];
            for (int i = 0; i < c.getCount(); i++) {
                id_list[i] = c.getString(c.getColumnIndex(SCHEME_PRIMARY_KEY));
                c.moveToNext();
            }
            c.close();
            return id_list;
        } else {
            c.close();
            return null;
        }
    }

    /**
     * Tracker Table Manipulations
     */
    public Location getHome(Location location) {
        String data = readFromFile();
        String[] values = string_to_item(data);
        if(values[0]!=null&&values[1]!=null) {
            if (!values[0].equals("") && !values[1].equals("")) {
                location.setLatitude(Double.parseDouble(values[0]));
                location.setLongitude(Double.parseDouble(values[1]));
                return location;
            }
            else
                return null;
        }else
            return null;
    }

    public void setHome(Location location) {
        String data = "Latitude;" + location.getLatitude() + ";Longitude;" + location.getLongitude();
        writeToFile(data);
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

    public void storeLocation(SQLiteDatabase db,String latitude, String longitude, String time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRACKER_PRIMARY_KEY, time);
        contentValues.put(TRACKER_LATITUDE, latitude);
        contentValues.put(TRACKER_LONGITUDE, longitude);
        db.insert(TRACKER_DATA_TABLE, null, contentValues);
    }
    public void removeLocation(SQLiteDatabase db,Location location){
        db.execSQL("DELETE FROM "+ TRACKER_DATA_TABLE+" WHERE "+TRACKER_PRIMARY_KEY+"= "+ location.getTime(),null);
    }

    public Location[] getLocation(SQLiteDatabase db,Location location) {
        Cursor c = db.rawQuery("SELECT * FROM " + TRACKER_DATA_TABLE,null);
        if (c.moveToFirst()) {
            Location[] locations = new Location[c.getCount()];
            for (int i = 0; i < c.getCount(); i++) {
                locations[i] = location;
                locations[i].setLatitude(Double.parseDouble(c.getString(c.getColumnIndex(TRACKER_LATITUDE))));
                locations[i].setLongitude(Double.parseDouble(c.getString(c.getColumnIndex(TRACKER_LONGITUDE))));
                locations[i].setTime(Long.parseLong(c.getString(c.getColumnIndex(TRACKER_PRIMARY_KEY))));
                c.moveToNext();
            }
            c.close();
            return locations;
        } else {
            c.close();
            return null;
        }
    }

    /**
     * Settings Table Manipulations
     * */
    public void ChangeSettings(SQLiteDatabase db,String key, String value){
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTINGS_NAME,key);
        contentValues.put(SETTINGS_VALUE,value);
        db.update(SETTINGS_DATA_TABLE,contentValues,SETTINGS_NAME+"=?",new String[]{key});
    }
    public String GetSettings(SQLiteDatabase db, String key){
        Cursor c = db.rawQuery("SELECT * FROM "+ SETTINGS_DATA_TABLE,null);
        String value=null;
        if(c.moveToFirst()){
            value=c.getString(c.getColumnIndex(key));
        }
        c.close();
        return value;
    }
}
