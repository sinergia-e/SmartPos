package intem.smartpos.constructors;
import java.io.Serializable;

public class clientsConstructor implements Serializable {

    //id ,CodeMyBusiness ,Name ,Export ,Street ,Colony , City ,CP ,RFC ,Email, Debt
    private int id;
    private String CodeMyBusiness;
    private String NameClient;
    private int Export;
    private String StreetClient;
    private String ColonyClient;
    private String CityClient;
    private String CpClient;
    private String RFCClient;
    private String EmailClient;
    private Double Debt;

    //id INTEGER,CodeMyBusiness TEXT,Name TEXT,Export INTEGER,Street TEXT,Colony TEXT, City TEXT,CP TEXT,RFC TEXT,Email TEXT,Debt REAL
    public clientsConstructor(int id, String codeMyBusiness, String nameClient,int Export, String streetClient, String colonyClient, String cityClient, String cpClient, String RFCClient, String emailClient, Double debt) {
        this.id = id;
        CodeMyBusiness = codeMyBusiness;
        NameClient = nameClient;
        Export = Export;
        StreetClient = streetClient;
        ColonyClient = colonyClient;
        CityClient = cityClient;
        CpClient = cpClient;
        this.RFCClient = RFCClient;
        EmailClient = emailClient;
        Debt = debt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodeMyBusiness() {
        return CodeMyBusiness;
    }

    public void setCodeMyBusiness(String codeMyBusiness) {
        CodeMyBusiness = codeMyBusiness;
    }

    public String getNameClient() {
        return NameClient;
    }

    public void setNameClient(String nameClient) {
        NameClient = nameClient;
    }

    public String getStreetClient() {
        return StreetClient;
    }

    public void setStreetClient(String streetClient) {
        StreetClient = streetClient;
    }

    public String getColonyClient() {
        return ColonyClient;
    }

    public void setColonyClient(String colonyClient) {
        ColonyClient = colonyClient;
    }

    public String getCityClient() {
        return CityClient;
    }

    public void setCityClient(String cityClient) {
        CityClient = cityClient;
    }

    public String getCpClient() {
        return CpClient;
    }

    public void setCpClient(String cpClient) {
        CpClient = cpClient;
    }

    public String getRFCClient() {
        return RFCClient;
    }

    public void setRFCClient(String RFCClient) {
        this.RFCClient = RFCClient;
    }

    public String getEmailClient() {
        return EmailClient;
    }

    public void setEmailClient(String emailClient) {
        EmailClient = emailClient;
    }

    public Double getDebt() {
        return Debt;
    }

    public void setDebt(Double debt) {
        Debt = debt;
    }
}
