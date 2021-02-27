package intem.smartpos.constructors;
import java.io.Serializable;

public class purchasesConstructor implements Serializable {

    private int id;
    private String Provider;
    private Float Amount;
    private String Date;
    private String Document;
    private String Status;
    private int Export;


    public purchasesConstructor(int id, String provider, Float amount, String date, String document, String status, int export) {
        this.id = id;
        Provider = provider;
        Amount = amount;
        Date = date;
        Document = document;
        Status = status;
        Export = export;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvider() {
        return Provider;
    }

    public void setProvider(String provider) {
        Provider = provider;
    }

    public Float getAmount() {
        return Amount;
    }

    public void setAmount(Float amount) {
        Amount = amount;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDocument() {
        return Document;
    }

    public void setDocument(String document) {
        Document = document;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public int getExport() {
        return Export;
    }

    public void setExport(int export) {
        Export = export;
    }
}
