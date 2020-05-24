package tk.jhordybarrera.soporteselectricaribe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.GalleryAdapter;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.OSManager;

public class AddEditActivity extends AppCompatActivity {
    private EditText os;
    private EditText nic;
    private String photoDir;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private RecyclerView imageGallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        os = findViewById(R.id.clientOS);
        nic = findViewById(R.id.clientNic);
        photoDir = this.getFilesDir().toString()+File.separator+"evidencia"+File.separator;
        imageGallery = findViewById(R.id.imageGallery);
        load_data();
    }


    private void load_data() {
        if(getIntent().hasExtra("nic")){
            nic.setText(getIntent().getStringExtra("nic"));
        }
        if(getIntent().hasExtra("os")){
            os.setText(getIntent().getStringExtra("os"));
        }
        if(folder()){
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),2);
            imageGallery.setLayoutManager(layoutManager);
            ArrayList<String> createLists = listar();
            GalleryAdapter adapter = new GalleryAdapter(getApplicationContext(), createLists);
            imageGallery.setAdapter(adapter);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        load_data();
    }
    /**
     * Guardar las fotos
     * **/
    public String almacenamiento(){
        return photoDir + nic.getText().toString() + File.separator+os.getText().toString();
    }
    public void add_photo_video(View v){//comprueba los permisos antes de abrir la camara
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {
                //explica porque necesitas los permisos
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},MY_PERMISSIONS_REQUEST_CAMERA);
            }
        }else{
            capturar();
        }
    }
    public void capturar(){
        if(nic.isEnabled()){//si todavia no se ha guardado guardamos
            save();
        }else{//
            dispatchTakePictureIntent();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();//revisar el creador del archivo de la imagen
            } catch (IOException ex) {}

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "tk.jhordybarrera.soporteselectricaribe.AddEditActivity",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(
                almacenamiento()
        );
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents
        //String rutaPhotoActual = image.getAbsolutePath();
        return image;
    }
    public ArrayList listar(){
        File folder = getExternalFilesDir(almacenamiento());
        File[] files = folder.listFiles();
        ArrayList<String> list = new ArrayList();
        String capturas="";
        for (int i = 0; i < files.length; i++){
            //Sacamos del array files un fichero
            File file = files[i];
            list.add(file.getAbsolutePath());
        }
        return list;
    }

    public static final int MY_PERMISSIONS_REQUEST_CAMERA=1;
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    add_photo_video(null);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,this.getString(R.string.not_granted),Toast.LENGTH_LONG).show();
                }
                return;
            }
            //
        }
    }


    public void deactivate(){
        nic.setEnabled(false);
        os.setEnabled(false);
    }
    public void save(){
        //primer caso, un item nuevo y completo tiene nic y os
        //segundo caso, un item nuevo e incompleto tiene solo nic
        //tercer caso el item ya existe, hay que verificar los cambios y mover directorios
        if (!getIntent().hasExtra("nic")) {//verificamos que no sea un existente
            if(nic.getText().toString().isEmpty()){//verificamos que el nic no esté en blanco
                Toast.makeText(this, "Nic no puede estar vacio",Toast.LENGTH_SHORT).show();
            }else{
                if(!os.getText().toString().isEmpty()){//verificamos que la orden de servicio no esté en blanco
                    //primer caso
                    if(folder()) {//verificamos el directorio antes de guardar
                        new OSManager(this.getApplicationContext()).save_os(getIntent().getStringExtra("id"), os.getText().toString(), nic.getText().toString());
                        deactivate();//si se guardo desactivamos para evitar cambios
                    }else{
                        Toast.makeText(this, "No se puede usar el directorio",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    //segundo caso
                    Toast.makeText(this, "Accion aun no implementada (Sin OS)",Toast.LENGTH_SHORT).show();
                }

                //this.finish();//cerramos despues de guardar
            }
        }else {//es un existente aplique validacion de cambios actualizacion de datos
            //tercer caso
            Toast.makeText(this, "Accion aun no implementada (Actualizar)",Toast.LENGTH_SHORT).show();

            //validar cambios de directorio antes de actualizar la base de datos
            //new OSManager(this.getApplicationContext()).update_os(getIntent().getStringExtra("id"), os.getText().toString(), nic.getText().toString(),getIntent().getStringExtra("nic"));
        }
    }

    public void delete(){
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("Estas seguro?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    // continue with delete
                    new OSManager(this.getApplicationContext()).delete_nic(nic.getText().toString());
                    this.finish();
                }).setNegativeButton("Cancelar", (dialog, which) -> {
                    // do nothing
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public void upload(){

    }

    /**
     *
     * Creacion de directorios
     *
     * */
    public boolean folder(){
        String cliente=nic.getText().toString();
        String trabajo=os.getText().toString();
        String ruta= photoDir +cliente;
        File folder = new File(ruta);
        //directory.mkdirs();
        if (!folder.exists()) {//si no existe el directorio se crea
            if(!folder.mkdirs()){
                //no se creo el irectorio
                return false;
            }
        }
        ruta = ruta+File.separator+trabajo;
        folder = new File(ruta);
        if (!folder.exists()) {//si no existe el directorio se crea
            if(!folder.mkdirs()){
                //no se creo el irectorio
                return false;
            }
        }
        return true;//si llega hasta aqui vamos bien, el direcctorio existe listo para guardar
    }


    /**
     * opciones de menú
     * */

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
}
