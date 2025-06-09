package com.example.anotamais.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotamais.adapters.AnotacaoRecyclerAdapter;
import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.controllers.BancoControllerNote;
import com.example.anotamais.models.NotaModel;
import com.example.anotamais.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Caderno extends AppCompatActivity {

    private Button btCriarAnotacao;
    TextView textoPlaceHolderCaderno, nomeCadernoCaderno;
    RecyclerView listaAnotacao;
    ImageButton btVoltarCaderno;
    String nomeCaderno;
    int idCaderno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caderno);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        idCaderno = getIntent().getIntExtra("idCaderno", 0);
        BancoControllerCaderno bdCad = new BancoControllerCaderno(getBaseContext());
        Cursor dados = bdCad.carregaDadosPeloId(idCaderno);
        if (dados != null && dados.moveToFirst()) {
            nomeCaderno = dados.getString(1);
        }
        dados.close();

        btCriarAnotacao = findViewById(R.id.btCriarAnotacao);
        btVoltarCaderno = findViewById(R.id.btVoltarCaderno);
        textoPlaceHolderCaderno = findViewById(R.id.textoPlaceHolderCaderno);
        nomeCadernoCaderno = findViewById(R.id.nomeCadernoCaderno);
        nomeCadernoCaderno.setText("Caderno: " + nomeCaderno);

        listarNotes();

        btVoltarCaderno.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, MainActivity.class);
            context.startActivity(intent);
        });

        btCriarAnotacao.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            String dataFormatada = sdf.format(calendar.getTime());
            BancoControllerNote bd = new BancoControllerNote(getBaseContext());
            long id = -1;
            id = bd.insereDados("Título da Página", "Conteúdo da Página", idCaderno, dataFormatada);
            if (id == -1) {
                Toast.makeText(this, "Erro ao criar nova página", Toast.LENGTH_LONG).show();
            } else {
                listarNotes();
                Context context = v.getContext();
                Intent intent = new Intent(context, Notes.class);
                int idPagina = (int) id;
                intent.putExtra("idPagina", idPagina);
                intent.putExtra("idCaderno", idCaderno);
                context.startActivity(intent);
            }
        });

    }


    private void listarNotes() {
        List<NotaModel> notas = consultaTodasAnotacoes();
        listaAnotacao = findViewById(R.id.listaNotes);

        int linhas = calcularLinhasPorAlturaTela();

        GridLayoutManager layoutManager = new GridLayoutManager(this, linhas, GridLayoutManager.HORIZONTAL, false);
        listaAnotacao.setLayoutManager(layoutManager);

        AnotacaoRecyclerAdapter adapter = new AnotacaoRecyclerAdapter(this, notas);
        listaAnotacao.setAdapter(adapter);
    }

    private List<NotaModel> consultaTodasAnotacoes() {
        List<NotaModel> anotacoes = new LinkedList<>();

        BancoControllerNote bd = new BancoControllerNote(getBaseContext());
        Cursor dados = bd.listarNotes(idCaderno);

        if (dados != null && dados.moveToFirst()) {
            do {
                TextView textoPlaceholderCaderno = findViewById(R.id.textoPlaceHolderCaderno);
                textoPlaceholderCaderno.setVisibility(View.GONE);
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
        dados.close();

        return anotacoes;
    }

    private int calcularLinhasPorAlturaTela() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        float screenHeightDp = metrics.heightPixels / metrics.density;

        if (screenHeightDp >= 1100) {
            return 6; // tablets grandes (muito grande)
        } else if (screenHeightDp >= 900) {
            return 5; // tablets médios
        } else if (screenHeightDp >= 600) {
            return 3; // celulares grandes tipo S23 Ultra e Pixel 5
        } else {
            return 2; // celulares pequenos
        }
    }




}