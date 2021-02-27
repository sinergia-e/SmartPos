package intem.smartpos.constructors;

import java.io.Serializable;

public class providersConstructor implements Serializable {

    private int Id;
    private String ProviderCode;
    private String ProviderName;

    public providersConstructor(int id, String providerCode, String providerName) {
        this.Id = id;
        ProviderCode = providerCode;
        ProviderName = providerName;
    }


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
    }

    public String getProviderCode() {
        return ProviderCode;
    }

    public void setProviderCode(String providerCode) {
        ProviderCode = providerCode;
    }

    public String getProviderName() {
        return ProviderName;
    }

    public void setProviderName(String providerName) {
        ProviderName = providerName;
    }
}
