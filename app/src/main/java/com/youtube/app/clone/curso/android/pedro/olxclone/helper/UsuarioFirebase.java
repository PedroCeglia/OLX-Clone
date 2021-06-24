package com.youtube.app.clone.curso.android.pedro.olxclone.helper;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.youtube.app.clone.curso.android.pedro.olxclone.config.ConfiguracaoFirebase;
import com.youtube.app.clone.curso.android.pedro.olxclone.model.Usuario;

import java.util.Objects;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return Objects.requireNonNull(usuario.getCurrentUser()).getUid();
    }

    /*
    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracaoFirebase.getFirebaseAutenticacao();
        return usuario.getCurrentUser();
    }


    public static boolean atualizarNomeUsuario(String nome){

        try {

            // Usuario Logado
            FirebaseUser user = getUsuarioAtual();
            // Configurando objeto para alteração do perfil
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName( nome )
                    .build();

            // Listener para checar se a operação foi bem sucedida
            user.updateProfile( profile ).addOnCompleteListener(task -> {
                if( !task.isSuccessful() ){
                    Log.d("Perfil", "Erro ao atualizar nome de perfil.");
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

    public static boolean atualizarFotoUsuario(Uri url){

        try {

            FirebaseUser user = getUsuarioAtual();
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setPhotoUri( url )
                    .build();

            user.updateProfile( profile ).addOnCompleteListener(task -> {
                if( !task.isSuccessful() ){
                    Log.d("Perfil", "Erro ao atualizar foto de perfil.");
                }
            });
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }

    // associa os dados do usuario atual a um model de Usuario
    public static Usuario getDadosUsuarioLogadoAuth(){

        // Retorna usuario atual do firebase
        FirebaseUser firebaseUser = getUsuarioAtual();

        // Salva no Usuario que iremos retornar o "Nome , Id , Email"
        Usuario usuario = new Usuario();
        usuario.setEmail( firebaseUser.getEmail() );
        usuario.setId(firebaseUser.getUid());

        // caso o usuario atual tenha uma foto, a mesma sera associada ao model
        if ( firebaseUser.getPhotoUrl() == null ){
            usuario.setFoto("");
        }else {
            usuario.setFoto( firebaseUser.getPhotoUrl().toString() );
        }

        return usuario;

    }

     */
}



