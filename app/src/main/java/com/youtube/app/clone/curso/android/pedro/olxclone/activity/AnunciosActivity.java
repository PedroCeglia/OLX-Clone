package com.youtube.app.clone.curso.android.pedro.olxclone.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.youtube.app.clone.curso.android.pedro.olxclone.R;
import com.youtube.app.clone.curso.android.pedro.olxclone.adapter.MeusAnunciosAdapter;
import com.youtube.app.clone.curso.android.pedro.olxclone.config.ConfiguracaoFirebase;
import com.youtube.app.clone.curso.android.pedro.olxclone.helper.Permissao;
import com.youtube.app.clone.curso.android.pedro.olxclone.helper.RecyclerItemClickListener;
import com.youtube.app.clone.curso.android.pedro.olxclone.model.Anuncio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class AnunciosActivity extends AppCompatActivity {

    private RecyclerView rv;
    private FirebaseAuth autenticacao;
    private MeusAnunciosAdapter adapter;
    private final List<Anuncio> listaDeAnuncio = new ArrayList<>();
    private DatabaseReference database;
    private DatabaseReference anuncioGeralRef;
    private ValueEventListener valueEventListener;
    private android.app.AlertDialog dialog;

    private final String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncios);

        inicializarComponentes();
        configurarRecyclerView();
    }



    private void inicializarComponentes(){
        rv = findViewById(R.id.rvAnuncioGeral);
        database = ConfiguracaoFirebase.getFirebaseDatabase();
    }

    private void configurarRecyclerView(){

        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        // Configurar Adapter
        adapter =new MeusAnunciosAdapter(this, listaDeAnuncio);
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(new RecyclerItemClickListener(
                this, rv,
                new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Anuncio anuncioSelecionado =  listaDeAnuncio.get(position);
                        Intent i = new Intent(AnunciosActivity.this, DetalhesActivity.class );
                        i.putExtra("anuncio", anuncioSelecionado);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    }
                }
        ));

    }

    private void recuperarAnunciosPublicos(){
        listaDeAnuncio.clear();

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Recuperando Anuncios")
                .setCancelable(false)
                .build();
        dialog.show();

        anuncioGeralRef = database.child("anuncios");
        valueEventListener = anuncioGeralRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot estados: snapshot.getChildren()){
                    for (DataSnapshot categorias: estados.getChildren()){
                        for (DataSnapshot ds: categorias.getChildren()){
                            Anuncio anuncio = ds.getValue(Anuncio.class);
                            listaDeAnuncio.add(anuncio);
                        }
                    }
                }
                Collections.reverse(listaDeAnuncio);
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        invalidateOptionsMenu();
        recuperarAnunciosPublicos();
        Permissao.validarPermissoes(permissoes, this, 1);
    }

    @Override
    protected void onStop() {
        super.onStop();
        anuncioGeralRef.removeEventListener(valueEventListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_anuncios, menu);

        return super.onCreateOptionsMenu(menu);
    }

    // Usamos para definir o menu antes de sua exibição na UI
    // podendo excluir a exibição de determinado item
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        if(autenticacao.getCurrentUser() == null){// usuario deslogado
            menu.setGroupVisible(R.id.group_deslogado,true);
        }else{// usuario logado
            menu.setGroupVisible(R.id.group_logado, true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_cadastro_login :
                abrirTelaDeCadastro();
                break;

            case R.id.menu_sair :
                autenticacao.signOut();
                invalidateOptionsMenu();
                break;

            case R.id.menu_meus_anuncios :
                abrirTelaDeMeusAnuncios();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirTelaDeCadastro(){
        Intent i = new Intent(AnunciosActivity.this, CadastroActivity.class);
        startActivity(i);
    }

    private void abrirTelaDeMeusAnuncios(){
        Intent i = new Intent(AnunciosActivity.this, MeusAnunciosActivity.class);
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults){
            if (permissaoResultado == PackageManager.PERMISSION_DENIED){

                alertarValidacaoPermissao();

            }
        }

    }

    private void alertarValidacaoPermissao(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissoes Negadas");
        builder.setMessage("Para utilizar o app é necessário aceitar as permissões \n Reeinstale seu App");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", (dialog, which) -> finish());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void filtrarPorEstado(View view){

        android.app.AlertDialog.Builder dialogEstado = new android.app.AlertDialog.Builder(this);
        dialogEstado.setTitle("Selecione o Estado Desejado");

        // Configurar Spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinner = viewSpinner.findViewById(R.id.spinnerDialog);

        String[] estados = getResources().getStringArray(R.array.estados);
        ArrayAdapter<String> adapterSpinerEstados = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                estados
        );
        adapterSpinerEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinerEstados);
        dialogEstado.setView(viewSpinner);

        dialogEstado.setPositiveButton("OK", (dialog, which) -> {
            // Recuperando Item Selecionado
            String filtroEstado = spinner.getSelectedItem().toString();
            recuperarAnunciosPorEstado(filtroEstado);
        });
        dialogEstado.setNegativeButton("Cancelar", (dialog, which) -> {

        });

        android.app.AlertDialog dddEstadoddd = dialogEstado.create();
        dddEstadoddd.show();
    }

    public void filtrarPorCategoria(View view){

        android.app.AlertDialog.Builder dialogCategoria = new android.app.AlertDialog.Builder(this);
        dialogCategoria.setTitle("Selecione a Categoria Desejada");

        // Configurar Spinner
        View viewSpinner = getLayoutInflater().inflate(R.layout.dialog_spinner, null);
        final Spinner spinner = viewSpinner.findViewById(R.id.spinnerDialog);
        String[] categorias = getResources().getStringArray(R.array.categorias);

        ArrayAdapter<String> adapterSpinerCategorias = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                categorias
        );
        adapterSpinerCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterSpinerCategorias);
        dialogCategoria.setView(viewSpinner);

        dialogCategoria.setPositiveButton("OK", (dialog, which) -> {

            String filtroCategorias = spinner.getSelectedItem().toString();
            recuperarAnunciosPorCategoria(filtroCategorias);
        });
        dialogCategoria.setNegativeButton("Cancelar", (dialog, which) -> {

        });

        android.app.AlertDialog dddEstadoddd = dialogCategoria.create();
        dddEstadoddd.show();
    }

    private void recuperarAnunciosPorEstado(String estado){
        if (!estado.equals("ESTADOS")){

            listaDeAnuncio.clear();

            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Recuperando Anuncios")
                    .setCancelable(false)
                    .build();
            dialog.show();

            anuncioGeralRef = database.child("anuncios").child(estado);
            valueEventListener = anuncioGeralRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot categorias: snapshot.getChildren()){
                        for (DataSnapshot ds: categorias.getChildren()){
                            Anuncio anuncio = ds.getValue(Anuncio.class);
                            listaDeAnuncio.add(anuncio);
                        }
                    }
                    Collections.reverse(listaDeAnuncio);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void recuperarAnunciosPorCategoria(String categoria){
        if (!categoria.equals("CATEGORIAS")){

            listaDeAnuncio.clear();

            dialog = new SpotsDialog.Builder()
                    .setContext(this)
                    .setMessage("Recuperando Anuncios")
                    .setCancelable(false)
                    .build();
            dialog.show();

            anuncioGeralRef = database.child("anuncios");
            valueEventListener = anuncioGeralRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    // primeiro recuperamos os nós de estados
                    for (DataSnapshot estados: snapshot.getChildren()){
                        // depois recuperamos o nó da categoria desejada dentro de todos os estados
                        DataSnapshot categorias = estados.child(categoria);
                        for (DataSnapshot ds: categorias.getChildren()){
                            Anuncio anuncio = ds.getValue(Anuncio.class);
                            listaDeAnuncio.add(anuncio);
                        }
                    }
                    Collections.reverse(listaDeAnuncio);
                    adapter.notifyDataSetChanged();
                    dialog.dismiss();
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }
}