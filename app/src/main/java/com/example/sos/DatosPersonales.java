package com.example.sos;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
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

import android.provider.Settings;
import android.provider.Settings.System;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

public class DatosPersonales extends AppCompatActivity {
    private EditText etnombre, etapellidoPaterno, etapellidoMaterno, etedad, etelefono;
    private RadioButton rdb_hombre, rdb_mujer;
    static String token = "CositasSOS", sesion = "0", id = "", nombre, apellidoPaterno, apellidoMaterno, edad, telefono, genero, personId;
    static double Latitude, Longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos_personales);
        CheckAllPermission();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();

        //if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        //        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //    return;
       // }
       // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        etnombre = findViewById(R.id.txt_nombre);
        etapellidoPaterno = findViewById(R.id.txt_apellidoPaterno);
        etapellidoMaterno = findViewById(R.id.txt_apellidoMaterno);
        etedad = findViewById(R.id.txt_edad);
        rdb_hombre = findViewById(R.id.rdb_hombre);
        rdb_mujer = findViewById(R.id.rdb_mujer);
        etelefono = findViewById(R.id.txt_telefono);

        // Configure el inicio de sesión para solicitar el ID del usuario, la dirección de correo electrónico y el perfil básico.
        // La identificación y el perfil básico están incluidos en DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(DatosPersonales.this);
        if (acct != null) {
            personId = acct.getId();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void Enviar(View view) {
        id = personId;
        nombre = etnombre.getText().toString();
        apellidoPaterno = etapellidoPaterno.getText().toString();
        apellidoMaterno = etapellidoMaterno.getText().toString();
        edad = etedad.getText().toString();
        if (rdb_hombre.isChecked()) {
            genero = "1";
        }
        if (rdb_mujer.isChecked()) {
            genero = "2";
        }
        telefono = etelefono.getText().toString();

        if (nombre.isEmpty() || apellidoPaterno.isEmpty() || apellidoMaterno.isEmpty() || edad.isEmpty() || genero.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(this, "Debes ingresar todos los campos para continuar", Toast.LENGTH_SHORT).show();
        } else {
            //Toast.makeText(this,"Datos guardados, gracias", Toast.LENGTH_SHORT).show();
            new guardarDB(DatosPersonales.this).execute(token, sesion, id, nombre, apellidoPaterno, apellidoMaterno, edad, genero, telefono);
            finish();
        }
    }


    public static class guardarDB extends AsyncTask<String, Void, String> {

        private WeakReference<Context> context;
        private Context context1;

        public guardarDB(Context context) {
            this.context = new WeakReference<>(context);
            this.context1 = context;
        }

        protected String doInBackground(String... params) {
            String registrar_url = "http://alprasystems.com/emprende/sesion.php";
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
                id = params[2];
                nombre = params[3];
                apellidoPaterno = params[4];
                apellidoMaterno = params[5];
                edad = params[6];
                genero = params[7];
                telefono = params[8];

                String data = URLEncoder.encode("token", "UTF_8") + "=" + URLEncoder.encode(token, "UTF_8") + "&" +
                        URLEncoder.encode("sesion", "UTF_8") + "=" + URLEncoder.encode(sesion, "UTF_8") + "&" +
                        URLEncoder.encode("imei", "UTF_8") + "=" + URLEncoder.encode(id, "UTF_8") + "&" +
                        URLEncoder.encode("nombre", "UTF_8") + "=" + URLEncoder.encode(nombre, "UTF_8") + "&" +
                        URLEncoder.encode("apellidoPaterno", "UTF_8") + "=" + URLEncoder.encode(apellidoPaterno, "UTF_8") + "&" +
                        URLEncoder.encode("apellidoMaterno", "UTF_8") + "=" + URLEncoder.encode(apellidoMaterno, "UTF_8") + "&" +
                        URLEncoder.encode("edad", "UTF_8") + "=" + URLEncoder.encode(edad, "UTF_8") + "&" +
                        URLEncoder.encode("genero", "UTF_8") + "=" + URLEncoder.encode(genero, "UTF_8") + "&" +
                        URLEncoder.encode("telefono", "UTF_8") + "=" + URLEncoder.encode(telefono, "UTF_8");

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
                resultado = "Ha ocurrido un error" + e.getMessage();
            }
            return resultado;
        }

        protected void onPostExecute(String resultado) {
            Toast.makeText(context.get(), resultado, Toast.LENGTH_LONG).show();
        }
    }

    private void CheckAllPermission()
    {
        try {
            String[] permissions = getPackageManager().getPackageInfo(getBaseContext().getPackageName(),
                    PackageManager.GET_PERMISSIONS).requestedPermissions;
            for(int i = 0; i < permissions.length; i++)
                CheckPermission(permissions[i]);
        } catch (PackageManager.NameNotFoundException e) {}
    }

    private void CheckPermission(String PermissionToCheck)
    {
        if (android.os.Build.VERSION.SDK_INT >= 23)
        {
            boolean ResultCheck = false;
            try
            {
                java.lang.reflect.Method methodCheckPermission = Activity.class.getMethod("checkSelfPermission", java.lang.String.class);
                Object resultObj = methodCheckPermission.invoke(this, PermissionToCheck);
                int result = Integer.parseInt(resultObj.toString());
                ResultCheck = result == PackageManager.PERMISSION_GRANTED;
            } catch (Exception e){}
            if (!ResultCheck) {
                try {
                    java.lang.reflect.Method methodRequestPermission = Activity.class.getMethod("requestPermissions", java.lang.String[].class, int.class);
                    methodRequestPermission.invoke(this, new String[] {PermissionToCheck}, 0x12345);
                } catch (Exception e) {}
            }
        }
    }

    //public String getDeviceIMEI() {
    //    String deviceUniqueIdentifier = null;
    //    TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
    //    if (null != tm) {
    //       if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
    //            return "";
    //       }
    //        deviceUniqueIdentifier = tm.getDeviceId();
    //    }
    //    if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
    //        deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
    //    }
    //    return deviceUniqueIdentifier;
    //}

    public class MyLocationListener implements LocationListener {

        public MyLocationListener() {
            super();
        }

        @Override
        public void onProviderDisabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onProviderEnabled(String arg0) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onLocationChanged(Location location) {
            Latitude = location.getLatitude();
            Longitude = location.getLongitude();
           // IMEICode = getDeviceIMEI();
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }
    }
}