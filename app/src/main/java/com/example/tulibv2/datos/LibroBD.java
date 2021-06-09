package com.example.tulibv2.datos;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.AbstractWindowedCursor;
import android.database.Cursor;
import android.database.CursorWindow;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.tulibv2.casosdeuso.ApiClient;
import com.example.tulibv2.entities.GenerosLiterarios;
import com.example.tulibv2.entities.Libros;
import com.example.tulibv2.entities.Pagina;
import com.example.tulibv2.entities.TokenUsuario;
import com.example.tulibv2.entities.Usuarios;
import com.example.tulibv2.entities.Valoraciones;
import com.example.tulibv2.presentacion.AnadirLibroActivity;
import com.example.tulibv2.presentacion.ContentMainActivity;
import com.example.tulibv2.userServices.ValoracionRequest;
import com.example.tulibv2.userServices.ValoracionResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import kotlin.text.Charsets;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.Body;

public class LibroBD extends SQLiteOpenHelper {


    ContentValues valoresUsuarios,valoresPerfil,valoresLibros,valoresPaginas,valoresValoraciones;
    boolean existeUsuario = false;

    /**
     * Constructor al cual se le pasa el contexto del cual es llamado
     * @param context
     */
    public LibroBD(@Nullable Context context) {
        super(context, ConstantesBD.BD_NAME, null, 1);
    }

