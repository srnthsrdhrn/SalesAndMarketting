package com.sakthisugars.salesandmarketing;

/**
 * Created by singapore on 23-06-2016.
 */
public class Scheme_data {

    private String main_items,offer_items,free_items,Scheme_name,Scheme_id;
    private int discount_amt, discount_value_type,per_discount;
    private boolean isSelected;
    private int rate;

    public int getRate() {return rate;}
    public String getScheme_name() {return Scheme_name;}
    public  String getScheme_id(){return Scheme_id;}
    public String getMain_items() {return main_items;}
    public int getDiscount_amt() {return discount_amt;}
    public int getper_discount(){return per_discount;}
    public int getDiscount_value_type() {return discount_value_type;}
    public String getOffer_items() {return offer_items;}
    public  String getfreeitems(){return free_items;}
    public boolean isSelected() {return isSelected;}

    public void setRate(int rate) {this.rate = rate;}
    public void setDiscount_value_type(int discount_value_type) {this.discount_value_type = discount_value_type;}
    public void setScheme_id(String scheme_id){this.Scheme_id=scheme_id;}
    public void setScheme_name(String Scheme_name){  this.Scheme_name = Scheme_name;}
    public void setMain_items(String main_items) {this.main_items = main_items;}
    public void setOffer_items(String offer_items) {this.offer_items = offer_items;}
    public  void setPer_Discount_amt(int per_discount_amt){this.per_discount=per_discount_amt;}
    public void setDiscount_amt(int discount_amt) {this.discount_amt = discount_amt;}
    public void setfree_item(String freeitems){this.free_items=freeitems;}
    public void setSelected(boolean isSelected) {this.isSelected = isSelected;}


}
