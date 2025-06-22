package com.example.anotamais.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.anotamais.BuildConfig;
import com.example.anotamais.R;
import com.example.anotamais.config.GeminiConfig;
import com.example.anotamais.controllers.ResumidorController;

public class Resumidor extends AppCompatActivity {
    private Button btResumirResumidor;
    private ImageButton btVoltarResumidor, salvar_resumidor;
    private EditText txtTituloResumidor, txtAnotacoesResumidor;
    private GeminiConfig gemini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumidor);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Inicializa Gemini
        gemini = new GeminiConfig(BuildConfig.GEMINI_API_KEY);

        // Referência para os elementos
        btResumirResumidor = findViewById(R.id.btResumirResumidor);
        btVoltarResumidor = findViewById(R.id.btVoltarResumidor);
        salvar_resumidor = findViewById(R.id.salvar_resumidor);
        txtTituloResumidor = findViewById(R.id.txtTituloResumidor);
        txtAnotacoesResumidor = findViewById(R.id.txtAnotacoesResumidor);

        // Ação do botão de voltar
        btVoltarResumidor.setOnClickListener(v -> ResumidorController.voltarParaMain(this));

        // Ação do botão de resumir
        btResumirResumidor.setOnClickListener(v ->
                ResumidorController.resumirTexto(this, gemini, txtAnotacoesResumidor, btResumirResumidor)
        );

        // Ação do botão de salvar
        salvar_resumidor.setOnClickListener(v ->
                ResumidorController.salvarTextoEmCaderno(this, txtTituloResumidor, txtAnotacoesResumidor)
        );
    }
}
