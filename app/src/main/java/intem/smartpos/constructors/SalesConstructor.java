package intem.smartpos.constructors;

import java.io.Serializable;

public class SalesConstructor implements Serializable{

    private int id;
    private String Client;
    private String Status;
    private Double Import;

    public SalesConstructor(int id, String client, String status, Double anImport) {
        this.id = id;
        Client = client;
        Status = status;
        Import = anImport;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClient() {
        return Client;
    }

    public void setClient(String client) {
        Client = client;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public Double getImport() {
        return Import;
    }

    public void setImport(Double anImport) {
        Import = anImport;
    }
}
