package com.example.sos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class AtencionEmergencia extends AppCompatActivity {

    private RecyclerView recyclerViewEmergencia;
    private RecyclerViewAdactador adactadorEmergencia;
    String IdEvento = "", Resultado,Cerrar="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_atencion_emergencia);

        Bundle dato1 = this.getIntent().getExtras();
        Cerrar = dato1.getString("Cerrar");
        if(Cerrar=="si"){
            finish();
        }

        Bundle dato = this.getIntent().getExtras();
        Resultado = dato.getString("Resultado");

        recyclerViewEmergencia = (RecyclerView) findViewById(R.id.recyclerEmergencia);
        recyclerViewEmergencia.setLayoutManager(new LinearLayoutManager(this));

        adactadorEmergencia = new RecyclerViewAdactador(obtenerEmegencia());
        //Selecion de la emergencia
        adactadorEmergencia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Se guarda en la variable persona el id
                IdEvento = obtenerEmegencia().get(recyclerViewEmergencia.getChildAdapterPosition(view)).getId();

                //Se muestra un mensaje en pantalla de lo que seleciono
                Next();
            }
        });
        recyclerViewEmergencia.setAdapter(adactadorEmergencia);
    }

    public List<EmergenciasModelo> obtenerEmegencia(){
        String[] parts = Resultado.split("@");
        int n = parts.length / 4;
        String[] idEvento = new String [n];
        String[] idUsuario = new String [n];
        String[] nombre = new String [n];
        String[] apellido = new String [n];
        int i = 0,y = 0, z = 0;
        do{
            idEvento[y] = parts[i]; i++;
            idUsuario[y] = parts[i]; i++;
            nombre[y] = parts[i]; i++;
            apellido[y] = parts[i]; i++;
            y++;
        }while(y < n);

        List<EmergenciasModelo> emergencia = new ArrayList<>();
        //llamada a la base de datos para llenar el array
        for (int j = 0; j < y; j++ ){
            emergencia.add(new EmergenciasModelo(idEvento[j],idUsuario[j],nombre[j],apellido[j]));
        }

        //emergencia.add(new EmergenciasModelo(parts[0],parts[1],parts[2],parts[3],parts[4],parts[5],parts[6]));
        return emergencia;
    }

    public void Next(){
        //Se envia el dato que se selecciono
        Bundle extras = new Bundle();
        extras.putString("IdEvento", IdEvento); // se obtiene el valor mediante getString(...)

        Intent intent = new Intent(this, AtendiendoEmergencia.class);
        //Agrega el objeto bundle a el Intne
        intent.putExtras(extras);
        //Inicia Activity
        startActivity(intent);
    }
    public void Atras(View view){
        finish();
    }

}
