package com.example.pasteleria.main.collections;

import java.io.Serializable;

public class Direccion implements Serializable {
    private String id;
    private String calle;
    private String telefono;
    private String nombre;

    public Direccion() {
    }

    public Direccion(String id ,String calle, String telefono, String nombre) {
        this.id = id;
        this.calle = calle;
        this.telefono = telefono;
        this.nombre = nombre;
    }
    public String getId(){return id;}
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
