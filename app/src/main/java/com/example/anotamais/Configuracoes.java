package com.example.anotamais;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

import de.hdodenhof.circleimageview.CircleImageView;

public class Configuracoes extends AppCompatActivity {

    private LinearLayout btnVoltar;
    private CircleImageView imgPerfil;
    private ImageView btnEditarPerfil;
    private TextView txtNomeUsuario;
    private TextView txtEmailUsuario;
    private LinearLayout btnAlterarInfoConta;
    private LinearLayout btnTema;
    private LinearLayout btnFonte;
    private SwitchMaterial switchSincronizarDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes); // Certifique-se que o nome do XML está correto

        // Inicializar Views
        btnVoltar = findViewById(R.id.btnVoltar);
        imgPerfil = findViewById(R.id.imgPerfil);
        btnEditarPerfil = findViewById(R.id.btnEditarPerfil);
        txtNomeUsuario = findViewById(R.id.txtNomeUsuario);
        btnAlterarInfoConta = findViewById(R.id.btnAlterarInfoConta);
        btnTema = findViewById(R.id.btnTema);
        btnFonte = findViewById(R.id.btnFonte);
        switchSincronizarDrive = findViewById(R.id.switchSincronizarDrive);

        // Carregar dados do usuário (Exemplo - substitua pela sua lógica)
        loadUserProfile();
        loadSettings();


        // Configurar Listeners
        btnVoltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnEditarPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Configuracoes.this, "Editar perfil clicado", Toast.LENGTH_SHORT).show();
                // TODO: Navegar para a tela de edição de perfil ou abrir um diálogo
                // Intent intent = new Intent(Configuracoes.this, EditProfileActivity.class);
                // startActivity(intent);
            }
        });

        imgPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Configuracoes.this, "Imagem de perfil clicada", Toast.LENGTH_SHORT).show();
                // TODO: Implementar lógica para alterar/visualizar imagem de perfil
            }
        });

        btnAlterarInfoConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Configuracoes.this, "Alterar informações da conta clicado", Toast.LENGTH_SHORT).show();
                // TODO: Navegar para a tela de alteração de informações
                // Intent intent = new Intent(Configuracoes.this, AccountInfoActivity.class);
                // startActivity(intent);
            }
        });

        btnTema.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Configuracoes.this, "Tema clicado", Toast.LENGTH_SHORT).show();
                // TODO: Abrir diálogo ou tela para seleção de tema
            }
        });

        btnFonte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Configuracoes.this, "Fonte clicada", Toast.LENGTH_SHORT).show();
                // TODO: Abrir diálogo ou tela para seleção de fonte
            }
        });

        switchSincronizarDrive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Toast.makeText(Configuracoes.this, "Sincronização com Google Drive ATIVADA", Toast.LENGTH_SHORT).show();
                // TODO: Salvar preferência e iniciar lógica de sincronização
            } else {
                Toast.makeText(Configuracoes.this, "Sincronização com Google Drive DESATIVADA", Toast.LENGTH_SHORT).show();
                // TODO: Salvar preferência e parar lógica de sincronização
            }
            saveSyncPreference(isChecked);
        });
    }

    private void loadUserProfile() {


        txtNomeUsuario.setText("David"); // Substitua por dados reais
        txtEmailUsuario.setText("david28347@gmail.com"); // Substitua por dados reais


    }

    private void loadSettings() {
        // Exemplo: Carregar o estado do switch de SharedPreferences
        // SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        // boolean syncEnabled = prefs.getBoolean("syncGoogleDrive", false);
        // switchSincronizarDrive.setChecked(syncEnabled);
        switchSincronizarDrive.setChecked(false); // Valor padrão
    }

    private void saveSyncPreference(boolean isEnabled) {
        // Exemplo: Salvar em SharedPreferences
        // SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        // SharedPreferences.Editor editor = prefs.edit();
        // editor.putBoolean("syncGoogleDrive", isEnabled);
        // editor.apply();
    }


}