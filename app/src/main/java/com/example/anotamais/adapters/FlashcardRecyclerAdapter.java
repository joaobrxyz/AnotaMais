package com.example.anotamais.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotamais.controllers.BancoControllerCard;
import com.example.anotamais.models.FlashcardModel;
import com.example.anotamais.R;

import java.util.List;

public class FlashcardRecyclerAdapter extends RecyclerView.Adapter<FlashcardRecyclerAdapter.ViewHolder> {
    private List<FlashcardModel> cards;
    private Context context;
    private FrameLayout fundoPopupFlashcards;
    private LinearLayout conteudoPrincipalFlashcards;
    private TextView txtPerguntaResCard, txtRespostaCard;

    public FlashcardRecyclerAdapter(Context context, List<FlashcardModel> cards, FrameLayout fundoPopupFlashcards, LinearLayout conteudoPrincipalFlashcards, TextView txtPerguntaResCard, TextView txtRespostaCard) {
        this.context = context;
        this.cards = cards;
        this.fundoPopupFlashcards = fundoPopupFlashcards;
        this.conteudoPrincipalFlashcards = conteudoPrincipalFlashcards;
        this.txtPerguntaResCard = txtPerguntaResCard;
        this.txtRespostaCard = txtRespostaCard;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_model_flashcard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FlashcardModel card = cards.get(position);
        holder.pergunta.setText(card.getPergunta());

        holder.itemCard.setOnClickListener(v -> {
            fundoPopupFlashcards.setVisibility(View.VISIBLE);
            conteudoPrincipalFlashcards.animate().alpha(0.3f).setDuration(200).start();
            txtPerguntaResCard.setText(card.getPergunta());
            txtRespostaCard.setText(card.getResposta());
        });

        holder.itemCard.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Excluir Flashcard")
                    .setMessage("Tem certeza que deseja excluir este Flashcard?")
                    .setPositiveButton("Excluir", (dialog, which) -> {
                        BancoControllerCard bdCard = new BancoControllerCard(context);
                        boolean sucesso = bdCard.excluirCard(card.getId());

                        if (sucesso) {
                            cards.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Flashcard excluído", Toast.LENGTH_SHORT).show();
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
        return cards.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView pergunta;
        LinearLayout itemCard;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pergunta = itemView.findViewById(R.id.textoPergunta);
            itemCard = itemView.findViewById(R.id.itemCard);

        }
    }
}