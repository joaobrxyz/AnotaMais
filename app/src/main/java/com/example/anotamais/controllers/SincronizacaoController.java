package com.example.anotamais.controllers;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.example.anotamais.models.CadernoModel;
import com.example.anotamais.models.FlashcardModel;
import com.example.anotamais.models.NotaModel;
import com.example.anotamais.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SincronizacaoController {

    private static final String PREF_NAME = "sincronizacao_prefs";
    private static final String KEY_ULTIMA_SYNC = "ultima_sync";

    public static void sincronizarSeLogado(Context context, boolean SyncAfter, int tempo) {
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            sincronizarComFirestore(context, firebaseUser, SyncAfter, tempo);
        }
    }

    public static void sincronizarComFirestore(Context context, FirebaseUser user, Boolean SyncAfter, int tempo) {
        if (user == null) {
            Toast.makeText(context, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }

        if (SyncAfter) {
            if (!deveSincronizar(context, tempo)) {
                return;
            }
        }

        BancoControllerUsuario bdUsuario = new BancoControllerUsuario(context);
        String uid = bdUsuario.getUidUsuario();
        if (uid == null) {
            return;
        }

        BancoController banco = new BancoController(context);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = user.getEmail().replace(".", "_");

        // Passo 1: baixar dados da nuvem
        db.collection("users").document(email).get()
                .addOnSuccessListener(document -> {
                    List<Map<String, Object>> usuariosRemotos = (List<Map<String, Object>>) document.get("usuarios");
                    List<Map<String, Object>> cadernosRemotos = (List<Map<String, Object>>) document.get("cadernos");
                    List<Map<String, Object>> notasRemotas = (List<Map<String, Object>>) document.get("notas");
                    List<Map<String, Object>> flashcardsRemotos = (List<Map<String, Object>>) document.get("flashcards");

                    if (usuariosRemotos == null) usuariosRemotos = new ArrayList<>();
                    if (cadernosRemotos == null) cadernosRemotos = new ArrayList<>();
                    if (notasRemotas == null) notasRemotas = new ArrayList<>();
                    if (flashcardsRemotos == null) flashcardsRemotos = new ArrayList<>();

                    // Passo 2: pegar dados locais (incluindo os deletados para mesclagem correta)
                    List<UserModel> usuariosLocais = banco.getAllUsersIncludeDeleted();
                    List<CadernoModel> cadernosLocais = banco.getAllCadernosIncludeDeleted();
                    List<NotaModel> notasLocais = banco.getAllNotasIncludeDeleted();
                    List<FlashcardModel> flashcardsLocais = banco.getAllFlashcardsIncludeDeleted();

                    // Passo 3: Mesclar dados remoto e local (atualizar local)
                    mesclarUsuarios(usuariosRemotos, usuariosLocais, banco, uid);
                    mesclarCadernos(cadernosRemotos, cadernosLocais, banco, uid);
                    mesclarNotas(notasRemotas, notasLocais, banco, uid);
                    mesclarFlashcards(flashcardsRemotos, flashcardsLocais, banco, uid);

                    // Passo extra: garantir que todos os dados locais tenham o UID antes de enviar para Firestore
                    garantirUserIdNosDadosLocais(banco, uid);

                    // Passo 4: pegar dados atualizados do banco local para enviar (agora só os não deletados ou com deleted = 0)
                    usuariosLocais = banco.getAllUsers();
                    cadernosLocais = banco.getAllCadernos();
                    notasLocais = banco.getAllNotas();
                    flashcardsLocais = banco.getAllFlashcards();

                    // Passo 5: converter para Map para Firestore
                    Map<String, Object> dadosParaEnviar = new HashMap<>();
                    dadosParaEnviar.put("usuarios", converterUsuariosParaMap(usuariosLocais));
                    dadosParaEnviar.put("cadernos", converterCadernosParaMap(cadernosLocais));
                    dadosParaEnviar.put("notas", converterNotasParaMap(notasLocais));
                    dadosParaEnviar.put("flashcards", converterFlashcardsParaMap(flashcardsLocais));

                    // Passo 6: enviar para Firestore
                    db.collection("users").document(email).set(dadosParaEnviar)
                            .addOnSuccessListener(aVoid -> {
                                salvarUltimaSync(context);
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(context, "Erro ao sincronizar: " + e.getMessage(), Toast.LENGTH_LONG).show());
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(context, "Erro ao baixar dados para sincronização: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public static boolean deveSincronizar(Context context, int tempo) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        long ultimaSync = prefs.getLong(KEY_ULTIMA_SYNC, 0);
        long agora = System.currentTimeMillis();
        long tempoIntervalo = 24 * 60 * 60 * 1000;
        if (tempo == 24) {
            tempoIntervalo = 24 * 60 * 60 * 1000; // 24h em milissegundos
        } else if (tempo == 30) {
            tempoIntervalo = 30 * 60 * 1000; // 30 minutos em milissegundos
        }
        return (agora - ultimaSync) > tempoIntervalo;
    }

    private static void salvarUltimaSync(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putLong(KEY_ULTIMA_SYNC, System.currentTimeMillis()).apply();
    }

    private static void mesclarUsuarios(List<Map<String, Object>> remotos, List<UserModel> locais, BancoController banco, String uid) {
        for (Map<String, Object> remoto : remotos) {
            String remoteId = (String) remoto.get("remoteId");
            long updatedAtRemoto = parseUpdatedAt(remoto.get("updatedAt"));

            UserModel local = findUserByRemoteId(locais, remoteId);

            if (local == null) {
                UserModel novo = new UserModel();
                novo.setRemoteId(remoteId);
                novo.setName((String) remoto.get("name"));
                novo.setUpdatedAt(updatedAtRemoto);
                novo.setUserId(uid);
                banco.inserirOuAtualizarUser(novo, uid);
                locais.add(novo);
            } else {
                if (updatedAtRemoto > local.getUpdatedAt()) {
                    local.setName((String) remoto.get("name"));
                    local.setUpdatedAt(updatedAtRemoto);
                    local.setUserId(uid);
                    banco.inserirOuAtualizarUser(local, uid);
                }
            }
        }
    }

    private static void mesclarCadernos(List<Map<String, Object>> remotos, List<CadernoModel> locais, BancoController banco, String uid) {
        for (Map<String, Object> remoto : remotos) {
            String remoteId = (String) remoto.get("remoteId");
            long updatedAtRemoto = parseUpdatedAt(remoto.get("updatedAt"));
            boolean deletedRemoto = parseDeleted(remoto.get("deleted"));

            CadernoModel local = findCadernoByRemoteId(locais, remoteId);

            if (local == null) {
                CadernoModel novo = new CadernoModel();
                novo.setRemoteId(remoteId);
                novo.setNome((String) remoto.get("nome"));
                novo.setFavorito(remoto.get("favorito") != null && (Boolean) remoto.get("favorito"));
                novo.setUpdatedAt(updatedAtRemoto);
                novo.setDeleted(deletedRemoto);
                novo.setUserId(uid);
                banco.inserirOuAtualizarCaderno(novo, uid);
                locais.add(novo);
            } else {
                if (updatedAtRemoto > local.getUpdatedAt()) {
                    local.setNome((String) remoto.get("nome"));
                    local.setFavorito(remoto.get("favorito") != null && (Boolean) remoto.get("favorito"));
                    local.setUpdatedAt(updatedAtRemoto);
                    local.setDeleted(deletedRemoto);
                    local.setUserId(uid);
                    banco.inserirOuAtualizarCaderno(local, uid);
                }
            }
        }
    }

    private static void mesclarNotas(List<Map<String, Object>> remotos, List<NotaModel> locais, BancoController banco, String uid) {
        for (Map<String, Object> remoto : remotos) {
            String remoteId = (String) remoto.get("remoteId");
            long updatedAtRemoto = parseUpdatedAt(remoto.get("updatedAt"));
            boolean deletedRemoto = parseDeleted(remoto.get("deleted"));
            String remoteIdCaderno = (String) remoto.get("remoteId_caderno");

            NotaModel local = findNotaByRemoteId(locais, remoteId);

            if (local == null) {
                NotaModel novo = new NotaModel();
                novo.setRemoteId(remoteId);
                novo.setTitulo((String) remoto.get("titulo"));
                novo.setConteudo((String) remoto.get("conteudo"));
                novo.setData((String) remoto.get("data"));
                novo.setRemoteIdCaderno(remoteIdCaderno);
                novo.setUpdatedAt(updatedAtRemoto);
                novo.setDeleted(deletedRemoto);
                novo.setUserId(uid);
                banco.inserirOuAtualizarNota(novo, uid);
                locais.add(novo);
            } else if (updatedAtRemoto > local.getUpdatedAt()) {
                local.setTitulo((String) remoto.get("titulo"));
                local.setConteudo((String) remoto.get("conteudo"));
                local.setData((String) remoto.get("data"));
                local.setRemoteIdCaderno(remoteIdCaderno);
                local.setUpdatedAt(updatedAtRemoto);
                local.setDeleted(deletedRemoto);
                local.setUserId(uid);
                banco.inserirOuAtualizarNota(local, uid);
            }
        }
    }

    private static void mesclarFlashcards(List<Map<String, Object>> remotos, List<FlashcardModel> locais, BancoController banco, String uid) {
        for (Map<String, Object> remoto : remotos) {
            String remoteId = (String) remoto.get("remoteId");
            long updatedAtRemoto = parseUpdatedAt(remoto.get("updatedAt"));
            boolean deletedRemoto = parseDeleted(remoto.get("deleted"));
            String remoteIdCaderno = (String) remoto.get("remoteId_caderno");
            String remoteIdNota = (String) remoto.get("remoteId_note");

            FlashcardModel local = findFlashcardByRemoteId(locais, remoteId);

            if (local == null) {
                FlashcardModel novo = new FlashcardModel();
                novo.setRemoteId(remoteId);
                novo.setPergunta((String) remoto.get("pergunta"));
                novo.setResposta((String) remoto.get("resposta"));
                novo.setRemoteIdCaderno(remoteIdCaderno);
                novo.setRemoteIdNote(remoteIdNota);
                novo.setUpdatedAt(updatedAtRemoto);
                novo.setDeleted(deletedRemoto);
                novo.setUserId(uid);
                banco.inserirOuAtualizarFlashcard(novo, uid);
                locais.add(novo);
            } else if (updatedAtRemoto > local.getUpdatedAt()) {
                local.setPergunta((String) remoto.get("pergunta"));
                local.setResposta((String) remoto.get("resposta"));
                local.setRemoteIdCaderno(remoteIdCaderno);
                local.setRemoteIdNote(remoteIdNota);
                local.setUpdatedAt(updatedAtRemoto);
                local.setDeleted(deletedRemoto);
                local.setUserId(uid);
                banco.inserirOuAtualizarFlashcard(local, uid);
            }
        }
    }

    // Função para garantir conversão correta do updatedAt
    private static long parseUpdatedAt(Object updatedAtObj) {
        if (updatedAtObj == null) return 0L;
        if (updatedAtObj instanceof Long) {
            return (Long) updatedAtObj;
        } else if (updatedAtObj instanceof Double) {
            return ((Double) updatedAtObj).longValue();
        } else if (updatedAtObj instanceof Integer) {
            return ((Integer) updatedAtObj).longValue();
        } else {
            return 0L;
        }
    }

    // Função para garantir conversão correta do campo deleted vindo do Firestore
    private static boolean parseDeleted(Object deletedObj) {
        if (deletedObj == null) return false;
        if (deletedObj instanceof Boolean) return (Boolean) deletedObj;
        if (deletedObj instanceof Long) return ((Long) deletedObj) != 0;
        if (deletedObj instanceof Integer) return ((Integer) deletedObj) != 0;
        if (deletedObj instanceof Double) return ((Double) deletedObj) != 0.0;
        return false;
    }

    // Métodos para converter listas de modelos para listas de Map (para Firestore)
    private static List<Map<String, Object>> converterUsuariosParaMap(List<UserModel> usuarios) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (UserModel u : usuarios) {
            Map<String, Object> map = new HashMap<>();
            map.put("remoteId", u.getRemoteId());
            map.put("name", u.getName());
            map.put("updatedAt", u.getUpdatedAt());
            map.put("uid", u.getUserId());
            lista.add(map);
        }
        return lista;
    }

    private static List<Map<String, Object>> converterCadernosParaMap(List<CadernoModel> cadernos) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (CadernoModel c : cadernos) {
            Map<String, Object> map = new HashMap<>();
            map.put("remoteId", c.getRemoteId());
            map.put("nome", c.getNome());
            map.put("favorito", c.isFavorito());
            map.put("updatedAt", c.getUpdatedAt());
            map.put("deleted", c.isDeleted());
            map.put("userId", c.getUserId());
            lista.add(map);
        }
        return lista;
    }

    private static List<Map<String, Object>> converterNotasParaMap(List<NotaModel> notas) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (NotaModel n : notas) {
            Map<String, Object> map = new HashMap<>();
            map.put("remoteId", n.getRemoteId());
            map.put("titulo", n.getTitulo());
            map.put("conteudo", n.getConteudo());
            map.put("data", n.getData());
            map.put("remoteId_caderno", n.getRemoteIdCaderno());
            map.put("updatedAt", n.getUpdatedAt());
            map.put("deleted", n.isDeleted());
            map.put("userId", n.getUserId());
            lista.add(map);
        }
        return lista;
    }

    private static List<Map<String, Object>> converterFlashcardsParaMap(List<FlashcardModel> flashcards) {
        List<Map<String, Object>> lista = new ArrayList<>();
        for (FlashcardModel f : flashcards) {
            Map<String, Object> map = new HashMap<>();
            map.put("remoteId", f.getRemoteId());
            map.put("pergunta", f.getPergunta());
            map.put("resposta", f.getResposta());
            map.put("remoteId_caderno", f.getRemoteIdCaderno());
            map.put("remoteId_note", f.getRemoteIdNote());
            map.put("updatedAt", f.getUpdatedAt());
            map.put("deleted", f.isDeleted());
            map.put("userId", f.getUserId());
            lista.add(map);
        }
        return lista;
    }

    // Funções para achar modelo pelo remoteId nas listas locais
    private static UserModel findUserByRemoteId(List<UserModel> lista, String remoteId) {
        for (UserModel u : lista) {
            if (u.getRemoteId() != null && u.getRemoteId().equals(remoteId)) {
                return u;
            }
        }
        return null;
    }

    private static CadernoModel findCadernoByRemoteId(List<CadernoModel> lista, String remoteId) {
        for (CadernoModel c : lista) {
            if (c.getRemoteId() != null && c.getRemoteId().equals(remoteId)) {
                return c;
            }
        }
        return null;
    }

    private static NotaModel findNotaByRemoteId(List<NotaModel> lista, String remoteId) {
        for (NotaModel n : lista) {
            if (n.getRemoteId() != null && n.getRemoteId().equals(remoteId)) {
                return n;
            }
        }
        return null;
    }

    private static FlashcardModel findFlashcardByRemoteId(List<FlashcardModel> lista, String remoteId) {
        for (FlashcardModel f : lista) {
            if (f.getRemoteId() != null && f.getRemoteId().equals(remoteId)) {
                return f;
            }
        }
        return null;
    }

    public static void garantirUserIdNosDadosLocais(BancoController banco, String uid) {
        for (UserModel u : banco.getAllUsersIncludeDeleted()) {
            u.setUserId(uid);
            banco.inserirOuAtualizarUser(u, uid);
        }
        for (CadernoModel c : banco.getAllCadernosIncludeDeleted()) {
            c.setUserId(uid);
            banco.inserirOuAtualizarCaderno(c, uid);
        }
        for (NotaModel n : banco.getAllNotasIncludeDeleted()) {
            n.setUserId(uid);
            banco.inserirOuAtualizarNota(n, uid);
        }
        for (FlashcardModel f : banco.getAllFlashcardsIncludeDeleted()) {
            f.setUserId(uid);
            banco.inserirOuAtualizarFlashcard(f, uid);
        }
    }

}
