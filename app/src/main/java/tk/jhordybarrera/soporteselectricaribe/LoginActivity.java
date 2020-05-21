package tk.jhordybarrera.soporteselectricaribe;
//https://medium.com/@cvallejo/sistema-de-autenticaci%C3%B3n-api-rest-con-laravel-5-6-240be1f3fc7d
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.AuthenticationLocal;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.AuthenticationLocal.*;

public class LoginActivity extends AppCompatActivity {
    private static final String url="http://soportapp.tk/api/auth/login";
    private EditText user;
    private EditText pass;
    private Button button;
    private ProgressBar pb;
    private RequestQueue queue;
    private UserSaveDbHelper dbMan;
    private SQLiteDatabase db;
    private boolean autenticated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button = findViewById(R.id.login_button);
        user = findViewById(R.id.login_user);
        pass = findViewById(R.id.login_pass);
        pb = findViewById(R.id.login_pb);
        dbMan = new AuthenticationLocal.UserSaveDbHelper(this.getApplicationContext());
        autenticated=false;
        db = dbMan.getWritableDatabase();
        if(check_saved()){
            check_login();
        }
        queue = Volley.newRequestQueue(this);
    }

    private boolean check_saved() {
        String[] projection = {
                BaseColumns._ID,
                UserSave.COLUMN_NAME_TOKEN
        };

        Cursor cursor = db.query(
                UserSave.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );

        List itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            long itemId = cursor.getLong(
                    cursor.getColumnIndexOrThrow(UserSave._ID));
            itemIds.add(itemId);
        }
        cursor.close();
        if(!itemIds.isEmpty()){
            autenticated = true;
            return true;
        }
        return false;
    }
    private void save_session(String token){
        ContentValues values = new ContentValues();
        values.put(UserSave.COLUMN_NAME_TOKEN, token);
        long newRowId = db.insert(UserSave.TABLE_NAME, null, values);
        Toast.makeText(this,"Guardada sesion con id "+newRowId, Toast.LENGTH_SHORT).show();
    }

    public void login_click(View v){
        cargando();
        String u = user.getText().toString();
        String p = pass.getText().toString();
        new loguear().execute(u, p);
    }
    public void user_test(View v){
        user.setText("usertest@soportapp.tk");
        pass.setText("123456789");
    }
    public void cargando(){
        user.setEnabled(false);
        pass.setEnabled(false);
        button.setEnabled(false);
        pb.setVisibility(View.VISIBLE);
    }
    public void login_error(){
        user.setEnabled(true);
        pass.setEnabled(true);
        button.setEnabled(true);
        pb.setVisibility(View.INVISIBLE);
    }

    public void check_login(){
        if(autenticated){
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }else{
            Toast.makeText(this,"Login incorrecto",Toast.LENGTH_SHORT).show();
            login_error();
        }
    }


    private class  loguear extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        // response
                        //Log.e("Response", response);
                        try{
                            JSONObject obj = new JSONObject(response);
                            if(obj.has("access_token")){
                                autenticated=true;

                                save_session(obj.getString("access_token"));
                            }
                        }catch(Exception e){
                            autenticated = false;
                            Log.e("Error", "parsing json");
                        }
                        check_login();
                    },
                    error -> {
                        // error
                        Log.e("Error.Response", error.getMessage());
                        check_login();
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("email", strings[0]);
                    params.put("password", strings[1]);
                    return params;
                }
            };

            queue.add(postRequest);

            return null;
        }
    }

}

