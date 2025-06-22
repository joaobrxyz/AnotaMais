package com.example.anotamais.activities;

import static com.example.anotamais.controllers.MainController.adjustLayoutForScreenHeight;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anotamais.R;
import com.example.anotamais.controllers.FavoritosController;

public class Favoritos extends AppCompatActivity {

    // Declaração dos componentes da interface
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

        // Inicializa os componentes da interface
        textoPlaceHolderCadernoFav = findViewById(R.id.textoPlaceHolderCadernoFav);
        listaCadernoFav = findViewById(R.id.listaCadernoFav);
        btHomeFavoritos = findViewById(R.id.btHomeFavoritos);
        btConfigFavoritos = findViewById(R.id.btConfigFavoritos);
        rodapeFavoritos = findViewById(R.id.rodapeFavoritos);

        // Ajusta layout do rodapé conforme a altura da tela
        adjustLayoutForScreenHeight(this, rodapeFavoritos);

        // Configura botões de navegação inferior (home e configurações)
        FavoritosController.configurarBotoesNavegacao(this, btHomeFavoritos, btConfigFavoritos);

        // Lista os cadernos marcados como favoritos
        FavoritosController.listarCadernosFavoritos(this, listaCadernoFav, textoPlaceHolderCadernoFav);
    }
}
