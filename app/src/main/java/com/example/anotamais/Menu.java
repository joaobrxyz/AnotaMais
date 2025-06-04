package com.example.anotamais;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class Menu extends AppCompatActivity {

    private static final int REQUEST_CODE_NOTES = 1;
    private LinearLayout containerNotas;
    private Button btCriarAnotacao;
    private TextView placeholderTextView;

    private ArrayList<Nota> listaNotas = new ArrayList<>();
    private int indiceNotaEditando = -1;
    private int idCadernoRecebido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        idCadernoRecebido = getIntent().getIntExtra("idCaderno", -1);

        containerNotas = findViewById(R.id.containerNotas);
        btCriarAnotacao = findViewById(R.id.btCriarAnotacao);
        placeholderTextView = findViewById(R.id.textoPlaceholder);

        btCriarAnotacao.setOnClickListener(v -> {
            indiceNotaEditando = -1;
            abrirTelaNotas(-1, null, null, idCadernoRecebido);
        });

        listarNotes();
    }

    private void abrirTelaNotas(Integer id, String titulo, String conteudo, int idCaderno) {
        Intent intent = new Intent(Menu.this, Notes.class);
        intent.putExtra("idCaderno", idCaderno);
        intent.putExtra("idNota", id);
        if (titulo != null && conteudo != null) {
            intent.putExtra("titulo", titulo);
            intent.putExtra("conteudo", conteudo);
        }
        startActivityForResult(intent, REQUEST_CODE_NOTES);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_NOTES && resultCode == RESULT_OK && data != null) {
            listarNotes();
        }
    }

    private void atualizarListaNotas() {
        containerNotas.removeAllViews();

        if (listaNotas.isEmpty()) {
            placeholderTextView.setVisibility(View.VISIBLE);
            containerNotas.addView(placeholderTextView);
            return;
        }

        placeholderTextView.setVisibility(View.GONE);

        for (int i = 0; i < listaNotas.size(); i++) {
            Nota nota = listaNotas.get(i);

            TextView tvTitulo = new TextView(this);
            tvTitulo.setText(nota.titulo);
            tvTitulo.setTextSize(18);
            tvTitulo.setTypeface(null, Typeface.BOLD);
            tvTitulo.setPadding(16, 16, 16, 16);
            tvTitulo.setTextColor(getResources().getColor(android.R.color.black));

            int index = i;
            tvTitulo.setOnClickListener(v -> {
                indiceNotaEditando = index;
                abrirTelaNotas(nota.id, nota.titulo, nota.conteudo, idCadernoRecebido);
            });

            containerNotas.addView(tvTitulo);
        }
    }

    private void listarNotes(){
        listaNotas.clear();
        listaNotas.addAll(consultaTodasAnotacoes());
        atualizarListaNotas();
    }

    private List<Nota> consultaTodasAnotacoes() {
        List<Nota> anotacoes = new ArrayList<>();

        BancoControllerNote bd = new BancoControllerNote(getBaseContext());
        Cursor dados = bd.listarNotes(idCadernoRecebido);

        if (dados != null && dados.moveToFirst()) {
            do {
                Nota anotacao = new Nota(dados.getInt(0), dados.getString(1), dados.getString(2), idCadernoRecebido);
                anotacoes.add(anotacao);
            } while (dados.moveToNext());
        } else {
            String msg = "Não há anotações cadastrados";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        dados.close();

        return anotacoes;
    }

}
