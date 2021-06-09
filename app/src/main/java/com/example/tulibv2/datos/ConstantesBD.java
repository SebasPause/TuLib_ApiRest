package com.example.tulibv2.datos;

import java.security.PublicKey;

import retrofit2.http.PUT;

public class ConstantesBD {

    //Nombre de la Base de Datos
    public static final  String BD_NAME = "DatosAplicacion";

    //Version de la BD
    public static final int BD_VERSION = 1;

    //Nombre de las tablas
    public static final String TABLE_NAME_LIBROS = "Libros";
    public static final String TABLE_NAME_PAGINAS = "Paginas";
    public static final String TABLE_NAME_VALORACIONES = "Valoraciones";
    public static final String TABLE_NAME_TOKEN = "Token";
    public static final String TABLE_NAME_GENEROSLITERARIOS = "GenerosLiterarios";
    public static final String TABLE_NAME_USUARIOS = "Usuarios";

    //Nombre de campos de la tabla Libros
    public static final String L_TITULO = "TITULO";
    public static final String L_AUTOR = "AUTOR";
    public static final String L_DESCRIPCION = "DESCRIPCION";
    public static final String L_FOTO = "FOTO";
    public static final String L_PUBLICADO = "PUBLICADO";
    public static final String L_FECHA_PUBLICADO = "FECHA_PUBLICADO";
    public static final String L_ID_LIBRO = "ID_LIBRO";
    public static final String L_USUARIO_LIBRO = "USUARIOLIBRO";
    public static final String L_ESTADO = "ESTADO";

    //Nombre de campos de la tabla Paginas
    public static final String PA_NUMERO_PAGINA = "NUMERO_PAGINA";
    public static final String PA_CONTENIDO = "CONTENIDO";
    public static final String PA_PAGINAID = "PAGINAID";
    public static final String PA_BOOKID = "BOOKID";

    //Nombre de campos de la tabla Valoraciones
    public static final String VA_COMENTARIO = "COMENTARIO";
    public static final String VA_VALOR = "VALOR";
    public static final String VA_VALORACIONID = "VALORACIONID";
    public static final String VA_LIBROID = "ID_LIBRO";

    //Nombre de campos de la tabla Token
    public static final String TA_TOKEN = "TOKEN";
    public static final String TA_USUARIO_ACTUAL = "USUARIOACTUAL";
    public static final String TA_USUARIO_ID = "USUARIOID";

    //Nombre de campos de la tabla GenerosLiterarios
    public static final String GL_GENEROID = "GENEROID";
    public static final String GL_LIBROID = "LIBROID";
    public static final String GL_GENEROVALOR = "GENEROVALOR";

    //Nombre de campos de la tabla Usuarios
    public static final String U_ID = "ID";
    public static final String U_AUTOR = "AUTOR";
    public static final String U_EMAIL = "EMAIL";
    public static final String U_USERNAME = "USERNAME";
    public static final String U_EDAD = "EDAD";
    public static final String U_DESCRIPCION = "DESCRIPCION";
    public static final String U_IMAGEN = "IMAGEN";

    //Codigo de creacion de la tabla de Libros
    public static final String CREATE_TABLE_LIBROS = "CREATE TABLE "+ TABLE_NAME_LIBROS + "("
            + L_ID_LIBRO + " TEXT, "
            + L_TITULO + " TEXT, "
            + L_AUTOR + " TEXT, "
            + L_DESCRIPCION + " TEXT, "
            + L_FOTO + " TEXT, "
            + L_PUBLICADO + " TEXT, "
            + L_FECHA_PUBLICADO + " TEXT, "
            + L_ESTADO + " TEXT, "
            + L_USUARIO_LIBRO + " TEXT"
           // + " FOREIGN KEY ("+L_USUARIO_LIBRO+") REFERENCES "+TABLE_NAME_USUARIO+"("+U_USUARIO+")"
            +")";

    //Codigo de creacion de la tabla de Paginas
    public static final String CREATE_TABLE_PAGINAS = "CREATE TABLE "+ TABLE_NAME_PAGINAS + "("
            + PA_NUMERO_PAGINA + " TEXT, "
            + PA_CONTENIDO + " TEXT, "
            + PA_PAGINAID + " TEXT, "
            + PA_BOOKID + " TEXT "
            + ")";

    //Codigo de creacion de la tabla de Valoraciones
    public static final String CREATE_TABLE_VALORACIONES = "CREATE TABLE "+ TABLE_NAME_VALORACIONES + "("
            + VA_COMENTARIO + " TEXT, "
            + VA_VALORACIONID + " TEXT, "
            + VA_LIBROID + " TEXT, "
            + VA_VALOR + " NUMBER "
            + ")";

    //Codigo de creacion de la tabla Token
    public static final String CREATE_TABLE_TOKEN = "CREATE TABLE " + TABLE_NAME_TOKEN + "("
            + TA_TOKEN + " TEXT, "
            + TA_USUARIO_ACTUAL + " TEXT, "
            + TA_USUARIO_ID + " TEXT "
            +")";

    //Codigo de creacion de la tabla GenerosLiterarios
    public static final String CREATE_TABLE_GENEROSLITERARIOS = "CREATE TABLE " + TABLE_NAME_GENEROSLITERARIOS + "("
            + GL_GENEROID + " TEXT, "
            + GL_LIBROID + " TEXT, "
            + GL_GENEROVALOR + " TEXT"
            +")";

    //Codigo de creacion de la tabla GenerosLiterarios
    public static final String CREATE_TABLE_USUARIOS = "CREATE TABLE " + TABLE_NAME_USUARIOS + "("
            + U_ID + " TEXT, "
            + U_AUTOR + " TEXT, "
            + U_EMAIL + " TEXT, "
            + U_USERNAME + " TEXT, "
            + U_EDAD + " NUMBER, "
            + U_DESCRIPCION + " TEXT, "
            + U_IMAGEN + " TEXT"
            +")";

}
