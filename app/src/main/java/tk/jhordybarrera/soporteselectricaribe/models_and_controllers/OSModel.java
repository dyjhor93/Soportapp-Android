package tk.jhordybarrera.soporteselectricaribe.models_and_controllers;

public class OSModel {
    private String nic;
    private String os;
    private String user_id;

    public OSModel() {
        this.nic="";
        this.os="";
        this.user_id="";
    }

    public OSModel( String os,String nic,String u_id) {
        this.nic = nic;
        this.os = os;
        this.user_id=u_id;
    }
    public OSModel(String nic) {
        this.nic = nic;
        this.os = "AddOS";
    }


    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }
}
