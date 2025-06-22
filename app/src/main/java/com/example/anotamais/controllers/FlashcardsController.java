package com.example.anotamais.controllers;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.MatrixCursor;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anotamais.R;
import com.example.anotamais.activities.Notes;
import com.example.anotamais.adapters.CadernoCursorAdapter;
import com.example.anotamais.adapters.FlashcardRecyclerAdapter;
import com.example.anotamais.models.FlashcardModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import java.util.LinkedList;
import java.util.List;

public class FlashcardsController {

    // Lista todos os flashcards na RecyclerView com base no filtro (página ou caderno)
    public static void listarCards(Context context,
                                   RecyclerView listaCards,
                                   Integer idPagina,
                                   Integer idCaderno,
                                   FrameLayout fundoPopup,
                                   LinearLayout conteudoPopup,
                                   TextView txtPergunta,
                                   TextView txtResposta,
                                   TextView textoPlaceholder,
                                   LinearLayout areaFiltro) {

        List<FlashcardModel> cards = consultaTodosCards(context, idPagina, idCaderno, textoPlaceholder, areaFiltro);

        // Define o layout da RecyclerView com número de colunas adaptável à tela
        int colunas = calcularColunas(context);
        GridLayoutManager layoutManager = new GridLayoutManager(context, colunas, GridLayoutManager.VERTICAL, false);

        listaCards.setLayoutManager(layoutManager);

        // Adapta os dados usando o adapter personalizado
        FlashcardRecyclerAdapter adapter = new FlashcardRecyclerAdapter(
                context, cards, fundoPopup, conteudoPopup, txtPergunta, txtResposta);
        listaCards.setAdapter(adapter);
    }

    // Consulta todos os flashcards do banco com base no caderno e/ou página
    private static List<FlashcardModel> consultaTodosCards(Context context,
                                                           Integer idPagina,
                                                           Integer idCaderno,
                                                           TextView textoPlaceholder,
                                                           LinearLayout areaFiltro) {
        List<FlashcardModel> cards = new LinkedList<>();

        BancoControllerCard bd = new BancoControllerCard(context);
        Cursor dados = bd.listarCards(idCaderno, idPagina);

        if (dados != null && dados.moveToFirst()) {
            // Mostra área de filtro se estiver em visualização geral
            if (idPagina == 0) {
                areaFiltro.setVisibility(View.VISIBLE);
            }

            // Oculta texto de "nenhum flashcard"
            textoPlaceholder.setVisibility(View.GONE);

            do {
                FlashcardModel card = new FlashcardModel();
                card.setId(dados.getInt(0));
                card.setPergunta(dados.getString(1));
                card.setResposta(dados.getString(2));
                card.setPaginaId(dados.getInt(3));
                cards.add(card);
            } while (dados.moveToNext());
        }

        if (dados != null) dados.close();

        return cards;
    }

    // Calcula a quantidade de colunas para o layout com base na largura da tela
    private static int calcularColunas(Context context) {
        float screenWidthDp = context.getResources().getDisplayMetrics().widthPixels /
                context.getResources().getDisplayMetrics().density;

        if (screenWidthDp >= 1000) return 5;
        else if (screenWidthDp >= 600) return 4;
        else return 2;
    }

    // Configura o toque fora do popup de flashcard para fechá-lo
    @SuppressLint("ClickableViewAccessibility")
    public static void configurarToqueForaPopup(FrameLayout fundoPopup, LinearLayout conteudoPrincipal) {
        fundoPopup.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Rect rect = new Rect();
                conteudoPrincipal.getGlobalVisibleRect(rect);
                if (!rect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    fundoPopup.setVisibility(View.GONE);
                    conteudoPrincipal.animate().alpha(1f).setDuration(200).start();
                    v.performClick(); // Importante para acessibilidade
                    return true;
                }
            }
            return false;
        });
    }

    // Configura o clique no botão de filtro de flashcards
    public static void configurarFiltro(View filtroView,
                                        Activity activity,
                                        int idPagina,
                                        RecyclerView listaCards,
                                        TextView textoPlaceholder,
                                        TextView txtFiltro,
                                        FrameLayout fundoPopup,
                                        LinearLayout conteudoPopup,
                                        TextView txtPergunta,
                                        TextView txtResposta) {

        filtroView.setOnClickListener(v -> abrirBottomSheetFiltro(
                activity, idPagina, listaCards,
                textoPlaceholder, txtFiltro, (LinearLayout) filtroView,
                fundoPopup, conteudoPopup, txtPergunta, txtResposta));
    }

    // Exibe o bottom sheet com lista de cadernos para filtrar os flashcards
    public static void abrirBottomSheetFiltro(Activity activity,
                                              int idPagina,
                                              RecyclerView listaCards,
                                              TextView textoPlaceholder,
                                              TextView txtFiltro,
                                              LinearLayout areaFiltro,
                                              FrameLayout fundoPopup,
                                              LinearLayout conteudoPopup,
                                              TextView txtPergunta,
                                              TextView txtResposta) {

        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        View view = LayoutInflater.from(activity).inflate(R.layout.bottomsheet_cadernos, null);
        ListView listView = view.findViewById(R.id.listaOpcoesCadernos);

        // Adiciona "Todos os cadernos" como opção extra no início
        BancoControllerCaderno controller = new BancoControllerCaderno(activity);
        Cursor cursorOriginal = controller.listarCadernosComFlashcards();
        MatrixCursor cursorExtra = new MatrixCursor(new String[]{"_id", "name"});
        cursorExtra.addRow(new Object[]{-1, "Todos os cadernos"});

        Cursor extendedCursor = new MergeCursor(new Cursor[]{cursorExtra, cursorOriginal});

        // Adapter customizado para exibir o nome dos cadernos no bottom sheet
        CadernoCursorAdapter adapter = new CadernoCursorAdapter(activity, extendedCursor);
        listView.setAdapter(adapter);

        // Ao selecionar uma opção, atualiza o filtro e recarrega os flashcards
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            Cursor cursor = (Cursor) parent.getItemAtPosition(position);
            int idSelecionado = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
            String nomeSelecionado = cursor.getString(cursor.getColumnIndexOrThrow("name"));

            txtFiltro.setText(nomeSelecionado);
            bottomSheetDialog.dismiss();

            if (idSelecionado == -1) {
                // Exibe todos os flashcards
                listarCards(activity, listaCards, idPagina, null,
                        fundoPopup, conteudoPopup, txtPergunta, txtResposta, textoPlaceholder, areaFiltro);
            } else {
                // Exibe apenas flashcards do caderno selecionado
                listarCards(activity, listaCards, idPagina, idSelecionado,
                        fundoPopup, conteudoPopup, txtPergunta, txtResposta, textoPlaceholder, areaFiltro);
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();
    }

    // Retorna para a tela de notas após sair do modo flashcard
    public static void voltarParaTelaAnterior(Context context, int idPagina, int idCaderno, String nomeCaderno) {
        Intent intent = new Intent(context, Notes.class);
        intent.putExtra("idPagina", idPagina);
        intent.putExtra("idCaderno", idCaderno);
        intent.putExtra("nomeCaderno", nomeCaderno);
        context.startActivity(intent);
    }
}
