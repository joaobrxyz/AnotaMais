package com.example.anotamais;

import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executor;

public class GeminiConfig {
    private final GenerativeModelFutures model;
    private final Executor executor;

    public GeminiConfig(String apiKey) {
        GenerativeModel gm = new GenerativeModel("gemini-2.0-flash-lite", apiKey);
        this.model = GenerativeModelFutures.from(gm);
        this.executor = Runnable::run;
    }

    public void getResponse(String prompt, GeminiCallback callback) {
        try {
            Content content = new Content.Builder()
                    .addText(prompt)
                    .build();

            ListenableFuture<GenerateContentResponse> response = model.generateContent(content);

            Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
                @Override
                public void onSuccess(GenerateContentResponse result) {
                    try {
                        String text = result.getText();
                        if (text == null || text.isEmpty()) {
                            callback.onError("Resposta vazia da API");
                        } else {
                            callback.onResponse(text);
                        }
                    } catch (Exception e) {
                        callback.onError("Erro ao processar resposta: " + e.getMessage());
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    callback.onError("Falha na requisição: " + t.getMessage());
                }
            }, executor);

        } catch (Exception e) {
            callback.onError("Erro ao criar requisição: " + e.getMessage());
        }
    }

    public interface GeminiCallback {
        void onResponse(String response);
        void onError(String error);
    }
}