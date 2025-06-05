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

import com.example.anotamais.controllers.BancoControllerNote;
import com.example.anotamais.models.NotaModel;
import com.example.anotamais.R;
import com.example.anotamais.activities.Notes;

import java.util.List;

public class AnotacaoRecyclerAdapter extends RecyclerView.Adapter<AnotacaoRecyclerAdapter.ViewHolder> {
    private List<NotaModel> notas;
    private Context context;

    public AnotacaoRecyclerAdapter(Context context, List<NotaModel> notas) {
        this.context = context;
        this.notas = notas;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_model_anotacao, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NotaModel nota = notas.get(position);
        holder.tituloAnotacao.setText(nota.getTitulo());

        holder.imagemNote.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, Notes.class);
            intent.putExtra("idPagina", nota.getId());
            intent.putExtra("idCaderno", nota.getIdCaderno());
            intent.putExtra("nomeCaderno", nota.getNomeCaderno());
            context.startActivity(intent);


        });

        holder.imagemNote.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Excluir Anotação")
                    .setMessage("Tem certeza que deseja excluir esta anotação?")
                    .setPositiveButton("Excluir", (dialog, which) -> {
                        BancoControllerNote notaDAO = new BancoControllerNote(context);
                        boolean sucesso = notaDAO.excluirNota(nota.getId());

                        if (sucesso) {
                            notas.remove(position);
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
        return notas.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tituloAnotacao;
        ImageButton imagemNote;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloAnotacao = itemView.findViewById(R.id.nomeNoteList);
            imagemNote = itemView.findViewById(R.id.imagemNote);
        }
    }
}