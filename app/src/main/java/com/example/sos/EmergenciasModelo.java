package com.example.sos;

public class EmergenciasModelo {

    String id;
    String idUsuario;
    String nombre;
    String apellido;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public EmergenciasModelo(String id, String idUsuario, String nombre, String apellido) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.apellido = apellido;
    }




}
