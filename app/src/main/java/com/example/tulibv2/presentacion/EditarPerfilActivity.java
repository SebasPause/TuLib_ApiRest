package com.example.tulibv2.presentacion;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.tulibv2.R;
import com.example.tulibv2.datos.LibroBD;
import com.example.tulibv2.entities.Usuarios;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class EditarPerfilActivity extends AppCompatActivity {

    ImageView imgEditarPerfil;
    ActionBar actionBar;
    EditText txtAutor,txtDescripcion,txtEdad;
    Button btnGuardarDatosPerfil;
    Uri uri;
    Usuarios usuario;
    byte[] inputData;
    byte[] byteArray;
    String foto;
    String encodedImage;

    //Para la foto de perfil
    private static final int REQUEST_PERMISION_CAMERA = 1;
    private static final int REQUEST_IMAGE_CAMERA = 2;
    private static final int REQUEST_PERMISION_EXTERNAL_STORAGE = 3;
    private static final int REQUEST_IMAGE_GALERY = 4;
    private static boolean valor = false;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        //Para la flecha de volver atras
        actionBar = getSupportActionBar();
        actionBar.setTitle("Editar Perfil");
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        txtAutor = (EditText) findViewById(R.id.txtAutor);
        txtDescripcion = (EditText) findViewById(R.id.txtDescripcion);
        txtEdad = (EditText) findViewById(R.id.txtAge);
        btnGuardarDatosPerfil = (Button) findViewById(R.id.btnGuardarDatosPerfil);
        imgEditarPerfil = (ImageView) findViewById(R.id.imgEditarPerfil);

        imgEditarPerfil.setOnClickListener(onClickListener);

        /**
         * Obtengo los datos existentes en la base de datos externa
         * y los aplico en sus correspondientes campos
         */
        LibroBD bd = new LibroBD(this);
        usuario = new Usuarios();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            usuario = bd.getUsuario();
            txtAutor.setText(usuario.fullName);
            if((usuario.description).equals(null)){
                //nada
            }else{
                txtDescripcion.setText(usuario.description);
            }
            if(String.valueOf(usuario.age).equals("0")){
                //nada
            }else{
                txtEdad.setText(String.valueOf(usuario.age));
            }


            if (usuario.image.equals("null")) {
                uri = null;
                uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + EditarPerfilActivity.this.getResources().getResourcePackageName(R.drawable.ic_person)
                        + '/' + EditarPerfilActivity.this.getResources().getResourceTypeName(R.drawable.ic_person)
                        + '/' + EditarPerfilActivity.this.getResources().getResourceEntryName(R.drawable.ic_person)
                );
                imgEditarPerfil.setImageURI(uri);
            } else {
                byte[] decodedString = Base64.decode(usuario.image, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                imgEditarPerfil.setImageBitmap(decodedByte);
                uri = Uri.parse(imgEditarPerfil.toString());
            }

        }





        /**
         * Al pulsar el boton de guardar datos del perfil
         * recogera todos los datos del layout y utilizara el metodo
         * updateChildren para actualizar esos datos en la base de datos externa
         */
        btnGuardarDatosPerfil.setOnClickListener(v -> {
            String autor = txtAutor.getText().toString();
            String descripcion = txtDescripcion.getText().toString();
            String edad = txtEdad.getText().toString();

            if(uri == null){
                uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + EditarPerfilActivity.this.getResources().getResourcePackageName(R.drawable.ic_person)
                        + '/' + EditarPerfilActivity.this.getResources().getResourceTypeName(R.drawable.ic_person)
                        + '/' + EditarPerfilActivity.this.getResources().getResourceEntryName(R.drawable.ic_person)
                );
            }else{
                Bitmap image = ((BitmapDrawable) imgEditarPerfil.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

            }

            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    bd.subirFoto(usuario.getId(),encodedImage,autor,descripcion,Integer.parseInt(edad),"Bearer "+bd.getTokenUsuario().getToken());
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            bd.setFoto(usuario.getId(),encodedImage,autor,descripcion,edad);
            Toast.makeText(this, "Datos modificados correctamente", Toast.LENGTH_LONG).show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                    Intent intent = new Intent(EditarPerfilActivity.this, PerfilActivity.class);
                    startActivity(intent);
                }
            }, 2000);
        });


    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    //Para volver atras
    @Override
    public boolean onSupportNavigateUp(){
        finish();
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    //FOTO PERFIL
    public View.OnClickListener onClickListener = v -> {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditarPerfilActivity.this);
        builder.setMessage("Elige una opcion")
                .setPositiveButton("CAMARA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Compruebo si tiene permisos
                        if(ActivityCompat.checkSelfPermission(EditarPerfilActivity.this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                            irCamara();
                        }else{
                            //Si no tiene permisos uso el requestPermissions
                            ActivityCompat.requestPermissions(EditarPerfilActivity.this,new String[]{Manifest.permission.CAMERA},REQUEST_PERMISION_CAMERA);
                        }
                    }
                })
                .setNegativeButton("GALERIA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //compruebo si tiene permisos de acceder a los archivos
                        if(ActivityCompat.checkSelfPermission(EditarPerfilActivity.this,Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                            irGaleria();
                        }else{
                            //Pido los permisos
                            ActivityCompat.requestPermissions(EditarPerfilActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},REQUEST_PERMISION_EXTERNAL_STORAGE);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }; //fin onClickListener

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
                    imgEditarPerfil.setImageBitmap(bitmap);
                    valor = true;
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
                imgEditarPerfil.setImageURI(imageUri);
                int rotateImage = getCameraPhotoOrientation(this, uri);
                imgEditarPerfil.setRotation(rotateImage);
                valor = true;
            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = resultado.getError();
                Toast.makeText(this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }

    } //fin onActivityResult

    public int getCameraPhotoOrientation(Context context, Uri imageUri) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            ExifInterface exif = new ExifInterface(uri.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }


    public void irCamara(){
        ContentValues valores = new ContentValues();
        valores.put(MediaStore.Images.Media.TITLE,"Titulo de la imagen");
        valores.put(MediaStore.Images.Media.DESCRIPTION,"Descripcion de la imagen");
        uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,valores);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        startActivityForResult(intent,REQUEST_IMAGE_CAMERA);
        int rotateImage = getCameraPhotoOrientation(this, uri);
        imgEditarPerfil.setRotation(rotateImage);
    } // fin irCamara()

    public void irGaleria(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");

        startActivityForResult(intent,REQUEST_IMAGE_GALERY);
    }

}