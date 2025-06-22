package com.example.anotamais.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.example.anotamais.controllers.BancoControllerUsuario;
import com.example.anotamais.R;

public class Login extends AppCompatActivity implements View.OnClickListener {

    // Elementos da interface
    TextView txtNomeLogin;
    Button btEntrarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Ativa o layout de ponta a ponta
        setContentView(R.layout.activity_login);

        // Ajusta as margens com base nos elementos do sistema (status/nav bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Desativa o modo escuro
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Liga os componentes da interface às variáveis
        txtNomeLogin = findViewById(R.id.txtNomeLogin);
        btEntrarLogin = findViewById(R.id.btEntrarLogin);

        // Define o botão como escutador de cliques
        btEntrarLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // Verifica se o botão clicado foi o de "Entrar"
        if (v.getId() == R.id.btEntrarLogin) {
            String nome = txtNomeLogin.getText().toString().trim(); // Lê o nome digitado

            // Se o nome estiver vazio, mostra mensagem de aviso
            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite seu nome", Toast.LENGTH_SHORT).show();
                return;
            }

            // Insere o nome do usuário no banco de dados
            BancoControllerUsuario bancoControllerUsuario = new BancoControllerUsuario(getBaseContext());
            bancoControllerUsuario.insereDados(nome);

            // Fecha o teclado após o clique
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtNomeLogin.getWindowToken(), 0);

            // Inicia a MainActivity passando o nome do usuário
            Intent tela = new Intent(this, MainActivity.class);
            tela.putExtra("nomeUsuario", nome);
            startActivity(tela);
            finish(); // Encerra a tela de login para que o usuário não volte ao pressionar "voltar"
        }
    }
}
