package com.example.anotamais;

import android.annotation.SuppressLint;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.List;

public class Notes extends AppCompatActivity {

    ImageButton btVoltarNotes, btFlashCard;
    Button btSalvarNotes, btCriarPagina, btGerarComIa, btSalvarFlashcard;
    EditText txtTitulo, txtConteudo, txtPergunta, txtResposta;
    String nomeUsuario;
    FrameLayout fundoPopup;
    LinearLayout criarFlashcard, conteudoPrincipal;
    GeminiConfig gemini;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);

        gemini = new GeminiConfig(BuildConfig.GEMINI_API_KEY);
        btVoltarNotes = findViewById(R.id.btVoltarNotes);
        //btSalvarNotes = findViewById(R.id.btSalvarNotes);
        btFlashCard = findViewById(R.id.btFlashCard);
        btGerarComIa = findViewById(R.id.btGerarComIa);
        conteudoPrincipal = findViewById(R.id.conteudoPrincipal);
        btSalvarFlashcard = findViewById(R.id.btSalvarFlashcard);
        txtTitulo = findViewById(R.id.txtTitulo);
        txtConteudo = findViewById(R.id.txtConteudo);
        txtPergunta = findViewById(R.id.txtPerguntaNotes);
        txtResposta = findViewById(R.id.txtRespostaNotes);
        fundoPopup = findViewById(R.id.fundoPopup);
        criarFlashcard = findViewById(R.id.criarFlashcard);

        Intent intentRecebida = getIntent();
        nomeUsuario = intentRecebida.getStringExtra("nomeUsuario");

        btVoltarNotes.setOnClickListener(v -> {
            finish();
        });

        /*btSalvarNotes.setOnClickListener(v -> {
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

            intent.putExtra("nomeUsuario", nomeUsuario);

            startActivity(intent);
            finish();

            Toast.makeText(this, "Caderno criado com sucesso!", Toast.LENGTH_SHORT).show();
        });*/
        btFlashCard.setOnClickListener(v -> {
            fundoPopup.setVisibility(View.VISIBLE);
            conteudoPrincipal.animate().alpha(0.3f).setDuration(200).start();
        });

        btSalvarFlashcard.setOnClickListener(v -> {
            BancoControllerCaderno bancoControllerCaderno = new BancoControllerCaderno(getBaseContext());
            bancoControllerCaderno.insereDados("Teste");

            BancoControllerNote bancoControllerNote = new BancoControllerNote(getBaseContext());
            bancoControllerNote.insereDados(txtTitulo.getText().toString(), txtConteudo.getText().toString(), 1);

            BancoControllerCard bancoControllerCard = new BancoControllerCard(getBaseContext());
            bancoControllerCard.insereDados(txtPergunta.getText().toString(), txtResposta.getText().toString(), 1);

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
    private List<FlashcardModel> consultaTodosFlashcards() {
        List<FlashcardModel> lista = new LinkedList<FlashcardModel>();


        BancoControllerCard bd = new BancoControllerCard(getBaseContext());
        Cursor dados = bd.carregaFlashcards();


        if (dados != null && dados.moveToFirst()) {
            do {
                FlashcardModel item = new FlashcardModel();
                item.setId(dados.getInt(0));
                item.setPergunta(dados.getString(1));
                item.setResposta(dados.getString(2));
                lista.add(item);
            } while (dados.moveToNext());
        }else{
            String msg = "Não há perguntas de flashcard criada";
            Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        }
        return lista;
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
}