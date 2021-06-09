package com.example.tulibv2.presentacion;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.tulibv2.R;
import com.example.tulibv2.datos.LibroBD;
import com.example.tulibv2.entities.Libros;
import com.example.tulibv2.entities.Usuarios;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AnadirLibroActivity extends AppCompatActivity {

    //Declaro variables
    private String usuario;
    private String id;
    ActionBar actionBar;
    FloatingActionButton anadirLibro;
    EditText txtAutor,txtDescripcion,txtTitulo;
    Spinner spinnerGeneros;
    ImageView imgAnadirLibro;
    Uri uri;
    List<Libros> listaLibrosPublicados;
    LibroBD bd;
    byte[] byteArray;
    String genero;
    String foto;

    //Para la foto del libro
    private static final int REQUEST_PERMISION_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private static final int REQUEST_PERMISION_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_IMAGE_GALERY = 4;


    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anadir_libro);

        /**
         * Obtengo los objetos que utilizare del layout
         */
        anadirLibro = findViewById(R.id.guardarLibro);
        txtAutor = findViewById(R.id.txtAutor);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        spinnerGeneros = findViewById(R.id.spinnerGeneros);
        txtTitulo = findViewById(R.id.txtTitulo);
        imgAnadirLibro = findViewById(R.id.imgAnadirLibro);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.generos_literarios, android.R.layout.simple_spinner_dropdown_item);
        spinnerGeneros.setAdapter(adapter);
        spinnerGeneros.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genero = spinnerGeneros.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(uri==null) {
            imgAnadirLibro.setImageURI(  Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + this.getResources().getResourcePackageName(R.drawable.ic_book)
                    + '/' + this.getResources().getResourceTypeName(R.drawable.ic_book)
                    + '/' + this.getResources().getResourceEntryName(R.drawable.ic_book)
            ));
        }

        Bundle extras = getIntent().getExtras();
        /**
         * Si al pasar de activity se recibe en extras
         * el key de "AnadirLibro"
         * significa que habra que realizar esa opcion.
         * Sino habra que realizar la modificacion
         * de los datos de un libro ya existente
         */
        if(extras.getBoolean("AnadirLibro")){
            /**
             * Obtengo la barra superior,
             * establezco el titulo
             * y habilito la flecha de volver hacia atras
             */
            actionBar = getSupportActionBar();
            actionBar.setTitle("Añadir Libro");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }else{
            /**
             * Obtengo la barra superior,
             * establezco el titulo
             * y habilito la flecha de volver hacia atras.
             */
            actionBar = getSupportActionBar();
            actionBar.setTitle("Editar Libro");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
            /**
             * cargarDatos() => metodo que obtiene los datos relacionados con el libro
             * y procede a hacer un set en cada campo que corresponde
             * cargarImagen() => metodo que obtiene del Storage la foto almacenada al crear el libro
             * y procede a hacer un set en la imagen para que se pueda observar
             */
            cargarDatos(extras.getString("IDlibro"),extras.getString("genero"));
        }

        /**
         * Boton con el cual se interactua
         */
        anadirLibro.setOnClickListener(v -> {
            /**
              * Como anteriormente, si el key es para Añadir un nuevo libro
             */
            if (extras.getBoolean("AnadirLibro")) {
                /**
                 * Obtengo los datos del layout para poder
                 * guardarlos en la base de datos externa
                 */
                if (txtAutor.getText().toString().equals(null) || txtDescripcion.getText().toString().equals(null) || genero.equals(null) || txtTitulo.getText().toString().equals(null)) {
                    Toast.makeText(this, "Tienes que completar todos los campos", Toast.LENGTH_SHORT).show();
                } else {
                    String autor = txtAutor.getText().toString();
                    String descripcion = txtDescripcion.getText().toString();
                    String titulo = txtTitulo.getText().toString();

                    /**
                     * En caso de que la uri sea null
                     * se le asignara ic_book
                     */
                    if(uri==null){
                        uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                                + "://" + this.getResources().getResourcePackageName(R.drawable.ic_book)
                                + '/' + this.getResources().getResourceTypeName(R.drawable.ic_book)
                                + '/' + this.getResources().getResourceEntryName(R.drawable.ic_book)
                        );
                    }else{
                        uri = Uri.parse(imgAnadirLibro.toString());
                        Bitmap image = ((BitmapDrawable) imgAnadirLibro.getDrawable()).getBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        foto = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    }

                    Usuarios usuario = new Usuarios();
                    bd = new LibroBD(this);
                    usuario = bd.getUsuario();
                    bd.subirLibro(usuario.id,titulo,autor,descripcion,"Sin Publicar",false,foto,genero);
                    Toast.makeText(this, "Libro creado,cargando datos...", Toast.LENGTH_LONG).show();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(AnadirLibroActivity.this, LibrosActivity.class);
                            startActivity(intent);
                        }
                    }, 3000);

                }
            } //fin extras null
            else {
                /**
                 * En caso de que la opción haya sido la de editar el libro,
                 * obtengo todas las referencias a la base de datos(Storage y DatabaseReference).
                 * Obtengo tambien los datos introducidos en los campos
                 */
                if(uri!=null){
                    char first = uri.toString().charAt(0);
                    String letra = String.valueOf(first);
                    if(letra.equals("a")) {
                        foto = uri.toString();
                    }else{
                        Bitmap image = ((BitmapDrawable) imgAnadirLibro.getDrawable()).getBitmap();
                        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                        image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                        byteArray = byteArrayOutputStream.toByteArray();
                        foto = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    }
                }else{
                    Bitmap image = ((BitmapDrawable) imgAnadirLibro.getDrawable()).getBitmap();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                    byteArray = byteArrayOutputStream.toByteArray();
                    foto = Base64.encodeToString(byteArray, Base64.DEFAULT);
                }


                String idLibro = extras.getString("IDlibro");
                Usuarios usuario = new Usuarios();
                bd = new LibroBD(this);
                usuario = bd.getUsuario();
                bd.actualizarInfoLibro(usuario.id,idLibro,txtTitulo.getText().toString(),txtAutor.getText().toString(),txtDescripcion.getText().toString(),foto,bd.getTokenUsuario().getToken());
                bd.actualizarGenero(extras.getString("IDlibro"),genero,bd.getTokenUsuario().getToken());
                Toast.makeText(this, "Actualizando datos del libro...", Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(AnadirLibroActivity.this, LibrosActivity.class);
                        startActivity(intent);
                    }
                }, 2000);
            }
        });

        imgAnadirLibro.setOnClickListener(e -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(AnadirLibroActivity.this);
            builder.setMessage("Elige una opcion")
                    .setPositiveButton("CAMARA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Compruebo si tiene permisos
                            if (ActivityCompat.checkSelfPermission(AnadirLibroActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                irCamara();
                            } else {
                                //Si no tiene permisos uso el requestPermissions
                                ActivityCompat.requestPermissions(AnadirLibroActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_PERMISION_CAMERA);
                            }
                        }
                    })
                    .setNegativeButton("GALERIA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //compruebo si tiene permisos de acceder a los archivos
                            if (ActivityCompat.checkSelfPermission(AnadirLibroActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                irGaleria();
                            } else {
                                //Pido los permisos
                                ActivityCompat.requestPermissions(AnadirLibroActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISION_EXTERNAL_STORAGE);
                            }
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }); //fin foto setonclicklistener

    } //fin onCreate

    /**
     * metodo que obtiene los datos relacionados con el libro
     * y procede a hacer un set en cada campo que corresponde
     * @param idLibro
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public void cargarDatos(String idLibro,String generoLibro){
        LibroBD bd = new LibroBD(this);
        Libros libro = new Libros();
        libro = bd.getLibro(idLibro);
        txtAutor.setText(libro.autor);
        txtTitulo.setText(libro.titulo);
        txtDescripcion.setText(libro.descripcion);
        Log.i("ASK","Genero libro: "+generoLibro);
        switch (generoLibro){
            case "Ciencia ficción":
                spinnerGeneros.setSelection(0);
                break;
            case "Terror":
                spinnerGeneros.setSelection(1);
                break;
            case "Humor":
                spinnerGeneros.setSelection(2);
                break;
            case "Cómics":
                spinnerGeneros.setSelection(3);
                break;
            case "Poesía":
                spinnerGeneros.setSelection(4);
                break;
            case "Teatro":
                spinnerGeneros.setSelection(5);
                break;
            case "Romanticismo":
                spinnerGeneros.setSelection(6);
                break;
            case "Aventuras":
                spinnerGeneros.setSelection(7);
                break;
            case "Misterio":
                spinnerGeneros.setSelection(8);
                break;
            case "Fantasía":
                spinnerGeneros.setSelection(9);
                break;
            case "Historia":
                spinnerGeneros.setSelection(10);
                break;
            case "Guerra":
                spinnerGeneros.setSelection(11);
                break;
            case "Ensayo":
                spinnerGeneros.setSelection(12);
                break;
        }

        if(libro.photo.equals(" ")){
            //nada
            uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + AnadirLibroActivity.this.getResources().getResourcePackageName(R.drawable.ic_book)
                    + '/' + AnadirLibroActivity.this.getResources().getResourceTypeName(R.drawable.ic_book)
                    + '/' + AnadirLibroActivity.this.getResources().getResourceEntryName(R.drawable.ic_book)
            );
            imgAnadirLibro.setImageURI(uri);
        }else{
            char first = libro.photo.charAt(0);
            String letra = String.valueOf(first);
            if(letra.equals("a")) {
                uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + this.getResources().getResourcePackageName(R.drawable.ic_book)
                        + '/' + this.getResources().getResourceTypeName(R.drawable.ic_book)
                        + '/' + this.getResources().getResourceEntryName(R.drawable.ic_book)
                );
                imgAnadirLibro.setImageURI(uri);
            }
            else{
                byte[] decodedString = Base64.decode(libro.photo, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgAnadirLibro.setImageBitmap(decodedByte);
            }

        }


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_PERMISION_CAMERA){
            //Si el usuario permite los permisos
            if(permissions.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                irCamara();
            }else{
                Toast.makeText(this, "Acepta los permisos para continuar", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == REQUEST_PERMISION_EXTERNAL_STORAGE){
            if(permissions.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                irGaleria();
            }else{
                Toast.makeText(this, "Acepta los permisos para continuar", Toast.LENGTH_SHORT).show();
            }
        }
    } //fin onrequest

    //Compruebo si se lanza el intent de la camara y compruebo si se ha tomado foto o no
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_IMAGE_CAMERA){
            if(resultCode== Activity.RESULT_OK){
                Bitmap bitmap;
                try{
                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
                    imgAnadirLibro.setImageBitmap(bitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        if(requestCode == REQUEST_IMAGE_GALERY){
            if(resultCode==Activity.RESULT_OK){
                CropImage.activity(data.getData())
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(this);
            }
            else{
                Toast.makeText(this, "No se ha seleccionado ninguna foto", Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult resultado = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = resultado.getUri();
                uri = imageUri;
                imgAnadirLibro.setImageURI(imageUri);
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = resultado.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }

    } //fin onActivityResult


    public void irCamara(){
        ContentValues valores = new ContentValues();
        valores.put(MediaStore.Images.Media.TITLE,"Titulo de la imagen");
        valores.put(MediaStore.Images.Media.DESCRIPTION,"Descripcion de la imagen");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,valores);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent,REQUEST_IMAGE_CAMERA);

    } // fin irCamara()

    public void irGaleria(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent,REQUEST_IMAGE_GALERY);
    }


    /**
     * Metodo que permite volver hacia atras cuando
     * la flecha del action bar haya sido pulsada
     * @return
     */
    @Override
    public boolean onSupportNavigateUp(){
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
