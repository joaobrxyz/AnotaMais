package com.example.anotamais.activities;

import static android.view.View.GONE;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anotamais.controllers.BancoControllerUsuario;
import com.example.anotamais.controllers.MainController;
import com.example.anotamais.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // Elementos da interface
    Button btCriarMateriaMain, btCriarCaderno, btResumirAulaMain, btCardsMain;
    ImageButton btConfigMain, btFavoritosMain;
    TextView tvTituloMateria, nomeUsuarioMain;
    EditText txtCaderno;
    LinearLayout newCaderno, rodapeMain;
    RecyclerView listaCaderno;
    FrameLayout fundoPopupMain;
    Cursor nomeUsuario;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Desativa o modo escuro forçadamente
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Verifica se o usuário já existe no banco; se não, redireciona para a tela de login
        BancoControllerUsuario bancoControllerUsuario = new BancoControllerUsuario(getBaseContext());
        if (!bancoControllerUsuario.verificaIdExiste()) {
            Intent tela = new Intent(this, Login.class);
            startActivity(tela);
        } else {
            // Se existe, recupera o nome do usuário e exibe na tela
            nomeUsuario = bancoControllerUsuario.carregaDadosPeloId();
            nomeUsuarioMain = findViewById(R.id.nomeUsuarioMain);
            nomeUsuarioMain.setText("Olá, " + nomeUsuario.getString(1) + " 👋");
        }

        // Vincula os elementos da interface às variáveis
        btCriarMateriaMain = findViewById(R.id.btCriarMateriaMain);
        btCriarCaderno = findViewById(R.id.btCriarCaderno);
        btResumirAulaMain = findViewById(R.id.btResumirAulaMain);
        btCardsMain = findViewById(R.id.btCardsMain);
        btConfigMain = findViewById(R.id.btConfigMain);
        btFavoritosMain = findViewById(R.id.btFavoritosMain);
        tvTituloMateria = findViewById(R.id.tvTituloMateria);
        newCaderno = findViewById(R.id.newCaderno);
        txtCaderno = findViewById(R.id.txtCaderno);
        fundoPopupMain = findViewById(R.id.fundoPopupMain);
        rodapeMain = findViewById(R.id.rodapeMain);
        listaCaderno = findViewById(R.id.listaCaderno);

        // Esconde o título "Matéria" por padrão
        tvTituloMateria.setVisibility(GONE);

        // Define os listeners dos botões
        btCriarMateriaMain.setOnClickListener(this);
        btCriarCaderno.setOnClickListener(this);
        btResumirAulaMain.setOnClickListener(this);
        btCardsMain.setOnClickListener(this);
        btConfigMain.setOnClickListener(this);
        btFavoritosMain.setOnClickListener(this);

        // Lista todos os cadernos do usuário
        MainController.listarCadernos(this, listaCaderno, rodapeMain);

        // Fecha o popup se o usuário tocar fora dele
        fundoPopupMain.setOnTouchListener(MainController.fecharPopupAoTocarFora(this, fundoPopupMain, newCaderno, txtCaderno,
                () -> MainController.listarCadernos(this, listaCaderno, rodapeMain)
        ));
    }

    // Lógica de clique para todos os botões
    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.btCriarMateriaMain) {
            // Exibe o popup para criar um novo caderno
            fundoPopupMain.setVisibility(View.VISIBLE);
            MainController.animarEntradaNovoCaderno(newCaderno, txtCaderno);
        }

        if (id == R.id.btCriarCaderno) {
            // Cria um novo caderno com o texto digitado
            MainController.criarCaderno(
                    this,
                    txtCaderno.getText().toString(),
                    txtCaderno,
                    fundoPopupMain,
                    newCaderno,
                    () -> MainController.listarCadernos(this, listaCaderno, rodapeMain)
            );
        }

        // Abre a tela do resumidor de aula
        if (id == R.id.btResumirAulaMain) MainController.abrirTela(this, Resumidor.class);

        // Abre a tela de flashcards
        if (id == R.id.btCardsMain) MainController.abrirTela(this, Flashcards.class);

        // Abre a tela de configurações
        if (id == R.id.btConfigMain) MainController.abrirTela(this, Configuracoes.class);

        // Abre a tela de favoritos
        if (id == R.id.btFavoritosMain) MainController.abrirTela(this, Favoritos.class);
    }
}
