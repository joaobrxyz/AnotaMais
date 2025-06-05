package com.example.anotamais.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.models.CadernoModel;
import com.example.anotamais.R;
import com.example.anotamais.activities.Caderno;

import java.util.List;

public class CadernoRecyclerAdapter extends RecyclerView.Adapter<CadernoRecyclerAdapter.ViewHolder> {
    private List<CadernoModel> cadernos;
    private Context context;

    public CadernoRecyclerAdapter(Context context, List<CadernoModel> cadernos) {
        this.context = context;
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
        CadernoModel caderno = cadernos.get(position);
        holder.nomeCaderno.setText(caderno.getNome());

        holder.imagemCaderno.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, Caderno.class);
            intent.putExtra("nomeCaderno", caderno.getNome());
            intent.putExtra("idCaderno", caderno.getId());
            context.startActivity(intent);
        });

        holder.imagemCaderno.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Excluir Caderno")
                    .setMessage("Tem certeza que deseja excluir esse caderno?")
                    .setPositiveButton("Excluir", (dialog, which) -> {
                        BancoControllerCaderno cad = new BancoControllerCaderno(context);
                        boolean sucesso = cad.excluirCaderno(caderno.getId());

                        if (sucesso) {
                            cadernos.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Anotação excluída", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Erro ao excluir", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
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