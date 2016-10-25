package com.sakthisugars.salesandmarketing;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by singapore on 28-06-2016.
 */
public class Scheme_data_adapter extends RecyclerView.Adapter<Scheme_data_adapter.MyViewHolder> {
    private List<Scheme_data> content_list;
    private Context context;
    public class MyViewHolder extends RecyclerView.ViewHolder {


        public TextView main_items,offer_items, discount_amt,per_discount,Schemename,Schemeid,freeitem;
        public View view;

        public MyViewHolder(View view) {
            super(view);
            this.view=view;
            main_items = (TextView) view.findViewById(R.id.main_items);
            offer_items = (TextView) view.findViewById(R.id.offer_items);
            discount_amt = (TextView) view.findViewById(R.id.discount_amt);
            per_discount = (TextView) view.findViewById(R.id.perdiscount);
            Schemename = (TextView) view.findViewById(R.id.Schemename);
           // Schemeid = (TextView) view.findViewById(R.id.Schemeid);
            freeitem = (TextView) view.findViewById(R.id.freeitem);
        }
    }
    public Scheme_data_adapter(List<Scheme_data> Scheme_data_list,Context context){
        this.context = context;
        this.content_list=Scheme_data_list;
    }
    @Override
    public Scheme_data_adapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.scheme_selection_row,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String amount="";
        String perdiscount="";
        int perdis;
        final Scheme_data content = content_list.get(position);
        int a =holder.getItemViewType();
        if(a==1){
            holder.view.setBackgroundColor(context.getResources().getColor(android.R.color.holo_orange_light));
        }
      holder.main_items.setText(content.getMain_items());
        holder.offer_items.setText(content.getOffer_items());
        amount = content.getDiscount_amt() + "";
        perdiscount = content.getper_discount() + "";
        perdis=Integer.parseInt(perdiscount);
       /* if (content.getDiscount_amt() == 0) {
            perdiscount = perdiscount + "%";
            holder.discount_amt.setText(perdiscount);
        } else if(content.getper_discount() == 0) {
                amount = "Rs." + amount;
            holder.per_discount.setText(amount);
            }*/
        if (perdis == 0) {
            amount = "Rs." + amount;
            holder.discount_amt.setText(amount);
            holder.per_discount.setText("0");
        } else {
            perdiscount = perdiscount + "%";
            holder.per_discount.setText(perdiscount);
        }
        holder.Schemename.setText(content.getScheme_name());
       // holder.Schemename.setText(content.getScheme_id());
      //  holder.Schemeid.setText(content.getScheme_id());
       // holder.main_items.setText(content.getMain_items());
       // holder.offer_items.setText(content.getOffer_items());
       // holder.per_discount.setText(content.getper_discount());
        //holder.discount_amt.setText(content.getDiscount_amt());
        holder.freeitem.setText(content.getfreeitems());

        }

    @Override
    public int getItemViewType(int position) {
        if(content_list.get(position).isSelected())
        return 1;
        else
            return 0;
    }

    @Override
    public int getItemCount() {
        return content_list.size();
    }
}
