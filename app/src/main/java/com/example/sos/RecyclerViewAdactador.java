package com.example.sos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdactador extends RecyclerView.Adapter<RecyclerViewAdactador.ViewHolder>{
    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView nombre, descripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nombre = (TextView)itemView.findViewById(R.id.txt_nombreModelo);
            descripcion = (TextView)itemView.findViewById(R.id.txt_descripcionModelo);
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
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.nombre.setText(emergenciaLista.get(position).getNombre());
        holder.descripcion.setText(emergenciaLista.get(position).getDescripcion());
    }

    @Override
    public int getItemCount() {
        return emergenciaLista.size();
    }
}
