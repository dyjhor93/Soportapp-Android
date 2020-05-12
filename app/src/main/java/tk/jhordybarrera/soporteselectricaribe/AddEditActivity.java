package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class AddEditActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

    }
    public void add_photo_video(View v){
        //la idea es que se guarden en una carpeta que contenga el nic del usuario y dentro otra carpeta con la orden de servicio
        //app_data/nic/os/{os_img1.jpg, os_img2.jpg, os_video1.mp4, os_x.x}
        //si no se tiene todavia orden de servicio, se deja en blanco o se usa un check box solo se usa en nic
        //app_data//nic/{nic_img1.jpg, nic_img2.jpg, nic_video1.mp4, nic_x.x}
        //esto se controlara antes de subir al servidor

    }
}
