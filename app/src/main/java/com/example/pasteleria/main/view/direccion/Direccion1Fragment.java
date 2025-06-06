package com.example.pasteleria.main.view.direccion;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentDireccion1Binding;
import com.example.pasteleria.main.collections.Direccion;
import com.example.pasteleria.main.model.FirestoreClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class Direccion1Fragment extends Fragment {
    private FragmentDireccion1Binding binding;
    private FirebaseAuth mAuth;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDireccion1Binding.inflate(inflater, container, false);
        binding.telefonoInput.setText("+34");
        requireActivity().setTitle("Dirección");
        if (getArguments() != null && getArguments().containsKey("direccion")) {
            Direccion direccion = (Direccion) getArguments().getSerializable("direccion");

            assert direccion != null;
            binding.direccionInput.setText(direccion.getCalle());
            binding.telefonoInput.setText(direccion.getTelefono());
            binding.nombrePedidoInput.setText(direccion.getNombre());

        }

        return binding.getRoot();    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = NavHostFragment.findNavController(this);
        binding.button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = Objects.requireNonNull(binding.nombrePedidoInput.getText()).toString();
                String direccion = Objects.requireNonNull(binding.direccionInput.getText()).toString();
                String numero = Objects.requireNonNull(binding.telefonoInput.getText()).toString();


                FirebaseUser user = mAuth.getCurrentUser();
                if (nombre.isEmpty() || direccion.isEmpty() || numero.isEmpty()) {
                    Toast.makeText(getContext(), "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                assert user != null;
                Direccion nuevaDireccion = new Direccion(user.getUid(), direccion,  numero, nombre );
                FirebaseFirestore db = FirestoreClient.getInstance().getDatabase();
                db.collection("direcciones").add(nuevaDireccion)
                        .addOnSuccessListener(aVoid -> {
                            NavOptions navOptions = new NavOptions.Builder()
                                    .setPopUpTo(R.id.direccion2Fragment, true)
                                    .build();
                            navController.navigate(R.id.action_direccion1Fragment_to_direccion2Fragment, null, navOptions);

                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Error al agregar Direccion", e));;
            }
        });

    }
}