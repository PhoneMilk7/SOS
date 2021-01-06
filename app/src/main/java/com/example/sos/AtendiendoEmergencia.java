package com.example.sos;
//Autores: Pereda Hernandez Leonel y Verdin Garcia José Antonio
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

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
import java.util.Locale;

public class AtendiendoEmergencia extends AppCompatActivity {
    static TextView Texto;
    static String token = "CositasSOS", sesion, Resultado, IdEvento, personId;
    static Double La , Lo ;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle dato = this.getIntent().getExtras();
        IdEvento = dato.getString("IdEvento");


        setContentView(R.layout.activity_atendiendo_emergencia);
        Texto = findViewById(R.id.txt_texto);
        //Se asigna el valor para deteminar entrara a la sesion que debe
        sesion = "4";
        // Configure el inicio de sesión para solicitar el ID del usuario, la dirección de correo electrónico y el perfil básico.
        // La identificación y el perfil básico están incluidos en DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(AtendiendoEmergencia.this);
        if (acct != null) {
            personId = acct.getId();
        }
        new AtendiendoEmergencia.BuscarDB(AtendiendoEmergencia.this).execute(token,sesion,IdEvento);
    }

    public void Atras(View view){
        //Inicializa el activity AtencionEmergencia
        finish();
    }
    //LLamar a la base de datos para pedir todoooo
    //Manda a buscar los datos de la emegencia

    public static class BuscarDB extends AsyncTask<String, Void, String> {

        private WeakReference<Context> context;
        @SuppressLint("StaticFieldLeak")
        private Context context1;

        public BuscarDB(Context context) {
            this.context = new WeakReference<>(context);
            this.context1 = context;
        }
        protected String doInBackground(String... params) {
            String registrar_url = "http://www.alprasystems.com/emprende/sesion.php"; //Ubicacion del archivo sesion en el HOST
            String resultado;

            try {
                URL url = new URL(registrar_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                token = params[0];
                sesion = params[1];
                IdEvento = params[2];

                //Crea la variable data con todos los datos en formato URLEncode
                String data = URLEncoder.encode("token", "UTF_8") + "=" + URLEncoder.encode(token, "UTF_8") + "&" +
                        URLEncoder.encode("sesion", "UTF_8") + "=" + URLEncoder.encode(sesion, "UTF_8") + "&" +
                        URLEncoder.encode("idEvento", "UTF_8") + "=" + URLEncoder.encode(IdEvento, "UTF_8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                resultado = stringBuilder.toString();

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                Log.d("MIAPP", "El formato de la URL es incorrecto");
                resultado = "Ha ocurrido un error ";
            } catch (IOException e) {
                Log.d("MIAPP", "Error ");
                resultado = "Ha ocurrido un error " + e.getMessage();
            }
            return resultado;
        }

        protected void onPostExecute(String resultado) {
            Resultado = resultado;
            String[] parts = Resultado.split("@");
            Texto.setText("Fecha de la emergencia: \n" + parts[0] +"\nNombre:\n" + parts[1] +" "+ parts[2] + "\nEdad:\n" + parts[3] + "\nTelefono:\n" + parts[4]);
            Lo = Double.parseDouble(parts[5]);
            La = Double.parseDouble(parts[6]);
        }
    }
    //Manda a cambiar el estado de la emegencia poniendola como no activa
    public static class AsignarestadoDB extends AsyncTask<String, Void, String> {

        private WeakReference<Context> context;
        @SuppressLint("StaticFieldLeak")
        private Context context1;

        public AsignarestadoDB(Context context) {
            this.context = new WeakReference<>(context);
            this.context1 = context;
        }
        protected String doInBackground(String... params) {
            String registrar_url = "http://www.alprasystems.com/emprende/sesion.php"; //Ubicacion del archivo sesion en el HOST
            String resultado;

            try {
                URL url = new URL(registrar_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                token = params[0];
                sesion = params[1];
                IdEvento = params[2];
                personId = params [3];

                //Crea la variable data con todos los datos en formato URLEncode
                String data = URLEncoder.encode("token", "UTF_8") + "=" + URLEncoder.encode(token, "UTF_8") + "&" +
                        URLEncoder.encode("sesion", "UTF_8") + "=" + URLEncoder.encode(sesion, "UTF_8") + "&" +
                        URLEncoder.encode("idEvento", "UTF_8") + "=" + URLEncoder.encode(IdEvento, "UTF_8") + "&" +
                        URLEncoder.encode("personId", "UTF_8") + "=" + URLEncoder.encode(personId, "UTF_8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }
                resultado = stringBuilder.toString();

                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();

            } catch (MalformedURLException e) {
                Log.d("MIAPP", "El formato de la URL es incorrecto");
                resultado = "Ha ocurrido un error ";
            } catch (IOException e) {
                Log.d("MIAPP", "Error ");
                resultado = "Ha ocurrido un error " + e.getMessage();
            }
            return resultado;
        }

        protected void onPostExecute(String resultado) {
            Toast.makeText(context.get(), resultado, Toast.LENGTH_LONG).show();
        }
    }

    public void mapa(View view){
        String uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?q=loc:%f,%f",Lo,La);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intent);
    }
    public void Atendida(View view){
        sesion = "5";
        new AtendiendoEmergencia.AsignarestadoDB(AtendiendoEmergencia.this).execute(token,sesion,IdEvento,personId);
        Bundle extras = new Bundle();
        extras.putString("Cerrar", "si"); // se obtiene el valor mediante getString(...)

        Intent intent = new Intent(this, Menu.class);
        //Agrega el objeto bundle a el Intne
        intent.putExtras(extras);
        //Inicia Activity
        startActivity(intent);
        finish();

    }
}
