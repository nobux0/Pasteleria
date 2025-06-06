package com.example.pasteleria.main.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentLoginBinding;
import com.example.pasteleria.databinding.FragmentMainBinding;
import com.example.pasteleria.main.MainActivity;
import com.example.pasteleria.main.collections.Anuncio;
import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.misc.AnuncioAdapter;
import com.example.pasteleria.main.misc.ProductosAdapter;
import com.example.pasteleria.main.viewmodel.CarritoViewModel;
import com.example.pasteleria.main.viewmodel.ProductoViewModel;
import com.example.pasteleria.main.viewmodel.ReciboViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainFragment extends Fragment {
    private Handler autoScrollHandler = new Handler();
    private int currentPage = 0;
    private ProductoViewModel productoViewModel;
    private CarritoViewModel carritoViewModel;
    private ReciboViewModel reciboViewModel;
    private NavController navController;
    private FragmentMainBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        productoViewModel = new ViewModelProvider(requireActivity()).get(ProductoViewModel.class);
        carritoViewModel = new ViewModelProvider(requireActivity()).get(CarritoViewModel.class);
        reciboViewModel = new ViewModelProvider(requireActivity()).get(ReciboViewModel.class);
        List<Anuncio> lista = Arrays.asList(
                new Anuncio(R.drawable.sample_promo, "Cupcakes 2x1 hoy"),
                new Anuncio(R.drawable.sample2, "Tartas decoradas 15%"),
                new Anuncio(R.drawable.sample3, "Envío gratis este finde")
        );

        AnuncioAdapter adapter = new AnuncioAdapter(lista, anuncio ->
                Toast.makeText(getContext(), "Clic en: " + anuncio.getTexto(), Toast.LENGTH_SHORT).show()
        );

        binding.viewPager.setAdapter(adapter);
        Runnable autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentPage == lista.size()) currentPage = 0;
                binding.viewPager.setCurrentItem(currentPage++, true);
                autoScrollHandler.postDelayed(this, 4000);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, 3000);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        binding.btnCatalogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mainFragment_to_itemsFragment);
            }
        });
        binding.btnUbicacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_mainFragment_to_mapFragment);
            }
        });

        binding.novedadesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putString("tabInicial", "novedades");
                navController.navigate(R.id.action_mainFragment_to_itemsFragment, args);
            }
        });
        ProductosAdapter novedadesAdapter = new ProductosAdapter(requireContext(), navController, productoViewModel, reciboViewModel, carritoViewModel, getViewLifecycleOwner());

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2,GridLayoutManager.HORIZONTAL,false);
        binding.recyclerViewMain.setLayoutManager(gridLayoutManager);


        novedadesAdapter.activarModoCompacto();
        binding.recyclerViewMain.setAdapter(novedadesAdapter);


        productoViewModel.obtenerProductos().observe(getViewLifecycleOwner(), productos -> {
            if (productos != null && !productos.isEmpty()) {
                assert binding.progressBar != null;
                binding.progressBar.setVisibility(View.GONE);
                binding.recyclerViewMain.setVisibility(View.VISIBLE);
            }
            List<Producto> novedades = productos.stream()
                    .filter(Producto::isNovedad)
                    .collect(Collectors.toList());

            novedadesAdapter.activarModoGrupo();
            novedadesAdapter.establecerListaProductos(novedades);
        });
    }


}