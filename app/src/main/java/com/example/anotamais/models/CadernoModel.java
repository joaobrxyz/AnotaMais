package com.example.anotamais.models;

public class CadernoModel {
    // modelo da tabela agendamento
    String nome;
    int id;
    boolean favorito;
    private long updatedAt;
    private String remoteId;
    private boolean deleted;
    private String userId;

    //método construtor
    public CadernoModel() {}
    public CadernoModel(String nome){
        this.nome = nome;
    }
    //gets
    public String getNome() {
        return this.nome;
    }

    //sets
    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isFavorito() {
        return favorito;
    }

    public void setFavorito(boolean favorito) {
        this.favorito = favorito;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getRemoteId() {
        return remoteId;
    }

    public void setRemoteId(String remoteId) {
        this.remoteId = remoteId;
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