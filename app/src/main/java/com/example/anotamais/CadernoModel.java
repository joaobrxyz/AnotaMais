package com.example.anotamais;

public class CadernoModel {
    // modelo da tabela agendamento
    String nome;
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

}