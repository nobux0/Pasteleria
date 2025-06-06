package com.example.pasteleria.main.view.direccion;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentDireccion2Binding;
import com.example.pasteleria.main.MainActivity;
import com.example.pasteleria.main.collections.Direccion;
import com.example.pasteleria.main.misc.DireccionAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.List;

public class Direccion2Fragment extends Fragment {
    private FragmentDireccion2Binding binding;
    private DireccionAdapter adapter;
    private List<Direccion> listaDirecciones = new ArrayList<>();
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDireccion2Binding.inflate(inflater, container, false);

        binding.recyclerViewDirecciones.setLayoutManager(new LinearLayoutManager(getContext()));
        requireActivity().setTitle("Dirección");

        adapter = new DireccionAdapter(listaDirecciones, direccion -> {
            Bundle bundle = new Bundle();
            NavController navController = NavHostFragment.findNavController(Direccion2Fragment.this);
            bundle.putSerializable("direccion", direccion);


            navController.navigate(R.id.action_direccion2Fragment_to_direccion1Fragment, bundle);

        });
        binding.recyclerViewDirecciones.setAdapter(adapter);
        binding.buttonDireccion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = NavHostFragment.findNavController(Direccion2Fragment.this);
                navController.navigate(R.id.action_direccion2Fragment_to_direccion1Fragment);
            }
        });
        binding.continuarButton2.setOnClickListener(v -> {
            if (listaDirecciones.isEmpty()) {
                Toast.makeText(getContext(), "Debes agregar al menos una dirección", Toast.LENGTH_SHORT).show();
                return;
            }
            Direccion direccionSeleccionada = listaDirecciones.get(0);

            Bundle bundle = new Bundle();
            bundle.putSerializable("direccionSeleccionada", direccionSeleccionada);

            NavController navController = NavHostFragment.findNavController(Direccion2Fragment.this);
            navController.navigate(R.id.action_direccion2Fragment_to_pagoFragment, bundle);
        });
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        cargarDirecciones();

        return binding.getRoot();
    }

    private void cargarDirecciones() {
        String userId = mAuth.getCurrentUser().getUid();

        db.collection("direcciones")
                .whereEqualTo("idUsuario", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    listaDirecciones.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Direccion direccion = doc.toObject(Direccion.class);
                        Log.d("DireccionDebug", "Dirección cargada: " + direccion.getNombre());
                        listaDirecciones.add(direccion);
                    }
                    Log.d("DireccionDebug", "Total direcciones: " + listaDirecciones.size());
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Error al cargar direcciones: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
