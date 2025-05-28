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
        resultado = db.insert("note", null, valores);
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
        cursor = db.query("note", campos, where, null, null, null,
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
        linha = db.update("note", valores, condicao, null) ;

        if (linha < 1){
            msg = "Erro ao alterar os dados";
        }

        db.close();
        return msg;
    }

    public String excluirDados(int id){
        String msg = "Registro Excluído";

        db = banco.getReadableDatabase();

        String condicao = "id = " + id;

        int linhas ;
        linhas = db.delete("note", condicao, null) ;

        if ( linhas < 1) {
            msg = "Erro ao Excluir" ;
        }

        db.close();
        return msg;
    }
}