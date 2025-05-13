package com.example.anotamais;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class Caderno extends AppCompatActivity {

    EditText txtTitulo, txtConteudo;
    ImageButton btVoltar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caderno);


        txtTitulo = findViewById(R.id.txtTituloDoCaderno);
        txtConteudo = findViewById(R.id.txtConteudoCaderno);
        btVoltar = findViewById(R.id.btVoltarCaderno);


        Intent intent = getIntent();
        String titulo = intent.getStringExtra("titulo");
        String conteudo = intent.getStringExtra("conteudo");


        txtTitulo.setText(titulo);
        txtConteudo.setText(conteudo);


        btVoltar.setOnClickListener(v -> {
            finish();
        });
    }
}
