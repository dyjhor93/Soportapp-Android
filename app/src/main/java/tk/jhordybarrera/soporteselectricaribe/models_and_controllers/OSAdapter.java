package tk.jhordybarrera.soporteselectricaribe.models_and_controllers;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import tk.jhordybarrera.soporteselectricaribe.Clickable;
import tk.jhordybarrera.soporteselectricaribe.R;

public class OSAdapter extends RecyclerView.Adapter<OSAdapter.ViewHolder> {
    private List<OSModel> osModelList;
    private Clickable clickable;
    public OSAdapter(List<OSModel> osModelList,Clickable clickable) {
        this.osModelList = osModelList;
        this.clickable = clickable;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.os_layout, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String nic = osModelList.get(position).getNic();
        holder.nic.setText("Nic: "+nic);

        String os = osModelList.get(position).getOs();
        if(os.equals("no os")){
            holder.os.setTextColor(Color.RED);
        }
        holder.os.setText("OS: "+os);
    }

    @Override
    public int getItemCount() {
        return osModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView nic;
        private TextView os;
        public ViewHolder(View v) {
            super(v);
            v.setOnClickListener(v1 -> clickable.onItemClick(getAdapterPosition()));
            nic = v.findViewById(R.id.clientNic);
            os = v.findViewById(R.id.clientOS);
        }
    }

}
