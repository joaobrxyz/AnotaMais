
package com.example.anotamais.models;
public class FlashcardModel {
    private int id;
    private String pergunta;
    private String resposta;
    private int paginaId;

    public FlashcardModel() {
    }

    public FlashcardModel(int id, String pergunta, String resposta) {
        this.id = id;
        this.pergunta = pergunta;
        this.resposta = resposta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPergunta() {
        return pergunta;
    }

    public void setPergunta(String pergunta) {
        this.pergunta = pergunta;
    }

    public String getResposta() {
        return resposta;
    }

    public void setResposta(String resposta) {
        this.resposta = resposta;
    }

    public int getPaginaId() {
        return paginaId;
    }

    public void setPaginaId(int paginaId) {
        this.paginaId = paginaId;
    }

}
