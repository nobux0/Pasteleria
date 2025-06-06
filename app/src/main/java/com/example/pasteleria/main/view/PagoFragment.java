package com.example.pasteleria.main.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.pasteleria.databinding.FragmentPagoBinding;
import com.example.pasteleria.main.collections.Direccion;
import com.example.pasteleria.main.collections.ProductoPedido;
import com.example.pasteleria.main.misc.ReciboAdapter;
import com.example.pasteleria.main.model.PedidoRepository;
import com.example.pasteleria.main.viewmodel.ReciboViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PagoFragment extends Fragment {
    private FragmentPagoBinding binding;
    private PaymentSheet paymentSheet;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ReciboViewModel reciboViewModel;
    private String direccionId;
    private PaymentSheet.CustomerConfiguration customerConfig;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PaymentConfiguration.init(
                requireContext().getApplicationContext(),
                "pk_test_51RERvYRtI6IUj8z1WzvNxvn1BUb7KakJk1FhFFsFj4IOwL6RloiZreKBM3IS9EIecLaQ9E2SRjPKuDxyKqhmQyxA00mOsw1M3t"
        );
        Direccion direccionSeleccionada = (Direccion) getArguments().getSerializable("direccionSeleccionada");

        paymentSheet = new PaymentSheet(this, paymentResult -> {
            if (paymentResult instanceof PaymentSheetResult.Completed) {
                Log.d("Stripe", "Pago completado");

                // Crear el pedido
                PedidoRepository pedidoRepository = new PedidoRepository(requireActivity().getApplication());
                String direccionId = direccionSeleccionada.getIdUsuario();
                List<ProductoPedido> productos = reciboViewModel.getProductosRecibo().getValue();

                if (productos != null && direccionId != null) {
                    pedidoRepository.crearPedido(direccionId, productos).observe(this, exito -> {
                        if (Boolean.TRUE.equals(exito)) {
                            Log.d("Pedido", "Pedido creado exitosamente");
                            Toast.makeText(requireContext(), "Pedido realizado con éxito", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e("Pedido", "Error al crear el pedido");
                            Toast.makeText(requireContext(), "error", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Log.e("Pedido", "No se puede crear el pedido faltan datos");
                    Toast.makeText(requireContext(), "errror", Toast.LENGTH_SHORT).show();
                }

            } else {
                Log.e("Stripe", "Pago fallido o cancelado");
            }
        });
        reciboViewModel = new ViewModelProvider(requireActivity()).get(ReciboViewModel.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPagoBinding.inflate(inflater, container, false);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        binding.pagarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.collection("clientes")
                        .document(uid)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                String stripeCustomerId = documentSnapshot.getString("stripeCustomerId");

                                if (stripeCustomerId != null && !stripeCustomerId.isEmpty()) {
                                    crearPaymentIntent(stripeCustomerId);
                                } else {
                                    Log.e("Firestore", "stripeCustomerId no encontrado");
                                }
                            } else {
                                Log.e("Firestore", "Documento del cliente no existe");
                            }
                        })
                        .addOnFailureListener(e -> Log.e("Firestore", "Error obteniendo el cliente", e));
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ReciboAdapter reciboAdapter = new ReciboAdapter(new ArrayList<>(), reciboViewModel);
        reciboViewModel.getProductosRecibo().observe(getViewLifecycleOwner(), new Observer<List<ProductoPedido>>() {
            @Override
            public void onChanged(List<ProductoPedido> productoPedidos) {
                reciboAdapter.setProductos(productoPedidos);
                reciboAdapter.notifyDataSetChanged();
                double precio = reciboViewModel.obtenerTotal();
                if (precio == (int) precio) {
                    binding.total.setText((int) precio + "€");
                } else {
                    binding.total.setText(String.format(Locale.getDefault(), "%.2f€", precio));
                }
            }
        });
        binding.recyclerView.setAdapter(reciboAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void crearPaymentIntent(String stripeCustomerId) {
        OkHttpClient client = new OkHttpClient();

        Map<String, Object> data = new HashMap<>();
        data.put("customerId", stripeCustomerId);
        data.put("amount", reciboViewModel.obtenerTotal() * 100); // Convertir a centimos
        data.put("currency", "eur");

        String json = new Gson().toJson(data);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:8080/api/stripe/create-payment-intent")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Stripe", "Error creando PaymentIntent", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    Log.d("Stripe", "PaymentIntent creado: " + responseBody);

                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String clientSecret = json.getString("clientSecret");

                        lanzarPaymentSheet(clientSecret, stripeCustomerId);

                    } catch (JSONException e) {
                        Log.e("Stripe", "Error parseando respuesta", e);
                    }

                } else {
                    Log.e("Stripe", "Fallo creando PaymentIntent: " + response.code());
                }
            }
        });
    }
    private void lanzarPaymentSheet(String clientSecret,String stripeCustomerId) {
        requireActivity().runOnUiThread(() -> {
            PaymentSheet.Configuration config = new PaymentSheet.Configuration.Builder("Pastelería Ejemplo")
                    .customer(new PaymentSheet.CustomerConfiguration( stripeCustomerId, "ephemeralKey_xxx"))
                    .allowsDelayedPaymentMethods(true)
                    .build();

            paymentSheet.presentWithPaymentIntent(clientSecret, config);
        });
    }


}