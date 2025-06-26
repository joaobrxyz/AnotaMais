package com.example.anotamais.controllers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.anotamais.R;
import com.example.anotamais.activities.MainActivity;
import com.example.anotamais.config.GeminiConfig;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class ResumidorController {

    public static void resumirTexto(Activity activity, GeminiConfig gemini, EditText txtAnotacoes, Button btResumir) {
        String texto = txtAnotacoes.getText().toString();

        if (texto.isEmpty()) {
            Toast.makeText(activity, "Digite algo primeiro", Toast.LENGTH_SHORT).show();
            return;
        }

        if (texto.length() < 150) {
            Toast.makeText(activity, "O conteúdo do texto é muito curto. Insira pelo menos 150 caracteres.", Toast.LENGTH_LONG).show();
            return;
        }

        txtAnotacoes.setText("Processando...");
        btResumir.setEnabled(false);

        String prompt = "Resuma este texto de forma clara e concisa (Quero somente o resumo, não quero que vc fale 'aqui está o resumo' por exemplo): " + texto;

        gemini.getResponse(prompt, new GeminiConfig.GeminiCallback() {
            @Override
            public void onResponse(String response) {
                activity.runOnUiThread(() -> {
                    btResumir.setEnabled(true);
                    if (response.startsWith("{") || response.contains("error")) {
                        txtAnotacoes.setText("Erro: resposta inválida da API");
                        Log.e("API_RESPONSE", response);
                    } else {
                        txtAnotacoes.setText(response);
                    }
                });
            }

            @Override
            public void onError(String error) {
                activity.runOnUiThread(() -> {
                    btResumir.setEnabled(true);
                    txtAnotacoes.setText("Erro: " + error);
                    Toast.makeText(activity, error, Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", error);
                });
            }
        });
    }

    public static void salvarTextoEmCaderno(Activity activity, EditText txtTitulo, EditText txtConteudo) {
        String titulo = txtTitulo.getText().toString().trim();
        String conteudo = txtConteudo.getText().toString().trim();

        if (titulo.isEmpty() || conteudo.isEmpty()) {
            Toast.makeText(activity, "Preencha o título e o conteúdo antes de salvar.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_escolher_caderno, null);

        ListView lista = view.findViewById(R.id.listaCadernosDialog);

        BancoControllerCaderno controller = new BancoControllerCaderno(activity);
        Cursor cursor = controller.listarCadernos(false);

        ArrayList<String> nomes = new ArrayList<>();
        ArrayList<Integer> ids = new ArrayList<>();
        while (!cursor.isAfterLast()) {
            nomes.add(cursor.getString(1));
            ids.add(cursor.getInt(0));
            cursor.moveToNext();
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, nomes);
        lista.setAdapter(adapter);

        AlertDialog dialog = builder.setView(view).create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        lista.setOnItemClickListener((parent, view1, position, id) -> {
            int cadernoIdSelecionado = ids.get(position);
            String nomeCaderno = nomes.get(position);

            BancoControllerCaderno controllerCaderno = new BancoControllerCaderno(activity);
            Cursor cursorCaderno = controllerCaderno.carregaDadosPeloId(cadernoIdSelecionado);
            cursorCaderno.moveToFirst();
            String remoteIdCaderno = cursorCaderno.getString(2);
            cursorCaderno.close();

            Toast.makeText(activity, "Página salva no caderno: " + nomeCaderno, Toast.LENGTH_SHORT).show();

            BancoControllerNote controllerNote = new BancoControllerNote(activity);
            String dataFormatada = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Calendar.getInstance().getTime());
            controllerNote.insereDados(titulo, conteudo, remoteIdCaderno, dataFormatada);

            dialog.dismiss();
        });

        dialog.show();
    }

    public static void voltarParaMain(Activity activity) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        activity.startActivity(intent);
        activity.finish();
    }
}
