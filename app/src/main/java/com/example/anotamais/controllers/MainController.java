package com.example.anotamais.controllers;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class MainController {
    public static int adjustLayoutForScreenHeight(Context context, LinearLayout footerLayout) {
        // Calcula a altura da tela em Density-Independent Pixels (dp)
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;

        // Define as margens em DP
        int defaultMarginDp = 10;
        int largeScreenMarginDp = 40;

        // Variável para armazenar a margem inferior final em pixels
        int finalMarginBottomPx;

        int rowCount; // Variável para armazenar o valor de retorno do rowCount

        // --- LÓGICA DE DEFINIÇÃO DE rowCount E MARGEM ---
        if (dpHeight >= 1520) {
            rowCount = 6;
            finalMarginBottomPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    largeScreenMarginDp,
                    displayMetrics
            );
        } else if (dpHeight >= 1320) {
            rowCount = 5;
            finalMarginBottomPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    largeScreenMarginDp,
                    displayMetrics
            );
        } else if (dpHeight >= 1120) {
            rowCount = 4;
            finalMarginBottomPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    largeScreenMarginDp,
                    displayMetrics
            );
        } else if (dpHeight >= 920) {
            rowCount = 3;
            finalMarginBottomPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    largeScreenMarginDp, // Margem de 40dp para telas >= 920dp
                    displayMetrics
            );

        } else if (dpHeight >= 883) {
            rowCount = 2;
            finalMarginBottomPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    largeScreenMarginDp, // Margem de 40dp para telas >= 883dp
                    displayMetrics
            );
        } else {
            // Para celulares pequenos e médios (abaixo de 883 dp)
            rowCount = 2;
            finalMarginBottomPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    defaultMarginDp, // Margem padrão de 10dp
                    displayMetrics
            );
        }

        // --- APLICAÇÃO DA MARGEM AO RODAPÉ PASSADO ---
        // Aplica a margem inferior calculada APENAS ao footerLayout fornecido.
        // É importante verificar se ele não é null, pois algumas telas podem não ter um rodapé.
        if (footerLayout != null) {
            ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) footerLayout.getLayoutParams();
            if (layoutParams != null) { // Verificação adicional para garantir que LayoutParams não é null
                layoutParams.bottomMargin = finalMarginBottomPx;
                footerLayout.setLayoutParams(layoutParams);
            }
        }

        return rowCount; // Retorna o rowCount para a Activity usar se precisar
    }
}
