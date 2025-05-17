package com.example.pasteleria.main;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.LiveData;
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
import com.example.pasteleria.main.viewmodel.UserViewModel;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private UserViewModel userViewModel;
    ActivityMainBinding binding;
    private FirebaseAuth mAuth;
    private ImageView fotoPerfil;
    private TextView nombrePerfil;
    private ImageButton ajustesButton;
    private SharedPreferencesHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        helper = new SharedPreferencesHelper(this);

        String idioma = helper.obtenerIdioma();
        Locale nuevaLocale = new Locale(idioma);
        Locale.setDefault(nuevaLocale);

        Configuration config = new Configuration();
        config.setLocale(nuevaLocale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        if (helper.obtenerTemaOscuro()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        super.onCreate(savedInstanceState);

        setContentView((binding = ActivityMainBinding.inflate(getLayoutInflater())).getRoot());
        setSupportActionBar(binding.toolbar);
        mAuth = FirebaseAuth.getInstance();
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.mainFragment,R.id.itemsFragment,R.id.mapFragment
        )
                .setOpenableLayout(binding.drawerLayout)
                .build();
        NavController navController = ((NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment))
                .getNavController();
        NavigationUI.setupWithNavController(binding.navView, navController);
        NavigationUI.setupWithNavController(binding.toolbar, navController, appBarConfiguration);

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (!helper.obtenerNombreBool()) {
                userViewModel.cargarNombreUsuario();
            }

        }

        NavigationView navigationView = findViewById(R.id.nav_view);
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        View headerView = navigationView.getHeaderView(0);
        fotoPerfil = headerView.findViewById(R.id.fotoPerfil);
        nombrePerfil = headerView.findViewById(R.id.nombrePerfil);
        ajustesButton = headerView.findViewById(R.id.ajustesPerfil);

        ajustesButton.setOnClickListener(v ->{
            binding.drawerLayout.closeDrawers();
                navController.navigate(R.id.userFragment);
        });

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
                            .load(url+ "?t=" + System.currentTimeMillis())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(fotoPerfil);
                } else {
                    fotoPerfil.setImageResource(R.drawable.default_profile);
                }
            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return true;
        }
        return super.onSupportNavigateUp();
    }
}