package com.example.anotamais.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anotamais.database.CriaBanco;

public class BancoControllerUsuario {
    private SQLiteDatabase db;
    private CriaBanco banco;

    public BancoControllerUsuario(Context context) {
        banco = new CriaBanco(context);
    }


    public String insereDados(String txtName) {
        ContentValues valores;
        long resultado;
        db = banco.getWritableDatabase();

        valores = new ContentValues();
        valores.put("name", txtName);
        resultado = db.insert("usuario", null, valores);
        db.close();

        if (resultado == -1)
            return "Erro ao inserir registro";
        else
            return "Registro Inserido com sucesso";
    }

    public Cursor carregaDadosPeloId() {
        Cursor cursor;
        String[] campos = { "id", "name" };
        String where = "id=1";
        db = banco.getReadableDatabase();
        cursor = db.query("usuario", campos, where, null, null, null,
                null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        db.close();
        return cursor;
    }

    public String alteraDados(String name){

        String msg = "Dados alterados com sucesso!!!" ;

        db = banco.getReadableDatabase();

        ContentValues valores = new ContentValues() ;
        valores.put("name" , name ) ;

        String condicao = "id = 1";

        int linha ;
        linha = db.update("usuario", valores, condicao, null) ;

        if (linha < 1){
            msg = "Erro ao alterar os dados" ;
        }

        db.close();
        return msg;
    }

    public boolean verificaIdExiste() {
        db = banco.getReadableDatabase();
        String query = "SELECT * FROM usuario WHERE id = 1";
        Cursor cursor = db.rawQuery(query, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return existe;
    }
}
