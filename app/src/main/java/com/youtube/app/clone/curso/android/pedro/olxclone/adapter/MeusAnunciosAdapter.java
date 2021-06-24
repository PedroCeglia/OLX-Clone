package com.youtube.app.clone.curso.android.pedro.olxclone.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.youtube.app.clone.curso.android.pedro.olxclone.R;
import com.youtube.app.clone.curso.android.pedro.olxclone.model.Anuncio;

import java.util.List;

public class MeusAnunciosAdapter extends RecyclerView.Adapter<MeusAnunciosAdapter.MyViewHolderMeusAnunciosAdapter> {

    private Context c;
    private List<Anuncio> listaDeAnuncios;

    public MeusAnunciosAdapter(Context c, List<Anuncio> listaDeAnuncios) {
        this.c = c;
        this.listaDeAnuncios = listaDeAnuncios;
    }

    @NonNull
    @Override
    public MyViewHolderMeusAnunciosAdapter onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.anuncio_adapter_layout,parent,false);
        return new MyViewHolderMeusAnunciosAdapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolderMeusAnunciosAdapter holder, int position) {

        Anuncio anuncioSelecionado = listaDeAnuncios.get(position);

        holder.tvValor.setText(anuncioSelecionado.getValor());
        holder.tvTitulo.setText(anuncioSelecionado.getTitulo());

        Glide.with(c)
                .load(anuncioSelecionado.getFotos().get(0))
                .into(holder.ivAnuncio);

    }

    @Override
    public int getItemCount() {
        return listaDeAnuncios.size();
    }

    public class MyViewHolderMeusAnunciosAdapter extends RecyclerView.ViewHolder{

        private ImageView ivAnuncio;
        private TextView tvTitulo, tvValor;

        public MyViewHolderMeusAnunciosAdapter(@NonNull View itemView) {
            super(itemView);
            ivAnuncio = itemView.findViewById(R.id.ivAnuncio);
            tvTitulo = itemView.findViewById(R.id.tvTitulo);
            tvValor = itemView.findViewById(R.id.tvPreco);
        }
    }
}
