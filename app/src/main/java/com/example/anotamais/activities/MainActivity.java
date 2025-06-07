package com.example.anotamais.activities;

import static android.view.View.GONE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.controllers.BancoControllerUsuario;
import com.example.anotamais.models.CadernoModel;
import com.example.anotamais.adapters.CadernoRecyclerAdapter;
import com.example.anotamais.R;

import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btCriarMateriaMain, btCriarCaderno, btResumirAulaMain, btCardsMain;
    ImageButton btConfigMain;
    TextView tvTituloMateria, nomeUsuarioMain;
    EditText txtCaderno;
    LinearLayout newCaderno;
    RecyclerView listaCaderno;
    FrameLayout fundoPopupMain;
    Cursor nomeUsuario;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        BancoControllerUsuario bancoControllerUsuario = new BancoControllerUsuario(getBaseContext());
        if (!bancoControllerUsuario.verificaIdExiste()) {
            Intent tela = new Intent(this, Login.class);
            startActivity(tela);
        } else {
            nomeUsuario = bancoControllerUsuario.carregaDadosPeloId();
            nomeUsuarioMain = findViewById(R.id.nomeUsuarioMain);
            nomeUsuarioMain.setText("Olá, " + nomeUsuario.getString(1) + " 👋");
        }

        listarCadernos();

        btCriarMateriaMain = findViewById(R.id.btCriarMateriaMain);
        btCriarCaderno = findViewById(R.id.btCriarCaderno);
        btResumirAulaMain = findViewById(R.id.btResumirAulaMain);
        btCardsMain = findViewById(R.id.btCardsMain);
        btConfigMain = findViewById(R.id.btConfigMain);
        tvTituloMateria = findViewById(R.id.tvTituloMateria);
        newCaderno = findViewById(R.id.newCaderno);
        txtCaderno = findViewById(R.id.txtCaderno);
        btCriarCaderno = findViewById(R.id.btCriarCaderno);
        fundoPopupMain = findViewById(R.id.fundoPopupMain);

        tvTituloMateria.setVisibility(GONE);

        btCriarMateriaMain.setOnClickListener(this);
        btCriarCaderno.setOnClickListener(this);
        btResumirAulaMain.setOnClickListener(this);
        btCardsMain.setOnClickListener(this);
        btConfigMain.setOnClickListener(this);

        if (!bancoControllerUsuario.verificaIdExiste()) {
            Intent tela = new Intent(this, Login.class);
            startActivity(tela);
        }

        fundoPopupMain.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.performClick();
                fundoPopupMain.setVisibility(View.GONE);
                newCaderno.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtCaderno.getWindowToken(), 0);
                new Handler(Looper.getMainLooper()).postDelayed(this::listarCadernos, 200);
                return true;
            }
            return false;
        });
    }
    private List<CadernoModel> consultaTodosCadernos() {
        List<CadernoModel> cadernos = new LinkedList<CadernoModel>();

        BancoControllerCaderno bd = new BancoControllerCaderno(getBaseContext());
        Cursor dados = bd.listarCadernos();

        if (dados != null && dados.moveToFirst()) {
            do {
                CadernoModel caderno = new CadernoModel();
                caderno.setId(dados.getInt(0));
                caderno.setNome(dados.getString(1));
                if (dados.getInt(2) == 1) {
                    caderno.setFavorito(true);
                } else {
                    caderno.setFavorito(false);
                }
                cadernos.add(caderno);
            } while (dados.moveToNext());
        }
        dados.close();

        return cadernos;
    }

    private void listarCadernos(){
        List<CadernoModel> cadernos = null;
        cadernos = consultaTodosCadernos();
        listaCaderno = findViewById(R.id.listaCaderno);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        listaCaderno.setLayoutManager(layoutManager);
        CadernoRecyclerAdapter adapter = new CadernoRecyclerAdapter(this, cadernos);
        adapter.setOnCadernoFavoritoChangeListener(() -> {
            listarCadernos();
        });
        listaCaderno.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btCriarMateriaMain) {
            fundoPopupMain.setVisibility(View.VISIBLE);
            newCaderno.setVisibility(View.VISIBLE);

            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setDuration(300);
            newCaderno.startAnimation(fadeIn);
            txtCaderno.setText("");
        }
        if (v.getId() == R.id.btCriarCaderno) {
            String nomeCaderno = txtCaderno.getText().toString();

            if (!nomeCaderno.isEmpty()) {
                BancoControllerCaderno bancoControllerCaderno = new BancoControllerCaderno(getBaseContext());
                String msg = bancoControllerCaderno.insereDados(nomeCaderno);
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtCaderno.getWindowToken(), 0);
                fundoPopupMain.setVisibility(View.GONE);
                newCaderno.setVisibility(View.GONE);
                new Handler(Looper.getMainLooper()).postDelayed(this::listarCadernos, 200);
            } else {
                Toast.makeText(this, "Digite um nome para o caderno", Toast.LENGTH_SHORT).show();
            }
        }
        if (v.getId() == R.id.btResumirAulaMain) {
            Intent tela = new Intent(this, Resumidor.class);
            startActivity(tela);
        }
        if (v.getId() == R.id.btCardsMain) {
            Intent tela = new Intent(this, Flashcards.class);
            startActivity(tela);
        }
        if (v.getId() == R.id.btConfigMain) {
            Intent tela = new Intent(this, Configuracoes.class);
            startActivity(tela);
        }
    }
}