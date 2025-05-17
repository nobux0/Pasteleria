package com.example.pasteleria.main.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.model.ProductoRepository;

import java.util.List;

public class ProductoViewModel extends AndroidViewModel {
    private ProductoRepository productoRepository;

    private MutableLiveData<Boolean> busqueda = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> precio = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> stock = new MutableLiveData<>(false);

    public MutableLiveData<Boolean> getBusqueda() {
        return busqueda;
    }

    public void setBusqueda(boolean valor) {
        busqueda.setValue(valor);
    }

    public MutableLiveData<Boolean> getPrecio() {
        return precio;
    }

    public void setPrecio(boolean valor) {
        precio.setValue(valor);
    }

    public LiveData<Boolean> getStock() {
        return stock;
    }

    public void setStock(boolean value) {
        stock.setValue(value);
    }
    public ProductoViewModel(@NonNull Application application) {
        super(application);
        productoRepository = new ProductoRepository(application);
    }
    public LiveData<List<Producto>> obtenerProductos() {
        return productoRepository.obtenerProductos();
    }
    public LiveData<List<Producto>> buscarPorNombre(String nombre) {
        return productoRepository.buscarPorNombre(nombre);
    }
    public LiveData<List<Producto>> obtenerProductosPorCategoria(String categoria) {
        return productoRepository.buscarPorCategoria(categoria);
    }


}
