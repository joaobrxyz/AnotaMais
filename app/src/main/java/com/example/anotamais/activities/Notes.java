package com.example.anotamais.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.controllers.BancoControllerCard;
import com.example.anotamais.controllers.BancoControllerNote;
import com.example.anotamais.BuildConfig;
import com.example.anotamais.models.CadernoModel;
import com.example.anotamais.config.GeminiConfig;
import com.example.anotamais.models.NotaModel;
import com.example.anotamais.R;

import java.util.Calendar;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class Notes extends AppCompatActivity {

    ImageButton btVoltarNotes, btFlashCard, btnewPage;
    Button btGerarComIa, btSalvarFlashcard;
    EditText txtTitulo, txtConteudo, txtPergunta, txtResposta;
    TextView txtTituloPagina, nomeCadernoNote;
    String nomeCaderno;
    FrameLayout fundoPopup;
    LinearLayout criarFlashcard, conteudoPrincipal;
    GeminiConfig gemini;
    int idPagina, idCaderno;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    String dataFormatada = sdf.format(calendar.getTime());

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        idPagina = getIntent().getIntExtra("idPagina", 0);
        idCaderno = getIntent().getIntExtra("idCaderno", 0);
        nomeCaderno = getIntent().getStringExtra("nomeCaderno");

        if (idPagina != 0) {
            recuperarPagina();
        }


        gemini = new GeminiConfig(BuildConfig.GEMINI_API_KEY);
        btVoltarNotes = findViewById(R.id.btVoltarNotes);
        btFlashCard = findViewById(R.id.btFlashCard);
        btGerarComIa = findViewById(R.id.btGerarComIa);
        btnewPage = findViewById(R.id.btnewPage);
        conteudoPrincipal = findViewById(R.id.conteudoPrincipal);
        btSalvarFlashcard = findViewById(R.id.btSalvarFlashcard);
        btVoltarNotes = findViewById(R.id.btVoltarNotes);
        txtTituloPagina = findViewById(R.id.txtTituloPagina);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtConteudo = findViewById(R.id.txtConteudo);
        txtPergunta = findViewById(R.id.txtPerguntaNotes);
        txtResposta = findViewById(R.id.txtRespostaNotes);
        fundoPopup = findViewById(R.id.fundoPopup);
        criarFlashcard = findViewById(R.id.criarFlashcard);


        btVoltarNotes.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, Caderno.class);
            intent.putExtra("idCaderno", idCaderno);
            intent.putExtra("nomeCaderno", nomeCaderno);
            context.startActivity(intent);
        });

        btnewPage.setOnClickListener(v -> {
            BancoControllerNote bdN = new BancoControllerNote(getBaseContext());
            long id = -1;
            id = bdN.insereDados("Título da Página", "Conteúdo da Página", idCaderno, dataFormatada);
            if (id == -1) {
                Toast.makeText(this, "Erro ao criar nova página", Toast.LENGTH_LONG).show();
            } else {
                Context context = v.getContext();
                Intent intent = new Intent(context, Notes.class);
                int idPagina = (int) id;
                intent.putExtra("idPagina", idPagina);
                intent.putExtra("idCaderno", idCaderno);
                context.startActivity(intent);
            }
        });



        txtTitulo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                txtTituloPagina.setText(txtTitulo.getText().toString());
                salvarNoBanco();
            }
        });

        txtConteudo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                salvarNoBanco();
            }
        });

        btFlashCard.setOnClickListener(v -> {
            fundoPopup.setVisibility(View.VISIBLE);
            conteudoPrincipal.animate().alpha(0.3f).setDuration(200).start();
            txtPergunta.setText("");
            txtResposta.setText("");
        });

        btSalvarFlashcard.setOnClickListener(v -> {

            BancoControllerCard bancoControllerCard = new BancoControllerCard(getBaseContext());
            bancoControllerCard.insereDados(txtPergunta.getText().toString(), txtResposta.getText().toString(), idPagina, idCaderno);

            Toast.makeText(this, "Flashcard criada com sucesso!", Toast.LENGTH_LONG).show();

            fundoPopup.setVisibility(View.GONE);
            conteudoPrincipal.animate().alpha(1f).setDuration(200).start();
        });

        btGerarComIa.setOnClickListener(v -> {
            txtPergunta.setText("Processando...");
            txtResposta.setText("Processando...");
            btSalvarFlashcard.setEnabled(false);
            btGerarComIa.setEnabled(false);

            BancoControllerCard bancoControllerCard = new BancoControllerCard(getBaseContext());

            String textoConteudo = txtConteudo.getText().toString();

            String promptPergunta = "Estou te utilizando como api em um projeto, então quero que vc apenas retorne oq eu pedir, sem vc falar nada. Estou gerando um flashcard, onde tem uma pergunta curta e uma resposta curta, nesse prompt quero que vc retorne apenas uma pergunta sobre essa aula (Lembre-se, pergunta curta!! com no máximo 80 caracteres): " + textoConteudo + " (Faça perguntas diferentes dessas, pois já foram criadas: " + consultaTodasAsPerguntasDosFlashcards();

            gemini.getResponse(promptPergunta, new GeminiConfig.GeminiCallback() {
                @Override
                public void onResponse(String questionResponse) {
                    runOnUiThread(() -> {
                        // Filtra respostas inesperadas
                        if (questionResponse.startsWith("{") || questionResponse.contains("error")) {
                            txtPergunta.setText("Erro: resposta inválida da API");
                            Log.e("API_RESPONSE", questionResponse);
                            btSalvarFlashcard.setEnabled(true); // Re-enable in case of error
                            btGerarComIa.setEnabled(true);       // Re-enable in case of error
                        } else {
                            txtPergunta.setText(questionResponse);

                            // Now, make the second API call for the answer
                            String promptResposta = "Estou te utilizando como api em um projeto, então quero que vc apenas retorne oq eu pedir, sem vc falar nada. Estou gerando um flashcard, onde tem uma pergunta curta e uma resposta curta, nesse prompt quero que vc retorne apenas a resposta sobre essa pergunta (Lembre-se, tem que ser resposta curta com no máximo 130 caracteres!!) : " + questionResponse;

                            gemini.getResponse(promptResposta, new GeminiConfig.GeminiCallback() {
                                @Override
                                public void onResponse(String answerResponse) {
                                    runOnUiThread(() -> {
                                        btSalvarFlashcard.setEnabled(true);
                                        btGerarComIa.setEnabled(true);
                                        // Filtra respostas inesperadas
                                        if (answerResponse.startsWith("{") || answerResponse.contains("error")) {
                                            txtResposta.setText("Erro: resposta inválida da API");
                                            Log.e("API_RESPONSE", answerResponse);
                                        } else {
                                            txtResposta.setText(answerResponse);
                                        }
                                    });
                                }

                                @Override
                                public void onError(String error) {
                                    runOnUiThread(() -> {
                                        btSalvarFlashcard.setEnabled(true); // Re-enable in case of error
                                        btGerarComIa.setEnabled(true);
                                        txtResposta.setText("Erro: " + error); // Update the answer field with the error
                                        Toast.makeText(Notes.this, error, Toast.LENGTH_LONG).show();
                                        Log.e("API_ERROR", error);
                                    });
                                }
                            });
                        }
                    });
                }

                @Override
                public void onError(String error) {
                    runOnUiThread(() -> {
                        btSalvarFlashcard.setEnabled(true); // Re-enable in case of error
                        btGerarComIa.setEnabled(true);       // Re-enable in case of error
                        txtPergunta.setText("Erro: " + error);
                        Toast.makeText(Notes.this, error, Toast.LENGTH_LONG).show();
                        Log.e("API_ERROR", error);
                    });
                }
            });
        });

        fundoPopup.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Rect outRect = new Rect();
                criarFlashcard.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    fundoPopup.setVisibility(View.GONE);
                    conteudoPrincipal.animate().alpha(1f).setDuration(200).start();
                }
            }
            return true;
        });
        txtPergunta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Conta as quebras de linha
                int lines = txtPergunta.getLineCount();
                if (lines > 3) {
                    // Remove o último caractere digitado
                    txtPergunta.setText(s.subSequence(0, s.length() - 1));
                    txtPergunta.setSelection(txtPergunta.getText().length()); // move o cursor pro final
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        txtResposta.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Conta as quebras de linha
                int lines = txtResposta.getLineCount();
                if (lines > 5) {
                    // Remove o último caractere digitado
                    txtResposta.setText(s.subSequence(0, s.length() - 1));
                    txtResposta.setSelection(txtResposta.getText().length()); // move o cursor pro final
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }
    private List<String> consultaTodasAsPerguntasDosFlashcards() {
        List<String> lista = new LinkedList<String>();


        BancoControllerCard bd = new BancoControllerCard(getBaseContext());
        Cursor dados = bd.carregaFlashcards();


        if (dados != null && dados.moveToFirst()) {
            do {
                String item;
                item = dados.getString(1);
                lista.add(item);
            } while (dados.moveToNext());
        }else{
            String msg = "Não há perguntas de flashcard criada";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        return lista;
    }

    private void recuperarPagina(){
        BancoControllerNote bd = new BancoControllerNote(getBaseContext());
        Cursor dados = bd.carregaDadosPeloId(idPagina);
        NotaModel pag = new NotaModel();
        pag.setId(dados.getInt(0));
        pag.setTitulo(dados.getString(1));
        pag.setConteudo(dados.getString(2));
        pag.setIdCaderno(dados.getInt(3));

        BancoControllerCaderno bd2 = new BancoControllerCaderno(getBaseContext());
        Cursor dados2 = bd2.carregaDadosPeloId(pag.getIdCaderno());
        CadernoModel cad = new CadernoModel();
        cad.setId(dados2.getInt(0));
        cad.setNome(dados2.getString(1));
        dados.close();

        nomeCadernoNote = findViewById(R.id.nomeCadernoNote);
        nomeCadernoNote.setText("Caderno: " + cad.getNome());

        txtTituloPagina = findViewById(R.id.txtTituloPagina);
        txtTituloPagina.setText(pag.getTitulo());

        txtTitulo = findViewById(R.id.txtTitulo);
        txtTitulo.setText(pag.getTitulo());

        txtConteudo = findViewById(R.id.txtConteudo);
        txtConteudo.setText(pag.getConteudo());
    }

    private void salvarNoBanco() {
        String novoTitulo = txtTitulo.getText().toString();
        String novoConteudo = txtConteudo.getText().toString();

        BancoControllerNote bd = new BancoControllerNote(this);
        bd.atualizarNota(idPagina, novoTitulo, novoConteudo, dataFormatada);
    }

}