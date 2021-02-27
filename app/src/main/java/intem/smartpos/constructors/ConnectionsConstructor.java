package intem.smartpos.constructors;

import java.io.Serializable;

public class ConnectionsConstructor implements Serializable {

    private int id;
    private String name;
    private String serverAdress;

    public ConnectionsConstructor(int id, String name, String serverAdress) {
        this.id = id;
        this.name = name;
        this.serverAdress = serverAdress;
    }

    public int getId() {
        return id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerAdress() {
        return serverAdress;
    }

    public void setServerAdress(String serverAdress) {
        this.serverAdress = serverAdress;
    }
}
