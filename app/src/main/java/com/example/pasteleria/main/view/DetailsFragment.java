package com.example.pasteleria.main.view;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pasteleria.databinding.FragmentDetailsBinding;
import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.misc.ImageUtils;

public class DetailsFragment extends Fragment {
    FragmentDetailsBinding binding;
    Producto producto;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null){
            producto = getArguments().getParcelable("producto");
        }

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
            binding.textCategoriaDetalle.setText(producto.getCategoria());

            if(producto.getImagenUrl() != null){
//                Bitmap fotoBit = ImageUtils.blobToBitmap(producto.getImagenUrl());
//                binding.imageDetalle.setImageBitmap(fotoBit);
            }
        } else {
            navController.popBackStack();
            Toast.makeText(getContext(),"Error al cargar la producto",Toast.LENGTH_SHORT).show();
        }
    }

}