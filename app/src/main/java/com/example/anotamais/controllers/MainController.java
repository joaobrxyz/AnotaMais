package com.example.anotamais.controllers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.anotamais.adapters.CadernoRecyclerAdapter;
import com.example.anotamais.models.CadernoModel;
import java.util.LinkedList;
import java.util.List;

public class MainController {

    /**
     * Consulta todos os cadernos no banco de dados, incluindo se são favoritos ou não.
     */
    public static List<CadernoModel> consultaTodosCadernos(Context context) {
        List<CadernoModel> cadernos = new LinkedList<>();

        BancoControllerCaderno bd = new BancoControllerCaderno(context);
        Cursor dados = bd.listarCadernos(false);

        if (dados != null && dados.moveToFirst()) {
            do {
                CadernoModel caderno = new CadernoModel();
                caderno.setId(dados.getInt(0));
                caderno.setNome(dados.getString(1));
                caderno.setFavorito(dados.getInt(2) == 1);
                cadernos.add(caderno);
            } while (dados.moveToNext());
            dados.close();
        }

        return cadernos;
    }

    /**
     * Configura o RecyclerView com os cadernos listados e ajusta layout conforme altura da tela.
     */
    public static void listarCadernos(Context context, RecyclerView listaCaderno, LinearLayout rodapeMain) {
        List<CadernoModel> cadernos = consultaTodosCadernos(context);

        // Ajusta layout para a altura da tela e define a quantidade de colunas visíveis
        int spanCount = adjustLayoutForScreenHeight(context, rodapeMain);

        GridLayoutManager layoutManager = new GridLayoutManager(context, spanCount, GridLayoutManager.HORIZONTAL, false);
        listaCaderno.setLayoutManager(layoutManager);

        // Cria o adapter para exibir os cadernos
        CadernoRecyclerAdapter adapter = new CadernoRecyclerAdapter(context, cadernos);

        // Atualiza a lista quando algum caderno for marcado/desmarcado como favorito
        adapter.setOnCadernoFavoritoChangeListener(() ->
                new Handler(Looper.getMainLooper()).post(() ->
                        listarCadernos(context, listaCaderno, rodapeMain)
                )
        );

        listaCaderno.setAdapter(adapter);
    }

    /**
     * Cria um novo caderno com o nome informado, atualiza UI e lista.
     */
    public static void criarCaderno(Context context, String nomeCaderno, EditText txtCaderno,
                                    FrameLayout fundoPopupMain, LinearLayout newCaderno, Runnable atualizarLista) {
        if (!nomeCaderno.isEmpty()) {
            BancoControllerCaderno bancoControllerCaderno = new BancoControllerCaderno(context);
            String msg = bancoControllerCaderno.insereDados(nomeCaderno);
            Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

            // Esconde o teclado
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(txtCaderno.getWindowToken(), 0);

            // Esconde o popup
            fundoPopupMain.setVisibility(View.GONE);
            newCaderno.setVisibility(View.GONE);

            // Atualiza a lista de cadernos após pequeno delay para garantir sincronização
            new Handler(Looper.getMainLooper()).postDelayed(atualizarLista, 200);
        } else {
            Toast.makeText(context, "Digite um nome para o caderno", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Abre a Activity desejada a partir do contexto atual.
     */
    public static void abrirTela(Context context, Class<?> destino) {
        Intent intent = new Intent(context, destino);
        context.startActivity(intent);
    }

    /**
     * Listener para fechar popup ao tocar fora da área visível do popup.
     * Também esconde teclado e atualiza lista após fechamento.
     */
    public static View.OnTouchListener fecharPopupAoTocarFora(Context context, FrameLayout fundoPopupMain,
                                                              LinearLayout newCaderno, EditText txtCaderno,
                                                              Runnable atualizarLista) {
        return (v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.performClick();

                // Esconde popup e teclado
                fundoPopupMain.setVisibility(View.GONE);
                newCaderno.setVisibility(View.GONE);
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(txtCaderno.getWindowToken(), 0);

                // Atualiza lista após delay
                new Handler(Looper.getMainLooper()).postDelayed(atualizarLista, 200);
                return true;
            }
            return false;
        };
    }

    /**
     * Animação simples de fade in para entrada do popup de novo caderno.
     * Também limpa o campo de texto para novo nome.
     */
    public static void animarEntradaNovoCaderno(View newCaderno, EditText txtCaderno) {
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setDuration(300);
        newCaderno.setVisibility(View.VISIBLE);
        newCaderno.startAnimation(fadeIn);
        txtCaderno.setText("");
    }

    /**
     * Ajusta o layout baseado na altura da tela (em dp), aplicando margens no rodapé e
     * retorna a quantidade de colunas (rowCount) que será usada para o RecyclerView.
     */
    public static int adjustLayoutForScreenHeight(Context context, LinearLayout footerLayout) {
        // Obtem dados da tela
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        // Margens em dp para diferentes tamanhos de tela
        int defaultMarginDp = 10;
        int largeScreenMarginDp = 40;

        int finalMarginBottomPx;
        int rowCount;

        // Define rowCount e margem inferior conforme a altura da tela em dp
        if (dpHeight >= 1520) {
            rowCount = 6;
            finalMarginBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, largeScreenMarginDp, displayMetrics);
        } else if (dpHeight >= 1320) {
            rowCount = 5;
            finalMarginBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, largeScreenMarginDp, displayMetrics);
        } else if (dpHeight >= 1120) {
            rowCount = 4;
            finalMarginBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, largeScreenMarginDp, displayMetrics);
        } else if (dpHeight >= 920) {
            rowCount = 3;
            finalMarginBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, largeScreenMarginDp, displayMetrics);
        } else if (dpHeight >= 883) {
            rowCount = 2;
            finalMarginBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, largeScreenMarginDp, displayMetrics);
        } else {
            rowCount = 2;
            finalMarginBottomPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, defaultMarginDp, displayMetrics);
        }

        // Aplica margem inferior no rodapé se existir
        if (footerLayout != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) footerLayout.getLayoutParams();
            if (layoutParams != null) {
                layoutParams.bottomMargin = finalMarginBottomPx;
                footerLayout.setLayoutParams(layoutParams);
            }
        }

        return rowCount;
    }
}
