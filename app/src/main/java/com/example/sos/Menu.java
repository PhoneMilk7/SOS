package com.example.sos;
//Autores: Pereda Hernandez Leonel y Verdin Garcia José Antonio
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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


public class Menu extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    static Button sign_out, btn_emergencia;
    TextView nameTV;
    TextView emailTV;
    TextView idTV;
    ImageView photoIV;
    static String token = "CositasSOS", sesion = "3", Resultado, personId, checar = "6";
    static boolean c = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        btn_emergencia = findViewById(R.id.btn_AtencionEmergencia);
        //Oculta el boton
        if(c){
            btn_emergencia.setVisibility(Button.VISIBLE);
        }else{
            btn_emergencia.setVisibility(Button.INVISIBLE);
        }

        sign_out = findViewById(R.id.btn_log_out);
        nameTV = findViewById(R.id.txt_name);
        emailTV = findViewById(R.id.txt_email);
        idTV = findViewById(R.id.txt_id);
        photoIV = findViewById(R.id.photo);


        new Menu.BuscarDB(Menu.this).execute(token,sesion);


        // Configure el inicio de sesión para solicitar el ID del usuario, la dirección de correo electrónico y el perfil básico.
        // La identificación y el perfil básico están incluidos en DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Cree un GoogleSignInClient con las opciones especificadas por gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Menu.this);
        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            nameTV.setText("Nombre:\n"+personName);
            emailTV.setText("Correo:\n"+personEmail);
            idTV.setText("ID:\n"+personId);
            Glide.with(this).load(personPhoto).into(photoIV);
        }
        new Menu.ChecarID(Menu.this).execute(token,checar,personId);
    }
    protected void onResume() {
        super.onResume();
        new Menu.BuscarDB(Menu.this).execute(token,sesion);
        new Menu.ChecarID(Menu.this).execute(token,checar,personId);
    }
    public void Atencion(View view){

        //Se envia el dato que se selecciono
        Bundle extras = new Bundle();
        extras.putString("Resultado", Resultado); // se obtiene el valor mediante getString(...)
        //Inicializa el activity AtencionEmergencia
        Intent AteEmergencia = new Intent(this, AtencionEmergencia.class);
        //Agrega el objeto bundle a el Intne
        AteEmergencia.putExtras(extras);
        startActivity(AteEmergencia);
    }
    public void signOut(View view) {
        //Cierra secion de la cuenta ya iniciada e inicializa el activity LoginGoogle
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(Menu.this, "Cerrado de sessión exitoso", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), LoginGoogle.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
    }
    public void Atras(View view){
        //Inicializa el activity Activity2
        finish();
    }

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

                //Crea la variable data con todos los datos en formato URLEncode
                String data = URLEncoder.encode("token", "UTF_8") + "=" + URLEncoder.encode(token, "UTF_8") + "&" +
                        URLEncoder.encode("sesion", "UTF_8") + "=" + URLEncoder.encode(sesion, "UTF_8");

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
        }
    }
    public static class ChecarID extends AsyncTask<String, Void, String> {

        private WeakReference<Context> context;
        @SuppressLint("StaticFieldLeak")
        private Context context1;

        public ChecarID(Context context) {
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
                checar = params[1];
                personId = params[2];

                //Crea la variable data con todos los datos en formato URLEncode
                String data = URLEncoder.encode("token", "UTF_8") + "=" + URLEncoder.encode(token, "UTF_8") + "&" +
                        URLEncoder.encode("sesion", "UTF_8") + "=" + URLEncoder.encode(checar, "UTF_8") + "&" +
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
            if(resultado.equals("2")){
                c = true;
                btn_emergencia.setVisibility(Button.VISIBLE);
            }else{
                btn_emergencia.setVisibility(Button.INVISIBLE);
            }
        }
    }
}
