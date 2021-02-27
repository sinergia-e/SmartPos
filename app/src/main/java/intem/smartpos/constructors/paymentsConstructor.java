package intem.smartpos.constructors;

import java.io.Serializable;

public class paymentsConstructor implements Serializable {
    //id INTEGER PRIMARY KEY AUTOINCREMENT,Client TEXT,amount REAL,date TEXT,Location TEXT,Status TEXT,Aplicated TEXT,User TEXT

    private int id;
    private Double amount;
    private String date;

    public paymentsConstructor(int id, double amount, String date) {
        this.id = id;
        this.amount = amount;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}