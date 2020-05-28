package tk.jhordybarrera.soporteselectricaribe;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.UploadAdapter;
import tk.jhordybarrera.soporteselectricaribe.models_and_controllers.UploadModel;

public class UploadActivity extends AppCompatActivity implements Clickable {
    private RecyclerView recyclerViewUpload;
    private RecyclerView.Adapter uploadAdapter;
    private RequestQueue queue;
    static final String urlUploadImage = "http://soportapp.tk/api/os/upload";
    ArrayList<String> lista;
    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        recyclerViewUpload = findViewById(R.id.recyclerViewUpload);
        recyclerViewUpload.setLayoutManager(new LinearLayoutManager(this));
        if (getIntent().hasExtra("lista")) {
            lista = getIntent().getStringArrayListExtra("lista");
            load_content();
        }
        queue = Volley.newRequestQueue(this);
    }

    private void load_content() {

        ArrayList<UploadModel> um = new ArrayList<>();
        for (int i = 0; i < lista.size(); i++) {
            String ruta = lista.get(i);
            um.add(new UploadModel(getIntent().getStringExtra("nic"), getIntent().getStringExtra("os"), 1/*reemplazar por idusuario*/, ruta));
        }
        uploadAdapter = new UploadAdapter(um,this);
        recyclerViewUpload.setAdapter(uploadAdapter);
    }


    @Override
    public void onItemClick(int position) {
        subir(position);
    }

    public void subir(int item) {
        ProgressBar progress = recyclerViewUpload.findViewHolderForAdapterPosition(item).itemView.findViewById(R.id.carga);
        TextView filename = recyclerViewUpload.findViewHolderForAdapterPosition(item).itemView.findViewById(R.id.image);
        ImageView image = recyclerViewUpload.findViewHolderForAdapterPosition(item).itemView.findViewById(R.id.previa);
        progress.setIndeterminate(true);
        //encode image to base64 string
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.logo);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        //decode base64 string to image
        imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        image.setImageBitmap(decodedImage);

        //Log.i("image",imageString);
        //sending image to server
        String url = "http://soportapp.tk/api/auth/login";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    // response
                    Log.e("Response", response);

                },
                error -> {
                    // error
                    Log.e("Error.Response", error.getMessage());
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("email", "strings[0]");
                params.put("password", "strings[1]");
                return params;
            }

        };

        queue.add(postRequest);
    }
}
