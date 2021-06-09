package com.example.tulibv2.presentacion;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tulibv2.R;
import com.example.tulibv2.casosdeuso.ApiClient;
import com.example.tulibv2.userServices.RegisterRequest;
import com.example.tulibv2.userServices.RegisterResponse;

import java.security.cert.CertificateException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegistroActivity extends AppCompatActivity {

    EditText etRepConReg,etConReg,etUsReg,etUsRegCorreo;
    Button btn_Finalizar,btnVolverRegistro;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        /**
         * Datos relacionados al menu superior
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Bienvenid@ a TULIB!");

        etRepConReg = (EditText)findViewById(R.id.etRepConReg);
        etConReg = (EditText)findViewById(R.id.etConReg);
        etUsReg = (EditText)findViewById(R.id.etUsReg);
        etUsRegCorreo = (EditText)findViewById(R.id.etUsRegCorreo);
        btn_Finalizar = (Button)findViewById(R.id.btn_Finalizar);
        btnVolverRegistro = (Button)findViewById(R.id.btnVolverRegistro);

        /**
         * Al seleccionar el boton de finalizar,se haran las comprobaciones correspondientes de firebase
         * y si no ha habido ningun problema, el registro se habra realizado correctamente
         */
        btn_Finalizar.setOnClickListener(v -> {
            if (etUsReg.getText().toString().isEmpty() || etConReg.getText().toString().isEmpty()) {
                Toast.makeText(RegistroActivity.this, "Introduce correo y/o contraseña", Toast.LENGTH_SHORT).show();
            }else if(!etConReg.getText().toString().equals(etRepConReg.getText().toString())){
                Toast.makeText(RegistroActivity.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
            } else{
                //Aqui va el codigo de registrar
                String username = etUsReg.getText().toString();
                String email = etUsRegCorreo.getText().toString();
                String password = etConReg.getText().toString();

                try {
                    register(username,email,password);
                } catch (CertificateException e) {
                    e.printStackTrace();
                }
            }
        }); //finaliza btn_Finalizar

        /**
         * Si se selecciona el boton de volver, se retorna al activity de login
         */
        btnVolverRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(RegistroActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }

    public void register(String username,String email,String password) throws CertificateException {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail(email);
        registerRequest.setUsername(username);
        registerRequest.setPassword(password);

        Call<RegisterResponse> registerResponseCall = ApiClient.getUserService().userRegister(registerRequest);
        registerResponseCall.enqueue(new Callback<RegisterResponse>() {
            @Override
            public void onResponse(Call<RegisterResponse> call, Response<RegisterResponse> response) {
                if(response.isSuccessful()){
                    Log.i("ASK", String.valueOf(response.body().isSucceeded()));
                    if(response.body().isSucceeded()){
                        Toast.makeText(RegistroActivity.this, "Ha funcionado", Toast.LENGTH_SHORT).show();
                    }else{
                        Log.i("ASK", response.body().getListaErrores().toString());
                        int longitudBody = response.body().getListaErrores().size();
                        Log.i("ASK", String.valueOf(longitudBody));
                        for(int i=0;i<longitudBody;i++){
                            Toast.makeText(RegistroActivity.this, response.body().getListaErrores().get(i).getDescription(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }else{
                    Toast.makeText(RegistroActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                Toast.makeText(RegistroActivity.this, "No ha funcionado", Toast.LENGTH_SHORT).show();
            }
        });
    }


}