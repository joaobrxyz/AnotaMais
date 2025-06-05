
package com.example.anotamais;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BancoControllerCard {
    private SQLiteDatabase db;
    private CriaBanco banco;

    public BancoControllerCard(Context context) {
        banco = new CriaBanco(context);
    }


    public String insereDados(String pergunta, String resposta, int idNote) {
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put("pergunta", pergunta);
        valores.put("resposta", resposta);
        valores.put("id_note", idNote);
        resultado = db.insert("card", null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir registro";
        else
            return "Registro Inserido com sucesso";
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
        int linhasAfetadas = db.delete("card", "id = ?", new String[]{String.valueOf(id)});
        db.close();
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

    public Cursor listarCards() {
        Cursor cursor;
        String[] campos = { "id", "pergunta", "resposta", "id_note" };
        db = banco.getReadableDatabase();
        cursor = db.query("card", campos, null, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

}
