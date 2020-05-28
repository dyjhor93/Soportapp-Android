package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.UploadAdapter;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.UploadModel;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.VolleyMultipartRequest;

public class UploadActivity extends AppCompatActivity implements Clickable {
    private RecyclerView recyclerViewUpload;
    private RecyclerView.Adapter uploadAdapter;
    private RequestQueue queue;
    static final String urlUploadImage = "http://soportapp.tk/api/os/upload";
    private static final String urlOS = "http://soportapp.tk/api/os/store";
    ArrayList<String> lista;
    Bitmap bitmap;
    ArrayList<UploadModel> um;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        recyclerViewUpload = findViewById(R.id.recyclerViewUpload);
        recyclerViewUpload.setLayoutManager(new LinearLayoutManager(this));
        um = new ArrayList<>();
        if (getIntent().hasExtra("lista")) {
            lista = getIntent().getStringArrayListExtra("lista");
            load_content();
        }
        queue = Volley.newRequestQueue(this);
    }

    private void load_content() {

        for (int i = 0; i < lista.size(); i++) {
            String ruta = lista.get(i);
            um.add(new UploadModel(getIntent().getStringExtra("nic"), getIntent().getStringExtra("os"), 1/*reemplazar por idusuario*/, ruta));
        }
        uploadAdapter = new UploadAdapter(um, this);
        recyclerViewUpload.setAdapter(uploadAdapter);
    }


    @Override
    public void onItemClick(int position) {
        subir(position);
    }

    public void subir(int item) {

        File i = new File(lista.get(item));
        String filePath = i.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        //imageView.setImageBitmap(bitmap);
        uploadBitmap(item, bitmap);

/*
        new subir().execute(
                nic.getText().toString(),
                os.getText().toString(),
                image.getText().toString(),
                imageString
        );
        */
    }

    private void uploadBitmap(int i, final Bitmap bitmap) {

        //
        TextView nic = recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.nic);
        TextView os = recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.os);
        TextView image = recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.image);
        ProgressBar progress = recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.carga);
        ImageView imageView = recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.previa);
        progress.setIndeterminate(true);

        //getting the tag from the edittext

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, urlUploadImage,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        Log.e("Response", "Response");
                        progress.setIndeterminate(false);
                        new guardar().execute();

                        try {
                            Log.e("Response.data", new String(response.data));
                            //JSONObject obj = new JSONObject(new String(response.data));
                            //Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Error", error.getMessage());
                        progress.setIndeterminate(false);
                    }
                }) {

            /*
             * If you want to add more parameters with the image
             * you can do it here
             * here we have only one parameter with the image
             * which is tags
             * */
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //params.put("tags", tags);


                params.put("nic", um.get(i).getNic());
                params.put("os", um.get(i).getOs());
                params.put("imagename", image.getText().toString());
                //params.put("imagestring", strings[3]);
                return params;
            }

            /*
             * Here we are passing image by renaming it with a unique name
             * */
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                //long imagename = System.currentTimeMillis();
                params.put("pic", new DataPart(image.getText().toString() + ".png", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    private class guardar extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            StringRequest postRequest = new StringRequest(Request.Method.POST, urlOS,
                    response -> {
                        // response
                        //Log.e("Response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("message")) {
                                mostrar_respuesta(obj.getString("message"));
                            }
                        } catch (Exception e) {

                        }
                    },
                    error -> {
                        // error
                        mostrar_respuesta(error.getMessage());
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("client_nic", getIntent().getStringExtra("nic"));
                    params.put("os", getIntent().getStringExtra("nic"));
                    params.put("user_id", "1");
                    return params;
                }

            };

            queue.add(postRequest);

            return null;
        }

        private void mostrar_respuesta(String message) {
            Toast.makeText(UploadActivity.this, message, Toast.LENGTH_SHORT).show();
        }

    }
}