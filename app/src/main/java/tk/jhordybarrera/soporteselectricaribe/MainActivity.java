package tk.jhordybarrera.soporteselectricaribe;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.AuthenticationLocal;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.OSAdapter;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.OSManager;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.OSModel;


public class MainActivity extends AppCompatActivity implements Clickable{
    private RecyclerView recyclerViewOS;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerViewOS = findViewById(R.id.recyclerViewOS);
        recyclerViewOS.setHasFixedSize(false);
        recyclerViewOS.setLayoutManager(new LinearLayoutManager(this));
        load_content();
    }

    private void load_content() {
        mAdapter = new OSAdapter(getData(),this);
        recyclerViewOS.setAdapter(mAdapter);
    }

    static final int REQUEST_SAVE = 1;
    public void add_evidence(View v){
        Intent intent = new Intent(this, AddEditActivity.class);
        if(getIntent().hasExtra("id")){
            intent.putExtra("id",getIntent().getStringExtra("id"));
        }
        startActivityForResult(intent,REQUEST_SAVE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        load_content();
    }

    public List<OSModel> getData() {
        return new OSManager(this.getApplicationContext()).list_os();
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra("nic",getData().get(position).getNic());
        intent.putExtra("os",getData().get(position).getOs());
        startActivityForResult(intent,REQUEST_SAVE);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.upload_all, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_upload_all:
                upload();
                return true;
            case R.id.menu_upgrade:
                upgrade();
                return true;
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void logout() {
        //destruir el token y regresar al activity splash
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesion")
                .setMessage("Estas seguro?")
                .setPositiveButton("Confirmar", (dialog, which) -> {
                    //ok, continue with action
                    new AuthenticationLocal(this.getApplicationContext()).delete_session();
                    this.finish();
                }).setNegativeButton("Cancelar", (dialog, which) -> {
                    // do nothing
                }).setIcon(android.R.drawable.ic_dialog_alert).show();

        //System.exit(0);//cerrar la app
    }


    private void upgrade() {
        Intent intent = new Intent(this, ActualizarActivity.class);
        startActivity(intent);
    }

    private void upload() {

    }

}
