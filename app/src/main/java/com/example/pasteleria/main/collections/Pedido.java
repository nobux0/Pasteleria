package com.example.pasteleria.main.collections;

import java.sql.Timestamp;
import java.util.List;

public class Pedido {
    private String id;
    private String clienteId;
    private String nombreCliente;
    private String direccion;
    private Timestamp fecha;
    private double total;
    private String estado;
    private List<ProductoPedido> productos;

    public Pedido() {
    }
}

