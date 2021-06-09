package com.example.tulibv2.presentacion;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tulibv2.R;
import com.example.tulibv2.datos.LibroBD;
import com.example.tulibv2.entities.Libros;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Map;

public class EscribirLibroActivity extends AppCompatActivity {

    private String usuario;
    EditText edtEscribirLibro;
    String autor,nrPagina,texto;
    FloatingActionButton btnForward,btnBack;
    HashMap<String,String> listaPaginas;
    int posicion;
    int numeroDePaginas;
    int numeroNuevaPagina;
    String IdLibro;
    Bundle extras;
    Boolean NuevaPagina;
    int posicionActual;
    TextView txtPagina;
    ActionBar actionBar;
    String contenido;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escribir_libro);

        LibroBD bd = new LibroBD(this);

        /**
         * Datos relacionados al menu superior
         * Permite volver hacia atras al pulsar la flecha que contiene
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Escribir Libro");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);


        /**
         * Gracias a los extras, obtengo desde el adaptador de libros,
         * el numero de paginas y su contenido
         * para poder interactuar con el
         */
        extras = getIntent().getExtras();
        IdLibro = extras.getString("IdLibro");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            listaPaginas = bd.getPaginasLibro(IdLibro);
        }
        IdLibro = extras.getString("IdLibro");
        NuevaPagina = extras.getBoolean("NuevaPagina");

        edtEscribirLibro = findViewById(R.id.edtEscribirLibro);
        btnForward = findViewById(R.id.btnForward);
        btnBack = findViewById(R.id.btnBack);
        txtPagina = findViewById(R.id.txtPagina);

        edtEscribirLibro.setMovementMethod(null);
        edtEscribirLibro.setMaxLines(20);

        //Habilito la flecha de avanzar
        btnForward.setEnabled(true);

        posicionActual = extras.getInt("Posicion");

        /**
         * Si es nueva pagina,obtendremos en la posicion
         * el numero de la ultima pagina creada
         */
        if(NuevaPagina){
            posicion = extras.getInt("Posicion");
            edtEscribirLibro.setText(listaPaginas.getOrDefault(String.valueOf(posicion),"No lo coge"));
            txtPagina.setText("Página "+posicion);
        }else{
            //Aqui hago el setText
            posicion = extras.getInt("Posicion");
            edtEscribirLibro.setText(listaPaginas.getOrDefault(String.valueOf(posicion),"No lo coge"));
            txtPagina.setText("Página "+posicion);
        }

        /**
         * Boton de retroceder
         */
        btnBack.setOnClickListener(v -> {
            /**
             * Si estamos en una nueva pagina
             * la posicion actual sera el numero total de paginas
             * y al pulsarlo,a la posicionActual se le restara 1
             * ademas de asignar al boolean de NuevaPagina que al ir hacia atras
             * ya no será una nueva pagina
             */
            if(NuevaPagina){
                numeroDePaginas = listaPaginas.size();
                posicionActual = numeroDePaginas;
                //Gracias a este metodo se actualiza la pagina en la base de datos externa
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    guardarTextoAtras(IdLibro);
                }

                Toast.makeText(EscribirLibroActivity.this, "Cargando Página ", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Intent i=new Intent(EscribirLibroActivity.this, EscribirLibroActivity.class);
                        i.putExtra("Paginas",listaPaginas);
                        i.putExtra("Posicion",posicionActual-=1);
                        i.putExtra("NuevaPagina",false);
                        i.putExtra("IdLibro",IdLibro);
                        startActivity(i);
                    }
                }, 2000);
            }else{
                /**
                 * Si la posicion actual es la primera
                 * Se guardan los datos
                 * y se avisa de que estamos en la primera pagina y no se puede retroceder mas
                 */
                if(posicionActual==1){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        guardarTextoAtras(IdLibro);
                    }
                    Toast.makeText(EscribirLibroActivity.this, "Cargando Página", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Intent i=new Intent(EscribirLibroActivity.this, EscribirLibroActivity.class);
                            i.putExtra("Paginas",listaPaginas);
                            i.putExtra("Posicion",posicionActual);
                            i.putExtra("NuevaPagina",false);
                            i.putExtra("IdLibro",IdLibro);
                            startActivity(i);
                        }
                    }, 2000);
                    //Toast.makeText(this, "Ya estas en la primera pagina", Toast.LENGTH_SHORT).show();
                    //edtEscribirLibro.setText(listaPaginas.getOrDefault("1","No lo coge"));
                    //txtPagina.setText("Página 1");
                }
                else{
                    /**
                     * En caso de que no estemos en la primera pagina,
                     * la posicion actual volvera a reducirse.
                     * Se establece el contenido de la pagina
                     * y textView donde aparece el numero de pagina actual
                     */
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        guardarTextoAtras(IdLibro);
                    }
                    Toast.makeText(EscribirLibroActivity.this, "Cargando Página", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Intent i=new Intent(EscribirLibroActivity.this, EscribirLibroActivity.class);
                            i.putExtra("Paginas",listaPaginas);
                            i.putExtra("Posicion",posicionActual-=1);
                            i.putExtra("NuevaPagina",false);
                            i.putExtra("IdLibro",IdLibro);
                            startActivity(i);
                        }
                    }, 2000);

                }
            }
        });

        /**
         * Boton para avanzar
         */
        btnForward.setOnClickListener(v -> {
            /**
             * Obtengo el numero de paginas total
             * y si se crea una nueva pagina obtengo su numero gracias
             * al numero de paginas totales + 1
             */
            numeroDePaginas = listaPaginas.size();
            numeroNuevaPagina = numeroDePaginas+1;

            /**
             * Si la posicion actual es menor que el numero de paginas totales
             * y no es nueva nueva pagina,
             * se incrementa la posicion actual y se guardan los datos
             */
            if((posicionActual<numeroDePaginas)&(NuevaPagina==false)){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    guardarTextoAdelante(IdLibro);
                }
                Toast.makeText(EscribirLibroActivity.this, "Cargando Página", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        Intent i=new Intent(EscribirLibroActivity.this, EscribirLibroActivity.class);
                        i.putExtra("Paginas",listaPaginas);
                        i.putExtra("Posicion",posicionActual+1);
                        i.putExtra("NuevaPagina",false);
                        i.putExtra("IdLibro",IdLibro);
                        startActivity(i);
                    }
                }, 2000);

            }else{
                /**
                 * si nos encontramos en la ultima pagina y seguimos avanzando,
                 * se creara una nueva pagina y se actualizaran estos datos en
                 * la base de datos externa
                 */
                if((posicionActual==numeroDePaginas)&NuevaPagina){
                    numeroDePaginas = listaPaginas.size();
                    posicionActual = numeroDePaginas;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        guardarTexto(IdLibro);
                    }
                    Toast.makeText(EscribirLibroActivity.this, "Cargando Página", Toast.LENGTH_SHORT).show();
                    /**
                     * Se procedera a hacer un intent a la misma pagina para poder obtener
                     * los datos actuales relacionada a las paginas del libro
                     */
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Intent i=new Intent(EscribirLibroActivity.this, EscribirLibroActivity.class);
                            i.putExtra("Posicion",posicionActual+1);
                            i.putExtra("NuevaPagina",true);
                            i.putExtra("IdLibro",IdLibro);
                            startActivity(i);
                        }
                    }, 3000);
                }else{
                    /**
                     * En el caso de que se creen mas de una pagina seguida,
                     * pasara por aqui para poder controlar de forma correcta el funcionamiento
                     * de la creacion de paginas.
                     * Por lo tanto se obtendran de nuevo todos los datos
                     */

                    numeroDePaginas = listaPaginas.size();
                    posicionActual = numeroDePaginas;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        guardarTexto(IdLibro);
                    }
                    Toast.makeText(EscribirLibroActivity.this, "Cargando Página", Toast.LENGTH_SHORT).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                            Intent i=new Intent(EscribirLibroActivity.this, EscribirLibroActivity.class);
                            i.putExtra("Paginas",listaPaginas);
                            i.putExtra("Posicion",posicionActual+1);
                            i.putExtra("NuevaPagina",true);
                            i.putExtra("IdLibro",IdLibro);
                            startActivity(i);
                        }
                    }, 2000);
                } //fin else
            } //fin else
        });


    } //fin on create

    /**
     * Metodo que permite guardar en la base de datos externa lo escrito y/o editado
     * al avanzar o retroceder entre las paginas de un lbirp
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void guardarTexto(String libroID){
        contenido = edtEscribirLibro.getText().toString();
        LibroBD bd = new LibroBD(this);
        bd.guardarPagina(libroID,contenido,posicionActual,bd.getTokenUsuario().getToken());
        bd.crearPagina(IdLibro,posicionActual+1);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            cargarPaginas(IdLibro);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void guardarTextoAtras(String libroID){
        contenido = edtEscribirLibro.getText().toString();
        LibroBD bd = new LibroBD(this);
        bd.guardarPagina(libroID,contenido,posicionActual,bd.getTokenUsuario().getToken());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            bd.insertarPaginas();
            cargarPaginas(IdLibro);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void guardarTextoAdelante(String libroID){
        contenido = edtEscribirLibro.getText().toString();
        LibroBD bd = new LibroBD(this);
        bd.guardarPagina(libroID,contenido,posicionActual,bd.getTokenUsuario().getToken());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            cargarPaginas(IdLibro);
        }
    }

    /**
     * Metodo que permite obtener el numero de paginas y el contenido del numero correspondiente
     * gracias a una consulta a la base de datos externa pasandole como parametro el id del libro
     * @param idLibro
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public HashMap<String,String> cargarPaginas(String idLibro){
        LibroBD bd = new LibroBD(this);
        HashMap<String,String> listaPaginas = bd.getPaginasLibro(idLibro);
        return listaPaginas;
    }

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            guardarTextoAtras(extras.getString("IdLibro"));
        }
        onBackPressed();
        return super.onSupportNavigateUp();
    }

}