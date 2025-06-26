package com.example.anotamais.controllers;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import com.example.anotamais.activities.Favoritos;
import com.example.anotamais.activities.MainActivity;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class ConfiguracoesController {

    private static final int RC_SIGN_IN = 9001;

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

    public static void iniciarLoginGoogle(Activity activity, GoogleSignInClient googleSignInClient) {
        activity.startActivityForResult(googleSignInClient.getSignInIntent(), RC_SIGN_IN);
    }

    public static void autenticarComGoogle(Activity activity, FirebaseAuth auth, String idToken, CompoundButton switchSincronizarDrive) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        auth.signInWithCredential(credential).addOnCompleteListener(activity, task -> {
            if (task.isSuccessful()) {
                Toast.makeText(activity, "Logado com sucesso!", Toast.LENGTH_SHORT).show();
                FirebaseUser user = auth.getCurrentUser();
                // Salvar UID e nome no SQLite
                if (user != null) {
                    String uid = user.getUid();

                    BancoControllerUsuario bd = new BancoControllerUsuario(activity);
                    bd.salvarOuAtualizarUidUsuario(uid);
                }

                // Inicia a sincronização
                SincronizacaoController.sincronizarComFirestore(activity, user, false, 0);
            } else {
                Toast.makeText(activity, "Falha na autenticação Firebase.", Toast.LENGTH_SHORT).show();
                switchSincronizarDrive.setChecked(false);
            }
        });
    }
}
