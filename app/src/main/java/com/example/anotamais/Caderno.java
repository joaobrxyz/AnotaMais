package com.example.anotamais;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class Caderno extends AppCompatActivity {
    TextView titulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caderno);

        titulo = findViewById(R.id.txtTituloDetalhes);

        String nomeCaderno = getIntent().getStringExtra("nomeCaderno");

        if (nomeCaderno != null) {
            titulo.setText("Caderno: " + nomeCaderno);
            // Aqui você pode puxar dados com base nesse nome
        }

    }
}
