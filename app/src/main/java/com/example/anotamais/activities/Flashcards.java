package com.example.anotamais.activities;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anotamais.R;
import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.controllers.BancoControllerNote;
import com.example.anotamais.controllers.FlashcardsController;

public class Flashcards extends AppCompatActivity {
    // Declaração de componentes da UI
    ImageButton btVoltarFlashcard, btVoltarRespostaFlashcard;
    TextView txtPerguntaResCard, txtRespostaCard, textoPlaceHolderFlashcards, txtFiltro;
    RecyclerView listaCards;
    LinearLayout conteudoPrincipalFlashcards, areaFiltro;
    FrameLayout fundoPopupFlashcards;

    // Dados da anotação atual
    int idPagina, idCaderno;
    String nomeCaderno, remoteIdNote, remoteIdCaderno = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flashcards);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Força o modo claro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Recupera dados da anotação da Intent
        idPagina = getIntent().getIntExtra("idPagina", 0);
        idCaderno = getIntent().getIntExtra("idCaderno", 0);
        remoteIdNote = getIntent().getStringExtra("remoteIdNote");

        // Pega o remoteId do caderno, e o nome do caderno do banco de dados
        BancoControllerCaderno bd = new BancoControllerCaderno(getBaseContext());
        Cursor dados = bd.carregaDadosPeloId(idCaderno);
        if (dados != null && dados.moveToFirst()) {
            nomeCaderno = dados.getString(1);
            remoteIdCaderno = dados.getString(2);
        }
        dados.close();

        // Pega o remoteId do note do banco de dados
        BancoControllerNote bdNote = new BancoControllerNote(getBaseContext());
        Cursor dadosNote = bdNote.carregaDadosPeloId(idPagina);
        if (dadosNote != null && dadosNote.moveToFirst()) {
            remoteIdNote = dadosNote.getString(4);
        }
        dadosNote.close();

        // Inicializa os componentes da tela
        btVoltarFlashcard = findViewById(R.id.btVoltarFlashcard);
        btVoltarRespostaFlashcard = findViewById(R.id.btVoltarRespostaFlashcard);
        areaFiltro = findViewById(R.id.areaFiltro);
        txtFiltro = findViewById(R.id.txtFiltroCaderno);
        txtPerguntaResCard = findViewById(R.id.txtPerguntaResCard);
        txtRespostaCard = findViewById(R.id.txtRespostaCard);
        textoPlaceHolderFlashcards = findViewById(R.id.textoPlaceHolderFlashcards);
        fundoPopupFlashcards = findViewById(R.id.fundoPopupFlashcards);
        conteudoPrincipalFlashcards = findViewById(R.id.conteudoPrincipalFlashcards);
        listaCards = findViewById(R.id.listaFlashcards);

        // Caso seja uma visualização dentro de uma anotação
        if (idPagina != 0) {
            textoPlaceHolderFlashcards.setText("Nenhum flashcard criado nessa anotação.");
        }

        // Lista os flashcards da anotação ou gerais caso idPagina = 0
        FlashcardsController.listarCards(this, listaCards, remoteIdNote , null, fundoPopupFlashcards, conteudoPrincipalFlashcards, txtPerguntaResCard, txtRespostaCard, textoPlaceHolderFlashcards, areaFiltro);

        // Habilita esconder o popup ao tocar fora
        FlashcardsController.configurarToqueForaPopup(fundoPopupFlashcards, conteudoPrincipalFlashcards);

        // Configura a funcionalidade de filtro dos flashcards
        FlashcardsController.configurarFiltro(areaFiltro, this, idPagina, remoteIdNote, listaCards, textoPlaceHolderFlashcards, txtFiltro, fundoPopupFlashcards, conteudoPrincipalFlashcards, txtPerguntaResCard, txtRespostaCard);

        // Ação do botão de voltar
        btVoltarFlashcard.setOnClickListener(v -> {
            if (idPagina == 0) {
                // Se estiver na visualização geral, volta para a MainActivity
                startActivity(new Intent(this, MainActivity.class));
            } else {
                // Caso contrário, volta para a tela do caderno
                FlashcardsController.voltarParaTelaAnterior(this, idPagina, idCaderno, nomeCaderno);
            }
        });

        // Fecha o popup de resposta do flashcard
        btVoltarRespostaFlashcard.setOnClickListener(v -> {
            fundoPopupFlashcards.setVisibility(FrameLayout.GONE);
            conteudoPrincipalFlashcards.animate().alpha(1f).setDuration(200).start();
        });
    }
}
