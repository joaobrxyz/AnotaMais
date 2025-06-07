package com.example.anotamais.models;

public class CadernoModel {
    // modelo da tabela agendamento
    String nome;
    int id;
    boolean favorito;

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
}