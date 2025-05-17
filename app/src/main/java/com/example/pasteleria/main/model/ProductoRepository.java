package com.example.pasteleria.main.model;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pasteleria.main.collections.Producto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ProductoRepository {
    private final FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private SupabaseStorageApi storageApi;
    private final CollectionReference productosCollection;

    public ProductoRepository(Application application) {
        db = FirestoreClient.getInstance().getDatabase();
        productosCollection = db.collection("productos");
        mAuth = FirebaseAuth.getInstance();
        storageApi = SupabaseClient.getClient().create(SupabaseStorageApi.class);
    }

    public LiveData<List<Producto>> obtenerProductos(){
        MutableLiveData<List<Producto>> productosLiveData = new MutableLiveData<>();
        productosCollection.get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Producto> productos = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Producto producto = document.toObject(Producto.class);
                        productos.add(producto);
                        Log.d("Firestore", "Producto: " + producto.getNombre());
                    }
                    productosLiveData.postValue(productos);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener los anuncios", e));
        return  productosLiveData;
    }
    public LiveData<List<Producto>> buscarPorCategoria(String categoria) {
        MutableLiveData<List<Producto>> productosLiveData = new MutableLiveData<>();
        productosCollection.whereEqualTo("categoria", categoria)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Producto> productos = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Producto producto = document.toObject(Producto.class);
                        productos.add(producto);
                    }
                    productosLiveData.postValue(productos);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener productos por categoría", e));
        return productosLiveData;
    }
    public LiveData<List<Producto>> buscarPorNombre(String nombre) {
        MutableLiveData<List<Producto>> productosLiveData = new MutableLiveData<>();
        productosCollection.whereEqualTo("nombre",nombre)
                           .get().addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Producto> productos = new ArrayList<>();
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        Producto producto = document.toObject(Producto.class);
                        productos.add(producto);
                        Log.d("Firestore", "Producto: " + producto.getNombre());
                    }
                    productosLiveData.postValue(productos);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error al obtener los anuncios", e));
        return  productosLiveData;
    }
}
