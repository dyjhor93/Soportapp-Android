package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditActivity extends AppCompatActivity {
    private EditText os;
    private EditText nic;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        os = findViewById(R.id.clientOS);
        nic = findViewById(R.id.clientNic);
        load_data();
    }

    private void load_data() {
        if(getIntent().hasExtra("nic")){
            nic.setText(getIntent().getStringExtra("nic"));
        }
        if(getIntent().hasExtra("os")){
            os.setText(getIntent().getStringExtra("os"));
        }
    }

    public void add_photo_video(View v){
        //la idea es que se guarden en una carpeta que contenga el nic del usuario y dentro otra carpeta con la orden de servicio
        //app_data/nic/os/{os_img1.jpg, os_img2.jpg, os_video1.mp4, os_x.x}
        //si no se tiene todavia orden de servicio, se deja en blanco o se usa un check box solo se usa en nic
        //app_data//nic/{nic_img1.jpg, nic_img2.jpg, nic_video1.mp4, nic_x.x}
        //esto se controlara antes de subir al servidor
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"No has consedido permisos de la camara",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this,"Permiso de la camara concedido",Toast.LENGTH_LONG).show();
        }
    }
}
