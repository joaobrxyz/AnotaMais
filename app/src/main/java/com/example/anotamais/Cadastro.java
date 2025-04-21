package com.example.anotamais;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Cadastro extends AppCompatActivity implements View.OnClickListener {
    TextView nameInputCadastro, emailInputCadastro, passwordInputCadastro;
    Button btCadastrarCadastro;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.cadastro), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        nameInputCadastro = findViewById(R.id.nameInputCadastro);
        emailInputCadastro = findViewById(R.id.emailInputCadastro);
        passwordInputCadastro = findViewById(R.id.passwordInputCadastro);
        btCadastrarCadastro = findViewById(R.id.btCadastrarCadastro);

        btCadastrarCadastro.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        BancoController bancoController = new BancoController(getBaseContext());
        String email = emailInputCadastro.getText().toString();
        String nome = nameInputCadastro.getText().toString();
        String password = passwordInputCadastro.getText().toString();
        bancoController.insereDados(email, nome, password);
    }
}