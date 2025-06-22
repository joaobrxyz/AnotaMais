package com.example.anotamais.activities;

import static com.example.anotamais.controllers.MainController.adjustLayoutForScreenHeight;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotamais.R;
import com.example.anotamais.adapters.CadernoRecyclerAdapter;
import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.models.CadernoModel;

import java.util.LinkedList;
import java.util.List;

public class Favoritos extends AppCompatActivity {

    TextView textoPlaceHolderCadernoFav;
    RecyclerView listaCadernoFav;
    ImageButton btHomeFavoritos, btConfigFavoritos;
    LinearLayout rodapeFavoritos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_favoritos);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.favoritos), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textoPlaceHolderCadernoFav = findViewById(R.id.textoPlaceHolderCadernoFav);
        listaCadernoFav = findViewById(R.id.listaCadernoFav);
        btHomeFavoritos = findViewById(R.id.btHomeFavoritos);
        btConfigFavoritos = findViewById(R.id.btConfigFavoritos);
        rodapeFavoritos = findViewById(R.id.rodapeFavoritos);

        listarCadernos();

        adjustLayoutForScreenHeight(this, rodapeFavoritos);

        btHomeFavoritos.setOnClickListener(view -> {
            Intent intent = new Intent(Favoritos.this, MainActivity.class);
            startActivity(intent);
        });
        btConfigFavoritos.setOnClickListener(view -> {
            Intent intent = new Intent(Favoritos.this, Configuracoes.class);
            startActivity(intent);
        });
    }

    private List<CadernoModel> consultaTodosCadernos() {
        List<CadernoModel> cadernos = new LinkedList<CadernoModel>();

        BancoControllerCaderno bd = new BancoControllerCaderno(getBaseContext());
        Cursor dados = bd.listarCadernos(true);

        if (dados != null && dados.moveToFirst()) {
            do {
                textoPlaceHolderCadernoFav.setVisibility(View.GONE);
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
        listaCadernoFav = findViewById(R.id.listaCadernoFav);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2, GridLayoutManager.HORIZONTAL, false);
        listaCadernoFav.setLayoutManager(layoutManager);
        CadernoRecyclerAdapter adapter = new CadernoRecyclerAdapter(this, cadernos);
        adapter.setOnCadernoFavoritoChangeListener(() -> {
            listarCadernos();
        });
        listaCadernoFav.setAdapter(adapter);
    }
}