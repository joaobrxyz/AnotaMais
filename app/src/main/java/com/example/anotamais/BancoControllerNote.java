package com.example.anotamais;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class BancoControllerNote {
    private SQLiteDatabase db;
    private CriaBanco banco;

    public BancoControllerNote(Context context) {
        banco = new CriaBanco(context);
    }


    public String insereDados(String titulo, String conteudo, int idCaderno) {
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put("titulo", titulo);
        valores.put("conteudo", conteudo);
        valores.put("id_caderno", idCaderno);
        resultado = db.insert("caderno", null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir registro";
        else
            return "Registro Inserido com sucesso";
    }

    public Cursor carregaDadosPeloId(int id) {
        Cursor cursor;
        String[] campos = { "titulo", "conteudo", "id_caderno" };
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

    public String alteraDados(int id, String titulo, String conteudo){

        String msg = "Dados alterados com sucesso!!!" ;

        db = banco.getReadableDatabase();

        ContentValues valores = new ContentValues() ;
        valores.put("titulo" , titulo);
        valores.put("conteudo" , conteudo);

        String condicao = "id = " + id;

        int linha ;
        linha = db.update("caderno", valores, condicao, null) ;

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
        linhas = db.delete("caderno", condicao, null) ;

        if ( linhas < 1) {
            msg = "Erro ao Excluir" ;
        }

        db.close();
        return msg;
    }
}