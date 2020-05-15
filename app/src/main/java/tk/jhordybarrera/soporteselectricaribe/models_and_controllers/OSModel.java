package tk.jhordybarrera.soporteselectricaribe.models_and_controllers;

public class OSModel {
    private String nic;
    private String os;

    public OSModel() {
        this.nic="";
        this.os="";
    }

    public OSModel(String nic, String os) {
        this.nic = nic;
        this.os = os;
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
