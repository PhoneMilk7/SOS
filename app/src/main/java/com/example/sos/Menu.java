package com.example.sos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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


public class Menu extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    Button sign_out;
    TextView nameTV;
    TextView emailTV;
    TextView idTV;
    ImageView photoIV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        ChecarPermisoEmergencia();

        sign_out = findViewById(R.id.log_out);
        nameTV = findViewById(R.id.name);
        emailTV = findViewById(R.id.email);
        idTV = findViewById(R.id.id);
        photoIV = findViewById(R.id.photo);

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
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();

            nameTV.setText("Name: "+personName);
            emailTV.setText("Email: "+personEmail);
            idTV.setText("ID: "+personId);
            Glide.with(this).load(personPhoto).into(photoIV);
        }
    }
    private void ChecarPermisoEmergencia() {
        //Pregunta a la base de datos si el usuario puede acceder al area de atencion de emergencia
        //Se debe de checar usando la funcion guardarDB y enviar de la clase Padecimeintos usando "personId"
        if(false){
            Button btn_emergencia = findViewById(R.id.AtencionEmergencia);
            btn_emergencia.setVisibility(Button.INVISIBLE);
        }
    }
    public void Atencion(View view){
        //Inicializa el activity AtencionEmergencia
        Intent AteEmergencia = new Intent(this, AtencionEmergencia.class);
        startActivity(AteEmergencia);
    }
    public void signOut(View view) {
        //Cierra secion de la cuenta ya iniciada e inicializa el activity LoginGoogle
        mGoogleSignInClient.signOut()
            .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(Menu.this, "Successfully signed out", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Menu.this, LoginGoogle.class));
                    finish();
                }
            });
    }
}
