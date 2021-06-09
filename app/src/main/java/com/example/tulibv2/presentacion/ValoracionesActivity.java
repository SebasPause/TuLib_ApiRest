package com.example.tulibv2.presentacion;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tulibv2.R;
import com.example.tulibv2.casosdeuso.AdaptadorValoraciones;
import com.example.tulibv2.datos.LibroBD;
import com.example.tulibv2.entities.Valoraciones;
import com.example.tulibv2.userServices.ValoracionRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ValoracionesActivity extends AppCompatActivity {

    ActionBar actionBar;
    RecyclerView rvValoraciones;
    RecyclerView.Adapter adapter2;
    RecyclerView.LayoutManager layoutManager2;
    List<Valoraciones> listaValoraciones;
    LibroBD bd;
    LinearLayout llContenido;
    Button btnEnviarComentario,btnEliminarComentario;
    private String idUsuario;
    String usuarioLibro;
    String idLibro;
    EditText edtComentario;
    RatingBar ratingBar;
    HashMap<String,String> cargarComentario;
    boolean existeComentario = false;
    boolean existeUsuario = true;

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valoraciones);

        llContenido = findViewById(R.id.llContenido);
        btnEnviarComentario = findViewById(R.id.btnEnviarComentario);
        btnEliminarComentario = findViewById(R.id.btnEliminarComentario);
        edtComentario = findViewById(R.id.edtComentario);
        ratingBar = findViewById(R.id.ratingBar);

        /**
         * Hago que el liner layout sea visible
         */
        llContenido.setVisibility(View.VISIBLE);

        /**
         * Datos relacionados al menu superior
         * Habilito una flecha para poder volver al activity anterior
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Valoraciones");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        /**
         * Establezco por defecto el comentario y el valor del rating bar
         * del linear layout
         */
        edtComentario.setText("");
        ratingBar.setRating(0.0f);

        Bundle extras = getIntent().getExtras();
        if(extras==null){
            //nada
        }else{
            /**
             * Siempre se llega a esta actividad con un putExtra
             * entonces el paso anterior es por si sucede algun error inesperado.
             * Con el metodo devolverValoraciones() obtengo todos los comentarios y valoraciones
             * realizadas en el libro actual y los guardo en un arrayList para que el adaptador
             * se encargue de manejar y mostrar esa informacion
             */
            idLibro = extras.getString("IDlibro");
            usuarioLibro = extras.getString("UsuarioLibro");
            bd = new LibroBD(this);
            listaValoraciones = bd.getValoracionesByBookId(idLibro);

            rvValoraciones = findViewById(R.id.rvValoraciones);
            adapter2 = new AdaptadorValoraciones(ValoracionesActivity.this,listaValoraciones);
            layoutManager2 = new LinearLayoutManager(ValoracionesActivity.this);
            rvValoraciones.setLayoutManager(layoutManager2);
            rvValoraciones.setHasFixedSize(true);
            rvValoraciones.setAdapter(adapter2);

            /**
             * Gracias al metodo cargarComentario(),
             * puedo averiguar si el usuario actual ha echo algun comentario en el libro actual.
             * Si no ha echo ningun comentario, podra realizar un comentario.
             * En el caso contrario, se introducirán en el campo del comentario del linear layout,
             * el comentario realizado y se establecerá su correspondiente valoracion en el ratingBar.
             * Gracias a esto podre eliminar la valoracion ya que el usuario actual no puede comentar
             * varias veces en el mismo libro
             */
            bd = new LibroBD(this);
            idUsuario = bd.getTokenUsuario().getUsuarioID();
            if(bd.cargarComentario(idUsuario,idLibro).size()<=0){
                //nada
            }else{
                cargarComentario = bd.cargarComentario(idUsuario,idLibro);
                edtComentario.setText(cargarComentario.get("Comentario"));
                ratingBar.setRating(Float.parseFloat(cargarComentario.get("Valor")));
                ratingBar.setIsIndicator(true);
                existeComentario = true;
            }


        }

        /**
         * Cuando se envie un comentario , el linear layout desaparecerá
         */

        btnEnviarComentario.setOnClickListener(v -> {

            /**
             * Obtengo los usuarios que han comentado en este libro gracias
             * al metodo devolverUsuarios() asignandole el id del libro como parametro.
             */
            LibroBD bd = new LibroBD(this);
            List<Valoraciones> valoraciones = bd.getValoracionesByBookId(idLibro);
            Log.i("ASK","Valores: " +bd.cargarComentario(idUsuario,idLibro).toString());
            if(bd.cargarComentario(idUsuario,idLibro).size() == 0){
                existeUsuario = false;
            }

            /**
             * Si no existe ese usuario en la valoracion de ese libro,
             * podra hacer su comentario.
             * En caso contrario se le informará de que ya ha realizado su comentario.
             */
            if(existeUsuario == false){
                bd.subirValoracion(edtComentario.getText().toString(),Math.round(ratingBar.getRating()),idLibro,idUsuario);

                Toast.makeText(this, "Comentario añadido, volviendo...", Toast.LENGTH_SHORT).show();
                bd.onUpgradeActualizar(bd.getWritableDatabase(),1,2);
                bd.insertarLibros();
                bd.insertarGenerosLiterarios();
                bd.insertarValoraciones();
                bd.insertarPaginas();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ValoracionesActivity.this, ContentMainActivity.class);
                        startActivity(intent);
                    }
                }, 3000);
            }else{
                Toast.makeText(this, "Ya has echo un comentario en este libro", Toast.LENGTH_SHORT).show();
            }

            llContenido.setVisibility(View.GONE);

        }); //fin btnEnviarComentario


        /**
         * Es el mismo procedimiento que el boton anterior, salvo que en este caso
         * es para eliminar el comentario.
         * Si existe el usuario en la valoracion del libro, se borrara el comentario.
         * En caso contrario se le informará de que no ha realizado ningun comentario.
         */
        btnEliminarComentario.setOnClickListener(v -> {
            LibroBD bd = new LibroBD(this);
            List<Valoraciones> valoraciones = bd.getValoracionesByBookId(idLibro);
            Log.i("ASK","Valores: " +bd.cargarComentario(idUsuario,idLibro).toString());
            if(bd.cargarComentario(idUsuario,idLibro).size() == 0){
                existeUsuario = false;
            }

            if(existeUsuario){
                AlertDialog.Builder builderEliminar = new AlertDialog.Builder(this);
                builderEliminar.setTitle("Estas seguro de querer eliminarlo?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.P)
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String valoracionID = bd.getValoracionID(idUsuario,idLibro);
                                Log.i("ASK","Valoracion: "+valoracionID);
                                bd.borrarValoracion(valoracionID,bd.getTokenUsuario().getToken());

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(ValoracionesActivity.this, "Comentario borrado, volviendo...", Toast.LENGTH_SHORT).show();
                                    }
                                }, 2000);

                                bd.onUpgradeActualizar(bd.getWritableDatabase(),1,2);
                                bd.insertarLibros();
                                bd.insertarGenerosLiterarios();
                                bd.insertarValoraciones();
                                bd.insertarPaginas();

                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ValoracionesActivity.this, ContentMainActivity.class);
                                        startActivity(intent);
                                    }
                                }, 3000);
                            }
                        })
                        .setNegativeButton("No",null);
                builderEliminar.create().show();

            }else{
                Toast.makeText(this, "No has echo ningun comentario en este libro", Toast.LENGTH_SHORT).show();
            }


        }); //fin btnEliminarComentario


    }

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}