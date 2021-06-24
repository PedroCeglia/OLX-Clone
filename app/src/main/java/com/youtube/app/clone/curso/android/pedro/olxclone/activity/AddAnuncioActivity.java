package com.youtube.app.clone.curso.android.pedro.olxclone.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.santalu.maskara.widget.MaskEditText;
import com.youtube.app.clone.curso.android.pedro.olxclone.R;
import com.youtube.app.clone.curso.android.pedro.olxclone.config.ConfiguracaoFirebase;
import com.youtube.app.clone.curso.android.pedro.olxclone.model.Anuncio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class AddAnuncioActivity extends AppCompatActivity
            implements View.OnClickListener{

    private EditText etTitulo, etDescricao;
    private Spinner sEstado, sCategoria;
    private CurrencyEditText etPreco;
    private Button btCadastrarAnuncio;
    private MaskEditText etTelefone;
    private ImageView ivCadastro1, ivCadastro2, ivCadastro3;
    private AlertDialog alertDialog;

    //---------------

    private String estado ;
    private String categoria;
    private String titulo;
    private String descricao;
    private String telefone;
    private String preco;
    private long precoLG;
    private final List<String> listaFotosRecuperadas =new ArrayList<>();
    private final List<String> listaFotosRecuperadas2 =new ArrayList<>();
    private final List<String> listaFotosFireBase =new ArrayList<>();
    //-------------

    private Anuncio anuncio;
    private StorageReference storage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_anuncio);

        inicializandoComponentes();

        recuperarDadosSpiners();

        btCadastrarAnuncio.setOnClickListener(v -> {
            recuperandoDadosDaUI();
            validarDadosAnuncio();
        });
    }

    private void inicializandoComponentes(){
        etTitulo = findViewById(R.id.etTitulo);
        etDescricao = findViewById(R.id.etDescricao);
        etPreco = findViewById(R.id.etPrecoCurrenciEditText);
        btCadastrarAnuncio = findViewById(R.id.btCadastrarAnuncio);
        etTelefone = findViewById(R.id.etTelefoneMaskara);
        ivCadastro1 = findViewById(R.id.iVCadastro1);
        ivCadastro2 = findViewById(R.id.ivCadastro2);
        ivCadastro3 = findViewById(R.id.ivCadastro3);
        sEstado = findViewById(R.id.spinnerEstado);
        sCategoria = findViewById(R.id.spinnerCategoria);

        // Configurando Localidade para pt -> português BR - Brasil
        Locale localeBr = new Locale("pt","BR");
        etPreco.setLocale(localeBr);

        // Configurando onClick das Imagens
        ivCadastro1.setOnClickListener(this);
        ivCadastro2.setOnClickListener(this);
        ivCadastro3.setOnClickListener(this);

        storage = ConfiguracaoFirebase.getFirebaseStorage();
    }

    private void recuperarDadosSpiners(){
        String[] estados = getResources().getStringArray(R.array.estados);
        String[] categorias = getResources().getStringArray(R.array.categorias);

        ArrayAdapter<String> adapterSpinerEstados = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                estados
        );
        adapterSpinerEstados.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sEstado.setAdapter(adapterSpinerEstados);

        ArrayAdapter<String> adapterSpinerCategorias = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item,
                categorias
        );
        adapterSpinerCategorias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sCategoria.setAdapter(adapterSpinerCategorias);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.iVCadastro1 :
                escolherImagem(1);
                break;

            case R.id.ivCadastro2 :
                escolherImagem(2);
                break;

            case R.id.ivCadastro3 :
                escolherImagem(3);
                break;
        }
    }

    public void escolherImagem(int requestCode){
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK){

            // Recuperar Imagem
            assert data != null;
            Uri imagemSelecionada = data.getData();
            String url = imagemSelecionada.toString();
            listaFotosRecuperadas.add(url);
            listaFotosRecuperadas2.add(url);
            if(requestCode == 1 ){
                ivCadastro1.setImageURI(imagemSelecionada);
            } else if(requestCode == 2 ){
                ivCadastro2.setImageURI(imagemSelecionada);
            } else if(requestCode == 3){
                ivCadastro3.setImageURI(imagemSelecionada);
            }
        }
    }

    private void recuperandoDadosDaUI(){
        estado = sEstado.getSelectedItem().toString();
        categoria = sCategoria.getSelectedItem().toString();
        titulo = etTitulo.getText().toString();
        descricao = etDescricao.getText().toString();
        telefone = etTelefone.getUnMasked();
        Log.i("telefone", telefone);
        preco = etPreco.getText().toString();
        precoLG = etPreco.getRawValue();
    }

    private void configurarAnuncio(){

        anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setDescricao(descricao);
        anuncio.setTitulo(titulo);
        anuncio.setValor(preco);
        anuncio.setTelefone(telefone);
        anuncio.setFotos(listaFotosRecuperadas);

    }

    private void validarDadosAnuncio(){

        configurarAnuncio();

        if (listaFotosRecuperadas.size() > 1){
            if (!estado.equals("ESTADOS")){
                if (!categoria.equals("CATEGORIAS")){
                    if (titulo.length() > 5){
                        if (descricao.length() > 20){
                            if (telefone.length() ==  11){
                                if (precoLG > 0 ){
                                    salvarAnuncio();
                                }else{
                                    exibirMensagemErro("Digite Um Valor A cima De 0");
                                }
                            }else {
                                exibirMensagemErro("Digite um Numero De Telefone Valido");
                            }
                        }else{
                            exibirMensagemErro("Escreva uma Descrição com Pelo Menos 20 Caracteres");
                        }
                    }else {
                        exibirMensagemErro("Digite um Titulo com Pelo Menos 5 Caractéres");
                    }
                }else{
                    exibirMensagemErro("Selecione Uma Categoria");
                }
            }else {
                exibirMensagemErro("Selecione Um Estado!");
            }
        }else{
            exibirMensagemErro("Selecione Pelo Menos Duas Imagens");
        }
    }




    private void salvarAnuncio(){

        alertDialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anuncio")
                .setCancelable(false)
                .build();
        alertDialog.show();

        // Salvar Imagens no Storage
        for (int i=0; i < listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas2.get(i);
            int tamanhoImagem = listaFotosRecuperadas2.size();
            salvarFotoStorage(urlImagem,tamanhoImagem, i);
        }
    }


    private void salvarFotoStorage(String urlImagem,int tamanhoLista, int contador){

        // Criar referencia Storage
        final StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+contador+".jpeg");
        // Fazer Upload do Arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlImagem));
        uploadTask.addOnFailureListener(this, e -> {
            exibirMensagemErro("Falha ao fazer upload \n"+ e.getMessage());
            Log.i("Erro Upload", e.getMessage());
        }).addOnSuccessListener(this, taskSnapshot -> imagemAnuncio.getDownloadUrl()
                        .addOnCompleteListener(task -> {
                            Uri url = task.getResult();
                            assert url != null;
                            listaFotosFireBase.add(url.toString());

                            if (tamanhoLista == listaFotosFireBase.size()){
                                anuncio.setFotos(listaFotosFireBase);
                                anuncio.salvar();
                                alertDialog.dismiss();
                                finish();
                            }
                        }));
    }

    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this,mensagem,Toast.LENGTH_SHORT).show();
    }
}