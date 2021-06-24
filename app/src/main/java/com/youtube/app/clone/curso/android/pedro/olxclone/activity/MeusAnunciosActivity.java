
package com.youtube.app.clone.curso.android.pedro.olxclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.youtube.app.clone.curso.android.pedro.olxclone.R;
import com.youtube.app.clone.curso.android.pedro.olxclone.adapter.MeusAnunciosAdapter;
import com.youtube.app.clone.curso.android.pedro.olxclone.config.ConfiguracaoFirebase;
import com.youtube.app.clone.curso.android.pedro.olxclone.helper.RecyclerItemClickListener;
import com.youtube.app.clone.curso.android.pedro.olxclone.helper.UsuarioFirebase;
import com.youtube.app.clone.curso.android.pedro.olxclone.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import dmax.dialog.SpotsDialog;

public class MeusAnunciosActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private RecyclerView rv;
    private final List<Anuncio> listaDeAnuncios = new ArrayList<>();
    private DatabaseReference database;
    private DatabaseReference meusAnunciosRef;
    private ValueEventListener valueEventListener;
    private MeusAnunciosAdapter adapter;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meus_anuncios);

        inicializandoComponentes();
        configurandoToolbar();

        fab.setOnClickListener(v -> abrindAddAnuncio());

        configurandoRecyclerView();
    }

    @Override
    protected void onStart() {
        super.onStart();
        recuperandoListadeAnuncios();
    }

    @Override
    protected void onStop() {
        super.onStop();
        meusAnunciosRef.removeEventListener(valueEventListener);
    }

    private void inicializandoComponentes(){
        fab = findViewById(R.id.fabAddAnuncio);
        rv = findViewById(R.id.rvMeusAnuncios);
        database = ConfiguracaoFirebase.getFirebaseDatabase().child("meus_anuncios");
    }

    private void configurandoToolbar(){
        Toolbar toolbar = findViewById(R.id.toolbar_principal);
        toolbar.setTitle("Meus Anuncios");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    private void configurandoRecyclerView(){
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        // Configurando Adapter
        adapter = new MeusAnunciosAdapter(this, listaDeAnuncios);
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this, rv,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                                Anuncio anuncioSelecionado = listaDeAnuncios.get(position);
                                anuncioSelecionado.remover();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    private void recuperandoListadeAnuncios(){

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setCancelable(false)
                .setMessage("PreparandoAnuncios")
                .build();
        dialog.show();

        listaDeAnuncios.clear();

        meusAnunciosRef = database.child(UsuarioFirebase.getIdentificadorUsuario());
        valueEventListener = meusAnunciosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Anuncio anuncioDs = ds.getValue(Anuncio.class);
                    listaDeAnuncios.add(anuncioDs);
                }
                // revertendo exibição da Lista
                Collections.reverse(listaDeAnuncios);
                adapter.notifyDataSetChanged();

                // removendo dialog
                dialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    public boolean onNavigateUp() {
        finish();
        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return false;
    }

    private void abrindAddAnuncio(){
        Intent i = new Intent(this, AddAnuncioActivity.class);
        startActivity(i);
    }
}