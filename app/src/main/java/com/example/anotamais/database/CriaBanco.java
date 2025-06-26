package com.example.anotamais.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CriaBanco extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "anotamais.db";
    private static final int VERSAO = 5;
    public CriaBanco(Context context) {
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE usuario ("
                + "id integer primary key autoincrement,"
                + "uid text unique,"
                + "remoteId text unique,"
                + "updatedAt INTEGER DEFAULT 0,"
                + "name text)";
        db.execSQL(sql);

        sql = "CREATE TABLE caderno ("
                + "id integer primary key autoincrement,"
                + "remoteId text unique,"
                + "updatedAt INTEGER DEFAULT 0,"
                + "name text,"
                + "userId text,"
                + "deleted INTEGER DEFAULT 0,"
                + "favorito BOOLEAN NOT NULL DEFAULT FALSE)";
        db.execSQL(sql);

        sql = "CREATE TABLE note ("
                + "id integer primary key autoincrement,"
                + "remoteId text unique,"
                + "updatedAt INTEGER DEFAULT 0,"
                + "titulo text,"
                + "conteudo text,"
                + "data text,"
                + "userId text,"
                + "deleted INTEGER DEFAULT 0,"
                + "remoteId_caderno text,"
                + "FOREIGN KEY (remoteId_caderno) REFERENCES caderno(remoteId))";
        db.execSQL(sql);

        sql = "CREATE TABLE card ("
                + "id integer primary key autoincrement,"
                + "remoteId text unique,"
                + "updatedAt INTEGER DEFAULT 0,"
                + "pergunta text,"
                + "resposta text,"
                + "deleted INTEGER DEFAULT 0,"
                + "userId text,"
                + "remoteId_note text,"
                + "remoteId_caderno text,"
                + "FOREIGN KEY (remoteId_note) REFERENCES note(remoteId),"
                + "FOREIGN KEY (remoteId_caderno) REFERENCES caderno(remoteId))";
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