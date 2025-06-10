package com.example.anotamais.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.database.MatrixCursor;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotamais.adapters.CadernoCursorAdapter;
import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.controllers.BancoControllerCard;
import com.example.anotamais.models.FlashcardModel;
import com.example.anotamais.adapters.FlashcardRecyclerAdapter;
import com.example.anotamais.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.LinkedList;
import java.util.List;

public class Flashcards extends AppCompatActivity {

    ImageButton btVoltarFlashcard, btVoltarRespostaFlashcard;
    TextView txtPerguntaResCard, txtRespostaCard, textoPlaceHolderFlashcards, txtFiltro;
    RecyclerView listaCards;
    LinearLayout conteudoPrincipalFlashcards, areaFiltro;
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
        areaFiltro = findViewById(R.id.areaFiltro);
        txtFiltro = findViewById(R.id.txtFiltroCaderno);
        txtPerguntaResCard = findViewById(R.id.txtPerguntaResCard);
        txtRespostaCard = findViewById(R.id.txtRespostaCard);
        textoPlaceHolderFlashcards = findViewById(R.id.textoPlaceHolderFlashcards);
        fundoPopupFlashcards = findViewById(R.id.fundoPopupFlashcards);
        conteudoPrincipalFlashcards = findViewById(R.id.conteudoPrincipalFlashcards);

        areaFiltro.setOnClickListener(v -> {
            abrirBottomSheetFiltro(); // ou o que você quiser que aconteça
        });

        listarCards(null);


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
    private void listarCards(Integer idCaderno){
        List<FlashcardModel> cards = consultaTodosCards(idCaderno);
        listaCards = findViewById(R.id.listaFlashcards);

        int colunas = calcularColunas();
        GridLayoutManager layoutManager = new GridLayoutManager(this, colunas, GridLayoutManager.VERTICAL, false);

        listaCards.setLayoutManager(layoutManager);
        FlashcardRecyclerAdapter adapter = new FlashcardRecyclerAdapter(this, cards, fundoPopupFlashcards, conteudoPrincipalFlashcards, txtPerguntaResCard, txtRespostaCard);
        listaCards.setAdapter(adapter);
    }

    private List<FlashcardModel> consultaTodosCards(Integer idCaderno) {
        List<FlashcardModel> cards = new LinkedList<FlashcardModel>();

        BancoControllerCard bd = new BancoControllerCard(getBaseContext());
        Cursor dados = bd.listarCards(idCaderno);

        if (dados != null && dados.moveToFirst()) {
            do {
                TextView textoPlaceHolderFlashcards = findViewById(R.id.textoPlaceHolderFlashcards);
                LinearLayout areaFiltro = findViewById(R.id.areaFiltro);
                areaFiltro.setVisibility(View.VISIBLE);
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

    private int calcularColunas() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float screenWidthDp = metrics.widthPixels / metrics.density;

        if (screenWidthDp >= 1000) {
            return 5;
        } else if (screenWidthDp >= 600) {
            return 4;
        } else {
            return 2;  // celular
        }
    }
    private void abrirBottomSheetFiltro() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottomsheet_cadernos, null);

        ListView listView = view.findViewById(R.id.listaOpcoesCadernos);

        BancoControllerCaderno controller = new BancoControllerCaderno(this);
        Cursor cursorOriginal = controller.listarCadernosComFlashcards();

        // Cria um cursor com a linha "Todos os cadernos"
        MatrixCursor cursorExtra = new MatrixCursor(new String[]{"_id", "name"});
        cursorExtra.addRow(new Object[]{-1, "Todos os cadernos"});

        // Junta o cursor extra com o original
        Cursor[] cursors = {cursorExtra, cursorOriginal};
        Cursor extendedCursor = new MergeCursor(cursors);

        CadernoCursorAdapter adapter = new CadernoCursorAdapter(this, extendedCursor);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int idSelecionado = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            String nomeSelecionado = cursor.getString(cursor.getColumnIndexOrThrow("name"));

            // Atualiza o texto do filtro com o nome do caderno
            TextView txtFiltroCaderno = findViewById(R.id.txtFiltroCaderno);
            txtFiltroCaderno.setText(nomeSelecionado);

            bottomSheetDialog.dismiss();

            if (idSelecionado == -1) {
                // Selecionou todos os cadernos, carregar todos
                listarCards(null);
            } else {
                // Filtra pelo id do caderno selecionado
                listarCards(idSelecionado);
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }
}