package com.example.anotamais;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AnotacaoRecyclerAdapter extends RecyclerView.Adapter<AnotacaoRecyclerAdapter.ViewHolder> {

    private final List<Nota> listaNotas;

    public AnotacaoRecyclerAdapter(List<Nota> listaNotas) {
        this.listaNotas = listaNotas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_model_anotacao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Nota nota = listaNotas.get(position);
        holder.bind(nota);
    }

    @Override
    public int getItemCount() {
        return listaNotas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView titulo;
        private final TextView conteudo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.tituloAnotacao);
            conteudo = itemView.findViewById(R.id.conteudoAnotacao);
        }

        public void bind(Nota nota) {
            titulo.setText(nota.titulo);
            conteudo.setText(nota.conteudo);
        }
    }
}
