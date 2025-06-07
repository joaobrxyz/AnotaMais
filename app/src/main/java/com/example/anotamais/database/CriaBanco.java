package com.example.anotamais.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CriaBanco extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "anotamais.db";
    private static final int VERSAO = 4;
    public CriaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE usuario ("
                + "id integer primary key autoincrement,"
                + "name text)";
        db.execSQL(sql);

        sql = "CREATE TABLE caderno ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "favorito BOOLEAN NOT NULL DEFAULT FALSE)";
        db.execSQL(sql);

        sql = "CREATE TABLE note ("
                + "id integer primary key autoincrement,"
                + "titulo text,"
                + "conteudo text,"
                + "data text,"
                + "id_caderno integer,"
                + "FOREIGN KEY (id_caderno) REFERENCES caderno(id))";
        db.execSQL(sql);

        sql = "CREATE TABLE card ("
                + "id integer primary key autoincrement,"
                + "pergunta text,"
                + "resposta text,"
                + "id_note integer,"
                + "id_caderno integer,"
                + "FOREIGN KEY (id_note) REFERENCES note(id),"
                + "FOREIGN KEY (id_caderno) REFERENCES caderno(id))";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuario");
        db.execSQL("DROP TABLE IF EXISTS caderno");
        db.execSQL("DROP TABLE IF EXISTS note");
        db.execSQL("DROP TABLE IF EXISTS card");
        onCreate(db);
    }
}