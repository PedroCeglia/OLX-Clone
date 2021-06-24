package com.youtube.app.clone.curso.android.pedro.olxclone.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;
import com.youtube.app.clone.curso.android.pedro.olxclone.R;
import com.youtube.app.clone.curso.android.pedro.olxclone.model.Anuncio;

import java.util.Objects;

public class DetalhesActivity extends AppCompatActivity {

    private Anuncio anuncioSelecionado;

    //---------------
    private TextView tvTitulo, tvPreco, tvDescricao, tvLocal;
    private CarouselView carouselView;
    private Button btVerContato;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhes);

        recuperandoBundle();
        iniciandoComponentes();
        definindoWidgets();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Detalhes Produto");

    }

    private void recuperandoBundle(){
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            anuncioSelecionado = (Anuncio) bundle.getSerializable("anuncio");
        }else {
            finish();
        }
    }

    private void iniciandoComponentes(){

        tvDescricao = findViewById(R.id.tvDescricaoDetalhe);
        tvLocal = findViewById(R.id.tvLocalizacaoDetalhe);
        tvPreco = findViewById(R.id.tvPrecoDetalhe);
        tvTitulo = findViewById(R.id.tvTituloDetalhe);
        carouselView = findViewById(R.id.carouselView);
        btVerContato = findViewById(R.id.btVerTelefone);

    }

    private void definindoWidgets(){

        tvTitulo.setText(anuncioSelecionado.getTitulo());
        tvPreco.setText(anuncioSelecionado.getValor());
        tvDescricao.setText(anuncioSelecionado.getDescricao());
        tvLocal.setText(anuncioSelecionado.getEstado());

        // Criando Listener Para o CarouselView
        ImageListener imageListener = (position, imageView) -> {
            String urlString = anuncioSelecionado.getFotos().get(position);
            Glide.with(getApplicationContext()).load(Uri.parse(urlString)).into(imageView);
        };

        carouselView.setPageCount(anuncioSelecionado.getFotos().size());
        carouselView.setImageListener(imageListener);

        btVerContato.setOnClickListener(v -> {
            Intent i = new Intent(
                    Intent.ACTION_DIAL,
                    Uri.fromParts("tel", anuncioSelecionado.getTelefone(), null));
            startActivity(i);
        });

    }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }
}