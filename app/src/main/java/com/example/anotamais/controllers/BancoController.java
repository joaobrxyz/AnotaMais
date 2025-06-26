package com.example.anotamais.controllers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.anotamais.database.CriaBanco;
import com.example.anotamais.models.CadernoModel;
import com.example.anotamais.models.FlashcardModel;
import com.example.anotamais.models.NotaModel;
import com.example.anotamais.models.UserModel;
import java.util.ArrayList;
import java.util.List;

public class BancoController {
    private SQLiteDatabase db;
    private CriaBanco banco;

    public BancoController(Context context) {
        banco = new CriaBanco(context);
    }

    // Métodos que pegam somente os registros não deletados (deleted = 0)
    public List<UserModel> getAllUsers() {
        List<UserModel> users = new ArrayList<>();
        SQLiteDatabase db = banco.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuario", null);
        while (cursor.moveToNext()) {
            UserModel user = new UserModel();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
            user.setRemoteId(cursor.getString(cursor.getColumnIndexOrThrow("remoteId")));
            user.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updatedAt")));
            users.add(user);
        }
        cursor.close();
        return users;
    }

    public List<CadernoModel> getAllCadernos() {
        List<CadernoModel> cadernos = new ArrayList<>();
        SQLiteDatabase db = banco.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM caderno WHERE deleted = 0", null);
        while (cursor.moveToNext()) {
            CadernoModel caderno = montarCadernoModel(cursor);
            cadernos.add(caderno);
        }
        cursor.close();
        return cadernos;
    }

    public List<NotaModel> getAllNotas() {
        List<NotaModel> notas = new ArrayList<>();
        SQLiteDatabase db = banco.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM note WHERE deleted = 0", null);
        while (cursor.moveToNext()) {
            NotaModel nota = montarNotaModel(cursor);
            notas.add(nota);
        }
        cursor.close();
        return notas;
    }

    public List<FlashcardModel> getAllFlashcards() {
        List<FlashcardModel> flashcards = new ArrayList<>();
        SQLiteDatabase db = banco.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM card WHERE deleted = 0", null);
        while (cursor.moveToNext()) {
            FlashcardModel flashcard = montarFlashcardModel(cursor);
            flashcards.add(flashcard);
        }
        cursor.close();
        return flashcards;
    }

    // MÉTODOS QUE PEGAM TODOS OS REGISTROS, INCLUINDO DELETADOS (sem filtro deleted = 0)
    public List<UserModel> getAllUsersIncludeDeleted() {
        List<UserModel> users = new ArrayList<>();
        SQLiteDatabase db = banco.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM usuario", null);
        while (cursor.moveToNext()) {
            UserModel user = montarUserModel(cursor);
            users.add(user);
        }
        cursor.close();
        return users;
    }

    public List<CadernoModel> getAllCadernosIncludeDeleted() {
        List<CadernoModel> cadernos = new ArrayList<>();
        SQLiteDatabase db = banco.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM caderno", null);
        while (cursor.moveToNext()) {
            CadernoModel caderno = montarCadernoModel(cursor);
            cadernos.add(caderno);
        }
        cursor.close();
        return cadernos;
    }

    public List<NotaModel> getAllNotasIncludeDeleted() {
        List<NotaModel> notas = new ArrayList<>();
        SQLiteDatabase db = banco.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM note", null);
        while (cursor.moveToNext()) {
            NotaModel nota = montarNotaModel(cursor);
            notas.add(nota);
        }
        cursor.close();
        return notas;
    }

    public List<FlashcardModel> getAllFlashcardsIncludeDeleted() {
        List<FlashcardModel> flashcards = new ArrayList<>();
        SQLiteDatabase db = banco.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM card", null);
        while (cursor.moveToNext()) {
            FlashcardModel flashcard = montarFlashcardModel(cursor);
            flashcards.add(flashcard);
        }
        cursor.close();
        return flashcards;
    }

    // MÉTODOS PARA INSERIR OU ATUALIZAR (AGORA CONSIDERANDO O VALOR DE deleted DO MODELO)
    public void inserirOuAtualizarUser(UserModel user, String uidUsuario) {
        SQLiteDatabase db = banco.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM usuario WHERE remoteId = ?", new String[]{user.getRemoteId()});
        boolean exists = cursor.moveToFirst();
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("name", user.getName());
        values.put("remoteId", user.getRemoteId());
        values.put("updatedAt", user.getUpdatedAt());
        values.put("uid", uidUsuario);

    }

    public void inserirOuAtualizarCaderno(CadernoModel caderno, String uidUsuario) {
        SQLiteDatabase db = banco.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM caderno WHERE remoteId = ?", new String[]{caderno.getRemoteId()});
        boolean exists = cursor.moveToFirst();
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("name", caderno.getNome());
        values.put("favorito", caderno.isFavorito() ? 1 : 0);
        values.put("remoteId", caderno.getRemoteId());
        values.put("updatedAt", caderno.getUpdatedAt());
        values.put("deleted", caderno.isDeleted() ? 1 : 0);

        if (exists) {
            db.update("caderno", values, "remoteId = ?", new String[]{caderno.getRemoteId()});
        } else {
            db.insert("caderno", null, values);
        }
    }


