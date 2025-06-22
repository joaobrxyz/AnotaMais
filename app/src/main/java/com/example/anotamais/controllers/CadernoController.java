package com.example.anotamais.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.anotamais.R;
import com.example.anotamais.adapters.AnotacaoRecyclerAdapter;
import com.example.anotamais.models.NotaModel;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class CadernoController {

    // Data atual formatada no padrão brasileiro (dd/MM/yyyy)
    static Calendar calendar = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    static String dataFormatada = sdf.format(calendar.getTime());

    // Retorna todas as anotações de um caderno específico
    public static List<NotaModel> consultaTodasAnotacoes(Context context, int idCaderno, String nomeCaderno) {
        List<NotaModel> anotacoes = new LinkedList<>();

        // Consulta ao banco de dados
        BancoControllerNote bd = new BancoControllerNote(context);
        Cursor dados = bd.listarNotes(idCaderno);

        // Se houver dados, preenche a lista de anotações
        if (dados != null && dados.moveToFirst()) {
            // Esconde o texto placeholder da tela
            TextView textoPlaceholderCaderno = ((Activity)context).findViewById(R.id.textoPlaceHolderCaderno);
            textoPlaceholderCaderno.setVisibility(android.view.View.GONE);

            do {
                NotaModel nota = new NotaModel();
                nota.setId(dados.getInt(0));
                nota.setTitulo(dados.getString(1));
                nota.setConteudo(dados.getString(2));
                nota.setIdCaderno(dados.getInt(3));
                nota.setData(dados.getString(4));
                nota.setNomeCaderno(nomeCaderno);
                anotacoes.add(nota);
            } while (dados.moveToNext());
        }
        if (dados != null) dados.close();

        return anotacoes;
    }

    // Lista todas as notas no RecyclerView de forma horizontal e adaptada à altura da tela
    public static void listarNotes(Context context, RecyclerView recyclerView, int idCaderno, String nomeCaderno) {
        List<NotaModel> notas = consultaTodasAnotacoes(context, idCaderno, nomeCaderno);

        // Calcula o número de linhas baseado na altura da tela
        int linhas = calcularLinhasPorAlturaTela(context);

        // Configura o RecyclerView com GridLayoutManager horizontal
        GridLayoutManager layoutManager = new GridLayoutManager(context, linhas, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Define o adaptador
        AnotacaoRecyclerAdapter adapter = new AnotacaoRecyclerAdapter(context, notas);
        recyclerView.setAdapter(adapter);
    }

    // Calcula a quantidade de linhas com base na altura da tela (em dp)
    public static int calcularLinhasPorAlturaTela(Context context) {
        float screenHeightDp = context.getResources().getDisplayMetrics().heightPixels /
                context.getResources().getDisplayMetrics().density;

        // Define a quantidade de linhas de acordo com faixas de altura
        if (screenHeightDp >= 1540) return 9;
        else if (screenHeightDp >= 1400) return 8;
        else if (screenHeightDp >= 1260) return 7;
        else if (screenHeightDp >= 1120) return 6;
        else if (screenHeightDp >= 980) return 5;
        else if (screenHeightDp >= 840) return 4;
        else return 3;
    }

    // Configura a ação do botão "Voltar" na tela do caderno
    public static void voltarCaderno(Activity activity, ImageButton botaoVoltar) {
        botaoVoltar.setOnClickListener(v -> {
            // Volta para a MainActivity
            Intent intent = new Intent(activity, com.example.anotamais.activities.MainActivity.class);
            activity.startActivity(intent);
        });
    }

    // Configura a ação do botão "Criar Anotação" dentro do caderno
    public static void CriarAnotacao(Activity activity, Button botaoCriarAnotacao, int idCaderno, String nomeCaderno) {
        botaoCriarAnotacao.setOnClickListener(v -> {

            // Insere nova nota no banco de dados
            BancoControllerNote bd = new BancoControllerNote(activity.getBaseContext());
            long id = bd.insereDados("Título da Página", "Conteúdo da Página", idCaderno, dataFormatada);

            // Verifica se a inserção foi bem-sucedida
            if (id == -1) {
                Toast.makeText(activity, "Erro ao criar nova página", Toast.LENGTH_LONG).show();
            } else {
                // Atualiza a lista de notas
                listarNotes(activity, activity.findViewById(R.id.listaNotes), idCaderno,
                        ((TextView) activity.findViewById(R.id.nomeCadernoCaderno)).getText().toString().replace("Caderno: ", ""));

                // Abre a nova página criada
                Intent intent = new Intent(activity, com.example.anotamais.activities.Notes.class);
                intent.putExtra("idPagina", (int) id);
                intent.putExtra("idCaderno", idCaderno);
                intent.putExtra("nomeCaderno", nomeCaderno);
                activity.startActivity(intent);
            }
        });
    }
}
