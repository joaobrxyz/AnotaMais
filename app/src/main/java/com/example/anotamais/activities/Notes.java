package com.example.anotamais.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import com.example.anotamais.R;
import com.example.anotamais.config.GeminiConfig;
import com.example.anotamais.controllers.NotesController;

public class Notes extends AppCompatActivity {

    // Declaração dos botões de imagem e botões normais da interface
    private ImageButton btVoltarNotes, btFlashCard, btMenuOpcoes;
    private Button btGerarComIa, btSalvarFlashcard;

    // Declaração dos campos de texto e labels da interface
    private EditText txtTitulo, txtConteudo, txtPergunta, txtResposta;
    private TextView txtTituloPagina, nomeCadernoNote;

    // Containers para o popup e layout principal
    private FrameLayout fundoPopup;
    private LinearLayout criarFlashcard, conteudoPrincipal;

    // IDs e nome do caderno para controle interno
    private int idPagina, idCaderno;
    private String nomeCaderno;

    // Objeto Gemini para chamadas da API IA
    private GeminiConfig gemini;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        // Define o modo noturno desativado para a activity
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Recebe dados da Intent que iniciou essa activity
        idPagina = getIntent().getIntExtra("idPagina", 0);
        idCaderno = getIntent().getIntExtra("idCaderno", 0);
        nomeCaderno = getIntent().getStringExtra("nomeCaderno");

        // Inicializa a configuração da Gemini API com a chave do BuildConfig
        gemini = new GeminiConfig(com.example.anotamais.BuildConfig.GEMINI_API_KEY);

        // Referencia todos os elementos visuais do layout
        btVoltarNotes = findViewById(R.id.btVoltarNotes);
        btFlashCard = findViewById(R.id.btFlashCard);
        btMenuOpcoes = findViewById(R.id.btMenuOpcoes);
        btGerarComIa = findViewById(R.id.btGerarComIa);
        btSalvarFlashcard = findViewById(R.id.btSalvarFlashcard);

        txtTitulo = findViewById(R.id.txtTitulo);
        txtConteudo = findViewById(R.id.txtConteudo);
        txtPergunta = findViewById(R.id.txtPerguntaNotes);
        txtResposta = findViewById(R.id.txtRespostaNotes);
        txtTituloPagina = findViewById(R.id.txtTituloPagina);
        nomeCadernoNote = findViewById(R.id.nomeCadernoNote);

        fundoPopup = findViewById(R.id.fundoPopup);
        criarFlashcard = findViewById(R.id.criarFlashcard);
        conteudoPrincipal = findViewById(R.id.conteudoPrincipal);

        // Carrega os dados da página e atualiza a interface (campos texto e labels)
        NotesController.recuperarPagina(this, idPagina, idCaderno, nomeCaderno,
                txtTitulo, txtConteudo, txtTituloPagina, nomeCadernoNote);

        // Configura o clique no botão de voltar para a tela do caderno
        btVoltarNotes.setOnClickListener(v ->
                NotesController.voltarParaCaderno(this, idCaderno, nomeCaderno)
        );

        // Configura o clique para abrir o menu de opções da nota
        btMenuOpcoes.setOnClickListener(v ->
                NotesController.abrirMenuOpcoes(this, v, idPagina, idCaderno, nomeCaderno)
        );

        // Listener para alterações no título da nota
        txtTitulo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Atualiza o título na interface
                txtTituloPagina.setText(s.toString());
                // Salva a nota atualizada no banco
                NotesController.salvarNota(Notes.this, idPagina, s.toString(), txtConteudo.getText().toString());
            }
        });

        // Listener para alterações no conteúdo da nota
        txtConteudo.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                // Salva a nota atualizada no banco
                NotesController.salvarNota(Notes.this, idPagina, txtTitulo.getText().toString(), s.toString());
            }
        });

        // Ação do botão para abrir o popup de criação de flashcards
        btFlashCard.setOnClickListener(v -> {
            NotesController.abrirPopupFlashcard(fundoPopup, conteudoPrincipal, txtPergunta, txtResposta);
        });

        // Ação do botão para salvar flashcard no banco e esconder popup após sucesso
        btSalvarFlashcard.setOnClickListener(v ->
                NotesController.salvarFlashcard(this, txtPergunta.getText().toString(), txtResposta.getText().toString(), idPagina, idCaderno,
                        () -> {
                            Toast.makeText(this, "Flashcard criada com sucesso!", Toast.LENGTH_LONG).show();
                            fundoPopup.setVisibility(View.GONE);
                            conteudoPrincipal.animate().alpha(1f).setDuration(200).start();
                        })
        );

        // Ação do botão para gerar flashcards com a IA via Gemini API
        btGerarComIa.setOnClickListener(v ->
                NotesController.gerarFlashcardComIA(this, gemini, txtConteudo.getText().toString(), txtPergunta, txtResposta, btSalvarFlashcard, btGerarComIa)
        );

        // Listener para detectar toque fora do popup para fechar o popup de flashcard
        fundoPopup.setOnTouchListener((v, event) ->
                NotesController.tratarToqueFundoPopup(event, fundoPopup, conteudoPrincipal, criarFlashcard)
        );

        // Limita linhas do campo pergunta (máximo 3 linhas)
        txtPergunta.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                NotesController.limitarLinhasPergunta(txtPergunta, s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Limita linhas do campo resposta (máximo 5 linhas)
        txtResposta.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                NotesController.limitarLinhasResposta(txtResposta, s);
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }
}
