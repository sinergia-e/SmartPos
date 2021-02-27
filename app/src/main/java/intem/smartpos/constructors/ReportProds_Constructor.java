package intem.smartpos.constructors;
import java.io.Serializable;


public class ReportProds_Constructor implements Serializable{

    //id ,ProductCode ,Family ,SubFam1 ,SubFam2 ,SubFam3 ,TotalPurchases ,LastPurchaseDate ,
    // LastPurchasePrice ,TotalSales ,AverageSales ,MainSupplier
    private int id;
    private String ProductCode;
    private String DescripProd;
    private String Family;
    private String SubFam1;
    private String SubFam2;
    private String SubFam3;
    private Double TotalPurchases;
    private String LastPurchaseDate;
    private String LastPurchasePrice;
    private Double TotalSales;
    private Double AverageSales;
    private String MainSupplier;
    private Double Existencia;

    public ReportProds_Constructor(int id, String productCode, String descripProd, String family, String subFam1, String subFam2, String subFam3, Double totalPurchases, String lastPurchaseDate, String lastPurchasePrice, Double totalSales, Double averageSales, String mainSupplier, Double existencia) {
        this.id = id;
        ProductCode = productCode;
        DescripProd = descripProd;
        Family = family;
        SubFam1 = subFam1;
        SubFam2 = subFam2;
        SubFam3 = subFam3;
        TotalPurchases = totalPurchases;
        LastPurchaseDate = lastPurchaseDate;
        LastPurchasePrice = lastPurchasePrice;
        TotalSales = totalSales;
        AverageSales = averageSales;
        MainSupplier = mainSupplier;
        Existencia = existencia;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProductCode() {
        return ProductCode;
    }

    public void setProductCode(String productCode) {
        ProductCode = productCode;
    }

    public String getDescripProd() {
        return DescripProd;
    }

    public void setDescripProd(String descripProd) {
        DescripProd = descripProd;
    }

    public String getFamily() {
        return Family;
    }

    public void setFamily(String family) {
        Family = family;
    }

    public String getSubFam1() {
        return SubFam1;
    }

    public void setSubFam1(String subFam1) {
        SubFam1 = subFam1;
    }

    public String getSubFam2() {
        return SubFam2;
    }

    public void setSubFam2(String subFam2) {
        SubFam2 = subFam2;
    }

    public String getSubFam3() {
        return SubFam3;
    }

    public void setSubFam3(String subFam3) {
        SubFam3 = subFam3;
    }

    public Double getTotalPurchases() {
        return TotalPurchases;
    }

    public void setTotalPurchases(Double totalPurchases) {
        TotalPurchases = totalPurchases;
    }

    public String getLastPurchaseDate() {
        return LastPurchaseDate;
    }

    public void setLastPurchaseDate(String lastPurchaseDate) {
        LastPurchaseDate = lastPurchaseDate;
    }

    public String getLastPurchasePrice() {
        return LastPurchasePrice;
    }

    public void setLastPurchasePrice(String lastPurchasePrice) {
        LastPurchasePrice = lastPurchasePrice;
    }

    public Double getTotalSales() {
        return TotalSales;
    }

    public void setTotalSales(Double totalSales) {
        TotalSales = totalSales;
    }

    public Double getAverageSales() {
        return AverageSales;
    }

    public void setAverageSales(Double averageSales) {
        AverageSales = averageSales;
    }

    public String getMainSupplier() {
        return MainSupplier;
    }

    public void setMainSupplier(String mainSupplier) {
        MainSupplier = mainSupplier;
    }

    public Double getExistencia() {
        return Existencia;
    }

    public void setExistencia(Double existencia) {
        Existencia = existencia;
    }
}
