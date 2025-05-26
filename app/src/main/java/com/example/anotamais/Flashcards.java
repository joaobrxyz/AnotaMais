package com.example.anotamais;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class Flashcards extends AppCompatActivity implements FlashcardAdapter.OnFlashcardListener {

    private RecyclerView recyclerViewFlashcards;
    private FlashcardAdapter flashcardAdapter;
    private List<FlashcardModel> flashcardList;
    //private BancoControllerCard bancoControllerCard; // MUDANÇA AQUI
    private FloatingActionButton fabAdicionarFlashcard;
    private TextView textViewEmptyFlashcards;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcards);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets; // Retornar os insets é importante
        });

        toolbar = findViewById(R.id.toolbarFlashcards);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Meus Flashcards");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        recyclerViewFlashcards = findViewById(R.id.recyclerViewFlashcards);
        fabAdicionarFlashcard = findViewById(R.id.fabAdicionarFlashcard);
        textViewEmptyFlashcards = findViewById(R.id.textViewEmptyFlashcards);

        //bancoControllerCard = new BancoControllerCard(this); // MUDANÇA AQUI
        flashcardList = new ArrayList<>();

        recyclerViewFlashcards.setLayoutManager(new LinearLayoutManager(this));
        flashcardAdapter = new FlashcardAdapter(this, flashcardList, this);
        recyclerViewFlashcards.setAdapter(flashcardAdapter);

        fabAdicionarFlashcard.setOnClickListener(v -> showAddEditFlashcardDialog(null));

        carregarFlashcards();
    } // FIM DO ONCREATE - OS MÉTODOS ABAIXO DEVEM ESTAR FORA DELE

    private void carregarFlashcards() {
        //List<FlashcardModel> novosFlashcards = bancoControllerCard.carregaTodosFlashcards();
        //flashcardAdapter.updateData(novosFlashcards);

        if (flashcardAdapter.getItemCount() == 0) {
            textViewEmptyFlashcards.setVisibility(View.VISIBLE);
            recyclerViewFlashcards.setVisibility(View.GONE);
        } else {
            textViewEmptyFlashcards.setVisibility(View.GONE);
            recyclerViewFlashcards.setVisibility(View.VISIBLE);
        }
    }

    private void showAddEditFlashcardDialog(final FlashcardModel flashcardModel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_edit_flashcard, null);
        builder.setView(dialogView);

        final EditText editTextPergunta = dialogView.findViewById(R.id.editTextPerguntaFlashcard);
        final EditText editTextResposta = dialogView.findViewById(R.id.editTextRespostaFlashcard);
        final TextView textViewDialogTitle = dialogView.findViewById(R.id.textViewDialogTitle);
        Button buttonSalvar = dialogView.findViewById(R.id.buttonSalvarFlashcard);
        Button buttonCancelar = dialogView.findViewById(R.id.buttonCancelarFlashcard);

        final AlertDialog dialog = builder.create();

        if (flashcardModel != null) {
            textViewDialogTitle.setText("Editar Flashcard");
            editTextPergunta.setText(flashcardModel.getPergunta());
            editTextResposta.setText(flashcardModel.getResposta());
        } else {
            textViewDialogTitle.setText("Adicionar Flashcard");
        }

        buttonSalvar.setOnClickListener(v -> {
            String pergunta = editTextPergunta.getText().toString().trim();
            String resposta = editTextResposta.getText().toString().trim();

            if (pergunta.isEmpty() || resposta.isEmpty()) {
                Toast.makeText(Flashcards.this, "Pergunta e resposta não podem estar vazias", Toast.LENGTH_SHORT).show();
                return;
            }

            String resultadoMsg;
            if (flashcardModel != null) {
                //resultadoMsg = bancoControllerCard.alteraDados(flashcardModel.getId(), pergunta, resposta);
            } else { // Adicionando

                // Exemplo: resultadoMsg = bancoControllerCard.insereDados(pergunta, resposta, 0);
                //resultadoMsg = bancoControllerCard.insereFlashcardSemNote(pergunta, resposta); // MÉTODO HIPOTÉTICO
            }
            //Toast.makeText(Flashcards.this, resultadoMsg, Toast.LENGTH_SHORT).show();
            carregarFlashcards();
            dialog.dismiss();
        });

        buttonCancelar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    @Override
    public void onEditClick(FlashcardModel flashcard) {
    }

    @Override
    public void onDeleteClick(final FlashcardModel flashcard) {
        new AlertDialog.Builder(this)
                .setTitle("Excluir Flashcard")
                .setMessage("Tem certeza que deseja excluir este flashcard?\n\nPergunta: " + flashcard.getPergunta())
                .setPositiveButton("Excluir", (dialog, which) -> {

                    //String resultadoMsg = bancoControllerCard.excluirDados(flashcard.getId());
                    //Toast.makeText(Flashcards.this, resultadoMsg, Toast.LENGTH_SHORT).show();
                    carregarFlashcards();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}