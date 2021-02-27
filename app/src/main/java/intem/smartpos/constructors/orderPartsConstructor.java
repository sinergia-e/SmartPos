package intem.smartpos.constructors;

import java.io.Serializable;

public class orderPartsConstructor implements Serializable{

    //id INTEGER ,OrderId INTEGER,Quantity REAL,ProdId TEXT,Price REAL,Descrip TEXT,Export INTEGER
    private int Id;
    private int OrderId;
    private Double Qty;
    private Double QtyPr;
    private String ProdId;
    private float Price;
    private String Descrip;
    private String DescripPr;
    private Double Discount;

    public orderPartsConstructor(int id, int orderId, Double qty, String prodId, float price, String descrip,Double discount,Double qtyPr,String descripPr) {
        this.Id = id;
        this.OrderId = orderId;
        this.Qty = qty;
        this.QtyPr = qtyPr;
        this.ProdId = prodId;
        this.Price = price;
        this.Descrip = descrip;
        this.DescripPr = descripPr;
        this.Discount = discount;
    }

    public int getId() {
        return Id;
    }

    public int getOrderId() {
        return OrderId;
    }

    public void setOrderId(int orderId) {
        OrderId = orderId;
    }

    public Double getQty() {
        return Qty;
    }

    public void setQty(Double qty) {
        Qty = qty;
    }

    public String getProdId() {
        return ProdId;
    }

    public void setProdId(String prodId) {
        ProdId = prodId;
    }

    public float getPrice() {
        return Price;
    }

    public void setPrice(float price) {
        Price = price;
    }

    public String getDescrip() {
        return Descrip;
    }

    public void setDescrip(String descrip) {
        Descrip = descrip;
    }

    public Double getDiscount() {
        return Discount;
    }

    public void setDiscount(Double discount) {
        Discount = discount;
    }

    public Double getQtyPr() {
        return QtyPr;
    }

    public void setQtyPr(Double qtyPr) {
        QtyPr = qtyPr;
    }

    public String getDescripPr() {
        return DescripPr;
    }

    public void setDescripPr(String descripPr) {
        DescripPr = descripPr;
    }
}
