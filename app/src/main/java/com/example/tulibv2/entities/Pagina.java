package com.example.tulibv2.entities;

public class Pagina {
    public String id,contenido,bookId;
    public int nrPagina;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getNrPagina() {
        return nrPagina;
    }

    public void setNrPagina(int nrPagina) {
        this.nrPagina = nrPagina;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
