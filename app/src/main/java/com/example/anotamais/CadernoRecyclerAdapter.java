package com.example.anotamais;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CadernoRecyclerAdapter extends RecyclerView.Adapter<CadernoRecyclerAdapter.ViewHolder> {
    private List<ModelCaderno> cadernos;

    public CadernoRecyclerAdapter(List<ModelCaderno> cadernos) {
        this.cadernos = cadernos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_model_caderno, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelCaderno caderno = cadernos.get(position);
        holder.nomeCaderno.setText(caderno.getNome());

        holder.imagemCaderno.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, Caderno.class);
            intent.putExtra("nomeCaderno", caderno.getNome());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return cadernos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeCaderno;
        ImageButton imagemCaderno;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCaderno = itemView.findViewById(R.id.nomeCadernoList);
            imagemCaderno = itemView.findViewById(R.id.imagemCaderno);
        }
    }
}