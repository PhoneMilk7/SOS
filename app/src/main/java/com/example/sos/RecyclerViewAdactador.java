package com.example.sos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdactador extends RecyclerView.Adapter<RecyclerViewAdactador.ViewHolder> implements View.OnClickListener{
    private View.OnClickListener listener;
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nombre, descripcion;
        private ImageView atender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = (TextView)itemView.findViewById(R.id.txt_nombreModelo);
            descripcion = (TextView)itemView.findViewById(R.id.txt_descripcionModelo);
            atender = (ImageView) itemView.findViewById(R.id.img_atender);
        }
    }

    public List<EmergenciasModelo> emergenciaLista;

    public RecyclerViewAdactador(List<EmergenciasModelo> emergenciaLista) {
        this.emergenciaLista = emergenciaLista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_emergencia,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);

        view.setOnClickListener(this);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombre.setText(emergenciaLista.get(position).getNombre());
        holder.descripcion.setText(emergenciaLista.get(position).getApellido());
        //holder.atender.setText(emergenciaLista.get(position).getId());
    }

    @Override
    public int getItemCount() {
        return emergenciaLista.size();
    }

    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }

    public void onClick(View view) {
        if(listener!=null){
            listener.onClick(view);
        }
    }
}
