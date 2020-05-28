package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class ActualizarActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_INTERNET=1;
    public static final int MY_PERMISSIONS_REQUEST_WRITE=2;
    private ProgressBar pb;
    private Button b;
    private TextView rv;
    private TextView wv;
    private RequestQueue queue;
    private int curVersionCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);
        pb = findViewById(R.id.pb);
        b = findViewById(R.id.b);
        pb.setVisibility(View.GONE);
        rv = findViewById(R.id.rv);
        wv = findViewById(R.id.wv);

        queue = Volley.newRequestQueue(this);
        new GetApkData().execute();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.refresh, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.refresh:
                rv.setText("");
                wv.setText("");
                new GetApkData().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class UpgradeTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {
            String path = "/sdcard/SoportApp.apk";

            try {
                URL url = new URL("https://raw.githubusercontent.com/dyjhor93/SoportesElectricaribe/master/app/release/app-release.apk");
                URLConnection connection = url.openConnection();
                connection.connect();

                int fileLength = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(path);

                byte data[] = new byte[1024];
                long total = 0;
                int count;
                int progress=0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    progress=(int) (total * 100 / fileLength);
                    publishProgress(progress);
                    output.write(data, 0, count);
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("Backgroung proccess", e.getMessage());
                Log.i("info",String.valueOf(e.getMessage()));
            }
            return path;
        }
        @Override
        protected void onPostExecute(String path) {
            lanzar_instalacion(path);
        }

        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }
    }

    public void lanzar_instalacion(String path){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                Uri contentUri = FileProvider.getUriForFile(
                        this,
                        BuildConfig.APPLICATION_ID + ".provider",
                        new File(path)
                );
                Intent install = new Intent(Intent.ACTION_VIEW);
                install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                install.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                install.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                install.setData(contentUri) ;
                this.startActivity(install);
                //this.unregisterReceiver(this);
            }catch (Exception e){
                Log.e("Post Execute",e.getMessage());
            }
        }else{
            try {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive" );
                startActivity(i);
            }catch (Exception e){
                Log.e("Post Execute",e.getMessage());
            }
        }

    }


    private void setProgressPercent(Integer progress) {
        rv.setText("%"+progress);
        pb.setProgress(progress);
    }

    public void click(View v){
        if(tiene_permiso()){
            actualizar();
            if (pb.getVisibility() != View.VISIBLE) {
                pb.setVisibility(View.VISIBLE);
            }
        }else{

            comprobar_permisos();
        }

    }

    public boolean tiene_permiso(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
            return true;
        }else{
            return false;
        }
    }

    private void comprobar_permisos() {
        //permiso sd
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explica porque quieres los permisos
            } else {
                // No necesita explicacion, pedir permiso
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
            }
        }

        //permiso internet
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.INTERNET)) {
                // Explica porque quieres los permisos
            } else {
                // No necesita explicacion, pedir permiso
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},1);
            }
        }

        //permiso install apps
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.REQUEST_INSTALL_PACKAGES)) {
                // Explica porque quieres los permisos
            } else {
                // No necesita explicacion, pedir permiso
                Toast.makeText(this,"Pidiendo permiso",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},3);
            }
        }
    }

    public void actualizar(){
        //String a = this.getFilesDir().getAbsolutePath();
        //Toast.makeText(this,a,Toast.LENGTH_SHORT).show();

        new UpgradeTask().execute();
    }
    class GetApkData extends AsyncTask<Void, Void, String>{
        @Override
        protected String doInBackground(Void... params) {
            String url="https://raw.githubusercontent.com/dyjhor93/SoportesElectricaribe/master/app/release/output.json";
            StringRequest postRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        // response
                        //Log.e("Response", response);
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject obj = jsonArray.getJSONObject(0);
                            if(obj.has("apkData")){
                                JSONObject jsonResponse = obj.getJSONObject("apkData");
                                String version = jsonResponse.getString("versionCode");
                                setVersion(version);
                            }
                        }catch(Exception e){
                            Log.e("Error", e.getMessage());
                        }
                    },
                    error -> {
                        // error
                        Log.e("Error.Response", error.getMessage());
                    }
            );

            queue.add(postRequest);
            return null;
        }

    }

    private void setVersion(String version) {

        wv.setText(version);
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            curVersionCode = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        rv.setText(curVersionCode+"");

        try {
            int local=Integer.parseInt(rv.getText().toString());
            int remota=Integer.parseInt(wv.getText().toString());

            /*
            if(remota>=local){
                b.setEnabled(true);
            }else{
                b.setEnabled(false);
            }*/
        }catch (Exception e){}
    }

}
