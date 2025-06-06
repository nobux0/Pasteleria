package com.example.pasteleria.main.view;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.pasteleria.databinding.FragmentDetailsBinding;
import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.collections.ProductoPedido;
import com.example.pasteleria.main.misc.ImageUtils;
import com.example.pasteleria.main.viewmodel.CarritoViewModel;
import com.example.pasteleria.main.viewmodel.ReciboViewModel;

import java.util.Locale;

public class DetailsFragment extends Fragment {
    FragmentDetailsBinding binding;

    private CarritoViewModel carritoViewModel;
    private ReciboViewModel reciboViewModel;
    Producto producto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            producto = getArguments().getParcelable("producto");
        }
        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        reciboViewModel = new ViewModelProvider(requireActivity()).get(ReciboViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDetailsBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        NavController navController = Navigation.findNavController(view);

        if (producto != null) {
            binding.textNombreDetalle.setText(producto.getNombre());
            binding.textDescripcionDetalle.setText(producto.getDescripcion());
            binding.anadirCarritoBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carritoViewModel.obtenerProductoYAgregarAlCarrito(producto.getId());
                    reciboViewModel.obtenerProductoYAgregarAlRecibo(producto.getId());
                    Toast.makeText(getContext(),"Producto añadido al carrito",Toast.LENGTH_SHORT).show();
                }
            });
            double precio = producto.getPrecio();
            if (precio == (int) precio) {
                binding.precioTv.setText((int) precio + "€");
            } else {
                binding.precioTv.setText(String.format(Locale.getDefault(), "%.2f€", precio));
            }

            if(producto.getImagenUrl() != null){
                Glide.with(requireActivity())
                        .load(producto.getImagenUrl())
                        .into(binding.imageDetalle);
            }
        } else {
            navController.popBackStack();
            Toast.makeText(getContext(),"Error al cargar el producto",Toast.LENGTH_SHORT).show();
        }
    }

}