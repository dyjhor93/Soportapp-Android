package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
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
    public static final int MY_PERMISSIONS_REQUEST_CAMERA=1;
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    add_photo_video(null);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,this.getString(R.string.not_granted),Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request.
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
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_CONTACTS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
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
