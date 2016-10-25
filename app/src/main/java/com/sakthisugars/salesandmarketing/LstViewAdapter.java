package com.sakthisugars.salesandmarketing;

/**
 * Created by user on 8/5/2016.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
public class LstViewAdapter extends ArrayAdapter<String> {
    int groupid;
    String[] item_list;
    ArrayList<String> desc;
    Context context;
    String tot = "";

    public LstViewAdapter(Context context, int vg, int id, String[] item_list) {
        super(context, vg, id, item_list);
        this.context = context;
        groupid = vg;
        this.item_list = item_list;

    }

    // Hold views of the ListView to improve its scrolling performance
    static class ViewHolder {
        public TextView textname;
        public TextView textqty;
        public TextView textprice;


    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View rowView = convertView;
        // Inflate the rowlayout.xml file if convertView is null
        if (rowView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(groupid, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textname = (TextView) rowView.findViewById(R.id.txtname);
            viewHolder.textqty = (TextView) rowView.findViewById(R.id.qty);
            viewHolder.textprice = (TextView) rowView.findViewById(R.id.txtprice);
            rowView.setTag(viewHolder);


        }
        // Set text to each TextView of ListView item
        String[] items = item_list[position].split("~");
        ViewHolder holder = (ViewHolder) rowView.getTag();
        holder.textname.setText(items[0]);
        holder.textqty.setText(items[1]);
        holder.textprice.setText(items[2]);
        //String value = items[2];
       // tot = sum(tot + value)
       // holder.textqty.setText(tot);
        return rowView;

}

}