    /**
     * Metodo que crea las tablas de la base de datos interna SQLiteDatabase
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ConstantesBD.CREATE_TABLE_TOKEN);
        db.execSQL(ConstantesBD.CREATE_TABLE_VALORACIONES);
        db.execSQL(ConstantesBD.CREATE_TABLE_PAGINAS);
        db.execSQL(ConstantesBD.CREATE_TABLE_LIBROS);
        db.execSQL(ConstantesBD.CREATE_TABLE_GENEROSLITERARIOS);
        db.execSQL(ConstantesBD.CREATE_TABLE_USUARIOS);
    }

    public void onCreateActualizar(SQLiteDatabase db) {
        db.execSQL(ConstantesBD.CREATE_TABLE_VALORACIONES);
        db.execSQL(ConstantesBD.CREATE_TABLE_PAGINAS);
        db.execSQL(ConstantesBD.CREATE_TABLE_LIBROS);
        db.execSQL(ConstantesBD.CREATE_TABLE_GENEROSLITERARIOS);
    }

    /**
     * Metodo que sirve para borrar las tablas existentes o no
     * para actualizar los datos de la base de datos externa(firebase)
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Volver a crear la tabla
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_TOKEN);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_VALORACIONES);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_PAGINAS);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_LIBROS);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_GENEROSLITERARIOS);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_USUARIOS);
        onCreate(db);
    }

    public void onUpgradeActualizar(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Volver a crear la tabla
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_VALORACIONES);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_PAGINAS);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_LIBROS);
        db.execSQL("DROP TABLE IF EXISTS " + ConstantesBD.TABLE_NAME_GENEROSLITERARIOS);
        onCreateActualizar(db);
    }



    /**
     * Metodo para borrar los libros de la tabla
     * ya que al pasar entre intents se producen duplicaciones
     * y por lo tanto es necesario borrar datos
     * que luego son obtenidos de nuevo cuando son requeridos
     */
    public void borrarLibros(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ConstantesBD.TABLE_NAME_LIBROS);
        db.close();
    }

    public void borrarPaginas(){
        SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("DELETE FROM "+ConstantesBD.TABLE_NAME_PAGINAS);
        db.close();
    }

    /**
     * Metodo para obtener todas las valoraciones despues de que el metodo obtenerDatos() haya sido ejecutado
     * A este metodo se le pasa un parametro que permite hacer una consulta sobre la tabla valoraciones
     * con el fin de crear un nuevo objeto que
     * a su vez se añadira a una lista para poder iterar sobre ella
     * mostrando cada objeto en un cardview
     * @param  id
     *//**
    @RequiresApi(api = Build.VERSION_CODES.P)
    public ArrayList<Valoracion> devolverValoraciones(String id){
        ArrayList<Valoracion> valoraciones = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+ " WHERE ID_VALORACIONES LIKE '"+id+"'";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                valoraciones.add(new Valoracion(
                        cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_COMENTARIO)),
                        cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_VALOR))
                ));
            }while(cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return valoraciones;
    }*/

    /**
     * Metodo el cual recibe el id del libro
     * que permite hacer una consulta sobre la tabla Paginas
     * donde se obtienen las paginas y el contenido de cada pagina
     * para poder mostrarla cuando se invoque la opcion
     * de ver libro o editar libro
     * En este caso se devuelve un HashMap con parametros String ya que los dos valores
     * que son necesarios de la tabla, se han introducido con ese formato
     * @param id
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public HashMap<String,String> cargarPaginasLibro(String id){
        HashMap<String,String> paginasLibro = new HashMap<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM Paginas WHERE ID_PAGINAS LIKE '"+id+"'";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                String nr = cursor.getString(cursor.getColumnIndex(ConstantesBD.PA_NUMERO_PAGINA));
                String contenido= cursor.getString(cursor.getColumnIndex(ConstantesBD.PA_CONTENIDO));;
                paginasLibro.put(nr,contenido);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return paginasLibro;
    }

    //Metodo para cargar el comentario y la valoracion del libro el cual hemos comentado

    /**
     * Metodo que establece el comentario que hemos echo sobre un libro
     * para darnos cuenta que ya hemos echo ese paso
     * con el fin de informar al usuario que puede eliminar el comentario
     * y volver a insertar uno nuevo si asi lo desea
     * Si no ha echo ningun comentario, los dos campos establecidos estaran vacios
     * @param usuarioID
     * @param idLibro
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public HashMap<String,String> cargarComentario(String usuarioID,String idLibro){
        HashMap<String,String> comentarios = new HashMap<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+" va,"+ConstantesBD.TABLE_NAME_LIBROS+" li WHERE va."+ConstantesBD.VA_LIBROID+" LIKE '"+idLibro+"' AND li."+ConstantesBD.L_USUARIO_LIBRO+" LIKE '"+usuarioID+"';";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                String comentario = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_COMENTARIO));
                String valor = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_VALOR));
                comentarios.put("Comentario",comentario);
                comentarios.put("Valor",valor);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return comentarios;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public String getValoracionID(String usuarioId, String libroID){
        String valoracionID = "";

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+" va,"+ConstantesBD.TABLE_NAME_LIBROS+" li WHERE va."+ConstantesBD.VA_LIBROID+" LIKE '"+libroID+"' AND li."+ConstantesBD.L_USUARIO_LIBRO+" LIKE '"+usuarioId+"';";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                valoracionID = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_VALORACIONID));
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return valoracionID;
    }

    /**
     * Metodo al cual se le pasa el parametro del usuario actual
     * para poder establecer el ratingBar del Perfil del usuario
     * haciendo una consulta a la tabla de valoraciones
     * donde la columna de "USUARIOLIBRO" de la tabla
     * devuelva todos los comentarios que se han echo sobre
     * todos los libros publicados por dicho usuario
     * @param idUsuario
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.P)
    public Float cargarRatingPerfil(String idUsuario){
        Float rating = 0.0f;

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+" WHERE "+ConstantesBD.VA_LIBROID+"=(SELECT ID_LIBRO FROM Libros WHERE USUARIOLIBRO='"+idUsuario+"');";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        float valores = 0.0f;
        int nrDeValores = 0;

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                String valor = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_VALOR));;
                valores = valores + Float.parseFloat(valor);
                nrDeValores += 1;
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        rating = valores / nrDeValores;

        return rating;
    }



    //Devolver usuarios de las valoraciones

    /**
     * Metodo que devuelve una lista de usuarios
     * donde la columna de id_valoraciones de la tabla valoraciones
     * sea el libro pasada por parametro.
     * Gracias a este metodo se puede saber si el usuario actual ya ha comentado
     * o no en el actual libro
     * @param id
     * @return
     */
    /*
    @RequiresApi(api = Build.VERSION_CODES.P)
    public List<String> devolverUsuarios(String id){
        List<String> usuarios =  new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+" WHERE ID_VALORACIONES LIKE '"+id+"'";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                String usuario = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_USUARIO));
                usuarios.add(usuario);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usuarios;
    }*/

    /**
     * Desde aqui empieza el codigo de la nueva app
     */
    public void setTokenUsuario(String token,String usuario,String usuarioID){
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO "+ConstantesBD.TABLE_NAME_TOKEN+" VALUES("+"'"+token+"','"+usuario+"','"+usuarioID+"');";
        db.execSQL(query);
        db.close();
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public TokenUsuario getTokenUsuario(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TA_TOKEN;
        TokenUsuario tokenUsuario = new TokenUsuario();

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                tokenUsuario.setToken(cursor.getString(cursor.getColumnIndex(ConstantesBD.TA_TOKEN)));
                tokenUsuario.setUsuario(cursor.getString(cursor.getColumnIndex(ConstantesBD.TA_USUARIO_ACTUAL)));
                tokenUsuario.setUsuarioID(cursor.getString(cursor.getColumnIndex(ConstantesBD.TA_USUARIO_ID)));
            }while(cursor.moveToNext());
        }


        cursor.close();
        db.close();

        return tokenUsuario;
    }

    /**
     *
     *
     *
     *
     * APARTADO DE INSERTAR DATOS EN LA BASE DE DATOS LOCAL
     *
     *
     *
     *
     */

    public void insertarLibros(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Call<ArrayList<Libros>> librosResponseCall = ApiClient.getUserService().getLibros("Bearer " + getTokenUsuario().getToken());
            librosResponseCall.enqueue(new Callback<ArrayList<Libros>>() {
                @Override
                public void onResponse(Call<ArrayList<Libros>> call, Response<ArrayList<Libros>> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                            for (int i = 0; i < response.body().size(); i++) {
                                String foto =" ";
                                String idLibro = response.body().get(i).bookID;
                                String titulo = response.body().get(i).titulo;
                                String autor = response.body().get(i).autor;
                                String descripcion = response.body().get(i).descripcion;
                                if(response.body().get(i).photo==null){
                                    //nada
                                }else{
                                    foto = response.body().get(i).photo.toString();
                                }
                                String publicado = String.valueOf(response.body().get(i).publicado);
                                String fechaPublicado = response.body().get(i).fechaPublicado;
                                String usuarioId = response.body().get(i).applicationUserId;
                                String estado = response.body().get(i).estado;

                                String query = "INSERT INTO " + ConstantesBD.TABLE_NAME_LIBROS + " VALUES(" + "'" + idLibro + "','" + titulo + "','" + autor + "','" + descripcion + "','" + foto + "','" + publicado + "','" + fechaPublicado + "','" + estado + "','" + usuarioId + "');";
                                SQLiteDatabase db = getWritableDatabase();
                                db.execSQL(query);
                                db.close();
                            }
                        } else {
                            Log.i("ASK", "Body es null");
                        }
                    } else {
                        Log.i("ASK", "Response is false");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Libros>> call, Throwable t) {
                    Log.i("ASK", "NO FUNCIONA");
                }
            });
        }
    }


    /**
     * Metodo que hace una llamada a un servicio para insertar todos los GenerosLiterarios
     */
    public void insertarGenerosLiterarios(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Call<ArrayList<GenerosLiterarios>> generosResponseCall = ApiClient.getUserService().getGenerosLiterarios("Bearer "+getTokenUsuario().getToken());
            generosResponseCall.enqueue(new Callback<ArrayList<GenerosLiterarios>>() {
                @Override
                public void onResponse(Call<ArrayList<GenerosLiterarios>> call, Response<ArrayList<GenerosLiterarios>> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            for(int i=0;i<response.body().size();i++){
                                String id= response.body().get(i).id;
                                String bookId= response.body().get(i).bookId;
                                String generoValor = response.body().get(i).genero;
                                String query = "INSERT INTO "+ConstantesBD.TABLE_NAME_GENEROSLITERARIOS+" VALUES("+"'"+id+"','"+bookId+"','"+generoValor+"');";
                                SQLiteDatabase db = getWritableDatabase();
                                db.execSQL(query);
                                db.close();
                            }
                        }else{
                            Log.i("ASK","Body es null");
                        }
                    }else {
                        Log.i("ASK","Response is false");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<GenerosLiterarios>> call, Throwable t) {
                    Log.i("ASK","NO FUNCIONA");
                }
            });

        }
    }

    /**
     * Metodo que hace una llamada a un servicio para insertar todas las Valoraciones
     */
    public void insertarValoraciones(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Call<ArrayList<Valoraciones>> generosResponseCall = ApiClient.getUserService().getValoraciones("Bearer "+getTokenUsuario().getToken());
            generosResponseCall.enqueue(new Callback<ArrayList<Valoraciones>>() {
                @Override
                public void onResponse(Call<ArrayList<Valoraciones>> call, Response<ArrayList<Valoraciones>> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            for(int i=0;i<response.body().size();i++){
                                String id = response.body().get(i).id;
                                String comentario = response.body().get(i).comentario;
                                String bookID = response.body().get(i).bookID;
                                int valor = response.body().get(i).valor;
                                String query = "INSERT INTO "+ConstantesBD.TABLE_NAME_VALORACIONES+" VALUES("+"'"+comentario+"','"+id+"','"+bookID+"',"+valor+");";
                                SQLiteDatabase db = getWritableDatabase();
                                db.execSQL(query);
                                db.close();
                            }
                        }else{
                            Log.i("ASK","Body es null");
                        }
                    }else {
                        Log.i("ASK","Response is false");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Valoraciones>> call, Throwable t) {
                    Log.i("ASK","NO FUNCIONA");
                }
            });

        }
    }

    /**
     * Metodo que hace una llamada a un servicio para insertar todas las Paginas
     */
    public void insertarPaginas(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Call<ArrayList<Pagina>> paginaResponseCall = ApiClient.getUserService().getPaginas("Bearer "+getTokenUsuario().getToken());
            paginaResponseCall.enqueue(new Callback<ArrayList<Pagina>>() {
                @Override
                public void onResponse(Call<ArrayList<Pagina>> call, Response<ArrayList<Pagina>> response) {
                    if(response.isSuccessful()){
                        if(response.body() != null){
                            for(int i=0;i<response.body().size();i++){
                                Log.i("ASK,","Rsponse is not null");
                                String id = response.body().get(i).bookId;
                                String contenido = response.body().get(i).contenido;
                                int nrPagina = response.body().get(i).nrPagina;
                                String bookID = response.body().get(i).bookId;
                                String query = "INSERT INTO "+ConstantesBD.TABLE_NAME_PAGINAS+" VALUES('"+nrPagina+"','"+contenido+"','"+id+"','"+bookID+"');";
                                SQLiteDatabase db = getWritableDatabase();
                                db.execSQL(query);
                                db.close();
                            }
                        }else{
                            Log.i("ASK","Body es null");
                        }
                    }else {
                        Log.i("ASK","Response is false");
                    }
                }

                @Override
                public void onFailure(Call<ArrayList<Pagina>> call, Throwable t) {
                    Log.i("ASK","NO FUNCIONA");
                }
            });

        }
    }

    public void insertarUsuario(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Call<Usuarios> usuariosResponseCall = ApiClient.getUserService().getUsuario("Bearer " + getTokenUsuario().getToken());
            usuariosResponseCall.enqueue(new Callback<Usuarios>() {
                @Override
                public void onResponse(Call<Usuarios> call, Response<Usuarios> response) {
                    if (response.isSuccessful()) {
                        if (response.body() != null) {
                                String idUsuario = response.body().id;
                                String autor = response.body().fullName;
                                String email = response.body().email;
                                String userName = response.body().userName;
                                int edad = response.body().age;
                                String descripcion = response.body().description;
                                String foto = "";
                                if(response.body().image==null){
                                    //nada
                                }else{
                                    foto = response.body().image;
                                }

                                String query = "INSERT INTO " + ConstantesBD.TABLE_NAME_USUARIOS + " VALUES(" + "'" + idUsuario + "','" + autor + "','" + email + "','" + userName + "'," + edad + ",'" + descripcion + "','" + foto +"');";
                                SQLiteDatabase db = getWritableDatabase();
                                db.execSQL(query);
                                db.close();

                        } else {
                            Log.i("ASK", "Body es null");
                        }
                    } else {
                        Log.i("ASK", "Response is false");
                    }
                }

                @Override
                public void onFailure(Call<Usuarios> call, Throwable t) {
                    Log.i("ASK", "NO FUNCIONA");
                }
            });
        }
    }


    /**
     *
     *
     *
     *
     * APARTADO DE DEVOLVER DATOS DE LA BASE DE DATOS LOCAL
     *
     *
     *
     *
     */

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ArrayList<Libros> getLibrosPublicados(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_LIBROS+";";
        ArrayList<Libros> librosPublicados = new ArrayList<>();

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                Libros libro = new Libros();
                libro.bookID = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ID_LIBRO));
                libro.titulo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_TITULO));
                libro.autor = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_AUTOR));
                libro.descripcion = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_DESCRIPCION));
                libro.photo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FOTO));
                libro.publicado = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(ConstantesBD.L_PUBLICADO)));
                libro.fechaPublicado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FECHA_PUBLICADO));
                libro.applicationUserId = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_USUARIO_LIBRO));
                libro.estado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ESTADO));
                librosPublicados.add(libro);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return librosPublicados;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public Libros getLibro(String bookID){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_LIBROS+" WHERE "+ConstantesBD.L_ID_LIBRO+"='"+bookID+"';";
        Libros libro = new Libros();

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                libro.bookID = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ID_LIBRO));
                libro.titulo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_TITULO));
                libro.autor = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_AUTOR));
                libro.descripcion = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_DESCRIPCION));
                libro.photo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FOTO));
                libro.publicado = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(ConstantesBD.L_PUBLICADO)));
                libro.fechaPublicado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FECHA_PUBLICADO));
                libro.applicationUserId = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_USUARIO_LIBRO));
                libro.estado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ESTADO));
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return libro;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ArrayList<Libros> getLibrosPublicadosByUserId(String userID){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_LIBROS+" WHERE "+ConstantesBD.L_USUARIO_LIBRO+"='"+userID+"';";
        ArrayList<Libros> librosPublicados = new ArrayList<>();

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                Libros libro = new Libros();
                libro.bookID = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ID_LIBRO));
                libro.titulo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_TITULO));
                libro.autor = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_AUTOR));
                libro.descripcion = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_DESCRIPCION));
                libro.photo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FOTO));
                libro.publicado = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(ConstantesBD.L_PUBLICADO)));
                libro.fechaPublicado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FECHA_PUBLICADO));
                libro.applicationUserId = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_USUARIO_LIBRO));
                libro.estado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ESTADO));
                librosPublicados.add(libro);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return librosPublicados;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ArrayList<Libros> getLibrosPublicadosAceptados(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_LIBROS+" WHERE Estado='Aceptado';";
        ArrayList<Libros> librosPublicados = new ArrayList<>();

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                Libros libro = new Libros();
                libro.bookID = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ID_LIBRO));
                libro.titulo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_TITULO));
                libro.autor = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_AUTOR));
                libro.descripcion = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_DESCRIPCION));
                libro.photo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FOTO));
                libro.publicado = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(ConstantesBD.L_PUBLICADO)));
                libro.fechaPublicado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FECHA_PUBLICADO));
                libro.applicationUserId = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_USUARIO_LIBRO));
                libro.estado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ESTADO));
                librosPublicados.add(libro);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return librosPublicados;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ArrayList<GenerosLiterarios> getGeneros(String genero){
        SQLiteDatabase db = getWritableDatabase();
        String queryGeneros = "SELECT * FROM "+ConstantesBD.TABLE_NAME_GENEROSLITERARIOS+" WHERE "+ConstantesBD.GL_GENEROVALOR+"='"+genero+"';";
        Cursor cursor;
        cursor = db.rawQuery(queryGeneros,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        ArrayList<GenerosLiterarios> generos = new ArrayList<>();

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                GenerosLiterarios generoLiterario = new GenerosLiterarios();
                generoLiterario.setBookId(cursor.getString(cursor.getColumnIndex(ConstantesBD.GL_LIBROID)));
                generos.add(generoLiterario);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return generos;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public ArrayList<Libros> getLibrosPublicadosAceptadosPorGenero(String genero){
        ArrayList<GenerosLiterarios> generos =  getGeneros(genero);
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_LIBROS+" WHERE Estado='Aceptado';";
        ArrayList<Libros> librosPublicados = new ArrayList<>();

        Cursor cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                Libros libro = new Libros();
                libro.bookID = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ID_LIBRO));
                libro.titulo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_TITULO));
                libro.autor = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_AUTOR));
                libro.descripcion = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_DESCRIPCION));
                libro.photo = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FOTO));
                libro.publicado = Boolean.getBoolean(cursor.getString(cursor.getColumnIndex(ConstantesBD.L_PUBLICADO)));
                libro.fechaPublicado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_FECHA_PUBLICADO));
                libro.applicationUserId = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_USUARIO_LIBRO));
                libro.estado = cursor.getString(cursor.getColumnIndex(ConstantesBD.L_ESTADO));
                for(int i=0;i<generos.size();i++){
                    if(libro.bookID.equals(generos.get(i).bookId)){
                        librosPublicados.add(libro);
                    }
                }
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return librosPublicados;
    }


    @RequiresApi(api = Build.VERSION_CODES.P)
    public float getValoracionByBookId(String bookID){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT "+ConstantesBD.VA_VALOR+" FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+" WHERE "+ConstantesBD.VA_LIBROID+"='"+bookID+"';";
        float valor = 0.0f;
        float valores = 0.0f;
        float resultado = 0.0f;

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                valor = valor + cursor.getInt(cursor.getColumnIndex(ConstantesBD.VA_VALOR));
                valores++;
            }while(cursor.moveToNext());
        }

        if(valor==0 &&valores==0){
            resultado = 0;
        }else{
            resultado = (valor / valores);
        }

        cursor.close();
        db.close();

        return resultado;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public List<Valoraciones> getValoracionesByBookId(String bookID){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_VALORACIONES+" WHERE "+ConstantesBD.VA_LIBROID+"='"+bookID+"';";
        List<Valoraciones> valoraciones = new ArrayList<>();

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                Valoraciones valoracion = new Valoraciones();
                valoracion.bookID = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_LIBROID));
                valoracion.id = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_VALORACIONID));
                valoracion.comentario = cursor.getString(cursor.getColumnIndex(ConstantesBD.VA_COMENTARIO));
                valoracion.valor = cursor.getInt(cursor.getColumnIndex(ConstantesBD.VA_VALOR));
                valoraciones.add(valoracion);
            }while(cursor.moveToNext());
        }


        cursor.close();
        db.close();

        return valoraciones;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public String getGeneroLiterarioByBookId(String bookID){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT "+ConstantesBD.GL_GENEROVALOR+" FROM "+ConstantesBD.TABLE_NAME_GENEROSLITERARIOS+" WHERE "+ConstantesBD.GL_LIBROID+"='"+bookID+"';";
        String generoValor ="";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                generoValor = cursor.getString(cursor.getColumnIndex(ConstantesBD.GL_GENEROVALOR));
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return generoValor;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public HashMap<String,String> getPaginasLibro(String bookID){
        HashMap<String,String> paginasLibro = new HashMap<>();
        String nr = "";
        String contenido = "";

        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_PAGINAS+" WHERE "+ConstantesBD.PA_BOOKID+"='"+bookID+"';";

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        Log.i("ASK","Cursor: "+cursor.getCount());
        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                nr = cursor.getString(cursor.getColumnIndex(ConstantesBD.PA_NUMERO_PAGINA));
                contenido = cursor.getString(cursor.getColumnIndex(ConstantesBD.PA_CONTENIDO));
                Log.i("ASK","Numero: "+nr+" Contenido: "+contenido);
                paginasLibro.put(nr,contenido);
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return paginasLibro;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public Usuarios getUsuario(){
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM "+ConstantesBD.TABLE_NAME_USUARIOS+";";
        Usuarios usuario = new Usuarios();

        Cursor cursor;
        cursor = db.rawQuery(query,null);
        CursorWindow cursorWindow = new CursorWindow("test",500000000);
        AbstractWindowedCursor ac = (AbstractWindowedCursor) cursor;
        ac.setWindow(cursorWindow);

        if(cursor.getCount()>0){
            cursor.moveToFirst();
            do{
                usuario.setId(cursor.getString(cursor.getColumnIndex(ConstantesBD.U_ID)));
                usuario.setFullName(cursor.getString(cursor.getColumnIndex(ConstantesBD.U_AUTOR)));
                usuario.setEmail(cursor.getString(cursor.getColumnIndex(ConstantesBD.U_EMAIL)));
                usuario.setUserName(cursor.getString(cursor.getColumnIndex(ConstantesBD.U_USERNAME)));
                usuario.setAge(cursor.getInt(cursor.getColumnIndex(ConstantesBD.U_EDAD)));
                usuario.setDescription(cursor.getString(cursor.getColumnIndex(ConstantesBD.U_DESCRIPCION)));
                usuario.setImage(cursor.getString(cursor.getColumnIndex(ConstantesBD.U_IMAGEN)));
            }while(cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return usuario;
    }

    /**
     *
     *
     *
     *
     * APARTADO DE SUBIR DATOS A LA BASE DE DATOS LOCAL
     *
     *
     *
     *
     */
    public void subirValoracion(String valoracion, int valor, String libroID,String idUsuario){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ValoracionRequest valoraciones = new ValoracionRequest();
            valoraciones.setBookID(libroID);
            valoraciones.setValor(valor);
            valoraciones.setComentario(valoracion);
            valoraciones.setId(UUID.randomUUID().toString());
            valoraciones.setIdUsuario(idUsuario);

            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().subirValoracion(valoraciones);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                        Log.i("ASK","Ha funcionado");
                    } else {
                        Log.i("ASK", "Response is false");
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                    Log.i("ASK", "NO FUNCIONA");
                }
            });
        }
    }

    public void subirLibro(String idUsuario,String titulo,String autor,String descripcion,String estado,boolean publicado,String foto,String genero){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Libros libro = new Libros();
            String libroID = UUID.randomUUID().toString();
            libro.setBookID(libroID);
            libro.setApplicationUserId(idUsuario);
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setDescripcion(descripcion);
            libro.setEstado(estado);
            libro.setPublicado(publicado);
            libro.setPhoto(foto);


            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().subirLibro(libro);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                        Log.i("ASK","Ha funcionado la subida del libro");
                        subirGenero(libroID,genero);
                    } else {
                        Log.i("ASK", "Response is false de la subida del libro");
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                    Log.i("ASK", "NO FUNCIONA la subida");
                }
            });
        }
    }

    public void subirGenero(String bookID,String genero){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            GenerosLiterarios generoLiterario = new GenerosLiterarios();
            generoLiterario.setId(UUID.randomUUID().toString());
            generoLiterario.setBookId(bookID);
            generoLiterario.setGenero(genero);

            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().subirGenero(generoLiterario);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                        Log.i("ASK","Ha funcionado la subida del genero");
                        crearPagina(bookID);
                    } else {
                        Log.i("ASK", "Response is false de la subida del genero");
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                    Log.i("ASK", "NO FUNCIONA la subida");
                }
            });
        }
    }

    public void actualizarGenero(String bookID,String genero,String token){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            GenerosLiterarios generoLiterario = new GenerosLiterarios();
            generoLiterario.setBookId(bookID);
            generoLiterario.setGenero(genero);

            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().actualizarGenero(generoLiterario,"Bearer "+token);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                        Log.i("ASK","Ha funcionado la subida");
                        onUpgradeActualizar(getWritableDatabase(),1,2);
                        insertarLibros();
                        insertarGenerosLiterarios();
                        insertarValoraciones();
                        insertarPaginas();
                        insertarUsuario();
                    } else {
                        Log.i("ASK", "Response is false");
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                    Log.i("ASK", "NO FUNCIONA la subida");
                }
            });
        }
    }

    public void guardarPagina(String bookID,String contenido,int posicionActual,String token){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Pagina pagina = new Pagina();
            pagina.setBookId(bookID);
            pagina.setNrPagina(posicionActual);
            pagina.setContenido(contenido);

            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().actualizarPagina(pagina,"Bearer "+token);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                        borrarPaginas();
                        insertarPaginas();
                    } else {
                        onUpgradeActualizar(getWritableDatabase(),1,2);
                        borrarPaginas();
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                }
            });
        }
    }

    public void crearPagina(String bookID){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Pagina pagina = new Pagina();
            pagina.setId(UUID.randomUUID().toString());
            pagina.setBookId(bookID);
            pagina.setNrPagina(1);
            pagina.setContenido(" ");

            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().crearPrimeraPagina(pagina);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                        onUpgradeActualizar(getWritableDatabase(),1,2);
                        insertarLibros();
                        insertarGenerosLiterarios();
                        insertarValoraciones();
                        insertarPaginas();
                        insertarUsuario();
                    } else {
                        onUpgradeActualizar(getWritableDatabase(),1,2);
                        insertarLibros();
                        insertarGenerosLiterarios();
                        insertarValoraciones();
                        insertarPaginas();
                        insertarUsuario();
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                }
            });
        }
    }

    public void crearPagina(String bookID,int nrPagina){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Pagina pagina = new Pagina();
            pagina.setId(UUID.randomUUID().toString());
            pagina.setBookId(bookID);
            pagina.setNrPagina(nrPagina);
            pagina.setContenido(" ");

            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().crearPrimeraPagina(pagina);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                        borrarPaginas();
                        insertarPaginas();
                    } else {
                        onUpgradeActualizar(getWritableDatabase(),1,2);
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                }
            });
        }
    }

    public void actualizarInfoLibro(String idUsuario,String bookID,String titulo,String autor,String descripcion,String foto,String token){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Libros libro = new Libros();
            libro.setBookID(bookID);
            libro.setApplicationUserId(idUsuario);
            libro.setTitulo(titulo);
            libro.setAutor(autor);
            libro.setDescripcion(descripcion);
            libro.setPhoto(foto);


            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().actualizarInfoLibro(libro,"Bearer "+token);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                }
            });
        }
    }

    public void borrarValoracion(String idValoracion,String token){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().borrarValoracion(idValoracion, "Bearer " +token);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                    } else {
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                }
            });
        }
    }

    public void subirFoto(String usuarioID, String foto,String autor,String descripcion,int edad,String token) throws UnsupportedEncodingException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Usuarios usuario = new Usuarios();
            usuario.setId(usuarioID);
            usuario.setFullName(autor);
            usuario.setAge(edad);
            usuario.setDescription(descripcion);
            usuario.setImage(foto);

            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().subirFoto(usuario,token);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                    } else {
                        Log.i("ASK",response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                }
            });
        }
    }

    public void setFoto(String usuarioID, String foto,String autor,String descripcion,String edad){
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE "+ConstantesBD.TABLE_NAME_USUARIOS+" SET "+ConstantesBD.U_IMAGEN+"='"+foto+"',"+ConstantesBD.U_DESCRIPCION+
                "='"+descripcion+"',"+ConstantesBD.U_AUTOR+"='"+autor+"',"+ConstantesBD.U_EDAD+"='"+edad+"' WHERE "+ConstantesBD.U_ID+"='"+usuarioID+"';";
        db.execSQL(query);
        db.close();
    }

    public void publicarLibro(String bookID,String token,String estado,boolean publicado){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Libros libro = new Libros();
            libro.setBookID(bookID);
            libro.setEstado(estado);
            libro.setPublicado(publicado);
            Date cDate = new Date();
            String fDate = new SimpleDateFormat("dd-MM-yyyy").format(cDate);
            libro.fechaPublicado = fDate;

            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().publicarLibro(libro,"Bearer "+token);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                        onUpgradeActualizar(getWritableDatabase(),1,2);
                        insertarLibros();
                        insertarGenerosLiterarios();
                        insertarValoraciones();
                        insertarPaginas();
                        insertarUsuario();
                    } else {
                        Log.i("ASK",response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                }
            });
        }
    }

    public void  eliminarLibro(String bookID,String token){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {

            Call<ValoracionResponse> valoracionResponseCall = ApiClient.getUserService().borrarLibro(bookID,"Bearer "+token);
            valoracionResponseCall.enqueue(new Callback<ValoracionResponse>() {
                @Override
                public void onResponse(Call<ValoracionResponse> call, Response<ValoracionResponse> response) {
                    if (response.isSuccessful()) {
                        onUpgradeActualizar(getWritableDatabase(),1,2);
                        insertarLibros();
                        insertarGenerosLiterarios();
                        insertarValoraciones();
                        insertarPaginas();
                        insertarUsuario();
                    } else {
                        Log.i("ASK",response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<ValoracionResponse> call, Throwable t) {
                    Log.i("ASK",t.getMessage());
                }
            });
        }
    }

}
