package com.example.anotamais.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anotamais.database.CriaBanco;

import java.util.UUID;

public class BancoControllerNote {
    private SQLiteDatabase db;
    private CriaBanco banco;
    private Context context;

    public BancoControllerNote(Context context) {
        this.context = context;
        banco = new CriaBanco(context);
    }


    public long insereDados(String titulo, String conteudo, String remoteIdCaderno, String data) {
        ContentValues valores = new ContentValues();
        db = banco.getWritableDatabase();

        String remoteId = UUID.randomUUID().toString();
        long updatedAt = System.currentTimeMillis();

        valores.put("titulo", titulo);
        valores.put("conteudo", conteudo);
        valores.put("remoteId_caderno", remoteIdCaderno);
        valores.put("data", data);
        valores.put("remoteId", remoteId);
        valores.put("updatedAt", updatedAt);

        long idInserido = db.insert("note", null, valores);
        db.close();

        SincronizacaoController.sincronizarSeLogado(context, false, 0);

        return idInserido; // -1 se falhou, id válido se inseriu
    }


    public Cursor carregaDadosPeloId(int id) {
        Cursor cursor;
        String[] campos = { "id", "titulo", "conteudo", "remoteId_caderno", "remoteId" };
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

    public boolean atualizarNota(int id, String titulo, String conteudo, String data) {
        SQLiteDatabase db = banco.getWritableDatabase();

        long updatedAt = System.currentTimeMillis();

        ContentValues valores = new ContentValues();
        valores.put("titulo", titulo);
        valores.put("conteudo", conteudo);
        valores.put("data", data);
        valores.put("updatedAt", updatedAt);

        int linhasAfetadas = db.update("note", valores, "id = ?", new String[]{String.valueOf(id)});
        db.close();

        SincronizacaoController.sincronizarSeLogado(context, true, 30);

        return linhasAfetadas > 0;
    }

    public boolean excluirNota(long id, String remoteId) {
        SQLiteDatabase db = banco.getWritableDatabase();

        ContentValues valuesCard = new ContentValues();
        valuesCard.put("deleted", 1);
        valuesCard.put("updatedAt", System.currentTimeMillis());

        ContentValues valuesNote = new ContentValues();
        valuesNote.put("deleted", 1);
        valuesNote.put("updatedAt", System.currentTimeMillis());

        int linhasAfetadasCard = db.update("card", valuesCard, "remoteId_note = ?", new String[]{String.valueOf(id)});
        int linhasAfetadasNote = db.update("note", valuesNote, "id = ?", new String[]{String.valueOf(id)});

        db.close();

        SincronizacaoController.sincronizarSeLogado(context, false, 0);

        return linhasAfetadasNote + linhasAfetadasCard > 0;
    }


    public Cursor listarNotes(String remoteIdCaderno) {
        Cursor cursor;
        String[] campos = { "id", "titulo", "conteudo", "remoteId_caderno", "data" };
        String where = "remoteId_caderno = ? AND deleted = 0";
        String[] whereArgs = { String.valueOf(remoteIdCaderno) };

        db = banco.getReadableDatabase();
        cursor = db.query("note", campos, where, whereArgs, null, null, "data DESC", null);

        if (cursor != null) {
            cursor.moveToFirst();
        }

        return cursor;
    }

}