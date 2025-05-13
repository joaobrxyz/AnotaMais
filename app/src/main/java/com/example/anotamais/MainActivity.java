package com.example.anotamais;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    ImageButton btImageCaderno;
    Button btCriarMateria;

    String tituloRecebido, conteudoRecebido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btImageCaderno = findViewById(R.id.btImageCaderno);
        btCriarMateria = findViewById(R.id.btCriarMateria);


        btImageCaderno.setVisibility(View.GONE);


        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("mostrarCaderno", false)) {
            tituloRecebido = intent.getStringExtra("titulo");
            conteudoRecebido = intent.getStringExtra("conteudo");

            btImageCaderno.setVisibility(View.VISIBLE);


            btImageCaderno.setOnClickListener(v -> {
                Intent intentCaderno = new Intent(MainActivity.this, Caderno.class);
                intentCaderno.putExtra("titulo", tituloRecebido);
                intentCaderno.putExtra("conteudo", conteudoRecebido);
                startActivity(intentCaderno);
            });
        }


        btCriarMateria.setOnClickListener(v -> {
            Intent intentCriar = new Intent(MainActivity.this, Notes.class);
            startActivity(intentCriar);
        });
    }
}
