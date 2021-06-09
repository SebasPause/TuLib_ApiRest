package com.example.tulibv2.casosdeuso;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
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
import com.example.tulibv2.entities.Libros;
import com.example.tulibv2.presentacion.ValoracionesActivity;
import com.example.tulibv2.presentacion.VerLibroActivity;
//import com.example.tulibv2.presentacion.ValoracionesActivity;
//import com.example.tulibv2.presentacion.VerLibroActivity;
//import com.google.android.gms.tasks.OnSuccessListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

public class AdaptadorLibrosPublicados extends RecyclerView.Adapter<AdaptadorLibrosPublicados.LibrosPublicadosViewHolder> {

    private Context contexto;
    List<Libros> librosPublicados;
    String generoLiterario;
    Uri uri;

    public AdaptadorLibrosPublicados(Context contexto, List<Libros> librosPublicados){
        this.contexto = contexto;
        this.librosPublicados = librosPublicados;
    }

    @NonNull
    @Override
    public LibrosPublicadosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(contexto).inflate(R.layout.lista_libros_publicados,parent,false);
        LibrosPublicadosViewHolder librospublicadosViewHolder = new LibrosPublicadosViewHolder(v);
        return librospublicadosViewHolder;
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @Override
    public void onBindViewHolder(@NonNull LibrosPublicadosViewHolder holder, int position) {
        LibroBD bd = new LibroBD(contexto);
        String autor = librosPublicados.get(position).autor;
        String titulo = librosPublicados.get(position).titulo;
        String descripcion = librosPublicados.get(position).descripcion;
        String foto = librosPublicados.get(position).photo;
        String bookId = librosPublicados.get(position).bookID;
        String fechaPublicado = librosPublicados.get(position).fechaPublicado;
        String usuarioLibro = librosPublicados.get(position).applicationUserId;
        String genero = bd.getGeneroLiterarioByBookId(bookId);
        float valoracion = bd.getValoracionByBookId(bookId);

        holder.ratingBar.setRating(valoracion);

        if(foto.equals("")){
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

        holder.txtAutor.setText(autor);
        holder.txtDescripcion.setText(descripcion);
        holder.txtGenero.setText(genero);
        //holder.imagenListaLibros.setImageURI(uri);
        holder.txtFechaPublicado.setText(" "+fechaPublicado);
        holder.txtTitulo.setText(titulo);

        //Si clicko en un libro
        holder.itemView.setOnClickListener(v -> {
            Intent i=new Intent(contexto, VerLibroActivity.class);
            i.putExtra("LibroPublicado",true);
            i.putExtra("IdLibro",bookId);
            contexto.startActivity(i);
        });

        //Para la imagen de opciones
        holder.imagenOpciones.setOnClickListener(v -> {
            mostrarOpciones(bookId,usuarioLibro);
        });

        
    }

    public void mostrarOpciones(String bookId, String usuarioLibro) {
        //Array para que aparezca en el dialogo
        String[] opciones = {"Ver Libro","Valoraciones"};

        final AlertDialog.Builder builder = new AlertDialog.Builder(contexto);
        builder.setTitle("Selecciona una opción");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Primera opcion es Ver Libro = 0
                if(which == 0){
                    Intent i=new Intent(contexto, VerLibroActivity.class);
                    i.putExtra("LibroPublicado",true);
                    i.putExtra("IdLibro",bookId);
                    contexto.startActivity(i);
                }
                //Segunda opcion es Valoraciones = 1
                if(which == 1){
                    LibroBD bd = new LibroBD(contexto);
                    Intent i=new Intent(contexto, ValoracionesActivity.class);
                    i.putExtra("IDlibro",bookId);
                    i.putExtra("UsuarioLibro",usuarioLibro);
                    contexto.startActivity(i);
                }
            }
        });
        builder.create().show();
    }

    @Override
    public int getItemCount() {
        return librosPublicados.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView){
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class LibrosPublicadosViewHolder extends  RecyclerView.ViewHolder{
        CardView cvListaLibrosPublicados;
        ImageButton imagenOpciones;
        RatingBar ratingBar;
        ImageView imagenListaLibros;
        TextView txtAutor, txtDescripcion, txtGenero,txtFechaPublicado,txtTitulo;


        public LibrosPublicadosViewHolder(@NonNull View itemView) {
            super(itemView);
            cvListaLibrosPublicados = (CardView)itemView.findViewById(R.id.cvListaLibrosPublicados);
            imagenOpciones = (ImageButton)itemView.findViewById(R.id.imagen_opciones);
            ratingBar = (RatingBar)itemView.findViewById(R.id.ratingBar);
            imagenListaLibros = (ImageView)itemView.findViewById(R.id.imgListaLibros);
            txtAutor = (TextView)itemView.findViewById(R.id.txtAutor);
            txtDescripcion = (TextView)itemView.findViewById(R.id.txtDescripcion);
            txtGenero = (TextView)itemView.findViewById(R.id.txtGenero);
            txtFechaPublicado = (TextView)itemView.findViewById(R.id.txtFechaPublicado);
            txtTitulo = (TextView)itemView.findViewById(R.id.txtTitulo);
        }
    }



}
