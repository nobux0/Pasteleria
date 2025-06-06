package com.example.pasteleria.login;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.ActivityLoginBinding;
import com.example.pasteleria.main.misc.SharedPreferencesHelper;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private FirebaseAuth mAuth;

    private SharedPreferencesHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new SharedPreferencesHelper(this);
        // Tema oscuro
        AppCompatDelegate.setDefaultNightMode(
                helper.obtenerTemaOscuro() ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );
        binding = ActivityLoginBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());

        NavController navController = ((NavHostFragment)getSupportFragmentManager().findFragmentById(R.id.auth_nav_host_fragment)).getNavController();
        Log.d("LoginActivity", "NavController: " + navController.getCurrentDestination());

    }
}