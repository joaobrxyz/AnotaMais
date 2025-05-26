package com.example.anotamais;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class FlashcardAdapter extends RecyclerView.Adapter<FlashcardAdapter.FlashcardViewHolder> {

    private List<FlashcardModel> flashcardList;
    private Context context;
    private OnFlashcardListener listener;

    public interface OnFlashcardListener {
        void onEditClick(FlashcardModel flashcard);
        void onDeleteClick(FlashcardModel flashcard);
    }

    public FlashcardAdapter(Context context, List<FlashcardModel> flashcardList, OnFlashcardListener listener) {
        this.context = context;
        this.flashcardList = flashcardList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FlashcardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_flashcard, parent, false);
        return new FlashcardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlashcardViewHolder holder, int position) {
        FlashcardModel flashcard = flashcardList.get(position);
        holder.textViewPergunta.setText(flashcard.getPergunta());
        holder.textViewResposta.setText(flashcard.getResposta());

        // Resetar visibilidade da resposta ao reciclar a view
        holder.textViewResposta.setVisibility(View.GONE);
        holder.textViewRespostaLabel.setVisibility(View.GONE);

        holder.itemView.setOnClickListener(v -> {
            boolean isRespostaVisible = holder.textViewResposta.getVisibility() == View.VISIBLE;
            holder.textViewResposta.setVisibility(isRespostaVisible ? View.GONE : View.VISIBLE);
            holder.textViewRespostaLabel.setVisibility(isRespostaVisible ? View.GONE : View.VISIBLE);
        });

        holder.buttonEditar.setOnClickListener(v -> listener.onEditClick(flashcard));
        holder.buttonExcluir.setOnClickListener(v -> listener.onDeleteClick(flashcard));
    }

    @Override
    public int getItemCount() {
        return flashcardList.size();
    }

    public void updateData(List<FlashcardModel> newFlashcards) {
        this.flashcardList.clear();
        this.flashcardList.addAll(newFlashcards);
        notifyDataSetChanged();
    }

    static class FlashcardViewHolder extends RecyclerView.ViewHolder {
        TextView textViewPergunta, textViewResposta, textViewPerguntaLabel, textViewRespostaLabel;
        ImageButton buttonEditar, buttonExcluir;

        public FlashcardViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewPergunta = itemView.findViewById(R.id.textViewPergunta);
            textViewResposta = itemView.findViewById(R.id.textViewResposta);
            textViewPerguntaLabel = itemView.findViewById(R.id.textViewPerguntaLabel);
            textViewRespostaLabel = itemView.findViewById(R.id.textViewRespostaLabel);
            buttonEditar = itemView.findViewById(R.id.buttonEditarFlashcard);
            buttonExcluir = itemView.findViewById(R.id.buttonExcluirFlashcard);
        }
    }
}