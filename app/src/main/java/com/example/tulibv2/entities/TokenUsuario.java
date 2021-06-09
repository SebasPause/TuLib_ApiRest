package com.example.tulibv2.entities;

public class TokenUsuario {

    private String token;
    private String usuario;
    private String usuarioID;

    public TokenUsuario(){}

    public TokenUsuario(String token,String usuario,String usuarioID){
        this.token = token;
        this.usuario = usuario;
        this.usuarioID = usuarioID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }
}
