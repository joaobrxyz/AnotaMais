package com.example.anotamais.models;

public class NotaModel {

    public Integer id;
    private String remoteId;
    public String titulo;
    public String conteudo;
    public String nomeCaderno;
    public String data;
    public String remoteIdCaderno;
    private long updatedAt;
    private boolean deleted;
    public String userId;

    public NotaModel(Integer id, String titulo, String conteudo, String remoteIdCaderno) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.remoteIdCaderno = remoteIdCaderno;
    }

    public NotaModel() {

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public String getRemoteIdCaderno() {
        return remoteIdCaderno;
    }

    public void setRemoteIdCaderno(String remoteIdCaderno) {
        this.remoteIdCaderno = remoteIdCaderno;
    }

    public String getNomeCaderno() {
        return nomeCaderno;
    }

    public void setNomeCaderno(String nomeCaderno) {
        this.nomeCaderno = nomeCaderno;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
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