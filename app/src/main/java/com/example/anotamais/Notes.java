package com.example.anotamais;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Notes extends AppCompatActivity {

    ImageButton btVoltarNotes;
    Button btSalvarNotes, btCriarPagina;
    EditText txtTitulo, txtConteudo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        btVoltarNotes = findViewById(R.id.btVoltarNotes);
        btSalvarNotes = findViewById(R.id.btSalvarNotes);
        btCriarPagina = findViewById(R.id.btCriarPagina);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtConteudo = findViewById(R.id.txtConteudo);

        btVoltarNotes.setOnClickListener(v -> {
            finish();
        });

        btSalvarNotes.setOnClickListener(v -> {
            String titulo = txtTitulo.getText().toString().trim();
            String conteudo = txtConteudo.getText().toString().trim();

            if (titulo.isEmpty() || conteudo.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intent = new Intent(Notes.this, MainActivity.class);
            intent.putExtra("mostrarCaderno", true);
            intent.putExtra("titulo", titulo);
            intent.putExtra("conteudo", conteudo);
            startActivity(intent);
            finish();

            Toast.makeText(this, "Caderno criado com sucesso!", Toast.LENGTH_SHORT).show();
        });
    }
}
