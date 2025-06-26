package com.example.anotamais.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.anotamais.database.CriaBanco;

import java.util.UUID;

public class BancoControllerUsuario {
    private SQLiteDatabase db;
    private CriaBanco banco;
    private Context context;

    public BancoControllerUsuario(Context context) {
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
        resultado = db.insert("usuario", null, valores);
        db.close();

        SincronizacaoController.sincronizarSeLogado(context, false, 0);

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

        long updatedAt = System.currentTimeMillis();

        ContentValues valores = new ContentValues() ;
        valores.put("name" , name );
        valores.put("updatedAt", updatedAt);

        String condicao = "id = 1";

        int linha ;
        linha = db.update("usuario", valores, condicao, null) ;

        if (linha < 1){
            msg = "Erro ao alterar os dados" ;
        }

        db.close();

        SincronizacaoController.sincronizarSeLogado(context, false, 0);

        return msg;
    }

    public boolean verificaIdExiste() {
        db = banco.getReadableDatabase();
        String query = "SELECT * FROM usuario WHERE id = 1";
        Cursor cursor = db.rawQuery(query, null);
        boolean existe = cursor.getCount() > 0;
        cursor.close();
        return existe;
    }

    public void salvarOuAtualizarUidUsuario(String uid) {
        db = banco.getWritableDatabase();

        ContentValues valores = new ContentValues();
        valores.put("uid", uid);
        valores.put("updatedAt", System.currentTimeMillis());

        db.update("usuario", valores, "id = 1", null);

        db.close();
    }

    public String getUidUsuario() {
        db = banco.getReadableDatabase();
        String uid = null;
        Cursor cursor = db.rawQuery("SELECT remoteId FROM usuario WHERE id = 1", null);
        if (cursor.moveToFirst()) {
            uid = cursor.getString(0);
        }
        cursor.close();
        db.close();
        return uid;
    }
}
