package intem.smartpos.constructors;

import android.view.ViewDebug;

import java.io.Serializable;

/**
 * id INTEGER PRIMARY KEY AUTOINCREMENT,Client TEXT,Date TEXT,Amount REAl,Location TEXT,Status TEXT,Export INTEGER
 */

public class orderConstructor implements Serializable{

    private int id;
    private String Client;
    private String Date;
    private double Amount;
    private String Location;
    private String Status;
    private int Export;
    private int IdInServer;
    private int ConsecInServer;


    public orderConstructor(int id, String client, String date, double amount, String location, String status, int IdInServer, int ConsecInServer, int export) {
        this.id = id;
        this.Client = client;
        this.Date = date;
        this.Amount = amount;
        this.Location = location;
        this.Status = status;
        this.IdInServer = IdInServer;
        this.ConsecInServer = ConsecInServer;

        this.Export = export;
    }


    public int getId() {
        return id;
    }

    public int getIdInServer() {
        return IdInServer;
    }

    public void setIdInServer(int idInServer) {
        IdInServer = idInServer;
    }

    public int getConsecInServer() {
        return ConsecInServer;
    }

    public void setConsecInServer(int consecInServer) {
        ConsecInServer = consecInServer;
    }


    public String getClient() {
        return Client;
    }

    public void setClient(String client) {
        Client = client;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getLocation() {
        return Location;
    }

    public void setLocation(String location) {
        Location = location;
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

