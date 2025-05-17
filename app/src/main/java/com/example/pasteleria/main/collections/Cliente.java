package com.example.pasteleria.main.collections;

import java.util.List;
import java.util.Objects;

public class Cliente {
    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private Direccion direccion;
    private String imageUrl;

    public Cliente() {
    }

    public Cliente(String id, String nombre, String email, String telefono, Direccion direccion,String imageUrl) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public Direccion getDireccion() {
        return direccion;
    }
}
