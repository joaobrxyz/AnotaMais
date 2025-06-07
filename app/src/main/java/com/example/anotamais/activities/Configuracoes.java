package com.example.anotamais.activities;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.anotamais.R;
import com.example.anotamais.controllers.BancoControllerUsuario;


public class Configuracoes extends AppCompatActivity {
    ImageButton btHomeConfiguracoes, BtFavoritosConfiguracoes;
    EditText newNameUser;
    TextView txtNomeUsuarioConfig;
    Button btAlterarNomeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        btHomeConfiguracoes = findViewById(R.id.btHomeConfiguracoes);
        BtFavoritosConfiguracoes = findViewById(R.id.BtFavoritosConfiguracoes);
        newNameUser = findViewById(R.id.newNameUser);
        btAlterarNomeUser = findViewById(R.id.btAlterarNomeUser);
        txtNomeUsuarioConfig = findViewById(R.id.txtNomeUsuarioConfig);

        carregarNome();

        btHomeConfiguracoes.setOnClickListener(v ->  {
            Intent tela = new Intent(Configuracoes.this, MainActivity.class);
            startActivity(tela);
        });
        BtFavoritosConfiguracoes.setOnClickListener(v ->  {
            Intent tela = new Intent(Configuracoes.this, Favoritos.class);
            startActivity(tela);
        });

        btAlterarNomeUser.setOnClickListener(v -> {
            alterarNome();
        });



    }
    public void alterarNome() {
        String novoNome = newNameUser.getText().toString();
        if (!novoNome.isEmpty()) {
            BancoControllerUsuario bd = new BancoControllerUsuario(getBaseContext());
            bd.alteraDados(novoNome);
            newNameUser.setText("");
            carregarNome();
            Toast.makeText(this, "Nome alterado com sucesso!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Por favor, insira um novo nome.", Toast.LENGTH_SHORT).show();
        }
    }

    public void carregarNome() {
        BancoControllerUsuario bd = new BancoControllerUsuario(getBaseContext());
        Cursor cursor = bd.carregaDadosPeloId();
        if (cursor.moveToFirst()) {
            String nome = cursor.getString(1);
            txtNomeUsuarioConfig.setText("Olá, " + nome + "! 🙂");
        }
    }
}