package com.example.pasteleria.main.collections;

import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Cliente {
    private String id;
    private String nombre;
    private String email;
    private String telefono;
    private List<Direccion> direccion;
    private String imageUrl;
    private Date cumpleanos;

    public Cliente() {
    }

    public Cliente(String id, String nombre, String email, String telefono, List<Direccion> direccion,String imageUrl, Date cumpleanos) {
        this.id = id;
        this.nombre = nombre;
        this.email = email;
        this.telefono = telefono;
        this.direccion = direccion;
        this.imageUrl = imageUrl;
        this.cumpleanos = cumpleanos;
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

    public Date getCumpleanos() {
        return cumpleanos;
    }

    public void setCumpleanos(Date cumpleanos) {
        this.cumpleanos = cumpleanos;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefono() {
        return telefono;
    }

    public List<Direccion> getDireccion() {
        return direccion;
    }
}
