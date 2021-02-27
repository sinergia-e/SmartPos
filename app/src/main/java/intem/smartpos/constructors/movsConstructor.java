package intem.smartpos.constructors;

import java.io.Serializable;



public class movsConstructor implements Serializable {

    //id ,TypeMov ,DateMov ,User
    private int id;
    private String TypeMov;
    private String DateMov;
    private Integer IdInServer;
    private Integer Folio;
    private String User;

    public movsConstructor(Integer id,String TypeMov,String DateMov,Integer IdInServer,Integer Folio, String User){
        this.id = id;
        this.TypeMov = TypeMov;
        this.DateMov = DateMov;
        this.IdInServer = IdInServer;
        this.Folio = Folio;
        this.User = User;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTypeMov() {
        return TypeMov;
    }

    public void setTypeMov(String typeMov) {
        TypeMov = typeMov;
    }

    public String getDateMov() {
        return DateMov;
    }

    public void setDateMov(String dateMov) {
        DateMov = dateMov;
    }

    public String getUser() {
        return User;
    }

    public void setUser(String user) {
        User = user;
    }

    public Integer getIdInServer() {
        return IdInServer;
    }

    public void setIdInServer(Integer idInServer) {
        IdInServer = idInServer;
    }

    public Integer getFolio() {
        return Folio;
    }

    public void setFolio(Integer folio) {
        Folio = folio;
    }
}



