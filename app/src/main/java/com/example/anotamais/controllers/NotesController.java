package com.example.anotamais.controllers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.example.anotamais.R;
import com.example.anotamais.activities.Caderno;
import com.example.anotamais.activities.Flashcards;
import com.example.anotamais.config.GeminiConfig;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class NotesController {

    static Calendar calendar = Calendar.getInstance();
    static SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    static String dataFormatada = sdf.format(calendar.getTime());

    // Interface para callbacks de finalização (ex: após salvar flashcard)
    public interface Callback {
        void onComplete();
    }

    // Método para voltar da tela de notas para a tela do caderno
    public static void voltarParaCaderno(Context context, int idCaderno, String nomeCaderno) {
        Intent intent = new Intent(context, Caderno.class);
        intent.putExtra("idCaderno", idCaderno);
        intent.putExtra("nomeCaderno", nomeCaderno);
        context.startActivity(intent);
    }

    // Exibe um menu popup com opções: nova página, flashcards, excluir anotação
    public static void abrirMenuOpcoes(Context context, View anchor, int idPagina, int idCaderno, String nomeCaderno, String remoteIdCaderno, String remoteIdNote) {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_opcoes_notes, popup.getMenu());

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.opcao_nova_pagina) {
                criarNovaPagina(context, remoteIdCaderno , nomeCaderno, dataFormatada, idCaderno);
                return true;
            } else if (id == R.id.opcao_flashcards) {
                visualizarFlashcards(context, idPagina, idCaderno, nomeCaderno);
                return true;
            } else if (id == R.id.opcao_excluir) {
                // Confirma exclusão com diálogo
                new androidx.appcompat.app.AlertDialog.Builder(context)
                        .setTitle("Confirmar exclusão")
                        .setMessage("Tem certeza que deseja excluir essa anotação?")
                        .setPositiveButton("Sim", (dialog, which) -> {
                            excluirAnotacao(context, idPagina, idCaderno, remoteIdNote);
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            }
            return false;
        });

        popup.show();
    }

    // Recupera os dados da página (título, conteúdo) do banco e atualiza os campos da UI
    public static void recuperarPagina(Context context, int idPagina, int idCaderno, String nomeCaderno,
                                       EditText txtTitulo, EditText txtConteudo,
                                       TextView txtTituloPagina, TextView nomeCadernoNote) {
        BancoControllerNote bd = new BancoControllerNote(context);
        Cursor dados = bd.carregaDadosPeloId(idPagina);
        if (dados != null && dados.moveToFirst()) {
            String titulo = dados.getString(1);
            String conteudo = dados.getString(2);
            dados.close();

            txtTitulo.setText(titulo);
            txtConteudo.setText(conteudo);
            txtTituloPagina.setText(titulo);
            nomeCadernoNote.setText("Caderno: " + nomeCaderno);
        }
    }

    // Atualiza o título e conteúdo da nota no banco de dados
    public static void salvarNota(Context context, int idPagina, String novoTitulo, String novoConteudo) {
        BancoControllerNote bd = new BancoControllerNote(context);
        bd.atualizarNota(idPagina, novoTitulo, novoConteudo, dataFormatada);
    }

    // Cria uma nova página (nota) com valores padrão e abre ela para edição
    public static void criarNovaPagina(Context context, String remoteIdCaderno, String nomeCaderno, String data, int idCaderno) {
        BancoControllerNote bd = new BancoControllerNote(context);
        long id = bd.insereDados("Título da Página", "Conteúdo da Página", remoteIdCaderno, data);
        if (id == -1) {
            Toast.makeText(context, "Erro ao criar nova página", Toast.LENGTH_LONG).show();
        } else {
            Intent intent = new Intent(context, com.example.anotamais.activities.Notes.class);
            intent.putExtra("idPagina", (int) id);
            intent.putExtra("idCaderno", idCaderno);
            intent.putExtra("nomeCaderno", nomeCaderno);
            intent.putExtra("remoteIdCaderno", remoteIdCaderno);
            context.startActivity(intent);
        }
    }

    // Abre a tela de flashcards referente a uma página e caderno específicos
    public static void visualizarFlashcards(Context context, int idPagina, int idCaderno, String nomeCaderno) {
        Intent intent = new Intent(context, Flashcards.class);
        intent.putExtra("idPagina", idPagina);
        intent.putExtra("idCaderno", idCaderno);
        intent.putExtra("nomeCaderno", nomeCaderno);
        context.startActivity(intent);
    }

    // Exclui uma anotação e fecha a tela atual retornando para o caderno
    public static void excluirAnotacao(Context context, int idPagina, int idCaderno, String remoteIdNote) {
        BancoControllerNote bd = new BancoControllerNote(context);
        bd.excluirNota(idPagina, remoteIdNote);
        // Se o contexto for uma Activity, fecha ela
        if (context instanceof android.app.Activity) {
            ((android.app.Activity) context).finish();
        }
        voltarParaCaderno(context, idCaderno, null);
    }

    // Salva um flashcard no banco e chama callback após salvar
    public static void salvarFlashcard(Context context, String pergunta, String resposta,
                                       String remoteIdNote, String remoteIdCaderno, Callback callback) {
        BancoControllerCard bdCard = new BancoControllerCard(context);
        bdCard.insereDados(pergunta, resposta, remoteIdNote, remoteIdCaderno);
        if (callback != null) callback.onComplete();
    }

    // Gera flashcard automático usando a API Gemini (IA), atualiza UI e gerencia estados dos botões
    public static void gerarFlashcardComIA(Context context, GeminiConfig gemini, String textoConteudo,
                                           EditText txtPergunta, EditText txtResposta,
                                           Button btSalvarFlashcard, Button btGerarComIa) {
        // Valida texto do conteúdo
        if (textoConteudo == null || textoConteudo.isEmpty()) {
            Toast.makeText(context, "O conteúdo do texto está vazio. Insira pelo menos um texto.", Toast.LENGTH_LONG).show();
            return;
        }

        if (textoConteudo.length() < 150) {
            Toast.makeText(context, "O conteúdo do texto é muito curto. Insira pelo menos 150 caracteres.", Toast.LENGTH_LONG).show();
            return;
        }

        // Mostra mensagem de processamento e desabilita botões
        txtPergunta.setText("Processando...");
        txtResposta.setText("Processando...");
        btSalvarFlashcard.setEnabled(false);
        btGerarComIa.setEnabled(false);

        // Prompt para gerar pergunta via API
        String promptPergunta = "Estou te utilizando como api em um projeto, então quero que vc apenas retorne oq eu pedir, sem vc falar nada. " +
                "Estou gerando um flashcard, onde tem uma pergunta curta e uma resposta curta, nesse prompt quero que vc retorne apenas uma pergunta sobre essa aula " +
                "(Lembre-se, pergunta curta!! com no máximo 80 caracteres): " + textoConteudo + " (Faça perguntas diferentes dessas, pois já foram criadas: " +
                consultaTodasAsPerguntasDosFlashcards(context, null) + ")";

        gemini.getResponse(promptPergunta, new GeminiConfig.GeminiCallback() {
            @Override
            public void onResponse(String questionResponse) {
                if (questionResponse.startsWith("{") || questionResponse.contains("error")) {
                    // Caso erro na API, mostra na UI e reabilita botões
                    txtPergunta.post(() -> {
                        txtPergunta.setText("Erro: resposta inválida da API");
                        btSalvarFlashcard.setEnabled(true);
                        btGerarComIa.setEnabled(true);
                    });
                    Log.e("API_RESPONSE", questionResponse);
                    return;
                }
                txtPergunta.post(() -> txtPergunta.setText(questionResponse));

                // Prompt para gerar resposta via API
                String promptResposta = "Estou te utilizando como api em um projeto, então quero que vc apenas retorne oq eu pedir, sem vc falar nada. " +
                        "Estou gerando um flashcard, onde tem uma pergunta curta e uma resposta curta, nesse prompt quero que vc retorne apenas a resposta sobre essa pergunta " +
                        "(Lembre-se, tem que ser resposta curta com no máximo 130 caracteres!!) : " + questionResponse;

                gemini.getResponse(promptResposta, new GeminiConfig.GeminiCallback() {
                    @Override
                    public void onResponse(String answerResponse) {
                        txtResposta.post(() -> {
                            if (answerResponse.startsWith("{") || answerResponse.contains("error")) {
                                txtResposta.setText("Erro: resposta inválida da API");
                                Log.e("API_RESPONSE", answerResponse);
                            } else {
                                txtResposta.setText(answerResponse);
                            }
                            // Reabilita os botões
                            btSalvarFlashcard.setEnabled(true);
                            btGerarComIa.setEnabled(true);
                        });
                    }

                    @Override
                    public void onError(String error) {
                        // Tratamento de erro na resposta da API
                        txtResposta.post(() -> {
                            txtResposta.setText("Erro: " + error);
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                            Log.e("API_ERROR", error);
                            btSalvarFlashcard.setEnabled(true);
                            btGerarComIa.setEnabled(true);
                        });
                    }
                });
            }

            @Override
            public void onError(String error) {
                // Tratamento de erro na resposta da API para a pergunta
                txtPergunta.post(() -> {
                    txtPergunta.setText("Erro: " + error);
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show();
                    Log.e("API_ERROR", error);
                    btSalvarFlashcard.setEnabled(true);
                    btGerarComIa.setEnabled(true);
                });
            }
        });
    }

    // Consulta todas as perguntas dos flashcards para evitar repetição na geração automática
    private static List<String> consultaTodasAsPerguntasDosFlashcards(Context context, String remoteIdNote) {
        List<String> perguntas = new LinkedList<>();
        BancoControllerCard bd = new BancoControllerCard(context);
        Cursor cursor = bd.listarCards(null, remoteIdNote);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                perguntas.add(cursor.getString(1));
            } while (cursor.moveToNext());
            cursor.close();
        }
        return perguntas;
    }

    // Limita o número de linhas do campo pergunta a no máximo 3 linhas
    public static void limitarLinhasPergunta(EditText editText, CharSequence s) {
        int lines = editText.getLineCount();
        if (lines > 3) {
            editText.setText(s.subSequence(0, s.length() - 1));
            editText.setSelection(editText.getText().length());
        }
    }

    // Limita o número de linhas do campo resposta a no máximo 5 linhas
    public static void limitarLinhasResposta(EditText editText, CharSequence s) {
        int lines = editText.getLineCount();
        if (lines > 5) {
            editText.setText(s.subSequence(0, s.length() - 1));
            editText.setSelection(editText.getText().length());
        }
    }

    // Abre o popup para criação de flashcard, mostrando a tela e limpando campos
    public static void abrirPopupFlashcard(View fundoPopup, View conteudoPrincipal, EditText txtPergunta, EditText txtResposta) {
        fundoPopup.setVisibility(View.VISIBLE);
        conteudoPrincipal.animate().alpha(0.3f).setDuration(200).start();
        txtPergunta.setText("");
        txtResposta.setText("");
    }

    // Trata toque fora do popup para fechá-lo, com animação de volta para opacidade total
    public static boolean tratarToqueFundoPopup(MotionEvent event, View fundoPopup, View conteudoPrincipal, View criarFlashcard) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            Rect outRect = new Rect();
            criarFlashcard.getGlobalVisibleRect(outRect);
            if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                fundoPopup.setVisibility(View.GONE);
                conteudoPrincipal.animate().alpha(1f).setDuration(200).start();
                return true; // evento consumido, não propaga para trás
            }
        }
        return false; // evento não consumido, propaga normalmente
    }
}
