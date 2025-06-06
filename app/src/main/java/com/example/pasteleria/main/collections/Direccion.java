package com.example.pasteleria.main.collections;

import java.io.Serializable;

public class Direccion implements Serializable {
    private String idUsuario;
    private String calle;
    private String telefono;
    private String nombre;

    public Direccion() {
    }

    public Direccion( String idUsuario,String calle, String telefono, String nombre) {
        this.idUsuario = idUsuario;
        this.calle = calle;
        this.telefono = telefono;
        this.nombre = nombre;
    }
    public String getIdUsuario() {
        return idUsuario;
    }
    public String getCalle() {
        return calle;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getNombre() {
        return nombre;
    }

}
