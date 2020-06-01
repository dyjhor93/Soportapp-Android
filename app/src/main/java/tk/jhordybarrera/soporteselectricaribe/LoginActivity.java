package tk.jhordybarrera.soporteselectricaribe;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.AuthenticationLocal;

public class LoginActivity extends AppCompatActivity {
    private static final String urlLogin ="http://soportapp.tk/api/auth/login";
    private static final String urlUser ="http://soportapp.tk/api/auth/user";
    private EditText user;
    private EditText pass;
    private Button button;
    private ProgressBar pb;
    private RequestQueue queue;
    private String userId;
    private boolean autenticated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        button = findViewById(R.id.login_button);
        user = findViewById(R.id.login_user);
        pass = findViewById(R.id.login_pass);
        pb = findViewById(R.id.login_pb);
        autenticated=false;

        if(new AuthenticationLocal(this.getApplicationContext()).check_saved()){
            autenticated=true;
            check_login();
        }
        queue = Volley.newRequestQueue(this);
    }



    public void login_click(View v){

        String u = user.getText().toString();
        String p = pass.getText().toString();
        if(u.isEmpty()||p.isEmpty()){
            Toast.makeText(this,"Ingrese su usuario y contraseña",Toast.LENGTH_LONG).show();
        }else {
            cargando();
            new loguear().execute(u, p);
        }
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
            intent.putExtra("id",userId);
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
            StringRequest postRequest = new StringRequest(Request.Method.POST, urlLogin,
                    response -> {
                        // response
                        //Log.e("Response", response);
                        try{
                            JSONObject obj = new JSONObject(response);
                            String tk="",tk_tp="";
                            if(obj.has("access_token")){
                                tk=obj.getString("access_token");
                                tk_tp = obj.getString("token_type");
                                new get_user().execute(tk,tk_tp);
                            }
                        }catch(Exception e){
                            autenticated = false;
                            Log.e("Error", "parsing json");
                        }
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

    public class get_user extends AsyncTask <String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {

            StringRequest postRequest = new StringRequest(Request.Method.GET, urlUser,
                    response -> {
                        // response
                        //Log.e("Response", response);
                        try{
                            JSONObject obj = new JSONObject(response);
                            String id;
                            if(obj.has("id")){
                                autenticated=true;//para verificar el login
                                id=obj.getString("id");
                                userId=id;
                                new guardar_token().execute(strings[0],id);
                                //Toast.makeText(this,"Bienvenido "+obj.getString("name") + " id "+id,Toast.LENGTH_SHORT).show();
                            }
                        }catch(Exception e){
                            autenticated = false;
                            Log.e("Error", "getting id");
                        }
                        check_login();
                    },
                    error -> {
                        // error
                        Log.e("Error.Response", error.getMessage());
                        check_login();
                    }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    String authorization=strings[1]+" "+strings[0];
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Content-Type", "application/json");
                    params.put("Authorization", authorization);
                    return params;
                }
            };
            queue.add(postRequest);
            return null;
        }


    }
    public class guardar_token extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            new AuthenticationLocal(getApplicationContext()).save_session(strings[0],strings[1]);
            return null;
        }
    }
}

