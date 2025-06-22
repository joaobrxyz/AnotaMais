package com.example.anotamais.activities;

import static com.example.anotamais.controllers.MainController.adjustLayoutForScreenHeight;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.anotamais.R;
import com.example.anotamais.controllers.ConfiguracoesController;

public class Configuracoes extends AppCompatActivity {

    // Declaração de elementos da interface
    ImageButton btHomeConfiguracoes, BtFavoritosConfiguracoes;
    EditText newNameUser;
    TextView txtNomeUsuarioConfig;
    Button btAlterarNomeUser;
    LinearLayout rodapeConfiguracoes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        // Define o modo claro (sem dark mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Inicializa elementos da interface
        btHomeConfiguracoes = findViewById(R.id.btHomeConfiguracoes);
        BtFavoritosConfiguracoes = findViewById(R.id.BtFavoritosConfiguracoes);
        rodapeConfiguracoes = findViewById(R.id.rodapeConfiguracoes);
        newNameUser = findViewById(R.id.newNameUser);
        btAlterarNomeUser = findViewById(R.id.btAlterarNomeUser);
        txtNomeUsuarioConfig = findViewById(R.id.txtNomeUsuarioConfig);

        // Ajusta a altura do rodapé de acordo com a altura da tela
        adjustLayoutForScreenHeight(this, rodapeConfiguracoes);

        // Carrega e exibe o nome atual do usuário
        ConfiguracoesController.carregarNome(this, txtNomeUsuarioConfig);

        // Configura os botões de navegação inferior (home e favoritos)
        ConfiguracoesController.configurarBotoesNavegacao(this, btHomeConfiguracoes, BtFavoritosConfiguracoes);

        // Configura o botão "Alterar Nome" para atualizar o nome do usuário
        ConfiguracoesController.configurarBotaoAlterarNome(this, btAlterarNomeUser, newNameUser, txtNomeUsuarioConfig);
    }
}
