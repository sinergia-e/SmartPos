package intem.smartpos.constructors;

import android.widget.BaseAdapter;

import java.io.Serializable;

/**
 * Created by soluc on 04/10/2017.
 */

public class countsConstructor implements Serializable {

    private int id;
    private int MovId;
    private double Qty;
    private String CodeProd;
    private String Descrip;
    private Integer Export;


    public countsConstructor(Integer id, Integer MovId, double Quantity, String CodeProd,String Descrip,Integer Export) {
        this.id = id;
        this.MovId = MovId;
        this.Qty = Quantity;
        this.CodeProd = CodeProd;
        this.Descrip = Descrip;
        this.Export = Export;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMovId() {
        return MovId;
    }

    public void setMovId(int movId) {
        MovId = movId;
    }

    public double getQty() {
        return Qty;
    }

    public void setQty(double qty) {
        Qty = qty;
    }

    public String getCodeProd() {
        return CodeProd;
    }

    public void setCodeProd(String codeProd) {
        CodeProd = codeProd;
    }

    public String getDescrip() {
        return Descrip;
    }

    public void setDescrip(String descrip) {
        Descrip = descrip;
    }

    public Integer getExport() {
        return Export;
    }

    public void setExport(Integer export) {
        Export = export;
    }
}