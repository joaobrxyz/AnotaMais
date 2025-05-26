// teste banco controller flash card


//package com.example.anotamais;
//
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import java.util.ArrayList; // Adicionar import
//import java.util.List;    // Adicionar import
//
//public class BancoControllerCard {
//    private SQLiteDatabase db;
//    private CriaBanco banco; // Assume que CriaBanco define a tabela "note" corretamente
//
//    public BancoControllerCard(Context context) {
//        banco = new CriaBanco(context);
//    }
//
//
//    public List<FlashcardModel> carregaTodosFlashcards() {
//        List<FlashcardModel> flashcards = new ArrayList<>();
//        Cursor cursor;
//
//        String[] campos = {CriaBanco.ID_FLASHCARD, CriaBanco.PERGUNTA_FLASHCARD, CriaBanco.RESPOSTA_FLASHCARD}; // Use constantes de CriaBanco
//        db = banco.getReadableDatabase();
//        cursor = db.query(CriaBanco.TABELA_NOTE, campos, null, null, null, null, null, null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                int id = cursor.getInt(cursor.getColumnIndexOrThrow(CriaBanco.ID_FLASHCARD));
//                String pergunta = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.PERGUNTA_FLASHCARD));
//                String resposta = cursor.getString(cursor.getColumnIndexOrThrow(CriaBanco.RESPOSTA_FLASHCARD));
//                flashcards.add(new FlashcardModel(id, pergunta, resposta));
//            } while (cursor.moveToNext());
//            cursor.close();
//        }
//        db.close();
//        return flashcards;
//    }
//
//
//
//    public String insereFlashcardSemNote(String pergunta, String resposta) {
//        ContentValues valores;
//        long resultado;
//        db = banco.getWritableDatabase();
//
//        valores = new ContentValues();
//        valores.put(CriaBanco.PERGUNTA_FLASHCARD, pergunta);
//        valores.put(CriaBanco.RESPOSTA_FLASHCARD, resposta);
//
//        resultado = db.insert(CriaBanco.TABELA_NOTE, null, valores);
//        db.close();
//
//        if (resultado == -1)
//            return "Erro ao inserir flashcard";
//        else
//            return "Flashcard inserido com sucesso";
//    }
//
//
//    public String insereDados(String pergunta, String resposta, int idNote) {
//        ContentValues valores;
//        long resultado;
//        db = banco.getWritableDatabase();
//
//        valores = new ContentValues();
//        valores.put(CriaBanco.PERGUNTA_FLASHCARD, pergunta);
//        valores.put(CriaBanco.RESPOSTA_FLASHCARD, resposta);
//        valores.put(CriaBanco.ID_NOTE_FK, idNote); // Assumindo que ID_NOTE_FK é o nome da coluna
//        resultado = db.insert(CriaBanco.TABELA_NOTE, null, valores);
//        db.close();
//
//        if (resultado == -1)
//            return "Erro ao inserir registro";
//        else
//            return "Registro Inserido com sucesso";
//    }
//
//    public Cursor carregaDadosPeloId(int idFlashcard) { // Renomeado id para clareza
//        Cursor cursor;
//        // Adicione a coluna "id" se você precisar dela para o FlashcardModel
//        String[] campos = {CriaBanco.ID_FLASHCARD, CriaBanco.PERGUNTA_FLASHCARD, CriaBanco.RESPOSTA_FLASHCARD, CriaBanco.ID_NOTE_FK};
//        String where = CriaBanco.ID_FLASHCARD + "=" + idFlashcard;
//        db = banco.getReadableDatabase();
//        cursor = db.query(CriaBanco.TABELA_NOTE, campos, where, null, null, null, null, null);
//        if (cursor != null) {
//            cursor.moveToFirst();
//        }
//
//        return cursor;
//    }
//
//    public String alteraDados(int idFlashcard, String pergunta, String resposta) { // Renomeado id
//        String msg = "Flashcard alterado com sucesso!!!";
//        db = banco.getWritableDatabase(); // Use getWritableDatabase para updates
//
//        ContentValues valores = new ContentValues();
//        valores.put(CriaBanco.PERGUNTA_FLASHCARD, pergunta);
//        valores.put(CriaBanco.RESPOSTA_FLASHCARD, resposta);
//
//        String condicao = CriaBanco.ID_FLASHCARD + " = " + idFlashcard;
//        int linha = db.update(CriaBanco.TABELA_NOTE, valores, condicao, null);
//
//        if (linha < 1) {
//            msg = "Erro ao alterar o flashcard";
//        }
//        db.close();
//        return msg;
//    }
//
//    public String excluirDados(int idFlashcard) { // Renomeado id
//        String msg = "Flashcard excluído";
//        db = banco.getWritableDatabase(); // Use getWritableDatabase para deletes
//
//        String condicao = CriaBanco.ID_FLASHCARD + " = " + idFlashcard;
//        int linhas = db.delete(CriaBanco.TABELA_NOTE, condicao, null);
//
//        if (linhas < 1) {
//            msg = "Erro ao excluir o flashcard";
//        }
//        db.close();
//        return msg;
//    }
//}