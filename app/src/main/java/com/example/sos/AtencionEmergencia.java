package com.example.sos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class AtencionEmergencia extends AppCompatActivity {

    private RecyclerView recyclerViewEmergencia;
    private RecyclerViewAdactador adactadorEmergencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atencion_emergencia);

        recyclerViewEmergencia=(RecyclerView)findViewById(R.id.recyclerEmergencia);
        recyclerViewEmergencia.setLayoutManager(new LinearLayoutManager(this));

        adactadorEmergencia=new RecyclerViewAdactador(obtenerEmegencia());
        recyclerViewEmergencia.setAdapter(adactadorEmergencia);
    }

    public List<EmergenciasModelo> obtenerEmegencia(){
        List<EmergenciasModelo> emergencia = new ArrayList<>();
        emergencia.add(new EmergenciasModelo("holaaaa 1","1111"));
        emergencia.add(new EmergenciasModelo("holaaaa 2","2222"));
        emergencia.add(new EmergenciasModelo("holaaaa 3","3333"));
        emergencia.add(new EmergenciasModelo("holaaaa 4","4444"));
        return emergencia;
    }
}
