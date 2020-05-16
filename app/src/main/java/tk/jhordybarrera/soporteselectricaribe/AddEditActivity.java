package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AddEditActivity extends AppCompatActivity {
    private EditText os;
    private EditText nic;
    private ImageView imageView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save_delete_update, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_save:
                save();
                return true;
            case R.id.menu_delete:
                delete();
                return true;
            case R.id.menu_upload:
                upload();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        os = findViewById(R.id.clientOS);
        nic = findViewById(R.id.clientNic);
        imageView = findViewById(R.id.gallery);
        load_data();
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);
        }
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
            Toast.makeText(this,this.getString(R.string.not_granted),Toast.LENGTH_LONG).show();
        }else{
            dispatchTakePictureIntent();
        }
    }

    public void save(){

    }
    public void delete(){
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("Estas seguro?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    // continue with delete
                })
    .setNegativeButton("Cancelar", (dialog, which) -> {
        // do nothing
    })
    .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }
    public void upload(){

    }
}
