package com.example.anotamais;

public class Nota {

    public Integer id;
    public String titulo;
    public String conteudo;
    public int idCaderno;

    public Nota(Integer id, String titulo, String conteudo, int idCaderno) {
        this.id = id;
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.idCaderno = idCaderno;
    }
}
