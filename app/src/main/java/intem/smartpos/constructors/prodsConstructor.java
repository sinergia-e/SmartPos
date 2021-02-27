package intem.smartpos.constructors;
import java.io.Serializable;
import java.util.ArrayList;


public class prodsConstructor implements Serializable {

    private int id;
    private String Code;
    private String Description;
    private String ProdCategory;
    private String Brand;
    private String Tax;
    private String Exist;
    private double Price;
    private String ExSucursal;
    private int Export;
    private double offer;

    public prodsConstructor(int id, String code, String description, String prodCategory, String brand, String tax, double price , String exist, String ExSucursal , int export, double Offer) {
        this.id = id;
        this.Code = code;
        this.Description = description;
        this.ProdCategory = prodCategory;
        this.Brand = brand;
        this.Tax = tax;
        this.Exist = exist;
        this.Price = price;
        this.ExSucursal = ExSucursal;
        this.Export = export;
        this.offer = Offer;

    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getProdCategory() {
        return ProdCategory;
    }

    public void setProdCategory(String prodCategory) {
        ProdCategory = prodCategory;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getTax() {
        return Tax;
    }

    public void setTax(String tax) {
        Tax = tax;
    }

    public String getExist() {
        return Exist;
    }

    public void setExist(String exist) {
        Exist = exist;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getExSucursal() {
        return ExSucursal;
    }

    public void setExSucursal(String exSucursal) {
        ExSucursal = exSucursal;
    }

    public int getExport() {
        return Export;
    }

    public void setExport(int export) {
        Export = export;
    }

    public double getOffer() {
        return offer;
    }

    public void setOffer(double offer) {
        this.offer = offer;
    }
}

