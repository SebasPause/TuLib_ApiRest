package com.example.tulibv2.presentacion;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tulibv2.R;
import com.example.tulibv2.casosdeuso.AdaptadorLibrosPublicados;
import com.example.tulibv2.entities.Libros;
import com.example.tulibv2.datos.LibroBD;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContentMainActivity extends BaseActivity implements Serializable{

    private String usuario;
    BottomNavigationView btnNavegacion;
    ActionBar actionBar;
    RecyclerView librosPublicados;
    RecyclerView.Adapter adapter2;
    RecyclerView.LayoutManager layoutManager2;

    @SuppressLint("RestrictedApi")
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);

        actionBar = getSupportActionBar();
        actionBar.setTitle("Publicaciones");

        Bundle extras = getIntent().getExtras();
        if(extras == null){
            /**
             * Obtengo todos los datos de la base de datos interna
             * mediante el metodo .devolverLibros()
             * Estos datos los guardo en un arraylist el cual se usara
             * para crear un nuevo adaptador
             */
            LibroBD bd = new LibroBD(this);
            List<Libros> listaLibrosPublicados = new ArrayList<>();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                listaLibrosPublicados =  bd.getLibrosPublicadosAceptados();
            }

            librosPublicados = findViewById(R.id.rvListaLibrosPublicados);
            adapter2 = new AdaptadorLibrosPublicados(ContentMainActivity.this,listaLibrosPublicados);
            layoutManager2 = new LinearLayoutManager(ContentMainActivity.this);
            librosPublicados.setLayoutManager(layoutManager2);
            librosPublicados.setHasFixedSize(true);
            librosPublicados.setAdapter(adapter2);

        }else{
            /**
             * Aqui entra siempre al inciar sesion en la aplicacion
             * Una vez dentro de la aplicacion, ya no vuelve a pasar por aqui
             */
            if(extras.getBoolean("LibrosPublicados")){
                List<Libros> listaLibrosPublicados = new ArrayList<>();
                LibroBD bd = new LibroBD(this);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    listaLibrosPublicados =  bd.getLibrosPublicadosAceptados();
                }

                librosPublicados = findViewById(R.id.rvListaLibrosPublicados);
                adapter2 = new AdaptadorLibrosPublicados(ContentMainActivity.this,listaLibrosPublicados);
                layoutManager2 = new LinearLayoutManager(ContentMainActivity.this);
                librosPublicados.setLayoutManager(layoutManager2);
                librosPublicados.setHasFixedSize(true);
                librosPublicados.setAdapter(adapter2);

            }else if(extras.getBoolean("Genero")){
                LibroBD bd = new LibroBD(this);
                List<Libros> listaLibrosPublicados = new ArrayList<>();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    listaLibrosPublicados =  bd.getLibrosPublicadosAceptadosPorGenero(extras.getString("valor"));
                }
                librosPublicados = findViewById(R.id.rvListaLibrosPublicados);
                adapter2 = new AdaptadorLibrosPublicados(ContentMainActivity.this,listaLibrosPublicados);
                layoutManager2 = new LinearLayoutManager(ContentMainActivity.this);
                librosPublicados.setLayoutManager(layoutManager2);
                librosPublicados.setHasFixedSize(true);
                librosPublicados.setAdapter(adapter2);
            }
            else{
                /**
                 * Utilizado en caso de que ocurra algo inesperado
                 * se borran los libros actuales que hay en la tabla de libros
                 * y se vuelven a cargar
                 */
                LibroBD bd = new LibroBD(this);
                List<Libros> listaLibrosPublicados = new ArrayList<>();
                bd.borrarLibros();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    listaLibrosPublicados =  bd.getLibrosPublicadosAceptados();
                }
                librosPublicados = findViewById(R.id.rvListaLibrosPublicados);
                adapter2 = new AdaptadorLibrosPublicados(ContentMainActivity.this,listaLibrosPublicados);
                layoutManager2 = new LinearLayoutManager(ContentMainActivity.this);
                librosPublicados.setLayoutManager(layoutManager2);
                librosPublicados.setHasFixedSize(true);
                librosPublicados.setAdapter(adapter2);
            }
        }

        /**
         * Hacemos que el menu inferior sepa que estamos en este Activity
         */
        btnNavegacion = (BottomNavigationView)findViewById(R.id.btnNavegacion);
        btnNavegacion.setOnNavigationItemSelectedListener(this);


    } //fin onCreate

    /**
     * Opciones del menu inferior
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        btnNavegacion.postDelayed(() -> {
            int itemId = item.getItemId();
            Intent intent;
            if(itemId == R.id.irPerfil){
                intent = new Intent(this, PerfilActivity.class);
                startActivity(intent);
            }
            if(itemId == R.id.irPrincipal){
                intent = new Intent(this, ContentMainActivity.class);
                startActivity(intent);
            }
            if(itemId == R.id.irLibros){
                intent = new Intent(this, LibrosActivity.class);
                startActivity(intent);
            }
            finish();
        },300);
        return true;
    }


    @Override
    int getBottomNavigationMenuItemId() {
        return R.id.irPrincipal;
    }

    @Override
    int getLayoutId() {
        return R.layout.content_main;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actualizar,menu);
        return true;
    }

    /**
     * Permite averiguar que item se selecciono del menu superior
     * del activity para poder actualizar los libros publicados en este caso
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        /**
         * Lo que hace es llamar al metodo unUpgrade() para resetear la base de datos interna
         * y con el metodo obtenerDatos() inserto todos los datos en la base de datos interna
         */
        if(id == R.id.btnActualizarLibros){
            Toast.makeText(ContentMainActivity.this, "Actualizando la lista de libros publicados", Toast.LENGTH_SHORT).show();
            LibroBD bd = new LibroBD(this);
            bd.onUpgradeActualizar(bd.getWritableDatabase(),1,2);
            bd.insertarLibros();
            bd.insertarGenerosLiterarios();
            bd.insertarValoraciones();
            bd.insertarPaginas();
            bd.insertarUsuario();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(ContentMainActivity.this, ContentMainActivity.class);
                    startActivity(intent);
                }
            }, 2000);
            return true;
        }

        if(id == R.id.ordenar){
            LibroBD bd = new LibroBD(this);
            bd.onUpgradeActualizar(bd.getWritableDatabase(),1,2);
            bd.insertarLibros();
            bd.insertarGenerosLiterarios();
            bd.insertarValoraciones();
            bd.insertarPaginas();
            bd.insertarUsuario();
            ordenarPorGenero();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();  // optional depending on your needs
        Intent intent = new Intent(ContentMainActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public void ordenarPorGenero(){
        final CharSequence opcionesGenero[] = new CharSequence[]{"Ciencia ficción","Terror","Humor","Cómics","Poesía","Teatro","Romanticismo","Aventuras","Misterio","Fantasía","Historia","Guerra","Ensayo","Todos"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Filtrar por Genero: ")
                .setItems(opcionesGenero,(dialog, which) -> {
                    switch (which){
                        case 0:
                            aplicarGenero("Ciencia ficción");
                            break;
                        case 1:
                            aplicarGenero("Terror");
                            break;
                        case 2:
                            aplicarGenero("Humor");
                            break;
                        case 3:
                            aplicarGenero("Cómics");
                            break;
                        case 4:
                            aplicarGenero("Poesía");
                            break;
                        case 5:
                            aplicarGenero("Teatro");
                            break;
                        case 6:
                            aplicarGenero("Romanticismo");
                            break;
                        case 7:
                            aplicarGenero("Aventuras");
                            break;
                        case 8:
                            aplicarGenero("Misterio");
                            break;
                        case 9:
                            aplicarGenero("Fantasía");
                            break;
                        case 10:
                            aplicarGenero("Historia");
                            break;
                        case 11:
                            aplicarGenero("Guerra");
                            break;
                        case 12:
                            aplicarGenero("Ensayo");
                            break;
                        case 13:
                            Toast.makeText(this, "Cargando", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(ContentMainActivity.this, ContentMainActivity.class);
                                    startActivity(intent);
                                }
                            }, 2000);
                            break;
                    }
                });
        builder.create().show();
    }

    public void aplicarGenero(String genero){
        Toast.makeText(this, "Cargando", Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(ContentMainActivity.this, ContentMainActivity.class);
                intent.putExtra("Genero",true);
                intent.putExtra("valor",genero);
                startActivity(intent);
            }
        }, 2000);
    }

}
