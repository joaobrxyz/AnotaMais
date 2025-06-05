package com.example.anotamais.activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotamais.adapters.AnotacaoRecyclerAdapter;
import com.example.anotamais.controllers.BancoControllerNote;
import com.example.anotamais.models.NotaModel;
import com.example.anotamais.R;

import java.util.LinkedList;
import java.util.List;

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

        nomeCaderno = getIntent().getStringExtra("nomeCaderno");
        idCaderno = getIntent().getIntExtra("idCaderno", 0);

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
            BancoControllerNote bd = new BancoControllerNote(getBaseContext());
            long id = -1;
            id = bd.insereDados("Título da Página", "Conteúdo da Página", idCaderno);
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


    private void listarNotes(){
        List<NotaModel> notas = null;
        notas = consultaTodasAnotacoes();
        listaAnotacao = findViewById(R.id.listaNotes);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3, GridLayoutManager.HORIZONTAL, false);
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
                nota.setNomeCaderno(nomeCaderno);
                anotacoes.add(nota);
            } while (dados.moveToNext());
        } else {
            String msg = "Não há anotações cadastradas";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        dados.close();

        return anotacoes;
    }

}