package com.example.pasteleria.login;

import static android.app.Activity.RESULT_OK;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pasteleria.main.MainActivity;
import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentLoginBinding;
import com.example.pasteleria.main.collections.Cliente;
import com.example.pasteleria.main.model.FirestoreClient;
import com.example.pasteleria.main.viewmodel.UserViewModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {
    private ActivityResultLauncher<Intent> googleSignInLauncher;
    private GoogleSignInClient googleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FragmentLoginBinding binding;
    private FirebaseAuth mAuth;
    private NavController navController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding = FragmentLoginBinding.inflate(inflater, container, false)).getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        navController = Navigation.findNavController(view);
        // Verificar si el usuario ya está autenticado y redirigir a MainActivity
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            iniciarMainActivity();
        }

        // Evento para registro
        binding.registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_loginFragment_to_registerFragment);
            }
        });

        // Evento para inicio de sesión
        binding.iniciarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
        configurarClienteGoogleSignIn();
        inicializarLauncherGoogleSignIn();
        binding.googleSignInButton.setOnClickListener(v-> signInWithGoogle());
    }


    private void signInWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        googleSignInLauncher.launch(signInIntent);
    }

    private void loginUser() {
        String email = binding.correoInput.getText().toString().trim();
        String password = binding.contrasenaInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(), "Completa todos los campos", Toast.LENGTH_SHORT).
            show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser usuario = mAuth.getCurrentUser();
                        Toast.makeText(requireActivity(), "Inicio de sesión exitoso: " + usuario.getEmail(), Toast.LENGTH_SHORT).show();
                        iniciarMainActivity();
                    } else {
                        Toast.makeText(requireActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Inicia la activity principal
    private void iniciarMainActivity() {
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }

    // Registra a un nuevo usuario con los valores introducidos
    private void registerUser() {
        String email = binding.correoInput.getText().toString().trim();
        String password = binding.contrasenaInput.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireActivity(), "Por favor completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
            if (task.isSuccessful()) {
                // Recuperamos el usuario creado
                FirebaseUser user = mAuth.getCurrentUser();
                iniciarMainActivity();
            }
        });

    }
    private void configurarClienteGoogleSignIn() {
        // Configurar Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Inicializar Google Sign-In a partir de la configuración previa
        googleSignInClient = GoogleSignIn.getClient(this.getActivity(), gso);
    }

    private void inicializarLauncherGoogleSignIn() {
        // Inicializar el ActivityResultLauncher para manejar la respuesta de Google Sign-In
        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                        gestionarResultadoSignIn(task);
                    } else {
                        Toast.makeText(this.getActivity(), "Error en el inicio de sesión con Google", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
    private void gestionarResultadoSignIn(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            Toast.makeText(this.getActivity(), "Error al iniciar sesión con Google", Toast.LENGTH_SHORT).show();
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this.getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        Cliente nuevoCliente = new Cliente(user.getUid(), user.getDisplayName(), user.getEmail(), null, null,"");
                        FirebaseFirestore db = FirestoreClient.getInstance().getDatabase();

                        db.collection("clientes").document(nuevoCliente.getId()).set(nuevoCliente)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Cliente agregado correctamente")) // Se ejecuta si el anuncio se agregó correctamente
                                .addOnFailureListener(e -> Log.e("Firestore", "Error al agregar cliente", e));;
                        Toast.makeText(this.getActivity(), "Inicio de sesión con Google exitoso", Toast.LENGTH_SHORT).show();
                        iniciarMainActivity();
                    } else {
                        Toast.makeText(this.getActivity(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}