package com.example.pasteleria.main.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.collections.ProductoPedido;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CarritoViewModel extends ViewModel {
    private MutableLiveData<List<ProductoPedido>> productosCarrito = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<Double> totalProductos = new MutableLiveData<>(0.0);
    public LiveData<List<ProductoPedido>> getProductosCarrito() {
        return productosCarrito;
    }

    public LiveData<Double> getTotalProductos() {
        return totalProductos;
    }

    public void obtenerProductoYAgregarAlCarrito(String productoId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("productos")
                .whereEqualTo("id", productoId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Producto producto = document.toObject(Producto.class);
                        if (producto != null) {
                            ProductoPedido productoPedido = new ProductoPedido(
                                    producto.getId(),
                                    producto.getNombre(),
                                    1,
                                    producto.getPrecio()
                            );
                            agregarProducto(productoPedido);
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al filtrar productos", e));
    }
    public void restarProducto(ProductoPedido producto) {
        List<ProductoPedido> listaActual = new ArrayList<>(productosCarrito.getValue());

        for (ProductoPedido p : listaActual) {
            if (p.getProductoId().equals(producto.getProductoId())) {
                if (p.getCantidad() != 1){
                    p.setCantidad(p.getCantidad() - 1);
                    eliminarProducto(p);
                }
                break;

            }

        }

        productosCarrito.setValue(listaActual);
    }
    public void agregarProducto(ProductoPedido producto) {
        List<ProductoPedido> listaActual = new ArrayList<>(productosCarrito.getValue());

        boolean encontrado = false;
        for (ProductoPedido p : listaActual) {
            if (p.getProductoId().equals(producto.getProductoId())) {
                p.setCantidad(p.getCantidad() + 1);
                encontrado = true;
                obtenerTotal();
                break;
            }
        }
        if (!encontrado) {
            listaActual.add(producto);
        }
        productosCarrito.setValue(listaActual);
        obtenerTotal();
    }

    public void eliminarProducto(ProductoPedido producto) {
        List<ProductoPedido> listaActual = new ArrayList<>(productosCarrito.getValue());
        listaActual.remove(producto);
        productosCarrito.setValue(listaActual);
    }

    public void vaciarCarrito() {
        productosCarrito.setValue(new ArrayList<>());
    }

    public double obtenerTotalProducto(ProductoPedido producto) {
        double total = 0;
            total += producto.getPrecioUnitario() * producto.getCantidad();
        return total;
    }
    public double obtenerTotal() {
        double total = 0;
        for (ProductoPedido p : productosCarrito.getValue()) {
            total += p.getPrecioUnitario() * p.getCantidad();
        }
        totalProductos.setValue(total);
        return total;
    }
}

