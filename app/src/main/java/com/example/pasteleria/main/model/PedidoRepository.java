package com.example.pasteleria.main.model;

import static java.security.AccessController.getContext;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.pasteleria.main.collections.Pedido;
import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.collections.ProductoPedido;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PedidoRepository {
    private final FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private String direccionIdDocumento;
    private final CollectionReference pedidosCollection;

    public PedidoRepository(Application application) {
        db = FirestoreClient.getInstance().getDatabase();
        pedidosCollection = db.collection("pedidos");
        mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> crearPedido(String idDireccion, List<ProductoPedido> productos) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("direcciones")
                .whereEqualTo("idUsuario", userId)
                .limit(1)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        // Guardamos el id del documento (no un campo dentro del documento)
                        direccionIdDocumento = querySnapshot.getDocuments().get(0).getId();
                        Log.d("PagoFragment", "ID documento dirección: " + direccionIdDocumento);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("PagoFragment", "Error al cargar la dirección", e);
                });
        MutableLiveData<Boolean> resultado = new MutableLiveData<>();

        String idCliente = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : null;
        if (idCliente == null) {
            resultado.setValue(false);
            return resultado;
        }

        Timestamp fechaActual = Timestamp.now();

        Pedido nuevoPedido = new Pedido(
                idCliente,
                idDireccion,
                "pendiente",
                productos,
                fechaActual
        );

        pedidosCollection.add(nuevoPedido)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Pedido creado con ID: " + documentReference.getId());
                    resultado.setValue(true);
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error al crear pedido", e);
                    resultado.setValue(false);
                });

        return resultado;
    }
}