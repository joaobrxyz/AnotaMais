package com.example.anotamais.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.anotamais.BuildConfig;
import com.example.anotamais.config.GeminiConfig;
import com.example.anotamais.R;

public class Resumidor extends AppCompatActivity implements View.OnClickListener {
    Button btResumirResumidor;
    ImageButton btVoltarResumidor;
    EditText txtTituloResumidor, txtAnotacoesResumidor;

    GeminiConfig gemini;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resumidor);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Inicializa o GeminiConfig com a API Key
        gemini = new GeminiConfig(BuildConfig.GEMINI_API_KEY);

        btResumirResumidor = findViewById(R.id.btResumirResumidor);
        btVoltarResumidor = findViewById(R.id.btVoltarResumidor);
        txtTituloResumidor = findViewById(R.id.txtTituloResumidor);
        txtAnotacoesResumidor = findViewById(R.id.txtAnotacoesResumidor);

        btResumirResumidor.setOnClickListener(this);
        btVoltarResumidor.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btResumirResumidor) {
            String texto = txtAnotacoesResumidor.getText().toString();
            if (texto.isEmpty()) {
                Toast.makeText(this, "Digite algo primeiro", Toast.LENGTH_SHORT).show();
                return;
            }

            // Mostra que está processando
            txtAnotacoesResumidor.setText("Processando...");
            btResumirResumidor.setEnabled(false);

            String prompt = "Resuma este texto de forma clara e concisa (Quero somente o resumo, não quero que vc fale 'aqui está o resumo' por exemplo): " + texto;

            gemini.getResponse(prompt, new GeminiConfig.GeminiCallback() {
                @Override
                public void onResponse(String response) {
                    runOnUiThread(() -> {
                        btResumirResumidor.setEnabled(true);
                        // Filtra respostas inesperadas
                        if (response.startsWith("{") || response.contains("error")) {
                            txtAnotacoesResumidor.setText("Erro: resposta inválida da API");
                            Log.e("API_RESPONSE", response);
                        } else {
                            txtAnotacoesResumidor.setText(response);
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        btResumirResumidor.setEnabled(true);
                        txtAnotacoesResumidor.setText("Erro: " + error);
                        Toast.makeText(Resumidor.this, error, Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR", error);
                    });
                }
            });
            }
        if (v.getId() == R.id.btVoltarResumidor) {
            finish(); // Fecha a activity atual
        }
    }
}