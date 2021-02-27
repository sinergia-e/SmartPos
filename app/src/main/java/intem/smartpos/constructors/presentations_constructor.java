package intem.smartpos.constructors;

import java.io.Serializable;


public class presentations_constructor implements Serializable{
    private int Id;
    private String CodePResentation;
    private String DescripPresentation;
    private Double QtyPr;
    private Double PricePr;

    public presentations_constructor(int id, String codePResentation, String descripPresentation,double QtyPresentations,double pricePr ) {
        Id = id;
        CodePResentation = codePResentation;
        DescripPresentation = descripPresentation;
        QtyPr = QtyPresentations;
        PricePr = pricePr;
    }


    public int getId() {
        return Id;
    }

    public String getCodePResentation() {
        return CodePResentation;
    }

    public void setCodePResentation(String codePResentation) {
        CodePResentation = codePResentation;
    }

    public String getDescripPresentation() {
        return DescripPresentation;
    }

    public void setDescripPresentation(String descripPresentation) {
        DescripPresentation = descripPresentation;
    }


    public Double getQtyPr() {
        return QtyPr;
    }

    public void setQtyPr(Double qtyPr) {
        QtyPr = qtyPr;
    }


    public Double getPricePr() {
        return PricePr;
    }

    public void setPricePr(Double pricePr) {
        PricePr = pricePr;
    }
}
