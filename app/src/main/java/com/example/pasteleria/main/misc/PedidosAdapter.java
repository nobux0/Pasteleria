package com.example.pasteleria.main.misc;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pasteleria.databinding.ViewholderPedidoBinding;
import com.example.pasteleria.main.collections.Pedido;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PedidosAdapter extends RecyclerView.Adapter<PedidosAdapter.ViewHolder> {
    private List<Pedido> pedidos;
    private PedidosAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Pedido pedido);
    }

    public PedidosAdapter(List<Pedido> pedidos, PedidosAdapter.OnItemClickListener listener) {
        this.pedidos = pedidos;
        this.listener = listener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ViewholderPedidoBinding binding;

        public ViewHolder(ViewholderPedidoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pedido pedido, PedidosAdapter.OnItemClickListener listener) {
            binding.idTV.setText(pedido.getIdDocumento().substring(0, 10));
            binding.estadoTV.setText(pedido.getEstado());
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            if (pedido.getFecha() instanceof com.google.firebase.Timestamp) {
                Date fecha = ((com.google.firebase.Timestamp) pedido.getFecha()).toDate();
                String fecha2 = dateFormat.format(fecha);
                binding.fechaTV.setText(fecha2);
            }

            binding.getRoot().setOnClickListener(v -> listener.onItemClick(pedido));
        }
    }

    @NonNull
    @Override
    public PedidosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ViewholderPedidoBinding binding = ViewholderPedidoBinding.inflate(inflater, parent, false);
        return new PedidosAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidosAdapter.ViewHolder holder, int position) {
        holder.bind(pedidos.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return pedidos.size();
    }
}
