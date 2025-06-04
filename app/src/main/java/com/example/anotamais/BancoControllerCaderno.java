package com.example.anotamais;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

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

    public boolean excluirCaderno(long id) {
        SQLiteDatabase db = banco.getWritableDatabase();
        int linhasAfetadas = db.delete("caderno", "id = ?", new String[]{String.valueOf(id)});
        db.close();
        return linhasAfetadas > 0;
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

    public Cursor listarCadernos() {
        Cursor cursor;
        //SELECT idAgendamento, data, hora, email FROM agendamento
        String[] campos = { "id", "name" };
        db = banco.getReadableDatabase();
        cursor = db.query("caderno", campos, null, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }
        db.close();
        return cursor;
    }

}
