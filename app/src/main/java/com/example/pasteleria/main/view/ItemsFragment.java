package com.example.pasteleria.main.view;


import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentItemsBinding;
import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.misc.ProductosAdapter;
import com.example.pasteleria.main.viewmodel.CarritoViewModel;
import com.example.pasteleria.main.viewmodel.ProductoViewModel;
import com.example.pasteleria.main.viewmodel.ReciboViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ItemsFragment extends Fragment {
    private FragmentItemsBinding binding;
    private FirebaseAuth mAuth;
    private NavController navController;
    private ProductoViewModel productoViewModel;
    private CarritoViewModel carritoViewModel;
    private ReciboViewModel reciboViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding = FragmentItemsBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);

        productoViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);
        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        reciboViewModel = new ViewModelProvider(requireActivity()).get(ReciboViewModel.class);
        ProductosAdapter productosAdapter = new ProductosAdapter(requireContext(),navController,productoViewModel,reciboViewModel ,carritoViewModel, getViewLifecycleOwner());
        binding.recyclerViewCatalogoBuscar.setAdapter(productosAdapter);

        binding.recyclerViewCatalogoBuscar.setLayoutManager(new LinearLayoutManager(getContext()));


        productoViewModel.obtenerProductos().observe(getViewLifecycleOwner(), new Observer<List<Producto>>() {
            @Override
            public void onChanged(List<Producto> productos) {
                if (productos != null) {
                    productosAdapter.establecerListaProductos(productos);
                }
            }
        });
        carritoViewModel.getTotalProductos().observe(getViewLifecycleOwner(),total -> binding.totalItems.setText(String.valueOf(total+"€")));
        productoViewModel.getStock().observe(getViewLifecycleOwner(), isChecked -> {
            binding.switchStock.setChecked(isChecked);
        });
        binding.switchStock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            productoViewModel.setStock(isChecked);
        });

        binding.carritoButton.setOnClickListener(v -> navController.navigate(R.id.action_itemsFragment_to_carritoFragment));
        binding.buttonPrecio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable currentDrawable = binding.buttonPrecio.getDrawable();
                if (currentDrawable.getConstantState().equals(getResources().getDrawable(R.drawable.arriba).getConstantState())) {
                    binding.buttonPrecio.setImageDrawable(getResources().getDrawable(R.drawable.abajo));
                    productoViewModel.setPrecio(false);
                } else {
                    binding.buttonPrecio.setImageDrawable(getResources().getDrawable(R.drawable.arriba));
                    productoViewModel.setPrecio(true);
                }
            }
        });

        binding.searchViewProductos.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    productoViewModel.setBusqueda(false);
                    productoViewModel.obtenerProductos().observe(getViewLifecycleOwner(), new Observer<List<Producto>>() {
                        @Override
                        public void onChanged(List<Producto> productos) {
                            if (productos != null) {
                                productosAdapter.establecerListaProductos(productos);
                            }
                        }
                    });
                } else {
                    productoViewModel.setBusqueda(true);
                    productoViewModel.buscarPorNombre(newText).observe(getViewLifecycleOwner(), productos -> {
                        productosAdapter.establecerListaProductos(productos);
                    });
                }
                return true;
            }
        });

    }
}