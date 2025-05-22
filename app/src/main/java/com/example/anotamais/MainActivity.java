package com.example.anotamais;

import static android.view.View.GONE;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ImageButton btImageCaderno;
    Button btCriarMateria, btCriarCaderno;
    TextView tvTituloMateria, olaaUsuario;
    EditText txtCaderno;
    LinearLayout newCaderno;
    RecyclerView listaCaderno;

    String tituloRecebido, conteudoRecebido;
    String nomeUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //btImageCaderno = findViewById(R.id.btImageCaderno);

        listarCadernos();

        btCriarMateria = findViewById(R.id.btCriarMateria);
        tvTituloMateria = findViewById(R.id.tvTituloMateria);
        olaaUsuario = findViewById(R.id.olaaUsuario);
        newCaderno = findViewById(R.id.newCaderno);
        txtCaderno = findViewById(R.id.txtCaderno);
        btCriarCaderno = findViewById(R.id.btCriarCaderno);

//        btImageCaderno.setVisibility(View.GONE);
        tvTituloMateria.setVisibility(GONE);

        btCriarMateria.setOnClickListener(this);
        btCriarCaderno.setOnClickListener(this);

        Intent intent = getIntent();

        BancoControllerUsuario bancoControllerUsuario = new BancoControllerUsuario(getBaseContext());
        if (!bancoControllerUsuario.verificaIdExiste()) {
            Intent tela = new Intent(this, Login.class);
            startActivity(tela);
        }





        /*if (intent != null && intent.getBooleanExtra("mostrarCaderno", false)) {
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
        }*/


    }

    private List<ModelCaderno> consultaTodosCadernos() {
        List<ModelCaderno> cadernos = new LinkedList<ModelCaderno>();


        BancoControllerCaderno bd = new BancoControllerCaderno(getBaseContext());
        Cursor dados = bd.listarCadernos();


        if (dados != null && dados.moveToFirst()) {
            do {
                ModelCaderno caderno = new ModelCaderno();
                caderno.setNome(dados.getString(0));
                cadernos.add(caderno);
            } while (dados.moveToNext());
        } else {
            String msg = "Não há cadernos cadastrados";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
        dados.close();

        return cadernos;
    }

    private void listarCadernos(){
        List<ModelCaderno> cadernos = null;
        cadernos = consultaTodosCadernos();
        listaCaderno = findViewById(R.id.listaCaderno);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        listaCaderno.setLayoutManager(layoutManager);
        CadernoRecyclerAdapter adapter = new CadernoRecyclerAdapter(cadernos);
        listaCaderno.setAdapter(adapter);
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
            newCaderno.setVisibility(GONE);
            listarCadernos();

        }
    }
}
