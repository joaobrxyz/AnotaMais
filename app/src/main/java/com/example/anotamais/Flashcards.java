package com.example.anotamais;

import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Flashcards extends AppCompatActivity {

    private static final String TAG = "FlashcardsActivity";

    private RecyclerView uiRecyclerViewFlashcards; // Nome da variável UI
    private FlashcardAdapter flashcardAdapter;
    private List<FlashcardModel> listaDeFlashcards = new ArrayList<>(); // Nome da variável
    private BancoControllerCard bancoControllerCard;

    private int currentNoteIdRecebido; // ID da anotação recebido via Intent
    private String currentNoteTitleRecebido; // Título da anotação recebido

    private TextView uiTvTituloMateria;
    private ImageButton uiBtVoltarNotes;
    private Toolbar uiToolbarFlashcards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flashcards);

        uiToolbarFlashcards = findViewById(R.id.toolbarFlashcards);
        setSupportActionBar(uiToolbarFlashcards);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        currentNoteIdRecebido = getIntent().getIntExtra("NOTE_ID", -1);
        currentNoteTitleRecebido = getIntent().getStringExtra("NOTE_TITLE");

        Log.i(TAG, "Activity Criada. NOTE_ID Recebido: " + currentNoteIdRecebido + ", Título: " + currentNoteTitleRecebido);

        if (currentNoteIdRecebido == -1) {
            Toast.makeText(this, "Erro: ID da Anotação não fornecido.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "NOTE_ID inválido (-1). Finalizando Activity.");
            finish();
            return;
        }

        bancoControllerCard = new BancoControllerCard(this);
        // open() será chamado em onResume

        uiRecyclerViewFlashcards = findViewById(R.id.recyclerViewFlashcards);
        uiTvTituloMateria = findViewById(R.id.tvTituloMateria);
        uiBtVoltarNotes = findViewById(R.id.btVoltarNotes);

        if (currentNoteTitleRecebido != null && !currentNoteTitleRecebido.isEmpty()) {
            uiTvTituloMateria.setText(currentNoteTitleRecebido);
        } else {
            uiTvTituloMateria.setText("Flashcards"); // Título padrão
        }
        uiTvTituloMateria.setVisibility(View.VISIBLE);


        // O listener é null porque esta activity é apenas para visualização e não lida com cliques de editar/deletar
        flashcardAdapter = new FlashcardAdapter(listaDeFlashcards, null);
        uiRecyclerViewFlashcards.setLayoutManager(new LinearLayoutManager(this));
        uiRecyclerViewFlashcards.setAdapter(flashcardAdapter);

        uiBtVoltarNotes.setOnClickListener(v -> finish());

        // loadFlashcards() será chamado em onResume
    }

    private void loadFlashcards() {
        if (bancoControllerCard == null || !bancoControllerCard.isOpen()) {
            Log.w(TAG, "loadFlashcards chamado mas o controller do banco não está pronto/aberto. Tentando abrir...");
            if (bancoControllerCard == null) bancoControllerCard = new BancoControllerCard(this);
            try {
                bancoControllerCard.open();
            } catch (SQLException e) {
                Log.e(TAG, "Falha crítica ao abrir banco em loadFlashcards: " + e.getMessage());
                Toast.makeText(this, "Erro de banco. Não foi possível carregar os flashcards.", Toast.LENGTH_LONG).show();
                return;
            }
        }

        if (currentNoteIdRecebido == -1) {
            Log.e(TAG, "Não é possível carregar flashcards: ID da Anotação é -1.");
            Toast.makeText(this, "ID da anotação inválido para carregar flashcards.", Toast.LENGTH_SHORT).show();
            listaDeFlashcards.clear(); // Limpa qualquer dado antigo
            flashcardAdapter.notifyDataSetChanged();
            uiRecyclerViewFlashcards.setVisibility(View.GONE);
            return;
        }

        Log.d(TAG, "Iniciando carregamento de flashcards para NOTE_ID: " + currentNoteIdRecebido);
        listaDeFlashcards.clear(); // Limpa a lista antes de carregar novos dados
        Cursor cursor = null;

        try {
            cursor = bancoControllerCard.carregaFlashcardsPorNoteId(currentNoteIdRecebido);
            if (cursor != null) {
                Log.d(TAG, "Cursor para NOTE_ID " + currentNoteIdRecebido + " obtido. Contagem: " + cursor.getCount());
                if (cursor.moveToFirst()) {
                    do {
                        int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                        String pergunta = cursor.getString(cursor.getColumnIndexOrThrow("pergunta"));
                        String resposta = cursor.getString(cursor.getColumnIndexOrThrow("resposta"));
                        // int idNoteRetornada = cursor.getInt(cursor.getColumnIndexOrThrow("id_note"));
                        // Log.v(TAG, "Lido do DB: Flashcard ID=" + id + ", Pergunta='" + pergunta + "', id_note=" + idNoteRetornada);
                        listaDeFlashcards.add(new FlashcardModel(id, pergunta, resposta));
                    } while (cursor.moveToNext());
                } else {
                    Log.d(TAG, "Nenhum flashcard encontrado no cursor para NOTE_ID: " + currentNoteIdRecebido);
                }
            } else {
                Log.w(TAG, "Cursor nulo retornado de carregaFlashcardsPorNoteId para NOTE_ID: " + currentNoteIdRecebido);
            }
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Erro ao ler o cursor (nome da coluna pode estar errado): " + e.getMessage(), e);
            Toast.makeText(this, "Erro ao processar dados dos flashcards.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) { // Pega outras exceções inesperadas
            Log.e(TAG, "Erro geral ao carregar flashcards: " + e.getMessage(), e);
            Toast.makeText(this, "Ocorreu um erro ao carregar os flashcards.", Toast.LENGTH_SHORT).show();
        }
        finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                Log.d(TAG, "Cursor fechado em loadFlashcards.");
            }
        }

        flashcardAdapter.notifyDataSetChanged();

        if (listaDeFlashcards.isEmpty()) {
            uiRecyclerViewFlashcards.setVisibility(View.GONE); // Conforme seu XML
            Log.d(TAG, "Lista de flashcards vazia. RecyclerView oculto.");
            // Considere mostrar um TextView com "Nenhum flashcard para esta anotação."
        } else {
            uiRecyclerViewFlashcards.setVisibility(View.VISIBLE);
            Log.i(TAG, "Flashcards carregados e exibidos. Quantidade: " + listaDeFlashcards.size());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Abrindo banco e carregando flashcards.");
        if (bancoControllerCard == null) { // Segurança, caso onCreate não tenha completado
            bancoControllerCard = new BancoControllerCard(this);
        }
        try {
            bancoControllerCard.open();
        } catch (SQLException e) {
            Log.e(TAG, "Erro fatal ao abrir banco em onResume: " + e.getMessage());
            Toast.makeText(this, "Erro de banco. A funcionalidade pode ser afetada.", Toast.LENGTH_LONG).show();
            // Poderia finalizar a activity ou desabilitar interações com o DB
            return;
        }
        loadFlashcards(); // Sempre recarrega ao se tornar visível
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: Verificando se fecha o banco.");
        // Se você quiser ser agressivo com recursos, feche aqui.
        // Mas para evitar abrir/fechar repetidamente, fechar em onDestroy é geralmente suficiente.
        // if (bancoControllerCard != null && bancoControllerCard.isOpen() && !isChangingConfigurations()) {
        //     bancoControllerCard.close();
        // }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Fechando banco.");
        if (bancoControllerCard != null && bancoControllerCard.isOpen()) {
            bancoControllerCard.close();
        }
    }
}