package com.example.anotamais;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Caderno extends AppCompatActivity {
    TextView titulo, conteudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caderno);

        titulo = findViewById(R.id.txtTituloDetalhes);
        conteudo = findViewById(R.id.txtConteudoDetalhes);

        String nomeCaderno = getIntent().getStringExtra("nomeCaderno");
        String conteudoCaderno = getIntent().getStringExtra("conteudoCaderno");

        if (nomeCaderno != null) {
            titulo.setText("Caderno: " + nomeCaderno);
        }

        if (conteudoCaderno != null) {
            conteudo.setText(conteudoCaderno);
        }
    }
}