    public void inserirOuAtualizarNota(NotaModel nota, String uidUsuario) {
        SQLiteDatabase db = banco.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM note WHERE remoteId = ?", new String[]{nota.getRemoteId()});
        boolean exists = cursor.moveToFirst();
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("titulo", nota.getTitulo());
        values.put("conteudo", nota.getConteudo());
        values.put("data", nota.getData());
        values.put("remoteId_caderno", nota.getRemoteIdCaderno());
        values.put("remoteId", nota.getRemoteId());
        values.put("updatedAt", nota.getUpdatedAt());
        values.put("deleted", nota.isDeleted() ? 1 : 0);
        values.put("userId", uidUsuario);

        if (exists) {
            db.update("note", values, "remoteId = ?", new String[]{nota.getRemoteId()});
        } else {
            db.insert("note", null, values);
        }
    }


    public void inserirOuAtualizarFlashcard(FlashcardModel flashcard, String uidUsuario) {
        SQLiteDatabase db = banco.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT id FROM card WHERE remoteId = ?", new String[]{flashcard.getRemoteId()});
        boolean exists = cursor.moveToFirst();
        cursor.close();

        ContentValues values = new ContentValues();
        values.put("pergunta", flashcard.getPergunta());
        values.put("resposta", flashcard.getResposta());
        values.put("remoteId_note", flashcard.getRemoteIdNote());
        values.put("remoteId_caderno", flashcard.getRemoteIdCaderno());
        values.put("remoteId", flashcard.getRemoteId());
        values.put("updatedAt", flashcard.getUpdatedAt());
        values.put("deleted", flashcard.isDeleted() ? 1 : 0);
        values.put("userId", uidUsuario);

        if (exists) {
            db.update("card", values, "remoteId = ?", new String[]{flashcard.getRemoteId()});
        } else {
            db.insert("card", null, values);
        }
    }


    // MÉTODOS AUXILIARES PARA MONTAR MODELOS A PARTIR DO CURSOR
    private UserModel montarUserModel(Cursor cursor) {
        UserModel user = new UserModel();
        user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        user.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        user.setRemoteId(cursor.getString(cursor.getColumnIndexOrThrow("remoteId")));
        user.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updatedAt")));
        user.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("uid")));
        return user;
    }

    private CadernoModel montarCadernoModel(Cursor cursor) {
        CadernoModel caderno = new CadernoModel();
        caderno.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        caderno.setNome(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        caderno.setFavorito(cursor.getInt(cursor.getColumnIndexOrThrow("favorito")) > 0);
        caderno.setRemoteId(cursor.getString(cursor.getColumnIndexOrThrow("remoteId")));
        caderno.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updatedAt")));
        caderno.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("userId")));
        int deletedInt = cursor.getInt(cursor.getColumnIndexOrThrow("deleted"));
        caderno.setDeleted(deletedInt != 0);
        return caderno;
    }

    private NotaModel montarNotaModel(Cursor cursor) {
        NotaModel nota = new NotaModel();
        nota.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        nota.setTitulo(cursor.getString(cursor.getColumnIndexOrThrow("titulo")));
        nota.setConteudo(cursor.getString(cursor.getColumnIndexOrThrow("conteudo")));
        nota.setData(cursor.getString(cursor.getColumnIndexOrThrow("data")));
        nota.setRemoteIdCaderno(cursor.getString(cursor.getColumnIndexOrThrow("remoteId_caderno")));
        nota.setRemoteId(cursor.getString(cursor.getColumnIndexOrThrow("remoteId")));
        nota.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updatedAt")));
        nota.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("userId")));
        int deletedInt = cursor.getInt(cursor.getColumnIndexOrThrow("deleted"));
        nota.setDeleted(deletedInt != 0);
        return nota;
    }

    private FlashcardModel montarFlashcardModel(Cursor cursor) {
        FlashcardModel flashcard = new FlashcardModel();
        flashcard.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
        flashcard.setPergunta(cursor.getString(cursor.getColumnIndexOrThrow("pergunta")));
        flashcard.setResposta(cursor.getString(cursor.getColumnIndexOrThrow("resposta")));
        flashcard.setRemoteIdNote(cursor.getString(cursor.getColumnIndexOrThrow("remoteId_note")));
        flashcard.setRemoteIdCaderno(cursor.getString(cursor.getColumnIndexOrThrow("remoteId_caderno")));
        flashcard.setRemoteId(cursor.getString(cursor.getColumnIndexOrThrow("remoteId")));
        flashcard.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow("updatedAt")));
        flashcard.setUserId(cursor.getString(cursor.getColumnIndexOrThrow("userId")));
        int deletedInt = cursor.getInt(cursor.getColumnIndexOrThrow("deleted"));
        flashcard.setDeleted(deletedInt != 0);
        return flashcard;
    }

}
