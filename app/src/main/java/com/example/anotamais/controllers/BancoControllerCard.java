
package com.example.anotamais.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anotamais.database.CriaBanco;

import java.util.UUID;

public class BancoControllerCard {
    private SQLiteDatabase db;
    private CriaBanco banco;
    private Context context;

    public BancoControllerCard(Context context) {
        this.context = context;
        banco = new CriaBanco(context);
    }


    public String insereDados(String pergunta, String resposta, String remoteIdNote, String remoteIdCaderno) {
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        String remoteId = UUID.randomUUID().toString();
        long updatedAt = System.currentTimeMillis();

        valores = new ContentValues();
        valores.put("pergunta", pergunta);
        valores.put("resposta", resposta);
        valores.put("remoteId_note", remoteIdNote);
        valores.put("remoteId_caderno", remoteIdCaderno);
        valores.put("remoteId", remoteId);
        valores.put("updatedAt", updatedAt);
        resultado = db.insert("card", null, valores);
        db.close();

        SincronizacaoController.sincronizarSeLogado(context, false, 0);

        if (resultado == -1)
            return "Erro ao inserir registro";
        else
            return "Flashcard Inserido com sucesso";
    }

    public Cursor carregaDadosPeloId(int id) {
        Cursor cursor;
        String[] campos = { "pergunta", "resposta", "id_note" };
        String where = "id="+id;
        db = banco.getReadableDatabase();
        cursor = db.query("card", campos, where, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }

    public String alteraDados(int id, String pergunta, String resposta){

        String msg = "Dados alterados com sucesso!!!" ;

        db = banco.getReadableDatabase();

        ContentValues valores = new ContentValues() ;
        valores.put("pergunta" , pergunta);
        valores.put("resposta" , resposta);

        String condicao = "id = " + id;

        int linha ;
        linha = db.update("card", valores, condicao, null) ;

        if (linha < 1){
            msg = "Erro ao alterar os dados";
        }

        db.close();
        return msg;
    }

    public boolean excluirCard(long id) {
        SQLiteDatabase db = banco.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("deleted", 1);
        values.put("updatedAt", System.currentTimeMillis());

        int linhasAfetadas = db.update("card", values, "id = ?", new String[]{String.valueOf(id)});
        db.close();

        SincronizacaoController.sincronizarSeLogado(context, false, 0);

        return linhasAfetadas > 0;
    }

    public Cursor carregaFlashcards() {
        Cursor cursor;
        String[] campos = { "id", "pergunta", "resposta" };
        db = banco.getReadableDatabase();
        cursor = db.query("card", campos, null, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close(); // essa parte
        return cursor;
    }

    public Cursor listarCards(String remoteIdCaderno, String remoteIdNote) {
        Cursor cursor = null;
        String[] campos = { "id", "pergunta", "resposta", "remoteId_note", "remoteId_caderno" };
        db = banco.getReadableDatabase();

        try {
            if (remoteIdCaderno != null) {
                String where = "remoteId_caderno = ? AND deleted = 0";
                String[] whereArgs = { remoteIdCaderno };
                cursor = db.query("card", campos, where, whereArgs, null, null, null);
            } else if (remoteIdNote != null) {
                String where = "remoteId_note='"+remoteIdNote+"' AND deleted = 0";
                cursor = db.query("card", campos, where, null, null, null, null);
            } else {
                String where = "deleted = 0";
                cursor = db.query("card", campos, where, null, null, null, null);
            }

            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
            }

            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) cursor.close();
            return null;
        }
    }


}
