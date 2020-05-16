package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.OSAdapter;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.OSModel;


public class MainActivity extends AppCompatActivity implements Clickable{
    private RecyclerView recyclerViewOS;
    private RecyclerView.Adapter mAdapter;
    private FloatingActionButton addNewJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addNewJob = findViewById(R.id.fab_main_add);
        recyclerViewOS = findViewById(R.id.recyclerViewOS);
        recyclerViewOS.setHasFixedSize(false);
        recyclerViewOS.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new OSAdapter(getData(),this);
        recyclerViewOS.setAdapter(mAdapter);
        addNewJob.setOnClickListener(view -> add_evidence());
    }

    public void add_evidence(){
        Intent intent = new Intent(this, AddEditActivity.class);
        startActivity(intent);
    }

    public List<OSModel> getData() {

        List<OSModel> OSModel = new ArrayList<>();
        //datos de prueba reemplazar por carga local sqlite
        //OSModel.add(new OSModel("Nic1","OS1"));
        //OSModel.add(new OSModel("Nic2","OS2"));
        //OSModel.add(new OSModel("Nic3"));
        //OSModel.add(new OSModel("Nic4"));
        //OSModel.add(new OSModel("Nic5","OS3"));
        //OSModel.add(new OSModel("Nic6"));

        return OSModel;
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra("nic",getData().get(position).getNic());
        intent.putExtra("os",getData().get(position).getOs());
        startActivity(intent);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void upload() {

    }
}
