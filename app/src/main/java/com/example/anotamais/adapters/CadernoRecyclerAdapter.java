package com.example.anotamais.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.anotamais.controllers.BancoControllerCaderno;
import com.example.anotamais.models.CadernoModel;
import com.example.anotamais.R;
import com.example.anotamais.activities.Caderno;

import java.util.List;

public class CadernoRecyclerAdapter extends RecyclerView.Adapter<CadernoRecyclerAdapter.ViewHolder> {
    private List<CadernoModel> cadernos;
    private Context context;
    private OnCadernoFavoritoChangeListener listener;

    public CadernoRecyclerAdapter(Context context, List<CadernoModel> cadernos) {
        this.context = context;
        this.cadernos = cadernos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_model_caderno, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CadernoModel caderno = cadernos.get(position);
        holder.nomeCaderno.setText(caderno.getNome());

        if (caderno.isFavorito()) {
            holder.btFavoritoMain.setImageResource(R.drawable.image_fav_on);
        } else {
            holder.btFavoritoMain.setImageResource(R.drawable.image_fav_off);
        }


        holder.imagemCaderno.setOnClickListener(v -> {
            Context context = v.getContext();
            Intent intent = new Intent(context, Caderno.class);
            intent.putExtra("nomeCaderno", caderno.getNome());
            intent.putExtra("idCaderno", caderno.getId());
            intent.putExtra("remoteIdCaderno", caderno.getRemoteId());
            context.startActivity(intent);
        });

        holder.btFavoritoMain.setOnClickListener(v -> {
            BancoControllerCaderno cad = new BancoControllerCaderno(context);
            if (caderno.isFavorito()) {
                boolean sucesso = cad.favoritarCaderno(caderno.getId(), 0);
                if (sucesso) {
                    caderno.setFavorito(false);
                    holder.btFavoritoMain.setImageResource(R.drawable.image_fav_off);
                }
            } else {
                boolean sucesso = cad.favoritarCaderno(caderno.getId(), 1);
                if (sucesso) {
                    caderno.setFavorito(true);
                    holder.btFavoritoMain.setImageResource(R.drawable.image_fav_on);
                }
            }
            if (listener != null) {
                listener.onFavoritoAlterado(); // Chama a função na Activity
            }
        });

        holder.imagemCaderno.setOnLongClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Excluir Caderno")
                    .setMessage("Tem certeza que deseja excluir esse caderno?")
                    .setPositiveButton("Excluir", (dialog, which) -> {
                        BancoControllerCaderno cad = new BancoControllerCaderno(context);
                        boolean sucesso = cad.excluirCaderno(caderno.getId());

                        if (sucesso) {
                            cadernos.remove(position);
                            notifyItemRemoved(position);
                            Toast.makeText(context, "Caderno excluído com sucesso", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Erro ao excluir", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
            return true;
        });
        Animation animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), android.R.anim.slide_in_left);
        holder.itemView.startAnimation(animation);
    }

    @Override
    public int getItemCount() {
        return cadernos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nomeCaderno;
        ImageButton imagemCaderno, btFavoritoMain;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeCaderno = itemView.findViewById(R.id.nomeCadernoList);
            imagemCaderno = itemView.findViewById(R.id.imagemCaderno);
            btFavoritoMain = itemView.findViewById(R.id.btFavoritoMain);
        }
    }


    public interface OnCadernoFavoritoChangeListener {
        void onFavoritoAlterado();
    }

    public void setOnCadernoFavoritoChangeListener(OnCadernoFavoritoChangeListener listener) {
        this.listener = listener;
    }
}