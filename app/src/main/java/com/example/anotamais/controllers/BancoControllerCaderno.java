package com.example.anotamais.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anotamais.database.CriaBanco;

public class BancoControllerCaderno {
    private SQLiteDatabase db;
    private CriaBanco banco;

    public BancoControllerCaderno(Context context) {
        banco = new CriaBanco(context);
    }


    public String insereDados(String txtName) {
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put("name", txtName);
        resultado = db.insert("caderno", null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir registro";
        else
            return "Registro Inserido com sucesso";
    }

    public Cursor carregaDadosPeloNome(String name) {
        Cursor cursor;
        String[] campos = { "name" };
        String where = "name="+name;
        db = banco.getReadableDatabase();
        cursor = db.query("caderno", campos, where, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }

    public String alteraDados(String name, String newName){

        String msg = "Dados alterados com sucesso!!!" ;

        db = banco.getReadableDatabase();

        ContentValues valores = new ContentValues() ;
        valores.put("name" , newName ) ;

        String condicao = "name = " + name;

        int linha ;
        linha = db.update("caderno", valores, condicao, null) ;

        if (linha < 1){
            msg = "Erro ao alterar os dados" ;
        }

        db.close();
        return msg;
    }

    public boolean favoritarCaderno(long id, int favorito) {
        db = banco.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("favorito", favorito);

        int linhasAfetadas = db.update("caderno", valores, "id = ?", new String[]{String.valueOf(id)});

        db.close();

        return linhasAfetadas > 0;
    }


    public boolean excluirCaderno(long id) {
        SQLiteDatabase db = banco.getWritableDatabase();
        int linhasAfetadasCard = db.delete("card", "id_caderno = ?", new String[]{String.valueOf(id)});
        int linhasAfetadasNote = db.delete("note", "id_caderno = ?", new String[]{String.valueOf(id)});
        int linhasAfetadasCaderno = db.delete("caderno", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return linhasAfetadasCaderno + linhasAfetadasNote + linhasAfetadasCard > 0;
    }

    public Cursor carregaDadosPeloId(int id) {
        Cursor cursor;
        String[] campos = {"id", "name" };
        String where = "id="+id;
        db = banco.getReadableDatabase();
        cursor = db.query("caderno", campos, where, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }

    public Cursor listarCadernos(Boolean filtrarFavoritos) {
        Cursor cursor;
        String[] campos = { "id", "name", "favorito" };
        db = banco.getReadableDatabase();
        if (filtrarFavoritos) {
            cursor = db.query("caderno", campos, "favorito = 1", null, null, null,
                    "favorito DESC", null);
        } else {
            cursor = db.query("caderno", campos, null, null, null, null,
                    "favorito DESC", null);
        }
        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

    public Cursor listarCadernosComFlashcards() {
        db = banco.getReadableDatabase();

        String query = "SELECT DISTINCT c.id AS _id, c.name " +
                "FROM caderno c " +
                "JOIN note n ON n.id_caderno = c.id " +
                "JOIN card f ON f.id_note = n.id";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }

}