package com.example.pasteleria.main.view;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.pasteleria.login.LoginActivity;
import com.example.pasteleria.main.MainActivity;
import com.example.pasteleria.main.misc.ImageUtils;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentLoginBinding;
import com.example.pasteleria.databinding.FragmentUserBinding;
import com.example.pasteleria.main.misc.SharedPreferencesHelper;
import com.example.pasteleria.main.viewmodel.UserViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.Locale;

public class UserFragment extends Fragment {
    private FragmentUserBinding binding;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth mAuth;
    private Uri selectedImageUri;
    private SharedPreferencesHelper helper;
    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding= FragmentUserBinding.inflate(inflater,container,false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso);

        helper = new SharedPreferencesHelper(requireContext());
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        binding.modoOscuroSwitch.setChecked(helper.obtenerTemaOscuro());
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            selectedImageUri = result.getData().getData();
                            if (selectedImageUri != null) {
                                userViewModel.agregarImagen(selectedImageUri).observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                                    @Override
                                    public void onChanged(Boolean aBoolean) {
                                    }
                                });
                            }
                        }
                    }
                }
        );

        userViewModel.getImageUrl(mAuth.getCurrentUser().getUid()).observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String url) {
                if (url != null && !url.isEmpty()) {
                    Glide.with(requireContext())
                            .load(url+ "?t=" + System.currentTimeMillis())
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.fotoPerfil);
                } else {
                    binding.fotoPerfil.setImageResource(R.drawable.default_profile);
                }
            }
        });

        binding.cambiarFotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });
        binding.modoOscuroSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            helper.guardarTemaOscuro(isChecked);

            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        binding.cerrarSesionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (!helper.obtenerNombreBool()) {
                 userViewModel.cargarNombreUsuario();
            }
        }
        userViewModel.nombre.observe(requireActivity(), nombre -> {
            if (nombre != null) {
                binding.nombreTextView.setText(nombre.split(" ")[0]);
            }
        });
        String [] idiomas = {"Español","Ingles"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), androidx.appcompat.R.layout.support_simple_spinner_dropdown_item,idiomas);
        binding.spinnerIdioma.setAdapter(adapter);
        String idiomaGuardado = helper.obtenerIdioma();
        if (idiomaGuardado.equals("es")) {
            binding.spinnerIdioma.setSelection(0);
        } else {
            binding.spinnerIdioma.setSelection(1);
        }
        binding.spinnerIdioma.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String nuevoIdioma;
                if (position == 0) {
                    nuevoIdioma = "es";
                } else {
                    nuevoIdioma = "en";
                }
                if (!nuevoIdioma.equals(helper.obtenerIdioma())) {
                    cambiarIdioma(nuevoIdioma);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void cambiarIdioma(String idioma) {
        Locale nuevaLocale = new Locale(idioma);
        Locale.setDefault(nuevaLocale);

        Configuration config = new Configuration();
        config.setLocale(nuevaLocale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());

        helper.guardarIdioma(idioma);

        Intent intent = requireActivity().getIntent();
        requireActivity().finish();
        startActivity(intent);
    }

    private void logoutUser() {
        boolean loginGoogle = isGoogleLogin();
        mAuth.signOut();
        if (loginGoogle) {
            googleSignInClient.signOut().addOnCompleteListener(getActivity(), task -> {
                Toast.makeText(this.getActivity(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
                redirectToLogin();
            });
        } else {
            Toast.makeText(this.getActivity(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
            redirectToLogin();
        }
    }
    private void redirectToLogin() {
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    private boolean isGoogleLogin() {
        FirebaseUser user = mAuth.getCurrentUser();
        for (UserInfo profile : user.getProviderData()) {
            if (profile.getProviderId().equals("google.com")) {
                return true;
            }
        }
        return false;
    }

}