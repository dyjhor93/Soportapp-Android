package tk.jhordybarrera.soporteselectricaribe;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
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
        ProgressBar progress;
        progress=recyclerViewUpload.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.carga);
        progress.getProgressDrawable().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        progress.setIndeterminate(true);

    }

    public void subir(int item) {

        File i = null;
        try {
            i = new Compressor(this).compressToFile(new File(lista.get(item)));
            //FileWriter writer = new FileWriter(i);
            //writer.append(sBody);
            //writer.flush();
            //writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //File i = Compressor.compress(this, original);
        String filePath = i.getPath();

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 3;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath,options);

        //progress.setIndeterminate(true);

//        Toast.makeText(this,"Comprimiendo imagen, espere",Toast.LENGTH_SHORT).show();
//        ByteArrayOutputStream out = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        uploadBitmap(item, bitmap);
    }

    private void uploadBitmap(int i, final Bitmap bitmap) {

        //
        TextView nic = recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.nic);
        TextView os = recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.os);
        TextView image = recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.image);
        ProgressBar progress;
        progress=recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.carga);
        ImageView imageView = recyclerViewUpload.findViewHolderForAdapterPosition(i).itemView.findViewById(R.id.previa);
        //progress.getProgressDrawable().setColorFilter(Color.GRAY, android.graphics.PorterDuff.Mode.SRC_IN);
        new guardar().execute();
        //getting the tag from the edittext

        //our custom volley request
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, urlUploadImage,
                new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        progress.setIndeterminate(false);
                        new guardar().execute();

                        try {
                            Log.e("Response.data", new String(response.data));
                            JSONObject obj = new JSONObject(new String(response.data));
                            if(obj.has("message")){
                                if(obj.getString("message").contains("required")){
                                    progress.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                                    Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                                }


                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),"Problema de conexion", Toast.LENGTH_SHORT).show();
                        progress.getProgressDrawable().setColorFilter(
                                Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
                        Log.e("Error", "Problema de conexion");
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
                params.put("pic", new DataPart(image.getText().toString() + ".jpg", getFileDataFromDrawable(bitmap)));
                return params;
            }
        };

        Toast.makeText(this,"Subiendo imagen, espere",Toast.LENGTH_SHORT).show();
        //adding the request to volley
        Volley.newRequestQueue(this).add(volleyMultipartRequest);
    }

    public byte[] getFileDataFromDrawable(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private void mostrar_respuesta(String message) {
        Toast.makeText(UploadActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    private class guardar extends AsyncTask<Void, Void, Void> {
    private String respuesta;
        @Override
        protected Void doInBackground(Void... voids) {
            StringRequest postRequest = new StringRequest(Request.Method.POST, urlOS,
                    response -> {
                        // response
                        Log.e("Response", response);
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("message")) {
                                if(obj.getString("message").equalsIgnoreCase("True")){
                                    Toast.makeText(getApplicationContext(), "Guardado nic y os en la base de datos en linea", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {

                        }
                    },
                    error -> {
                        // error
                        mostrar_respuesta("Conexion fallida");
                        Log.e("Response", "Conexion fallida");
                    }
            ) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("client_nic", getIntent().getStringExtra("nic"));
                    params.put("os", getIntent().getStringExtra("os"));
                    params.put("user_id", "1");
                    return params;
                }

            };

            queue.add(postRequest);

            return null;
        }

    }
/*
    public interface UploadAPIs {
        @Multipart
        @POST("/upload")
        Call<ResponseBody> uploadImage(@Part MultipartBody.Part file, @Part("name") RequestBody requestBody);
    }
    private void uploadToServer(String filePath) {
        Retrofit retrofit = NetworkClient.getRetrofitClient(this);
        UploadAPIs uploadAPIs = retrofit.create(UploadAPIs.class);
        //Create a file object using file path
        File file = new File(filePath);
        // Create a request body with file and image media type
        RequestBody fileReqBody = RequestBody.create(MediaType.parse("image/*"), file);
        // Create MultipartBody.Part using file request-body,file name and part name
        MultipartBody.Part part = MultipartBody.Part.createFormData("upload", file.getName(), fileReqBody);
        //Create request body with text description and text media type
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), "image-type");
        //
        Call call = uploadAPIs.uploadImage(part, description);
        call.enqueue(new Callback() {

            @Override
            public void onResponse(Call call, retrofit2.Response response) {

            }

            @Override
            public void onFailure(Call call, Throwable t) {

            }
        });
    }

 */
/*//apache http
    private DefaultHttpClient mHttpClient;


    public ServerCommunication() {
        HttpParams params = new BasicHttpParams();
        params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
        mHttpClient = new DefaultHttpClient(params);
    }


    public void uploadUserPhoto(File image) {

        try {

            HttpPost httppost = new HttpPost("some url");

            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
            multipartEntity.addPart("Title", new StringBody("Title"));
            multipartEntity.addPart("Nick", new StringBody("Nick"));
            multipartEntity.addPart("Email", new StringBody("Email"));
            multipartEntity.addPart("Description", new StringBody(Settings.SHARE.TEXT));
            multipartEntity.addPart("Image", new FileBody(image));
            httppost.setEntity(multipartEntity);

            mHttpClient.execute(httppost, new PhotoUploadResponseHandler());

        } catch (Exception e) {
            Log.e(ServerCommunication.class.getName(), e.getLocalizedMessage(), e);
        }
    }

    private class PhotoUploadResponseHandler implements ResponseHandler<Object> {

        @Override
        public Object handleResponse(HttpResponse response)
                throws ClientProtocolException, IOException {

            HttpEntity r_entity = response.getEntity();
            String responseString = EntityUtils.toString(r_entity);
            Log.d("UPLOAD", responseString);

            return null;
        }

    }
*/



}