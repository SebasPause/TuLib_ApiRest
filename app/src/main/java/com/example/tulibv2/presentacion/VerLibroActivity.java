package com.example.tulibv2.presentacion;

import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tulibv2.R;
import com.example.tulibv2.datos.LibroBD;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collection;
import java.util.HashMap;

public class VerLibroActivity extends AppCompatActivity {

    TextView edtEscribirLibro,txtPagina;
    FloatingActionButton btnForward,btnBack;
    HashMap<String,String> contenidoPagina = new HashMap<>();
    int numeroDePaginas;
    String IdLibro;
    Bundle extras;
    int posicionActual;
    ActionBar actionBar;
    Collection<String> valores;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_libro);

        /**
         * Siempre al entrar al activity la primera pagina tendrá el numero 1
         */
        posicionActual = 1;

        /**
         * Datos relacionados al menu superior
         * Habilita una flecha para volver al activity anterior
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Ver Libro");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        extras = getIntent().getExtras();
        /**
         * Si proviene del adaptador de libros publicados,
         * las paginas y su contenido seran obtenidos con el metodo getPaginasLibro()
         */
        if(extras.getBoolean("LibroPublicado")){
            IdLibro = extras.getString("IdLibro");
            LibroBD bd = new LibroBD(this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                contenidoPagina = bd.getPaginasLibro(IdLibro);
                valores = contenidoPagina.values();
            }
        }
        /**
         * Si proviene del adaptador de libros personales,
         * las paginas y su contenido seran obtenidos desde un putExtra
         */
        else{
            contenidoPagina = (HashMap<String,String>)extras.getSerializable("Paginas");
            IdLibro = extras.getString("IdLibro");
            valores = contenidoPagina.values();
        }

        edtEscribirLibro = findViewById(R.id.edtEscribirLibro);
        btnForward = findViewById(R.id.btnForward);
        btnBack = findViewById(R.id.btnBack);
        txtPagina = findViewById(R.id.txtPagina);

        //Aqui hago el setText
        edtEscribirLibro.setText(contenidoPagina.getOrDefault("1","Error"));
        txtPagina.setText("Página 1");

        /**
         * Boton para retroceder
         */
        btnBack.setOnClickListener(v -> {
            /**
             * Si la posicion actual es 1, significa que estoy en la primera pagina
             * Sino se reducira esa variable
             */
            if(posicionActual==1){
                Toast.makeText(this, "Estas en la primera pagina", Toast.LENGTH_SHORT).show();
                txtPagina.setText("Página "+posicionActual);
            } else{
                posicionActual -= 1;
                edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"Error"));
                txtPagina.setText("Página "+posicionActual);
            }
        });

        /**
         * Boton para avanzar
         */
        btnForward.setOnClickListener(v -> {
            /**
             * Obtengo el numero de paginas totales
             */
            numeroDePaginas = contenidoPagina.size();

            /**
             * Si la posicion actual es menor que el numero de paginas totales,
             * la variable se incrementa y vuelve a mostrar el contenido relacionado con ese numero de pagina.
             * Sino significa que ya esta en la ultima pagina
             */
            if(posicionActual<numeroDePaginas){
                posicionActual += 1;
                edtEscribirLibro.setText(contenidoPagina.getOrDefault(String.valueOf(posicionActual),"Error"));
                txtPagina.setText("Página "+posicionActual);
            }else{
                Toast.makeText(this, "Estas en la última página", Toast.LENGTH_SHORT).show();
            }
        });
    } //fin onCreate

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}