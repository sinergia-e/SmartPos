package intem.smartpos.constructors;
import java.io.Serializable;
import java.util.ArrayList;



public class usersConstructor implements Serializable {
    private int id;
    private String Nick;
    private String Name;
    private String Pass;
    private String Level;
    private Integer OrdersAccess;
    private Integer MovsAccess;
    private Integer ProdsAccess;
    private Integer ClientsAccess;
    private Integer POSAccess;
    private Integer Loged;

    //id,Nick,Name,Password,Level,OrdersAccess,MovsAccess,ProdsAccess,Clients,POS,Loged
    public usersConstructor(int id,String Nick, String Name,String Pass,String Level,Integer OrdersAccess,Integer MovsAccess,Integer ProdsAccess,Integer Clients,Integer POS,Integer Loged ) {
        super();
        this.id = id;
        this.Nick = Nick;
        this.Name = Name;
        this.Pass = Pass;
        this.Level = Level;
        this.OrdersAccess = OrdersAccess;
        this.MovsAccess = MovsAccess;
        this.ProdsAccess = ProdsAccess;
        this.ClientsAccess = Clients;
        this.POSAccess = POS;
        this.Loged = Loged;
    }




    public int getId() {
        return id;
    }

    public String getNick() {
        return Nick;
    }

    public void setNick(String nick) {
        Nick = nick;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPass() {
        return Pass;
    }

    public void setPass(String pass) {
        Pass = pass;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getOrdersAccess() {
        return OrdersAccess;
    }

    public void setOrdersAccess(Integer ordersAccess) {
        OrdersAccess = ordersAccess;
    }

    public Integer getMovsAccess() {
        return MovsAccess;
    }

    public void setMovsAccess(Integer movsAccess) {
        MovsAccess = movsAccess;
    }

    public Integer getProdsAccess() {
        return ProdsAccess;
    }

    public void setProdsAccess(Integer prodsAccess) {
        ProdsAccess = prodsAccess;
    }

    public Integer getClientsAccess() {
        return ClientsAccess;
    }

    public void setClientsAccess(Integer clientsAccess) {
        ClientsAccess = clientsAccess;
    }

    public Integer getPOSAccess() {
        return POSAccess;
    }

    public void setPOSAccess(Integer POSAccess) {
        this.POSAccess = POSAccess;
    }

    public Integer getLoged() {
        return Loged;
    }

    public void setLoged(Integer loged) {
        Loged = loged;
    }
}
