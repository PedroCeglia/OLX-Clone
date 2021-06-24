package com.youtube.app.clone.curso.android.pedro.olxclone.helper;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class SquareImageView   extends AppCompatImageView {

        public SquareImageView(Context context) {
            super(context);
        }

        public SquareImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);

            // Configurar tamanho do grid
            // recuperamos o tamanho da tela do usuario e dividimos por 3
            int tamanhoGrid = getResources().getDisplayMetrics().widthPixels;
            int tamanhoGridPadding = tamanhoGrid - 20;
            int tamanhoImagem = tamanhoGridPadding/3;

            setMeasuredDimension(tamanhoImagem,tamanhoImagem);
        }



}
