package com.example.anotamais;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btImageCaderno;
    Button btCriarMateria, btCriarCaderno;
    TextView tvTituloMateria, olaaUsuario;
    EditText txtCaderno;
    LinearLayout newCaderno;

    String tituloRecebido, conteudoRecebido;
    String nomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btImageCaderno = findViewById(R.id.btImageCaderno);
        btCriarMateria = findViewById(R.id.btCriarMateria);
        tvTituloMateria = findViewById(R.id.tvTituloMateria);
        olaaUsuario = findViewById(R.id.olaaUsuario);
        newCaderno = findViewById(R.id.newCaderno);
        txtCaderno = findViewById(R.id.txtCaderno);
        btCriarCaderno = findViewById(R.id.btCriarCaderno);

        btImageCaderno.setVisibility(View.GONE);
        tvTituloMateria.setVisibility(View.GONE);

        btCriarMateria.setOnClickListener(this);
        btCriarCaderno.setOnClickListener(this);

        Intent intent = getIntent();


        if (intent != null && intent.hasExtra("nomeUsuario")) {
            nomeUsuario = intent.getStringExtra("nomeUsuario");
            olaaUsuario.setText("Olá, " + nomeUsuario + " 👋");
        }


        if (intent != null && intent.getBooleanExtra("mostrarCaderno", false)) {
            tituloRecebido = intent.getStringExtra("titulo");
            conteudoRecebido = intent.getStringExtra("conteudo");

            btImageCaderno.setVisibility(View.VISIBLE);
            tvTituloMateria.setText(tituloRecebido);
            tvTituloMateria.setVisibility(View.VISIBLE);

            btImageCaderno.setOnClickListener(v -> {
                Intent intentCaderno = new Intent(MainActivity.this, Caderno.class);
                intentCaderno.putExtra("titulo", tituloRecebido);
                intentCaderno.putExtra("conteudo", conteudoRecebido);
                startActivity(intentCaderno);
            });
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btCriarMateria) {
            newCaderno.setVisibility(View.VISIBLE);
        }
        if (v.getId() == R.id.btCriarCaderno) {
            String nomeCaderno = txtCaderno.getText().toString();
            BancoControllerCaderno bancoControllerCaderno = new BancoControllerCaderno(getBaseContext());
            String msg = bancoControllerCaderno.insereDados(nomeCaderno);
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();

        }
    }
}
