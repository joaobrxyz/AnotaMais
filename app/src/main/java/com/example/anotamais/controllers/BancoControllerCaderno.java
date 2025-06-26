package com.example.anotamais.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anotamais.database.CriaBanco;

import java.util.UUID;

public class BancoControllerCaderno {
    private SQLiteDatabase db;
    private CriaBanco banco;
    private Context context;

    public BancoControllerCaderno(Context context) {
        this.context = context;
        banco = new CriaBanco(context);
    }


    public String insereDados(String txtName) {
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        String remoteId = UUID.randomUUID().toString();
        long updatedAt = System.currentTimeMillis();

        valores = new ContentValues();
        valores.put("name", txtName);
        valores.put("remoteId", remoteId);
        valores.put("updatedAt", updatedAt);
        resultado = db.insert("caderno", null, valores);
        db.close();

        SincronizacaoController.sincronizarSeLogado(context, false, 0);

        if (resultado == -1)
            return "Erro ao inserir registro";
        else
            return "Caderno Cadastrado com sucesso";
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

        long updatedAt = System.currentTimeMillis();

        ContentValues valores = new ContentValues();
        valores.put("favorito", favorito);
        valores.put("updatedAt", updatedAt);

        int linhasAfetadas = db.update("caderno", valores, "id = ?", new String[]{String.valueOf(id)});

        db.close();

        SincronizacaoController.sincronizarSeLogado(context, true, 30);

        return linhasAfetadas > 0;
    }


    public boolean excluirCaderno(long id) {
        SQLiteDatabase db = banco.getWritableDatabase();
        long now = System.currentTimeMillis();

        ContentValues values = new ContentValues();
        values.put("deleted", 1);
        values.put("updatedAt", now);

        int linhasAfetadasCard = db.update("card", values, "remoteId_caderno = ?", new String[]{String.valueOf(id)});
        int linhasAfetadasNote = db.update("note", values, "remoteId_caderno = ?", new String[]{String.valueOf(id)});
        int linhasAfetadasCaderno = db.update("caderno", values, "id = ?", new String[]{String.valueOf(id)});

        db.close();

        SincronizacaoController.sincronizarSeLogado(context, false, 0);

        return linhasAfetadasCaderno + linhasAfetadasNote + linhasAfetadasCard > 0;
    }


    public Cursor carregaDadosPeloId(int id) {
        Cursor cursor;
        String[] campos = {"id", "name", "remoteId" };
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

    public Cursor carregaDadosPeloRemoteId(String remoteId) {
        Cursor cursor;
        String[] campos = {"id", "name", "remoteId" };
        String where = "remoteId='"+remoteId+"'";
        db = banco.getReadableDatabase();
        cursor = db.query("caderno", campos, where, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

    public Cursor listarCadernos(Boolean filtrarFavoritos) {
        Cursor cursor;
        String[] campos = { "id", "name", "favorito" };
        db = banco.getReadableDatabase();
        if (filtrarFavoritos) {
            cursor = db.query("caderno", campos, "favorito = 1 AND deleted = 0", null, null, null,
                    "favorito DESC", null);
        } else {
            cursor = db.query("caderno", campos, "deleted = 0", null, null, null,
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
                "JOIN note n ON n.remoteId_caderno = c.remoteId " +
                "JOIN card f ON f.remoteId_note = n.remoteId " +
                "WHERE c.deleted = 0 AND n.deleted = 0 AND f.deleted = 0";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

}