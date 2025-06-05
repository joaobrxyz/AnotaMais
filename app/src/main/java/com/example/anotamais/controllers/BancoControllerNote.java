package com.example.anotamais.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anotamais.database.CriaBanco;

public class BancoControllerNote {
    private SQLiteDatabase db;
    private CriaBanco banco;

    public BancoControllerNote(Context context) {
        banco = new CriaBanco(context);
    }


    public long insereDados(String titulo, String conteudo, int idCaderno) {
        ContentValues valores = new ContentValues();
        db = banco.getWritableDatabase();

        valores.put("titulo", titulo);
        valores.put("conteudo", conteudo);
        valores.put("id_caderno", idCaderno);

        long idInserido = db.insert("note", null, valores);
        db.close();

        return idInserido; // -1 se falhou, id válido se inseriu
    }


    public Cursor carregaDadosPeloId(int id) {
        Cursor cursor;
        String[] campos = { "id", "titulo", "conteudo", "id_caderno" };
        String where = "id="+id;
        db = banco.getReadableDatabase();
        cursor = db.query("note", campos, where, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }

    public boolean atualizarNota(int id, String titulo, String conteudo) {
        SQLiteDatabase db = banco.getWritableDatabase();
        ContentValues valores = new ContentValues();
        valores.put("titulo", titulo);
        valores.put("conteudo", conteudo);

        int linhasAfetadas = db.update("note", valores, "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return linhasAfetadas > 0;
    }

    public boolean excluirNota(long id) {
        SQLiteDatabase db = banco.getWritableDatabase();
        int linhasAfetadas = db.delete("note", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return linhasAfetadas > 0;
    }


    public Cursor listarNotes(int idCaderno) {
        Cursor cursor;
        String[] campos = { "id", "titulo", "conteudo", "id_caderno" };
        String where = "id_caderno="+idCaderno;
        db = banco.getReadableDatabase();
        cursor = db.query("note", campos, where, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }
}