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
    TextView txtNomeLogin;
    Button btEntrarLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        txtNomeLogin = findViewById(R.id.txtNomeLogin);
        btEntrarLogin = findViewById(R.id.btEntrarLogin);

        btEntrarLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btEntrarLogin) {
            String nome = txtNomeLogin.getText().toString().trim();

            if (nome.isEmpty()) {
                Toast.makeText(this, "Digite seu nome", Toast.LENGTH_SHORT).show();
                return;
            }

            BancoControllerUsuario bancoControllerUsuario = new BancoControllerUsuario(getBaseContext());
            bancoControllerUsuario.insereDados(nome);

            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtNomeLogin.getWindowToken(), 0);

            Intent tela = new Intent(this, MainActivity.class);
            tela.putExtra("nomeUsuario", nome);
            startActivity(tela);
            finish();
        }
    }
}
