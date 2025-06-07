package com.example.anotamais.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotamais.controllers.BancoControllerCard;
import com.example.anotamais.models.FlashcardModel;
import com.example.anotamais.adapters.FlashcardRecyclerAdapter;
import com.example.anotamais.R;

import java.util.LinkedList;
import java.util.List;

public class Flashcards extends AppCompatActivity {

    ImageButton btVoltarFlashcard, btVoltarRespostaFlashcard;
    TextView txtPerguntaResCard, txtRespostaCard, textoPlaceHolderFlashcards;
    RecyclerView listaCards;
    LinearLayout conteudoPrincipalFlashcards;
    FrameLayout fundoPopupFlashcards;

    @SuppressLint("ClickableViewAccessibility")
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btVoltarFlashcard = findViewById(R.id.btVoltarFlashcard);
        btVoltarRespostaFlashcard = findViewById(R.id.btVoltarRespostaFlashcard);
        txtPerguntaResCard = findViewById(R.id.txtPerguntaResCard);
        txtRespostaCard = findViewById(R.id.txtRespostaCard);
        textoPlaceHolderFlashcards = findViewById(R.id.textoPlaceHolderFlashcards);
        fundoPopupFlashcards = findViewById(R.id.fundoPopupFlashcards);
        conteudoPrincipalFlashcards = findViewById(R.id.conteudoPrincipalFlashcards);

        listarCards();

        fundoPopupFlashcards.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Rect rect = new Rect();
                conteudoPrincipalFlashcards.getGlobalVisibleRect(rect);

                if (!rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    fundoPopupFlashcards.setVisibility(View.GONE);
                    conteudoPrincipalFlashcards.animate().alpha(1f).setDuration(200).start();
                    return true;
                }
            }
            return false;
        });

        btVoltarFlashcard.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        btVoltarRespostaFlashcard.setOnClickListener(v -> {
            fundoPopupFlashcards.setVisibility(View.GONE);
            conteudoPrincipalFlashcards.animate().alpha(1f).setDuration(200).start();
        });
    }
    private void listarCards(){
        List<FlashcardModel> cards = null;
        cards = consultaTodosCards();
        listaCards = findViewById(R.id.listaFlashcards);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        listaCards.setLayoutManager(layoutManager);
        FlashcardRecyclerAdapter adapter = new FlashcardRecyclerAdapter(this, cards, fundoPopupFlashcards, conteudoPrincipalFlashcards, txtPerguntaResCard, txtRespostaCard);
        listaCards.setAdapter(adapter);
    }

    private List<FlashcardModel> consultaTodosCards() {
        List<FlashcardModel> cards = new LinkedList<FlashcardModel>();

        BancoControllerCard bd = new BancoControllerCard(getBaseContext());
        Cursor dados = bd.listarCards();

        if (dados != null && dados.moveToFirst()) {
            do {
                TextView textoPlaceHolderFlashcards = findViewById(R.id.textoPlaceHolderFlashcards);
                textoPlaceHolderFlashcards.setVisibility(View.GONE);
                FlashcardModel card = new FlashcardModel();
                card.setId(dados.getInt(0));
                card.setPergunta(dados.getString(1));
                card.setResposta(dados.getString(2));
                card.setPaginaId(dados.getInt(3));
                cards.add(card);
            } while (dados.moveToNext());
        }
        dados.close();

        return cards;
    }
}