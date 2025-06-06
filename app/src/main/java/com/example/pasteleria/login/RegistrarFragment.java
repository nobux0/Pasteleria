package com.example.pasteleria.login;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.pasteleria.main.MainActivity;
import com.example.pasteleria.R;
import com.example.pasteleria.databinding.FragmentRegistrarBinding;
import com.example.pasteleria.main.collections.Cliente;
import com.example.pasteleria.main.model.FirestoreClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


public class RegistrarFragment extends Fragment {
    private FragmentRegistrarBinding binding;
    private FirebaseAuth mAuth;
    private NavController navController;
    private Date fecha;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return (binding = FragmentRegistrarBinding.inflate(inflater, container, false)).getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        navController = Navigation.findNavController(view);

        binding.fechaNac.setFocusable(false);
        binding.fechaNac.setOnClickListener(v -> mostrarDatePicker());

        // Evento para ir a pantalla de inicio de sesion
        binding.registrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navController.navigate(R.id.action_registerFragment_to_loginFragment);
            }
        });

        // Evento para Registro
        binding.iniciarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.contrasenaRepetirLayout.setError(null);
                registerUser();
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
        String nombre = binding.nombreInput.getText().toString().trim();
        String telefono = binding.telefonoInput.getText().toString().trim();
        String password = binding.contrasenaInput.getText().toString().trim();
        String passwordRepe = binding.contrasenaRepetirInput.getText().toString().trim();

        if (email.isEmpty()) {
            binding.correoInput.setError("El correo es obligatorio");
        }
        if (password.isEmpty()) {
            binding.contrasenaInput.setError("La contraseña es obligatoria");
        }
        if (telefono.isEmpty()) {
            binding.telefonoInput.setError("El telefono es obligatorio");
        }
        if (nombre.isEmpty()) {
            binding.nombreInput.setError("El nombre es obligatorio");
        }
        if (fecha == null) {
            binding.fechaNac.setError("La fecha de nacimiento es obligatoria");
        }
        if(password.equals(passwordRepe) ) {
            if (email.isEmpty() || password.isEmpty() || nombre.isEmpty() || fecha == null) {
                return;
            }

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(requireActivity(), task -> {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    Cliente nuevoCliente = new Cliente(user.getUid(), nombre, email, telefono, null,"",fecha);
                    FirebaseFirestore db = FirestoreClient.getInstance().getDatabase();
                    db.collection("clientes").document(nuevoCliente.getId()).set(nuevoCliente)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Cliente agregado correctamente");
                                crearClienteEnStripe(user.getUid(), email, nombre);
                            })
                            .addOnFailureListener(e -> Log.e("Firestore", "Error al agregar cliente", e));;
                    Toast.makeText(requireActivity(), "Registro exitoso: " + user.getEmail(), Toast.LENGTH_SHORT).show();
                    iniciarMainActivity();
                }
            });
        } else {
            binding.contrasenaRepetirLayout.setError("Las contraseñas no coinciden");
        }
    }
    private void mostrarDatePicker(){
        binding.fechaNac.setError(null);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -10);

        int anio = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), (datePicker, year1, month1, day1) -> {
            Calendar selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, day1 , 0,0,0);
            fecha = selectedDate.getTime();
            binding.fechaNac.setText(day1 + "/" + (month1 + 1) + "/" + year1);
            binding.fechaNac.setError(null);
        }, anio, mes, dia);
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());
        datePickerDialog.show();
    }
    private void crearClienteEnStripe(String uid, String email, String nombre) {
        OkHttpClient client = new OkHttpClient();

        Map<String, String> data = new HashMap<>();
        data.put("uid", uid);
        data.put("email", email);
        data.put("name", nombre);

        String json = new Gson().toJson(data);

        Log.d("StripeRequest", json);
        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/stripe/create-customer")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Stripe", "Error creando cliente en Stripe", e);
                Log.d("StripeRequest", json);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "Respuesta vacía";
                    try {
                        JSONObject jsonResponse = new JSONObject(responseBody);
                        String stripeCustomerId = jsonResponse.getString("customerId");
                        Map<String, Object> updateData = new HashMap<>();
                        updateData.put("stripeCustomerId", stripeCustomerId);
                        FirebaseFirestore db = FirestoreClient.getInstance().getDatabase();
                        db.collection("clientes")
                                .document(uid)
                                .update(updateData)
                                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Stripe UID guardado correctamente"))
                                .addOnFailureListener(e -> Log.e("Firestore", "Error guardando Stripe UID", e));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    Log.e("Stripe", "Fallo creando cliente en Stripe: " + response.code());
                }
            }
        });
    }


}