package com.example.tulibv2.presentacion;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tulibv2.R;
//import com.example.tulibv2.casosdeuso.DatosPerfil;
import com.example.tulibv2.datos.LibroBD;
import com.example.tulibv2.entities.Usuarios;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;

import kotlin.text.Charsets;

public class PerfilActivity extends BaseActivity implements Serializable {

    ImageView imgEditarPerfil;
    TextView txtAutor,txtDescripcion;
    BottomNavigationView btnNavegacion;
    RatingBar ratingBar;
    private String usuario;
    Uri uri;
    ActionBar actionBar;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        /**
         * Datos relacionados al menu superior
         */
        actionBar = getSupportActionBar();
        actionBar.setTitle("Perfil");

        txtAutor = (TextView)findViewById(R.id.txtAutor);
        txtDescripcion = (TextView)findViewById(R.id.txtDescripcion);
        imgEditarPerfil = (ImageView)findViewById(R.id.imgEditarPerfil);
        ratingBar = (RatingBar)findViewById(R.id.ratingBar);

        /**
         * Establezco el rating del perfil a 0
         * e indico que solo es un indicador y que no se puede modificar manualmente
         */
        ratingBar.setRating(0.0f);
        ratingBar.setIsIndicator(true);

        LibroBD bd = new LibroBD(this);
        Usuarios usuario = new Usuarios();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            usuario = bd.getUsuario();
            txtAutor.setText(usuario.fullName);
            if(usuario.description.equals(null)){
                //nada
            }else{
                txtDescripcion.setText(usuario.description);
            }
            if(usuario.image.equals("null")){
                //nada
                uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + PerfilActivity.this.getResources().getResourcePackageName(R.drawable.ic_person)
                        + '/' + PerfilActivity.this.getResources().getResourceTypeName(R.drawable.ic_person)
                        + '/' + PerfilActivity.this.getResources().getResourceEntryName(R.drawable.ic_person)
                );
                imgEditarPerfil.setImageURI(uri);
            }else{
                byte[] decodedString = Base64.decode(usuario.image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgEditarPerfil.setImageBitmap(decodedByte);
            }

            ratingBar.setRating(bd.cargarRatingPerfil(usuario.id));
        }

        /**
         * Indico al menu inferior que estoy en este Activity
         */
        btnNavegacion = (BottomNavigationView)findViewById(R.id.btnNavegacion);
        btnNavegacion.setOnNavigationItemSelectedListener(this);

    }

    /**
     * Metodo para actualizar el estado del menu inferior
     */
    private void updateNavigationBarState(){
        int actionId = getBottomNavigationMenuItemId();
        selectedBottomNavigationBarItem(actionId);
    }

    /**
     * Metodo para decirle al menu inferior que estamos en este Activity
     * @param itemId
     */
    void selectedBottomNavigationBarItem(int itemId){
        MenuItem item = btnNavegacion.getMenu().findItem(itemId);
        item.setChecked(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_perfil,menu);
        return true;
    }

    /**
     * Opciones del menu superior que llevan al ativity de editar el perfil
     * o a cerrar sesion(Esta opcion puede no funcionar si se viene desde algun otro activity
     * ya que supuestamente al cambiar de intents, su correspondiente activity deberia pasar por el onDestroy()
     * pero esto no sucede y al hacer click en esta opcion se vuelve a ese activity)
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.btnEditarPerfil){
            Intent intent = new Intent(this, EditarPerfilActivity.class);
            startActivity(intent);
            return true;
        }
        if(id == R.id.btnCerrarSesion){
            Toast.makeText(this, "Vuelva pronto", Toast.LENGTH_SHORT).show();
            finish();
            Intent i =  new Intent(PerfilActivity.this,MainActivity.class);
            i.putExtra("Codigo","cerrarSesion");
            startActivity(i);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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
        return R.id.irPerfil;
    }

    @Override
    int getLayoutId() {
        return R.layout.perfil;
    }

}