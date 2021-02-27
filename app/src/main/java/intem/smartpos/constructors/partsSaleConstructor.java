package intem.smartpos.constructors;
import android.util.EventLogTags;

import java.io.Serializable;
import java.util.ArrayList;


public class partsSaleConstructor implements Serializable{

    private int Id;
    private int SaleID;
    private String ProdCode;
    private Double Qty;
    private Double Price;
    private Integer PriceList;
    private Double Discount;
    private String ProdDescrip;

    public partsSaleConstructor(int id, int saleID, String prodCode, Double qty, Double price, Integer priceList, Double discount, String prodDescrip) {
        Id = id;
        SaleID = saleID;
        ProdCode = prodCode;
        Qty = qty;
        Price = price;
        PriceList = priceList;
        Discount = discount;
        ProdDescrip = prodDescrip;
    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public int getSaleID() {
        return SaleID;
    }

    public void setSaleID(int saleID) {
        SaleID = saleID;
    }

    public String getProdCode() {
        return ProdCode;
    }

    public void setProdCode(String prodCode) {
        ProdCode = prodCode;
    }

    public Double getQty() {
        return Qty;
    }

    public void setQty(Double qty) {
        Qty = qty;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public Integer getPriceList() {
        return PriceList;
    }

    public void setPriceList(Integer priceList) {
        PriceList = priceList;
    }

    public Double getDiscount() {
        return Discount;
    }

    public void setDiscount(Double discount) {
        Discount = discount;
    }

    public String getProdDescrip() {
        return ProdDescrip;
    }

    public void setProdDescrip(String prodDescrip) {
        ProdDescrip = prodDescrip;
    }
}
