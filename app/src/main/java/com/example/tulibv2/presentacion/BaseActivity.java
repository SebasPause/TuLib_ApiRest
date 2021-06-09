package com.example.tulibv2.presentacion;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tulibv2.R;
//import com.example.tulibv2.presentacion.PerfilActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Clase utilizada para realizar el menu de navegacion inferior
 * y para poder interactuar con los elementos que tiene
 */
public abstract class BaseActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    BottomNavigationView btnNavegacion;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        btnNavegacion = (BottomNavigationView)findViewById(R.id.btnNavegacion);
        btnNavegacion.setOnNavigationItemSelectedListener(this);

    }


    @Override
    protected void onStart() {
        super.onStart();
        updateNavigationBarState();
        overridePendingTransition(0, 0);
    }

    /**
     * Dependiendo el item seleccionado, se creara una nueva activity
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        btnNavegacion.postDelayed(() -> {
            int itemId = item.getItemId();
            Intent intent;
            if(itemId == R.id.irPerfil){
                //intent = new Intent(this, PerfilActivity.class);
                //startActivity(intent);
            }
            if(itemId == R.id.irPrincipal){
                intent = new Intent(this, ContentMainActivity.class);
                startActivity(intent);
            }
            if(itemId == R.id.irLibros){
                //intent = new Intent(this, LibrosActivity.class);
                //startActivity(intent);
            }
            finish();
        },300);
        return true;
    }

    /**
     * Metodo para actualizar el estado del menu
     */
    private void updateNavigationBarState(){
        int actionId = getBottomNavigationMenuItemId();
        selectedBottomNavigationBarItem(actionId);
    }

    /**
     * Metodo que permite obtener el item seleccionado del menu
     * @param itemId
     */
    void selectedBottomNavigationBarItem(int itemId){
        MenuItem item = btnNavegacion.getMenu().findItem(itemId);
        item.setChecked(true);
    }

    abstract int getBottomNavigationMenuItemId();

    abstract int getLayoutId();

}