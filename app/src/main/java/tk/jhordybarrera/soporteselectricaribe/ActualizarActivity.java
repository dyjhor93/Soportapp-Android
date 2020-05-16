package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class ActualizarActivity extends AppCompatActivity {
    public static final int MY_PERMISSIONS_REQUEST_INTERNET=1;
    public static final int MY_PERMISSIONS_REQUEST_WRITE=2;
    private ProgressBar pb;
    private Button b;
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar);
        pb = findViewById(R.id.pb);
        b = findViewById(R.id.b);
        tv = findViewById(R.id.tv);
        PackageInfo packageInfo = null;
        try {
            packageInfo = getPackageManager().getPackageInfo(this.getPackageName(), 0);
            int curVersionCode = packageInfo.versionCode;
            tv.setText("Version: "+curVersionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }
    private class UpgradeTask extends AsyncTask<URL, Integer, String> {

        @Override
        protected String doInBackground(URL... urls) {
            String path = "/sdcard/YourApp.apk";

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
                    Log.i("info",String.valueOf(progress));
                }

                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {
                Log.e("YourApp", "Well that didn't work out so well...");
                Log.e("YourApp", e.getMessage());
            }
            return path;
        }
        @Override
        protected void onPostExecute(String path) {
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.fromFile(new File(path)), "application/vnd.android.package-archive" );
            Log.d("Lofting", "About to install new .apk");
            ActualizarActivity.this.startActivity(i);
        }

        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }
    }



    private void setProgressPercent(Integer progress) {
        tv.setText("%"+progress);
    }

    public void click(View v){
        if (pb.getVisibility() == View.GONE) {
            pb.setVisibility(View.VISIBLE);
        } else {
            pb.setVisibility(View.GONE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET},MY_PERMISSIONS_REQUEST_INTERNET);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSIONS_REQUEST_WRITE);
        }else{
            actualizar();
        }
    }
    public void actualizar(){
        new UpgradeTask().execute();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_INTERNET: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    click(null);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this,this.getString(R.string.not_granted),Toast.LENGTH_LONG).show();
                }
                return;
            }
            // other 'case' lines to check for other permissions this app might request.
        }
    }

}
