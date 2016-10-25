package com.sakthisugars.salesandmarketing;

/**
 * Created by Administrator on 7/15/2016.
 */
public class ItemDetails {
    String ItemCode = null;
    String ItemName = null;
    String ItemId = null;
    String StockCount = null;
    String UOM = null;
    String UOMId = null;
   String Itemrate;


    public String getCode() {
        return ItemCode;
    }
    public void setCode(String ItemCode) {
        this.ItemCode = ItemCode;
    }
    public String getName() {
        return ItemName;
    }
    public void setName(String ItemName) {
        this.ItemName = ItemName;
    }
    public String getItemId() {
        return ItemId;
    }
    public void setItemId(String ItemId) {
        this.ItemId = ItemId;
    }
    public String getStockCount() {
        return StockCount;
    }
    public void setStockCount(String StockCount) {
        this.StockCount = StockCount;
    }
    public String getUOM() {
        return UOM;
    }
    public void setUOM(String UOM) {
        this.UOM = UOM;
    }
    public String getUOMId() {
        return UOMId;
    }
    public void setUOMId(String UOMId) {
        this.UOMId = UOMId;
    }
    public String getRate(){return Itemrate;}
    public void setRate(String Itemrate){ this.Itemrate=Itemrate;}
}

