package tk.jhordybarrera.soporteselectricaribe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.FileProviderClass;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.GalleryAdapter;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.OSManager;

public class AddEditActivity extends AppCompatActivity{
    private EditText os;
    private EditText nic;
    private String photoDir;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    private RecyclerView imageGallery;
    private OSManager osman;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        os = findViewById(R.id.clientOS);
        nic = findViewById(R.id.clientNic);
        osman = new OSManager(this.getApplicationContext());
        photoDir = getExternalFilesDir("/").getAbsolutePath()+File.separator+"evidencia";
        File dir = new File(photoDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        photoDir+=File.separator;
        imageGallery = findViewById(R.id.imageGallery);
        if(getIntent().hasExtra("nic")){
            nic.setText(getIntent().getStringExtra("nic"));
        }
        if(getIntent().hasExtra("os")){
            if(!getIntent().getStringExtra("os").equals("no os"))//si no se ingreso sin orden de servicio
                os.setText(getIntent().getStringExtra("os"));
        }
        load_data();
        //Toast.makeText(this,photoDir,Toast.LENGTH_LONG).show();
        //Log.e("Dir",photoDir);
    }


    private void load_data() {
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

     */
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
            deactivate();
        }
        if(nic.getText().toString().isEmpty()||os.getText().toString().isEmpty()){//verificamos que el nic no esté en blanco
            Toast.makeText(this, "No puede estar vacio",Toast.LENGTH_SHORT).show();
        }else {
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
        File storageDir = new File(almacenamiento());
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
        File folder = new File(almacenamiento());
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
        //if(nic.isEnabled())
        if (!getIntent().hasExtra("nic")) {//verificamos que no sea un existente
            if(nic.getText().toString().isEmpty()||os.getText().toString().isEmpty()){//verificamos que el nic no esté en blanco
                Toast.makeText(this, "No puede estar vacio",Toast.LENGTH_SHORT).show();
            }else{
                if(osman.exist(os.getText().toString())){
                    Toast.makeText(this, "La orden de servicio ya existe en la base de datos",Toast.LENGTH_SHORT).show();
                }else {
                    //si se introdujo la orden de servicio
                    if (!os.getText().toString().isEmpty()) {
                        if (folder()) {//verificamos el directorio antes de guardar
                            osman.save_os(getIntent().getStringExtra("id"), os.getText().toString(), nic.getText().toString());
                            deactivate();//si se guardo desactivamos para evitar cambios
                        } else {
                            Toast.makeText(this, "No se puede usar el directorio", Toast.LENGTH_SHORT).show();
                        }
                    } else {//si no se introdujo la orden de servicio
                        if (folder()) {//verificamos el directorio antes de guardar
                            osman.save_os(getIntent().getStringExtra("id"), "no os", nic.getText().toString());
                            deactivate();//si se guardo desactivamos para evitar cambios
                        } else {
                            Toast.makeText(this, "No se puede usar el directorio", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }else {//es un existente aplique validacion de cambios actualizacion de datos


            String nicNuevo=nic.getText().toString();

            String osNuevo=os.getText().toString();
            String nicViejo=getIntent().getStringExtra("nic");
            String osViejo=getIntent().getStringExtra("os");

            if(nicNuevo.isEmpty()){
                Toast.makeText(this, "Nic no puede estar vacio",Toast.LENGTH_SHORT).show();
            }else {
                boolean actualizar=false;
                //Si cambio la orden de servicio
                if (!getIntent().getStringExtra("os").equals(os.getText().toString())) {
                    if (os.getText().toString().isEmpty()) {
                        osNuevo = "no os";
                    }
                    move_folder(
                            new File(photoDir + nicViejo + File.separator + osViejo),
                            new File(photoDir + nicViejo + File.separator + osNuevo)
                    );
                    actualizar=true;
                }
                //si cambio el nic
                if (!getIntent().getStringExtra("nic").equals(nic.getText().toString())) {
                    move_folder(
                            new File(photoDir + nicViejo),
                            new File(photoDir + nicNuevo)
                    );
                    actualizar=true;
                }

                if(actualizar){
                    Toast.makeText(this, "Se actualizo la orden de servicio", Toast.LENGTH_SHORT).show();
                    //validar cambios de directorio antes de actualizar la base de datos
                    osman.update_os(getIntent().getStringExtra("id"), osNuevo, nicNuevo, nicViejo);
                    deactivate();
                }
            }
        }
        load_data();
    }

    public void delete(){
        new AlertDialog.Builder(this)
                .setTitle("Eliminar")
                .setMessage("Estas seguro?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    // continue with delete
                    osman.delete_nic(nic.getText().toString());
                    if(!delete_dir(nic.getText().toString())){
                        Toast.makeText(this,"No se pudieron eliminar las evidencias",Toast.LENGTH_SHORT).show();
                    }
                    this.finish();
                }).setNegativeButton("Cancelar", (dialog, which) -> {
                    // do nothing
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }



    public void upload(){
        Intent intent = new Intent(this, UploadActivity.class);
        intent.putExtra("nic",nic.getText().toString());
        intent.putExtra("os",os.getText().toString());
        intent.putExtra("lista",listar());
        startActivity(intent);
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
    public boolean move_folder(File sourceDir, File destDir){
        if (sourceDir.isFile() || destDir.isFile()) {
            System.out.println("origen y destino no pueden ser archivos");
            return false;
        }

        if (!destDir.exists()) {
            destDir.mkdirs();
        }
        if (!sourceDir.exists()) {
            System.out.println("El directorio origen no existe");
            return false;
        }

        File[] items = sourceDir.listFiles();
        if (items != null && items.length > 0) {
            for (File item : items) {
                if (item.isDirectory()) {
                    // create the directory in the destination
                    File newDir = new File(destDir, item.getName());
                    newDir.mkdir();
                    // copy the directory (recursive call)
                    move_folder(item, newDir);
                } else {
                    // copy the file
                    File destFile = new File(destDir, item.getName());
                    move_file(item, destFile);
                }
                item.delete();
            }
        }
        sourceDir.delete();
        return true;
    }

    private boolean delete_dir(String nicDel) {
return false;
    }

    public void move_file(File sourceFile,File destFile) {
        try {
            if (!destFile.exists()) {
                destFile.createNewFile();
            }
        }catch (Exception e){}

        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(sourceFile).getChannel();
            destChannel = new FileOutputStream(destFile).getChannel();
            sourceChannel.transferTo(0, sourceChannel.size(), destChannel);
        } catch (Exception e){
            try {
                if (sourceChannel != null) {
                    sourceChannel.close();
                }
                if (destChannel != null) {
                    destChannel.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        sourceFile.delete();
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

    @Override
    public void onBackPressed() {
        if(getIntent().hasExtra("nic")) {
            if (!getIntent().getStringExtra("nic").equals(nic.getText().toString()) || !getIntent().getStringExtra("os").equals(os.getText().toString())) {
                new AlertDialog.Builder(this)
                        .setTitle("Cambios sin guardar")
                        .setMessage(
                                "Nic: "+getIntent().getStringExtra("nic")+"->"+nic.getText().toString()
                                +"\nOS: "+getIntent().getStringExtra("os")+"->"+os.getText().toString()
                        )
                        .setPositiveButton("Guardar", (dialog, which) -> {
                            // continue with delete
                            save();
                            this.finish();
                        }).setNegativeButton("Ignorar", (dialog, which) -> {
                    // do nothing
                    this.finish();
                }).setIcon(android.R.drawable.ic_dialog_alert).show();
            }else{
                this.finish();
            }
        }else{
            this.finish();
        }

    }
}
