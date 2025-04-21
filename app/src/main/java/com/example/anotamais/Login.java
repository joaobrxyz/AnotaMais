package com.example.anotamais;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Login extends AppCompatActivity implements View.OnClickListener {
    TextView emailInputLogin, passwordInputLogin;
    Button btEntrarLogin, btCreateAccountLogin;

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

        emailInputLogin = findViewById(R.id.emailInputLogin);
        passwordInputLogin = findViewById(R.id.passwordInputLogin);
        btEntrarLogin = findViewById(R.id.btEntrarLogin);
        btCreateAccountLogin = findViewById(R.id.btCreateAccountLogin);

        btCreateAccountLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btCreateAccountLogin) {
            Intent screenCadastro = new Intent(this, Cadastro.class);
            startActivity(screenCadastro);
        }
        if (v.getId() == R.id.btEntrarLogin) {
            BancoController bancoController = new BancoController(getBaseContext());
            String email = emailInputLogin.getText().toString();
            String password = passwordInputLogin.getText().toString();

            Cursor dados = bancoController.carregaDadosPeloEmail(email, password);

            if (dados.moveToFirst()) {
                String msg= "Teste OK";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            } else {
                String msg= "Email ou senha incorreto(s)";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        }
    }
}