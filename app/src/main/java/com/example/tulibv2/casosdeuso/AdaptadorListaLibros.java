package com.example.tulibv2.casosdeuso;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tulibv2.R;
import com.example.tulibv2.datos.LibroBD;
//import com.example.tulibv2.presentacion.AnadirLibroActivity;
//import com.example.tulibv2.presentacion.EscribirLibroActivity;
import com.example.tulibv2.presentacion.AnadirLibroActivity;
import com.example.tulibv2.presentacion.EditarPerfilActivity;
import com.example.tulibv2.presentacion.EscribirLibroActivity;
import com.example.tulibv2.presentacion.LibrosActivity;
import com.example.tulibv2.presentacion.VerLibroActivity;
import com.example.tulibv2.entities.Libros;


import java.util.HashMap;
import java.util.List;

public class AdaptadorListaLibros extends RecyclerView.Adapter<AdaptadorListaLibros.LibrosViewHolder> {

    private Context contexto;
    private String usuario;
    List<Libros> libros;
    Uri uri;

    HashMap<String,String> contenidoPagina;
    String nrPagina,texto;


    public AdaptadorListaLibros(Context contexto, List<Libros> libros){
        this.contexto = contexto;
        this.libros = libros;
    }


    @NonNull
    @Override
    public LibrosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.lista_libros,parent,false);
        LibrosViewHolder librosViewHolder = new LibrosViewHolder(v);
        return librosViewHolder;
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBindViewHolder(@NonNull LibrosViewHolder holder, int position) {
        LibroBD bd = new LibroBD(contexto);
        String autor = libros.get(position).autor;
        String descripcion = libros.get(position).descripcion;
        String foto = libros.get(position).photo;
        String bookId = libros.get(position).bookID;
        String genero = bd.getGeneroLiterarioByBookId(bookId);
        String usuarioLibro = libros.get(position).applicationUserId;
        String titulo = libros.get(position).titulo;
        String estado = libros.get(position).estado;

        if(foto.equals(" ")){
            uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + contexto.getResources().getResourcePackageName(R.drawable.ic_book)
                    + '/' + contexto.getResources().getResourceTypeName(R.drawable.ic_book)
                    + '/' + contexto.getResources().getResourceEntryName(R.drawable.ic_book)
            );
            holder.imagenListaLibros.setImageURI(uri);
        }else{
            char first = foto.charAt(0);
            String letra = String.valueOf(first);
            if(letra.equals("a")) {
                uri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                        + "://" + contexto.getResources().getResourcePackageName(R.drawable.ic_book)
                        + '/' + contexto.getResources().getResourceTypeName(R.drawable.ic_book)
                        + '/' + contexto.getResources().getResourceEntryName(R.drawable.ic_book)
                );
                holder.imagenListaLibros.setImageURI(uri);

            }else{
                byte[] decodedString = Base64.decode(foto, Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                holder.imagenListaLibros.setImageBitmap(decodedByte);
            }
        }


        //Para obtener la valoracion del libro publicado
        float valoracion = bd.getValoracionByBookId(bookId);

        holder.txtAutor.setText(autor);
        holder.txtDescripcion.setText(descripcion);
        holder.txtGenero.setText(genero);
        holder.ratingBar.setRating(valoracion);
        holder.txtTitulo.setText(titulo);
        switch (estado){
            case "Aceptado":
                holder.txtEstado.setText(estado);
                holder.txtEstado.setTextColor(contexto.getResources().getColor(R.color.verde));
                break;
            case "Rechazado":
                holder.txtEstado.setText(estado);
                holder.txtEstado.setTextColor(contexto.getResources().getColor(R.color.rojo));
                break;
            case "Pendiente":
                holder.txtEstado.setText(estado);
                holder.txtEstado.setTextColor(contexto.getResources().getColor(R.color.naranja));
                break;
            case "Sin Publicar":
                holder.txtEstado.setText(estado);
                holder.txtEstado.setTextColor(contexto.getResources().getColor(R.color.black));
                break;
        }

        //Si clicko en un libro

        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(contexto, "Cargando Libro", Toast.LENGTH_SHORT).show();
            Intent i=new Intent(contexto, VerLibroActivity.class);
            i.putExtra("LibroPublicado",true);
            i.putExtra("IdLibro",bookId);
            contexto.startActivity(i);
        });

        //Para la imagen de opciones
        holder.imagenOpciones.setOnClickListener(v -> {
            mostrarOpciones(bookId,usuarioLibro,genero);
        });

    }

    public void mostrarOpciones(String bookId, String usuarioLibro,String genero){
        //Array para que aparezca en el dialogo
        String[] opciones = {"Ver Libro","Escribir Libro","Editar Informacion Libro","Publicar Libro","Eliminar Libro"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Selecciona una opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Primera opcion es 0 => Ver Libro
                if(which == 0){
                    Toast.makeText(contexto, "Cargando Libro", Toast.LENGTH_SHORT).show();
                    Intent i=new Intent(contexto, VerLibroActivity.class);
                    i.putExtra("LibroPublicado",true);
                    i.putExtra("IdLibro",bookId);
                    contexto.startActivity(i);
                }
                //Segunda opcion es 1 => Escribir Libro
                else if(which == 1){
                    Intent i=new Intent(contexto, EscribirLibroActivity.class);
                    i.putExtra("IdLibro",bookId);
                    i.putExtra("NuevaPagina",false);
                    i.putExtra("Posicion",1);
                    contexto.startActivity(i);
                }
                //Tercera opcion es 2 => Editar Informacion Libro
                else if(which == 2){
                    Intent intent = new Intent(contexto, AnadirLibroActivity.class);
                    intent.putExtra("EditarLibro",true);
                    intent.putExtra("IDlibro",bookId);
                    intent.putExtra("genero",genero);
                    intent.putExtra("AnadirLibro",false);
                    contexto.startActivity(intent);
                }
                //Cuarta opcion es 3 => Publicar Libro
                else if(which == 3){
                    //Para actualizar el campo Publicado del objeto libro
                    AlertDialog.Builder builderEliminar = new AlertDialog.Builder(contexto);
                    builderEliminar.setTitle("Elige una opcion:")
                            .setPositiveButton("Publicar", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.P)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LibroBD bd = new LibroBD(contexto);
                                    bd.publicarLibro(bookId,bd.getTokenUsuario().getToken(),"Pendiente",false);
                                    Toast.makeText(contexto, "Libro Publicado", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(contexto, "Actualizando datos del libro...", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(contexto, LibrosActivity.class);
                                            contexto.startActivity(intent);
                                        }
                                    }, 2000);
                                }
                            })
                            .setNegativeButton("Eliminar Publicación", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.P)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LibroBD bd = new LibroBD(contexto);
                                    bd.publicarLibro(bookId,bd.getTokenUsuario().getToken(),"Sin Publicar",false);
                                    Toast.makeText(contexto, "Publicación eliminada", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(contexto, "Actualizando datos del libro...", Toast.LENGTH_SHORT).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(contexto, LibrosActivity.class);
                                            contexto.startActivity(intent);
                                        }
                                    }, 2000);
                                }
                            });
                    builderEliminar.create().show();
                    builderEliminar.create().show();
                }
                //Quinta opcion es 4 => Eliminar Libro
                else if(which == 4){
                    //Para eliminar un libro
                    AlertDialog.Builder builderEliminar = new AlertDialog.Builder(contexto);
                    builderEliminar.setTitle("Estas seguro de querer eliminarlo?")
                            .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.P)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    LibroBD bd = new LibroBD(contexto);
                                    bd.eliminarLibro(bookId,bd.getTokenUsuario().getToken());
                                    Toast.makeText(contexto, "Eliminado libro...", Toast.LENGTH_LONG).show();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(contexto, LibrosActivity.class);
                                            contexto.startActivity(intent);
                                        }
                                    }, 2000);
                                }
                            })
                            .setNegativeButton("No",null);
                    builderEliminar.create().show();
                }
            }
        });
        builder.create().show();

    }

    /**
     * Metodo para cargar las paginas del libro requerido
     * @param idLibro
     * @return
     */
    public HashMap<String,String> cargarPaginas(String idLibro){
        contenidoPagina = new HashMap<>();
        /*myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()){
                    nrPagina = ds.getKey();
                    texto = ds.getValue(String.class);
                    contenidoPagina.put(nrPagina,texto);

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        */
        return contenidoPagina;
    }


    @Override
    public int getItemCount() {
        return libros.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class LibrosViewHolder extends  RecyclerView.ViewHolder{
        CardView cvListaLibros;
        ImageButton imagenOpciones;
        RatingBar ratingBar;
        ImageView imagenListaLibros;
        TextView txtAutor, txtDescripcion, txtGenero, txtTitulo, txtEstado;

        public LibrosViewHolder(@NonNull View itemView) {
            super(itemView);
            cvListaLibros = (CardView)itemView.findViewById(R.id.cvListaLibros);
            imagenOpciones = (ImageButton)itemView.findViewById(R.id.imagen_opciones);
            ratingBar = (RatingBar)itemView.findViewById(R.id.ratingBar);
            imagenListaLibros = (ImageView)itemView.findViewById(R.id.imgListaLibros);
            txtAutor = (TextView)itemView.findViewById(R.id.txtAutor);
            txtDescripcion = (TextView)itemView.findViewById(R.id.txtDescripcion);
            txtGenero = (TextView)itemView.findViewById(R.id.txtGenero);
            txtTitulo = (TextView)itemView.findViewById(R.id.txtTitulo);
            txtEstado = (TextView)itemView.findViewById(R.id.txtEstado);

        }
    }


}
