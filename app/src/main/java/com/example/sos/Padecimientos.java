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
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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

public class Padecimientos extends AppCompatActivity {
    private CheckBox CBox1, CBox2, CBox3, CBox4, CBox5, CBox6, CBox7;
    static String token = "CositasSOS",imei, sesion="2", datos1 = "",datos2 = "",datos3 = "",datos4 = "",datos5 = "",datos6 = "",datos7 = "",IMEICode;
    static double Latitude, Longitude;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_padecimientos);
        CheckAllPermission();
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new Padecimientos.MyLocationListener();

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
    }
    public void Enviar(View view){
            int contParams=0;
            if(CBox1.isChecked()){datos1="1";}
            if(CBox2.isChecked()){datos2="2";}
            if(CBox3.isChecked()){datos3="3";}
            if(CBox4.isChecked()){datos4="4";}
            if(CBox5.isChecked()){datos5="5";}
            if(CBox6.isChecked()){datos6="6";}
            if(CBox7.isChecked()){datos7="7";}

        new guardarDB(Padecimientos.this).execute(token,imei,sesion,datos1,datos2,datos3,datos4,datos5,datos6,datos7);
            finish();
    }
    public static class guardarDB extends AsyncTask<String, Void, String> {

        private WeakReference<Context> context;
        @SuppressLint("StaticFieldLeak")
        private Context context1;

        public guardarDB(Context context) {
            this.context = new WeakReference<>(context);
            this.context1 = context;
        }

        protected String doInBackground(String... params) {
            String registrar_url = "http://alprasystems.com/emprende/sesion.php"; //Ubicacion del archivo sesion en el HOST
            String resultado;

            try {
                URL url = new URL(registrar_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));

                token = params[0];
                imei = params[1];
                sesion = params[2];
                datos1 = params[3];
                datos2 = params[4];
                datos3 = params[5];
                datos4 = params[6];
                datos5 = params[7];
                datos6 = params[8];
                datos7 = params[9];

                String data =URLEncoder.encode("token", "UTF_8") + "=" + URLEncoder.encode(token, "UTF_8") + "&" +
                        URLEncoder.encode("imei", "UTF_8") + "=" + URLEncoder.encode(imei, "UTF_8") + "&" +
                        URLEncoder.encode("sesion", "UTF_8") + "=" + URLEncoder.encode(sesion, "UTF_8") + "&" +
                        URLEncoder.encode("datos1", "UTF_8") + "=" + URLEncoder.encode(datos1, "UTF_8") + "&" +
                        URLEncoder.encode("datos2", "UTF_8") + "=" + URLEncoder.encode(datos2, "UTF_8") + "&" +
                        URLEncoder.encode("datos3", "UTF_8") + "=" + URLEncoder.encode(datos3, "UTF_8") + "&" +
                        URLEncoder.encode("datos4", "UTF_8") + "=" + URLEncoder.encode(datos4, "UTF_8") + "&" +
                        URLEncoder.encode("datos5", "UTF_8") + "=" + URLEncoder.encode(datos5, "UTF_8") + "&" +
                        URLEncoder.encode("datos6", "UTF_8") + "=" + URLEncoder.encode(datos6, "UTF_8")+ "&" +
                        URLEncoder.encode("datos7", "UTF_8") + "=" + URLEncoder.encode(datos7, "UTF_8");

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

    /*public String getDeviceIMEI() {
        String deviceUniqueIdentifier = null;
        TelephonyManager tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (null != tm) {
            if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                return "";
            }
            deviceUniqueIdentifier = tm.getDeviceId();
        }
        if (null == deviceUniqueIdentifier || 0 == deviceUniqueIdentifier.length()) {
            deviceUniqueIdentifier = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return deviceUniqueIdentifier;
    }
    */

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
            //IMEICode = getDeviceIMEI();
        }

        @Override
        public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
            // TODO Auto-generated method stub

        }
    }

}
