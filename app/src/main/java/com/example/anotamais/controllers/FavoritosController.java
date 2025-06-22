package com.example.anotamais.controllers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anotamais.activities.Configuracoes;
import com.example.anotamais.activities.MainActivity;
import com.example.anotamais.adapters.CadernoRecyclerAdapter;
import com.example.anotamais.models.CadernoModel;
import java.util.LinkedList;
import java.util.List;

public class FavoritosController {

    // Configura os botões de navegação da tela de favoritos (Home e Configurações)
    public static void configurarBotoesNavegacao(Activity activity, ImageButton btHome, ImageButton btConfig) {
        btHome.setOnClickListener(v -> {
            // Navega para a MainActivity
            activity.startActivity(new Intent(activity, MainActivity.class));
        });

        btConfig.setOnClickListener(v -> {
            // Navega para a tela de Configurações
            activity.startActivity(new Intent(activity, Configuracoes.class));
        });
    }

    // Lista todos os cadernos marcados como favoritos na RecyclerView
    public static void listarCadernosFavoritos(Activity activity, RecyclerView recyclerView, TextView placeholder) {
        // Consulta os cadernos favoritos no banco de dados
        List<CadernoModel> cadernos = consultaCadernosFavoritos(activity, placeholder);

        // Define o layout da lista em duas colunas horizontais
        GridLayoutManager layoutManager = new GridLayoutManager(activity, 2, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        // Configura o adapter da RecyclerView com os cadernos favoritos
        CadernoRecyclerAdapter adapter = new CadernoRecyclerAdapter(activity, cadernos);

        // Listener para atualizar a lista quando um caderno for desmarcado como favorito
        adapter.setOnCadernoFavoritoChangeListener(() ->
                listarCadernosFavoritos(activity, recyclerView, placeholder)
        );

        recyclerView.setAdapter(adapter);
    }

    // Realiza a consulta dos cadernos favoritos no banco de dados
    private static List<CadernoModel> consultaCadernosFavoritos(Activity activity, TextView placeholder) {
        List<CadernoModel> cadernos = new LinkedList<>();

        // Acessa o banco de dados de cadernos
        BancoControllerCaderno bd = new BancoControllerCaderno(activity.getBaseContext());
        Cursor dados = bd.listarCadernos(true); // true indica que deve buscar apenas os favoritos

        // Se houver dados, percorre cada registro e monta os objetos CadernoModel
        if (dados != null && dados.moveToFirst()) {
            do {
                // Esconde o texto de "Nenhum favorito encontrado"
                placeholder.setVisibility(View.GONE);

                CadernoModel caderno = new CadernoModel();
                caderno.setId(dados.getInt(0));                // ID do caderno
                caderno.setNome(dados.getString(1));           // Nome do caderno
                caderno.setFavorito(dados.getInt(2) == 1);     // Define se está favoritado

                cadernos.add(caderno);
            } while (dados.moveToNext());
        }

        // Fecha o cursor após o uso
        if (dados != null) dados.close();

        return cadernos;
    }
}
