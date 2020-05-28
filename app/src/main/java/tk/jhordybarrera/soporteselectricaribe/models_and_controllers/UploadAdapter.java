package tk.jhordybarrera.soporteselectricaribe.models_and_controllers;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tk.jhordybarrera.soporteselectricaribe.Clickable;
import tk.jhordybarrera.soporteselectricaribe.R;

public class UploadAdapter extends RecyclerView.Adapter<UploadAdapter.ViewHolder>{
    private List<UploadModel> uploadModelList;
    private Clickable clickable;
    public UploadAdapter(List<UploadModel> uploadModelList,Clickable clickable) {
        this.uploadModelList=uploadModelList;
        this.clickable = clickable;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.upload_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UploadAdapter.ViewHolder holder, int position) {
        String nic = uploadModelList.get(position).getNic();
        String os = uploadModelList.get(position).getOs();
        String ruta = uploadModelList.get(position).getRutaImagen();
        holder.nic.setText("nic: "+nic);
        holder.os.setText("os: "+os);

        File imgFile = new  File(ruta);

        if(imgFile.exists()){
            holder.image.setText(imgFile.getName());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imageSrc.setImageBitmap(myBitmap);
        }
        holder.progress.setProgress(0);
    }

    @Override
    public int getItemCount() {
        return uploadModelList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nic;
        private TextView os;
        private TextView image;
        private ProgressBar progress;
        private ImageView imageSrc;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(v1 -> clickable.onItemClick(getLayoutPosition()));
            nic = v.findViewById(R.id.nic);
            os = v.findViewById(R.id.os);
            progress = v.findViewById(R.id.carga);
            imageSrc = v.findViewById(R.id.previa);
            image = v.findViewById(R.id.image);
        }
    }
}
