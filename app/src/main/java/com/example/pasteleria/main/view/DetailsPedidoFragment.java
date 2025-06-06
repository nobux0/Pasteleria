package com.example.pasteleria.main.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.pasteleria.R;
import com.example.pasteleria.main.collections.Pedido;
import com.example.pasteleria.main.collections.Producto;
import com.example.pasteleria.main.collections.ProductoPedido;
import com.example.pasteleria.main.misc.ProductosAdapter;
import com.example.pasteleria.main.misc.ReciboAdapter;

import java.util.List;

public class DetailsPedidoFragment extends Fragment {

    private Pedido pedido;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pedido = (Pedido) getArguments().getSerializable("pedido");
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_details_pedido, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerViewDetallesPedido);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (pedido != null) {
            List<ProductoPedido> productosDelPedido = pedido.getProductos();
            Log.d("DetallesPedido", "Productos recibidos: " + productosDelPedido.size());
        }
        if (pedido != null) {
            List<ProductoPedido> productosDelPedido = pedido.getProductos();
            ReciboAdapter adapter = new ReciboAdapter(productosDelPedido, null);
            recyclerView.setAdapter(adapter);
        }
    }



}