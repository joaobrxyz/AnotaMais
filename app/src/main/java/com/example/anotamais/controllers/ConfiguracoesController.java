package com.example.anotamais.controllers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.example.anotamais.activities.Favoritos;
import com.example.anotamais.activities.MainActivity;

public class ConfiguracoesController {

    // Recupera o nome do usuário no banco e exibe no TextView da tela de configurações
    public static void carregarNome(Activity activity, TextView txtNomeUsuario) {
        BancoControllerUsuario bd = new BancoControllerUsuario(activity.getBaseContext());
        Cursor cursor = bd.carregaDadosPeloId();
        if (cursor.moveToFirst()) {
            String nome = cursor.getString(1); // Nome está na coluna 1
            txtNomeUsuario.setText("Olá, " + nome + "! 🙂");
        }
        cursor.close();
    }

    // Altera o nome do usuário no banco e atualiza o texto da interface
    public static void alterarNome(Activity activity, EditText newNameUser, TextView txtNomeUsuario) {
        String novoNome = newNameUser.getText().toString();

        // Verifica se o campo não está vazio
        if (!novoNome.isEmpty()) {
            BancoControllerUsuario bd = new BancoControllerUsuario(activity.getBaseContext());
            bd.alteraDados(novoNome); // Salva novo nome no banco

            newNameUser.setText(""); // Limpa o campo de texto
            carregarNome(activity, txtNomeUsuario); // Atualiza saudação com o novo nome

            Toast.makeText(activity, "Nome alterado com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(activity, "Por favor, insira um novo nome.", Toast.LENGTH_SHORT).show();
        }
    }

    // Configura os botões inferiores de navegação para redirecionar para a Home ou Favoritos
    public static void configurarBotoesNavegacao(Activity activity, ImageButton btHome, ImageButton btFavoritos) {
        btHome.setOnClickListener(v -> {
            activity.startActivity(new Intent(activity, MainActivity.class));
        });

        btFavoritos.setOnClickListener(v -> {
            activity.startActivity(new Intent(activity, Favoritos.class));
        });
    }

    // Configura o botão de "Alterar Nome" para acionar a lógica de alteração no banco
    public static void configurarBotaoAlterarNome(Activity activity, Button botaoAlterar, EditText novoNome, TextView txtNomeUsuario) {
        botaoAlterar.setOnClickListener(v -> alterarNome(activity, novoNome, txtNomeUsuario));
    }
}
