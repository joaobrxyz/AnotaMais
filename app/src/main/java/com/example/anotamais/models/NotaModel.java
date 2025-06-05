package com.example.anotamais.models;

public class NotaModel {

    public Integer id;
    public String titulo;
    public String conteudo;
    public String nomeCaderno;
    public int idCaderno;

    public NotaModel(Integer id, String titulo, String conteudo, int idCaderno) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.idCaderno = idCaderno;
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

    public int getIdCaderno() {
        return idCaderno;
    }

    public void setIdCaderno(int idCaderno) {
        this.idCaderno = idCaderno;
    }

    public String getNomeCaderno() {
        return nomeCaderno;
    }

    public void setNomeCaderno(String nomeCaderno) {
        this.nomeCaderno = nomeCaderno;
    }
}