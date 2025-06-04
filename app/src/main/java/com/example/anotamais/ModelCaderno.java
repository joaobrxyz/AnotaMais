package com.example.anotamais;

public class ModelCaderno {
    // modelo da tabela agendamento
    int idCaderno;
    String nome;
    //método construtor
    public ModelCaderno() {}
    public ModelCaderno(int idCaderno, String nome){
        this.idCaderno = idCaderno;
        this.nome = nome;
    }

    public int getIdCaderno() {
        return idCaderno;
    }

    public void setIdCaderno(int idCaderno) {
        this.idCaderno = idCaderno;
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