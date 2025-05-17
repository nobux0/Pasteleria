package com.example.pasteleria.main.collections;

public class ProductoPedido {
    private String productoId;
    private String nombre;
    private int cantidad;
    private double precioUnitario;

    public ProductoPedido() {
    }

    public ProductoPedido(String productoId, String nombre, int cantidad, double precioUnitario) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public void setProductoId(String productoId) {
        this.productoId = productoId;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public void setPrecioUnitario(double precioUnitario) {
        this.precioUnitario = precioUnitario;
    }

    public String getProductoId() {
        return productoId;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }
}
