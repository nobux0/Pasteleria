package com.example.pasteleria.main.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentDireccion2Binding;
import com.example.pasteleria.databinding.FragmentPedidosBinding;
import com.example.pasteleria.main.collections.Direccion;
import com.example.pasteleria.main.collections.Pedido;
import com.example.pasteleria.main.misc.DireccionAdapter;
import com.example.pasteleria.main.misc.PedidosAdapter;
import com.example.pasteleria.main.view.direccion.Direccion2Fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class PedidosFragment extends Fragment {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private PedidosAdapter adapter;
    private FragmentPedidosBinding binding;

    private List<Pedido> listaPedidos = new ArrayList<>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPedidosBinding.inflate(inflater, container, false);
        binding.pedidosRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new PedidosAdapter(listaPedidos, pedido -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("pedido", pedido);
            NavController navController = NavHostFragment.findNavController(PedidosFragment.this);
            navController.navigate(R.id.action_pedidosFragment_to_detailsPedidoFragment, bundle);
        });

        binding.pedidosRecyclerView.setAdapter(adapter);
        cargarPedidos();
        return binding.getRoot();
    }

    private void cargarPedidos() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("pedidos")
                .whereEqualTo("idCliente", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaPedidos.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Pedido pedido = doc.toObject(Pedido.class);
                        if (pedido != null) {
                            pedido.setIdDocumento(doc.getId());
                            listaPedidos.add(pedido);
                        }
                    }

                    if (listaPedidos.isEmpty()) {
                        binding.textView13.setVisibility(View.VISIBLE);
                    } else {
                        binding.textView13.setVisibility(View.GONE);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar pedidos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

}