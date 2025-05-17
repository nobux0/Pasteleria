package com.example.pasteleria.main.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentCarritoBinding;
import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.collections.ProductoPedido;
import com.example.pasteleria.main.misc.CarritoAdapter;
import com.example.pasteleria.main.misc.ProductosAdapter;
import com.example.pasteleria.main.misc.SharedPreferencesHelper;
import com.example.pasteleria.main.view.direccion.Direccion1Fragment;
import com.example.pasteleria.main.view.direccion.Direccion2Fragment;
import com.example.pasteleria.main.viewmodel.CarritoViewModel;
import com.example.pasteleria.main.viewmodel.ReciboViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class CarritoFragment extends Fragment {
    private FragmentCarritoBinding binding;
    private NavController navController;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SharedPreferencesHelper helper;
    private CarritoViewModel carritoViewModel;
    private ReciboViewModel reciboViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        reciboViewModel = new ViewModelProvider(requireActivity()).get(ReciboViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding= FragmentCarritoBinding.inflate(inflater,container,false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        CarritoAdapter carritoAdapter = new CarritoAdapter(new ArrayList<>(), carritoViewModel, reciboViewModel);
        carritoViewModel.getProductosCarrito().observe(getViewLifecycleOwner(), new Observer<List<ProductoPedido>>() {
            @Override
            public void onChanged(List<ProductoPedido> productoPedidos) {
                carritoAdapter.setProductos(productoPedidos);
                carritoAdapter.notifyDataSetChanged();
                binding.textViewTotal.setText(String.valueOf(carritoViewModel.obtenerTotal()+"€"));
            }
        });
        binding.recyclerView.setAdapter(carritoAdapter);
        binding.papeleraButton.setOnClickListener(v -> {carritoViewModel.vaciarCarrito();
        reciboViewModel.vaciarRecibo();});
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.continuarButton.setOnClickListener(v -> {
            db = FirebaseFirestore.getInstance();
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            db.collection("direcciones").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (!isAdded()) return;

                        if (documentSnapshot.exists()) {
                            navController.navigate(R.id.action_carritoFragment_to_direccion2Fragment);
                        } else {
                            navController.navigate(R.id.action_carritoFragment_to_direccion1Fragment);
                        }
                    })
                    .addOnFailureListener(e -> {
                        if (isAdded()) {
                            Toast.makeText(requireContext(), "Error al cargar dirección", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

    }
}