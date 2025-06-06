package com.example.pasteleria.main.collections;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Producto implements Parcelable {
    private String id;
    private boolean novedad;
    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;
    private String categoria;
    private String imagenUrl;

    public Producto() {
    }

    public Producto(String id, String nombre, String descripcion, double precio, int stock, String categoria, String imagenUrl, boolean novedad) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria;
        this.imagenUrl = imagenUrl;
        this.novedad = novedad;
    }

    protected Producto(Parcel in) {
        id = in.readString();
        nombre = in.readString();
        descripcion = in.readString();
        precio = in.readDouble();
        stock = in.readInt();
        categoria = in.readString();
        imagenUrl = in.readString();
        novedad = in.readByte() != 0;
    }

    public static final Creator<Producto> CREATOR = new Creator<Producto>() {
        @Override
        public Producto createFromParcel(Parcel in) {
            return new Producto(in);
        }

        @Override
        public Producto[] newArray(int size) {
            return new Producto[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public int getStock() {
        return stock;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public boolean isNovedad() {
        return novedad;
    }

    public void setNovedad(boolean novedad) {
        this.novedad = novedad;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(nombre);
        dest.writeString(descripcion);
        dest.writeDouble(precio);
        dest.writeInt(stock);
        dest.writeString(categoria);
        dest.writeString(imagenUrl);
        dest.writeByte((byte) (novedad ? 1 : 0));
    }
}
