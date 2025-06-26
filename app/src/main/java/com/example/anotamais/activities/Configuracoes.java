package com.example.anotamais.activities;

import static com.example.anotamais.controllers.MainController.adjustLayoutForScreenHeight;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.anotamais.R;
import com.example.anotamais.controllers.ConfiguracoesController;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Configuracoes extends AppCompatActivity {

    // Declaração de elementos da interface
    ImageButton btHomeConfiguracoes, BtFavoritosConfiguracoes;
    EditText newNameUser;
    TextView txtNomeUsuarioConfig;
    Button btAlterarNomeUser;
    LinearLayout rodapeConfiguracoes;
    private static final int RC_SIGN_IN = 9001;

    private FirebaseAuth auth;
    private GoogleSignInClient googleSignInClient;
    private SwitchMaterial switchSincronizarDrive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Inicializa elementos da interface
        btHomeConfiguracoes = findViewById(R.id.btHomeConfiguracoes);
        BtFavoritosConfiguracoes = findViewById(R.id.BtFavoritosConfiguracoes);
        rodapeConfiguracoes = findViewById(R.id.rodapeConfiguracoes);
        newNameUser = findViewById(R.id.newNameUser);
        btAlterarNomeUser = findViewById(R.id.btAlterarNomeUser);
        txtNomeUsuarioConfig = findViewById(R.id.txtNomeUsuarioConfig);
        switchSincronizarDrive = findViewById(R.id.switchSincronizarDrive);

        adjustLayoutForScreenHeight(this, rodapeConfiguracoes);

        // Inicializa autenticação e Google Sign-In
        auth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // Se já estiver logado, ativa o switch
        FirebaseUser currentUser = auth.getCurrentUser();
        switchSincronizarDrive.setChecked(currentUser != null);

        switchSincronizarDrive.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                ConfiguracoesController.iniciarLoginGoogle(this, googleSignInClient);
            } else {
                auth.signOut();
                googleSignInClient.signOut();
                Toast.makeText(this, "Sincronização desligada", Toast.LENGTH_SHORT).show();
            }
        });

        ConfiguracoesController.carregarNome(this, txtNomeUsuarioConfig );
        ConfiguracoesController.configurarBotoesNavegacao(this, btHomeConfiguracoes, BtFavoritosConfiguracoes);
        ConfiguracoesController.configurarBotaoAlterarNome(this, btAlterarNomeUser, newNameUser, txtNomeUsuarioConfig);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                ConfiguracoesController.autenticarComGoogle(this, auth, account.getIdToken(), switchSincronizarDrive);
            } catch (ApiException e) {
                Toast.makeText(this, "Login Google falhou: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                switchSincronizarDrive.setChecked(false);
            }
        }
    }
}
