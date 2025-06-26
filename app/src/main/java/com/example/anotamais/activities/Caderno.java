package com.example.anotamais.activities;

import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.controllers.CadernoController;
import com.example.anotamais.R;

public class Caderno extends AppCompatActivity {

    // Declaração dos componentes da interface
    Button btCriarAnotacao;
    TextView textoPlaceHolderCaderno, nomeCadernoCaderno;
    RecyclerView listaAnotacao;
    ImageButton btVoltarCaderno;

    // Variáveis para armazenar os dados do caderno atual
    String nomeCaderno, remoteIdCaderno;
    int idCaderno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caderno);

        // Define o modo claro (sem dark mode)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Recupera o ID do caderno passado pela intent
        idCaderno = getIntent().getIntExtra("idCaderno", 0);

        // Pega o remoteId do caderno, e o nome do caderno do banco de dados
        BancoControllerCaderno bd = new BancoControllerCaderno(getBaseContext());
        Cursor dados = bd.carregaDadosPeloId(idCaderno);
        if (dados != null && dados.moveToFirst()) {
            nomeCaderno = dados.getString(1);
            remoteIdCaderno = dados.getString(2);
        }
        dados.close();

        // Referencia os elementos da UI
        btCriarAnotacao = findViewById(R.id.btCriarAnotacao);
        btVoltarCaderno = findViewById(R.id.btVoltarCaderno);
        textoPlaceHolderCaderno = findViewById(R.id.textoPlaceHolderCaderno);
        nomeCadernoCaderno = findViewById(R.id.nomeCadernoCaderno);
        listaAnotacao = findViewById(R.id.listaNotes);

        // Atualiza o TextView com o nome do caderno atual
        nomeCadernoCaderno.setText("Caderno: " + nomeCaderno);

        // Lista todas as anotações desse caderno no RecyclerView
        CadernoController.listarNotes(this, listaAnotacao, remoteIdCaderno, nomeCaderno);

        // Configura o botão de voltar para retornar à tela anterior
        CadernoController.voltarCaderno(this, btVoltarCaderno);

        // Configura o botão para criar uma nova anotação dentro desse caderno
        CadernoController.CriarAnotacao(this, btCriarAnotacao, idCaderno, nomeCaderno, remoteIdCaderno);
    }
}
