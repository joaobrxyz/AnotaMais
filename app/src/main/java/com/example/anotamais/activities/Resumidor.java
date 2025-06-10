package com.example.anotamais.activities;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.anotamais.BuildConfig;
import com.example.anotamais.config.GeminiConfig;
import com.example.anotamais.R;
import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.controllers.BancoControllerNote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class Resumidor extends AppCompatActivity implements View.OnClickListener {
    AlertDialog dialog;
    Button btResumirResumidor;
    ImageButton btVoltarResumidor, salvar_resumidor;
    EditText txtTituloResumidor, txtAnotacoesResumidor;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    String dataFormatada = sdf.format(calendar.getTime());
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
        salvar_resumidor = findViewById(R.id.salvar_resumidor);
        txtTituloResumidor = findViewById(R.id.txtTituloResumidor);
        txtAnotacoesResumidor = findViewById(R.id.txtAnotacoesResumidor);

        btResumirResumidor.setOnClickListener(this);
        salvar_resumidor.setOnClickListener(this);
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
        if (v.getId() == R.id.salvar_resumidor) {
            mostrarDialogEscolhaCaderno();
        }
        if (v.getId() == R.id.btVoltarResumidor) {
            finish(); // Fecha a activity atual
        }
    }
    private void mostrarDialogEscolhaCaderno() {
        String titulo = txtTituloResumidor.getText().toString().trim();
        String conteudo = txtAnotacoesResumidor.getText().toString().trim();

        // Verifica se título ou conteúdo estão vazios
        if (titulo.isEmpty() || conteudo.isEmpty()) {
            Toast.makeText(this, "Preencha o título e o conteúdo antes de salvar.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_escolher_caderno, null);

        ListView lista = view.findViewById(R.id.listaCadernosDialog);

        // Pega os cadernos do banco
        BancoControllerCaderno controller = new BancoControllerCaderno(this);
        Cursor cursor = controller.listarCadernos(false); // ou true se quiser filtrar favoritos

        ArrayList<String> nomes = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            nomes.add(cursor.getString(cursor.getColumnIndex("name")));
            ids.add(cursor.getInt(cursor.getColumnIndex("id")));
            cursor.moveToNext();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, nomes);
        lista.setAdapter(adapter);

        // Clique em item
        lista.setOnItemClickListener((parent, view1, position, id) -> {
            int cadernoIdSelecionado = ids.get(position);
            String nomeCaderno = nomes.get(position);

            Toast.makeText(this, "Página salva no caderno: " + nomeCaderno, Toast.LENGTH_SHORT).show();

            BancoControllerNote controllerNote = new BancoControllerNote(this);
            controllerNote.insereDados(titulo, conteudo, cadernoIdSelecionado, dataFormatada);

            dialog.dismiss(); // fecha o popup
        });

        builder.setView(view);
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }


}