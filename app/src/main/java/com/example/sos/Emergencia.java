package com.example.sos;
//Autores: Pereda Hernandez Leonel y Verdin Garcia José Antonio
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class Emergencia extends AppCompatActivity implements LocationListener {
    private static final int REQUEST_LOCATION = 1;
    GoogleSignInClient mGoogleSignInClient;
    static String token = "CositasSOS", sesion = "1", id , estado, longitud, latitud, tipo, personId, personNombre, apellidoPaterno = " ";
    static Double La, Lo;
    TextView textView;
    Button btnpropia;
    Button btntercero;
    Button btnGetLocation;

    //Parte de permisos de ubicacion
    final String TAG = "GPS";
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    LocationManager locationManager;
    Location loc;
    ArrayList<String> permissions = new ArrayList<>();
    ArrayList<String> permissionsToRequest;
    ArrayList<String> permissionsRejected = new ArrayList<>();
    boolean isGPS = false;
    boolean isNetwork = false;
    boolean canGetLocation = true;
    //---------------------------

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergencia);
        ActivityCompat.requestPermissions( this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        Button btnpropia = this.findViewById(R.id.buttonPropia);
        Button btntercero = this.findViewById(R.id.buttonTerceros);
        textView = (TextView) findViewById(R.id.textView);

        //Pedir la localizacion
        locationManager = (LocationManager) getSystemService(Service.LOCATION_SERVICE);
        isGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);

        if (!isGPS && !isNetwork) {
            Log.d(TAG, "Connection off");
            showSettingsAlert();
            getLastLocation();
        } else {
            Log.d(TAG, "Connection on");
            // check permissions
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (permissionsToRequest.size() > 0) {
                    requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]),
                            ALL_PERMISSIONS_RESULT);
                    Log.d(TAG, "Permission requests");
                    canGetLocation = false;
                }
            }
            // get location
            getLocation();
        }
        ////Pedir la localizacion termina

        // Configure el inicio de sesión para solicitar el ID del usuario, la dirección de correo electrónico y el perfil básico.
        // La identificación y el perfil básico están incluidos en DEFAULT_SIGN_IN
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Emergencia.this);
        if (acct != null) {
            personNombre = acct.getGivenName();
            String n = acct.getFamilyName();
            if (n!=null){
                String[] apellido =acct.getFamilyName().split(" ");
                apellidoPaterno = apellido[0];
            }
            personId = acct.getId();
        }
    }

    public void propia(View view){
        id = personId;          //El identificador
        estado = "1";           //El evento esta en estado activo
        tipo = "1";               //El evento es de tipo Emergencia propia
        new Emergencia.guardarDB(Emergencia.this).execute(token,sesion,id,estado,longitud,latitud,tipo, personNombre, apellidoPaterno);
        finish();
    }
    public void terceros(View view){
        id = personId;          //El identificador
        estado = "1";           //El evento esta en estado activo
        tipo = "2";               //El evento es de tipo Emergencia propia
        new Emergencia.guardarDB(Emergencia.this).execute(token,sesion,id,estado,longitud,latitud,tipo, personNombre, apellidoPaterno);
        finish();
    }

    //Manda a guardar los datos de la emegencia
    public static class guardarDB extends AsyncTask<String, Void, String> {

        private WeakReference<Context> context;
        @SuppressLint("StaticFieldLeak")
        private Context context1;

        public guardarDB(Context context) {
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
                id = params[2];
                estado = params[3];
                longitud = params[4];
                latitud = params[5];
                tipo = params[6];
                personNombre = params[7];
                apellidoPaterno = params[8];

                //Crea la variable data con todos los datos en formato URLEncode
                String data = URLEncoder.encode("token", "UTF_8") + "=" + URLEncoder.encode(token, "UTF_8") + "&" +
                        URLEncoder.encode("sesion", "UTF_8") + "=" + URLEncoder.encode(sesion, "UTF_8") + "&" +
                        URLEncoder.encode("id", "UTF_8") + "=" + URLEncoder.encode(id, "UTF_8") + "&" +
                        URLEncoder.encode("estado", "UTF_8") + "=" + URLEncoder.encode(estado, "UTF_8") + "&" +
                        URLEncoder.encode("longitud", "UTF_8") + "=" + URLEncoder.encode(longitud, "UTF_8") + "&" +
                        URLEncoder.encode("latitud", "UTF_8") + "=" + URLEncoder.encode(latitud, "UTF_8")+ "&" +
                        URLEncoder.encode("tipo", "UTF_8") + "=" + URLEncoder.encode(tipo, "UTF_8") + "&" +
                        URLEncoder.encode("personNombre", "UTF_8") + "=" + URLEncoder.encode(personNombre, "UTF_8") + "&" +
                        URLEncoder.encode("apellidoPaterno", "UTF_8") + "=" + URLEncoder.encode(apellidoPaterno, "UTF_8");

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
    //Autorizacion para usar la ubicacion
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged");
        updateUI(location);
    }

    public void onStatusChanged(String s, int i, Bundle bundle) {}

    public void onProviderEnabled(String s) {
        getLocation();
    }

    public void onProviderDisabled(String s) {
        if (locationManager != null) {
            locationManager.removeUpdates((LocationListener) this);
        }
    }

    private void getLocation() {
        try {
            if (canGetLocation) {
                Log.d(TAG, "Can get location");
                if (isGPS) {
                    // from GPS
                    Log.d(TAG, "GPS on");
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else if (isNetwork) {
                    // from Network Provider
                    Log.d(TAG, "NETWORK_PROVIDER on");
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, (LocationListener) this);

                    if (locationManager != null) {
                        loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (loc != null)
                            updateUI(loc);
                    }
                } else {
                    loc.setLatitude(0);
                    loc.setLongitude(0);
                    updateUI(loc);
                }
            } else {
                Log.d(TAG, "Can't get location");
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getLastLocation() {
        try {
            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(provider);
            Log.d(TAG, provider);
            Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private ArrayList findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList result = new ArrayList();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canAskPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canAskPermission() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case ALL_PERMISSIONS_RESULT:
                Log.d(TAG, "onRequestPermissionsResult");
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(
                                                        new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                } else {
                    Log.d(TAG, "No rejected permissions.");
                    canGetLocation = true;
                    getLocation();
                }
                break;
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("GPS is not Enabled!");
        alertDialog.setMessage("Do you want to turn on GPS?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(Emergencia.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    private void updateUI(Location loc) {
        Log.d(TAG, "updateUI");
        longitud = Double.toString(loc.getLatitude());
        latitud = Double.toString(loc.getLongitude());
        Lo= loc.getLongitude();
        La= loc.getLatitude();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates((LocationListener) this);
        }
    }
    public void Atras(View view){
        finish();
    }
}
