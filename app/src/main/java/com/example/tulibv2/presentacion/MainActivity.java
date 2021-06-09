package com.example.tulibv2.presentacion;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tulibv2.R;
import com.example.tulibv2.casosdeuso.ApiClient;
import com.example.tulibv2.userServices.LoginRequest;
import com.example.tulibv2.userServices.LoginResponse;
import com.example.tulibv2.datos.LibroBD;

import java.security.cert.CertificateException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    EditText txtCorreo,txtContrasena;
    Button btnEntrar,btnRegistrar;
    ActionBar actionBar;
    LibroBD bd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        actionBar = getSupportActionBar();
        actionBar.setTitle("Bienvenid@ a TULIB!");

        bd = new LibroBD(this);
        bd.onUpgrade(bd.getWritableDatabase(),1,1);

        //Incializo objetos
        txtCorreo = (EditText)findViewById(R.id.txtCorreo);
        txtContrasena = (EditText)findViewById(R.id.txtPassword);
        btnEntrar = (Button)findViewById(R.id.btn_Entrar);
        btnRegistrar = (Button)findViewById(R.id.btn_Registrar);

        btnEntrar.setOnClickListener(v -> {
            String username = txtCorreo.getText().toString();
            String password = txtContrasena.getText().toString();
            try {
                login(username,password);
            } catch (CertificateException e) {
                e.printStackTrace();
            }
        }); //fin btnEntrar

        btnRegistrar.setOnClickListener(v ->{
            Intent intent = new Intent(MainActivity.this, RegistroActivity.class);
            startActivity(intent);
        });


    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MainActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void login(String username,String password) throws CertificateException {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword(password);
        loginRequest.setUserName(username);

        Call<LoginResponse> loginResponseCall = ApiClient.getUserService().userLogin(loginRequest);
        loginResponseCall.enqueue(new Callback<LoginResponse>() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if(response.isSuccessful()){
                    Log.i("ASK", String.valueOf(response.body().getToken()));
                    bd.setTokenUsuario(response.body().getToken(),username,response.body().getUserID());
                    /**
                     * Aqui inserto todos los datos en la base de datos local
                     */
                    bd.insertarLibros();
                    bd.insertarGenerosLiterarios();
                    bd.insertarValoraciones();
                    bd.insertarPaginas();
                    bd.insertarUsuario();
                    Toast.makeText(MainActivity.this, "Cargando Libros Publicados", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(MainActivity.this, ContentMainActivity.class);
                            intent.putExtra("LibrosPublicados",true);
                            MainActivity.this.finish();
                            startActivity(intent);
                        }
                    }, 2001);

                }else{
                    Toast.makeText(MainActivity.this, "Datos incorrectos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "No ha funcionado", Toast.LENGTH_SHORT).show();
            }
        });
    }



}