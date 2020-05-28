package tk.jhordybarrera.soporteselectricaribe.models_and_controllers;

import java.util.ArrayList;

public class UploadModel {
    private String nic;
    private String os;
    private int user_id;
    private String rutaImagen;

    public UploadModel(String nic, String os, int user_id, String rutaImagen) {
        this.nic = nic;
        this.os = os;
        this.user_id = user_id;
        this.rutaImagen = rutaImagen;
    }
    public UploadModel() {
        this.nic = "";
        this.os = "";
        this.user_id = 0;
        this.rutaImagen = "";
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

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getRutaImagen() {
        return rutaImagen;
    }

    public void setRutaImagen(String rutaImagen) {
        this.rutaImagen = rutaImagen;
    }
}
