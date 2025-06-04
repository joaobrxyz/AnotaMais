package com.example.anotamais;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Notes extends AppCompatActivity {

    private ImageButton btVoltarNotes;
    private Button btSalvarNotes, btCriarPagina;
    private EditText txtTitulo, txtConteudo;

    private BancoControllerNote bancoControllerNote;
    private int idCaderno;
    private Integer idNota;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        btVoltarNotes = findViewById(R.id.btVoltarNotes);
        btSalvarNotes = findViewById(R.id.btSalvarNotes);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtConteudo = findViewById(R.id.txtConteudo);

        bancoControllerNote = new BancoControllerNote(this);

        Intent intentRecebida = getIntent();
        String tituloEdit = intentRecebida.getStringExtra("titulo");
        String conteudoEdit = intentRecebida.getStringExtra("conteudo");
        idCaderno = intentRecebida.getIntExtra("idCaderno", -1);
        idNota = intentRecebida.getIntExtra("idNota", -1);

        if (tituloEdit != null) {
            txtTitulo.setText(tituloEdit);
        }

        if (conteudoEdit != null) {
            txtConteudo.setText(conteudoEdit);
        }


        btSalvarNotes.setOnClickListener(v -> {
            String titulo = txtTitulo.getText().toString().trim();
            String conteudo = txtConteudo.getText().toString().trim();

            if (titulo.isEmpty() || conteudo.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String resultado;
            if (idNota != null && idNota != -1) {
                resultado = bancoControllerNote.alteraDados(idNota, titulo, conteudo);
            } else {
                resultado = bancoControllerNote.insereDados(titulo, conteudo, idCaderno);
            }
            Toast.makeText(this, resultado, Toast.LENGTH_SHORT).show();

            Intent intentResult = new Intent();
            intentResult.putExtra("titulo", titulo);
            intentResult.putExtra("conteudo", conteudo);
            setResult(RESULT_OK, intentResult);

            finish();
        });


        btVoltarNotes.setOnClickListener(v -> finish());
    }

}
