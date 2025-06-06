package com.example.pasteleria.main;

import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pasteleria.R;
import com.example.pasteleria.databinding.ActivityMainBinding;
import com.example.pasteleria.main.misc.SharedPreferencesHelper;
import com.example.pasteleria.main.viewmodel.CarritoViewModel;
import com.example.pasteleria.main.viewmodel.UserViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    private CarritoViewModel carritoViewModel;
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private ImageView fotoPerfil;
    private TextView nombrePerfil;
    private ImageButton ajustesButton;
    private SharedPreferencesHelper helper;
    private NavController navController;
    private boolean MapFragment = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        helper = new SharedPreferencesHelper(this);

        // Tema oscuro
        AppCompatDelegate.setDefaultNightMode(
                helper.obtenerTemaOscuro() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        // Color barra notificaciones
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.chocolate_oscuro1));
        }
        // Idioma
        String idioma = helper.obtenerIdioma();
        Locale nuevaLocale = new Locale(idioma);
        Locale.setDefault(nuevaLocale);
        Configuration config = new Configuration();
        config.setLocale(nuevaLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(true);
        mAuth = FirebaseAuth.getInstance();
        navController = ((NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment)).getNavController();

        //Ocultar BottomNavigationView en userFragment
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.userFragment|| destination.getId() == R.id.pedidosFragment|| destination.getId() == R.id.pagoFragment || destination.getId() == R.id.direccion1Fragment || destination.getId() == R.id.direccion2Fragment) {
                binding.bottomNavigationView.setVisibility(View.GONE);
            } else {
                binding.bottomNavigationView.setVisibility(View.VISIBLE);
            }
        });
        carritoViewModel = new ViewModelProvider(this).get(CarritoViewModel.class);

        carritoViewModel.getCantidadProductos().observe(this, total -> {
            BadgeDrawable badge = binding.bottomNavigationView.getOrCreateBadge(R.id.carritoFragment);
            if (total > 0) {
                badge.setNumber(total);
                badge.setVisible(true);
            } else {
                badge.clearNumber();
                badge.setVisible(false);
            }
        });
        //Solucion problema webview en MapFragment

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.mapFragment) {
                MapFragment = true;
            } else {
                if (MapFragment) {
                    MapFragment = false;
                    recreate();
                }
            }
        });
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment, R.id.itemsFragment, R.id.mapFragment
        ).setOpenableLayout(binding.drawerLayout).build();
        binding.fotoPerfildrawer.setOnClickListener(v -> {
            binding.drawerLayout.openDrawer(GravityCompat.END);
        });
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController);
        binding.toolbar.setNavigationOnClickListener(v -> {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END);
            } else {
                binding.drawerLayout.openDrawer(GravityCompat.END);
            }
        });
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            navController.popBackStack(R.id.mainFragment, false);
            if (itemId == R.id.mainFragment) {
                navController.navigate(R.id.mainFragment);
            } else if (itemId == R.id.itemsFragment) {
                navController.navigate(R.id.itemsFragment);
            } else if (itemId == R.id.carritoFragment) {
                navController.navigate(R.id.carritoFragment);
            } else if (itemId == R.id.mapFragment) {
                navController.navigate(R.id.mapFragment);
            }

            return true;
        });
        binding.navView.setNavigationItemSelectedListener(item -> {
            int destinationId = item.getItemId();
            int currentId = navController.getCurrentDestination() != null
                    ? navController.getCurrentDestination().getId() : -1;
            if (destinationId == currentId) {
                binding.drawerLayout.closeDrawers();
                return true;
            }
            try {
                navController.navigate(destinationId);
            } catch (IllegalArgumentException e) {
                navController.popBackStack(destinationId, false);
            }
            binding.drawerLayout.closeDrawers();
            return true;
        });

        // Header del Navigation Drawer
        NavigationView navigationView = binding.navView;
        View headerView = navigationView.getHeaderView(0);
        fotoPerfil = headerView.findViewById(R.id.fotoPerfil);
        nombrePerfil = headerView.findViewById(R.id.nombrePerfil);
        ajustesButton = headerView.findViewById(R.id.ajustesPerfil);

        ajustesButton.setOnClickListener(v -> {
            binding.drawerLayout.closeDrawers();
            navController.navigate(R.id.userFragment);
        });

        // ViewModel y usuario actual
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && !helper.obtenerNombreBool()) {
            userViewModel.cargarNombreUsuario();
        }

        // Observers para el nombre y la imagen de perfil
        userViewModel.nombre.observe(this, nombre -> {
            if (nombre != null) {
                nombrePerfil.setText(nombre.split(" ")[0]);
            }
        });

        userViewModel.getImageUrl(mAuth.getCurrentUser().getUid()).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String url) {
                if (url != null && !url.isEmpty()) {
                    Glide.with(MainActivity.this)
                            .load(url + "?t=" + System.currentTimeMillis())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(fotoPerfil);
                    Glide.with(MainActivity.this)
                            .load(url + "?t=" + System.currentTimeMillis())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.fotoPerfildrawer);
                } else {
                    fotoPerfil.setImageResource(R.drawable.default_profile);
                }
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, binding.drawerLayout);
    }
}
