package com.youtube.app.clone.curso.android.pedro.olxclone.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.youtube.app.clone.curso.android.pedro.olxclone.R;
import com.youtube.app.clone.curso.android.pedro.olxclone.config.ConfiguracaoFirebase;

import java.util.Objects;

public class CadastroActivity extends AppCompatActivity {

    private TextView tvLogin, tvCadastro;
    private Button btAcessar;
    private ProgressBar progressBar;
    private EditText etEmail, etSenha;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchCadastro;
    private FirebaseAuth autenticacao;
    private String textoEmail, textoSenha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastros);

        inicializandoComponentes();

        btAcessar.setOnClickListener(v -> {
            textoEmail = etEmail.getText().toString();
            textoSenha = etSenha.getText().toString();
            if (verificacaoDosCampos()){
                escondendoWidgets();
                if (switchCadastro.isChecked()){

                    cadastroUsuario();

                }else{

                    logarUsuario();

                }
            }
        });


    }

    private void inicializandoComponentes(){

        btAcessar = findViewById(R.id.btAcessar);
        etEmail = findViewById(R.id.etEmail);
        etSenha = findViewById(R.id.etSenha);
        switchCadastro = findViewById(R.id.switchCadastro);
        progressBar = findViewById(R.id.progressBarLoginCadastro);
        tvCadastro = findViewById(R.id.tvCadastrar);
        tvLogin = findViewById(R.id.tvLogar);

        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

    }

    private Boolean verificacaoDosCampos(){
        if (!textoEmail.isEmpty()){
            if (!textoSenha.isEmpty()){
                return true;
            }else{
                Toast.makeText(CadastroActivity.this, "Digite Seu Email!!\n Não Fazemos Milagres", Toast.LENGTH_SHORT).show();
                return false;
            }
        }else{
            Toast.makeText(CadastroActivity.this, "Digite Sua Senha!!\n Não Fazemos Milagres", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private void cadastroUsuario(){
        autenticacao.createUserWithEmailAndPassword(textoEmail, textoSenha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        try {
                            abrirTelaPrincipal();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else {
                        exibindoWidgets();
                        String excecao ;
                        try {
                            throw Objects.requireNonNull(task.getException());
                        }catch ( FirebaseAuthWeakPasswordException e){
                            excecao = "Digite uma senha mais forte!";
                        }catch ( FirebaseAuthInvalidCredentialsException e){
                            excecao= "Por favor, digite um e-mail válido";
                        }catch ( FirebaseAuthUserCollisionException e){
                            excecao = "Este conta já foi cadastrada";
                        }catch (Exception e){
                            excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                            e.printStackTrace();
                        }

                        Toast.makeText(CadastroActivity.this,
                                excecao,
                                Toast.LENGTH_SHORT).show();


                    }
                });
    }

    private void logarUsuario(){
        autenticacao.signInWithEmailAndPassword(textoEmail, textoSenha)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()){
                        try {
                            abrirTelaPrincipal();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }else{

                        exibindoWidgets();
                        String excecao;
                        try {
                            throw Objects.requireNonNull(task.getException());
                        }catch ( FirebaseAuthInvalidUserException e ) {
                            excecao = "Usuário não está cadastrado.";
                        }catch ( FirebaseAuthInvalidCredentialsException e ){
                            excecao = "E-mail e senha não correspondem a um usuário cadastrado";
                        }catch (Exception e){
                            excecao = "Erro ao cadastrar usuário: "  + e.getMessage();
                            e.printStackTrace();
                        }
                        Toast.makeText(CadastroActivity.this,
                                excecao,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void abrirTelaPrincipal(){
        //Intent intent = new Intent(CadastroActivity.this, AnunciosActivity.class);
        //startActivity( intent );
        finish();
    }

    private void escondendoWidgets(){
        progressBar.setVisibility(View.VISIBLE);
        btAcessar.setVisibility(View.GONE);
        etEmail.setVisibility(View.GONE);
        etSenha.setVisibility(View.GONE);
        switchCadastro.setVisibility(View.GONE);
        tvLogin.setVisibility(View.GONE);
        tvCadastro.setVisibility(View.GONE);

    }
    private void exibindoWidgets(){
        progressBar.setVisibility(View.GONE);
        btAcessar.setVisibility(View.VISIBLE);
        etEmail.setVisibility(View.VISIBLE);
        etEmail.setText("");
        etSenha.setVisibility(View.VISIBLE);
        etSenha.setText("");
        switchCadastro.setVisibility(View.VISIBLE);
        tvLogin.setVisibility(View.VISIBLE);
        tvCadastro.setVisibility(View.VISIBLE);
    }
}