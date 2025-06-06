package com.example.pasteleria.main.collections;

import com.example.pasteleria.main.collections.ProductoPedido;
import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {
    private String idDocumento;
    private String idCliente;
    private String idDireccion;
    private String estado;
    private List<ProductoPedido> productos;
    private Timestamp fecha;

    public Pedido() {}

    public Pedido(String idCliente, String idDireccion, String estado, List<ProductoPedido> productos, Timestamp fecha) {
        this.idCliente = idCliente;
        this.idDireccion = idDireccion;
        this.estado = estado;
        this.productos = productos;
        this.fecha = fecha;
    }
    public String getIdDocumento() { return idDocumento; }
    public void setIdDocumento(String idDocumento) { this.idDocumento = idDocumento; }
    public String getIdCliente() { return idCliente; }
    public void setIdCliente(String idCliente) { this.idCliente = idCliente; }

    public String getIdDireccion() { return idDireccion; }
    public void setIdDireccion(String idDireccion) { this.idDireccion = idDireccion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<ProductoPedido> getProductos() { return productos; }
    public void setProductos(List<ProductoPedido> productos) { this.productos = productos; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }
}
