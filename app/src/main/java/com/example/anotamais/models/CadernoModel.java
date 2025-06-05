package com.example.anotamais.models;

public class CadernoModel {
    // modelo da tabela agendamento
    String nome;
    int id;
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
}