package com.example.anotamais.models;

public class FlashcardModel {
    private int id;
    private String remoteId;
    private String pergunta;
    private String resposta;
    private String remoteIdNote;
    private String remoteIdCaderno;
    private long updatedAt;
    private boolean deleted;
    private String userId;

    public FlashcardModel() {}

    public FlashcardModel(int id, String pergunta, String resposta, String remoteIdNote, String remoteIdCaderno) {
        this.id = id;
        this.pergunta = pergunta;
        this.resposta = resposta;
        this.remoteIdNote = remoteIdNote;
        this.remoteIdCaderno = remoteIdCaderno;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
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

    public String getRemoteIdNote() {
        return remoteIdNote;
    }

    public void setRemoteIdNote(String remoteIdNote) {
        this.remoteIdNote = remoteIdNote;
    }

    public String getRemoteIdCaderno() {
        return remoteIdCaderno;
    }

    public void setRemoteIdCaderno(String remoteIdCaderno) {
        this.remoteIdCaderno = remoteIdCaderno;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
