package com.example.anotamais;

public class ModelCaderno {
    // modelo da tabela agendamento
    String nome;
    //método construtor
    public ModelCaderno() {}
    public ModelCaderno(String nome){
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