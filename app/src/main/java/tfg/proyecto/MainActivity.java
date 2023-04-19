package tfg.proyecto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;

import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // para la opción de hacer una foto en el momento
    Button botonCamara;
    // para la opción de elegir la foto de la galeria
    Button botonGaleria;
    ImageView imgView;
    // ruta de la imagen capturada con la cámara
    String imagenCamara;

    MiImagen miImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonCamara  = findViewById(R.id.botonCamara);
        botonGaleria = findViewById(R.id.botonGaleria);
        imgView      = findViewById(R.id.imageView);

        miImagen = new MiImagen();

        // para escuchar en el momento de presionar el boton de cámara
        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamara();
            }
        });
        // para escuchar en el momento de presionar el boton de galeria
        botonGaleria.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                abrirGaleria();
            }
        });
    }

    private void abrirCamara(){
        Intent camara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (camara.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(camara, 1);
        }
    }

    private void abrirGaleria(){
        Intent galeria = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria,2);
    }

    // para el resultado de la actividad
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // ------------ CÁMARA ------------

        if (requestCode == 1 && resultCode == RESULT_OK) {

            Intent intent = new Intent(this,ShowImage.class);

            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            miImagen.setEstado(0); // cámara
            miImagen.setBitmapCamara(imageBitmap);

            startActivity(intent);
        }

        // ------------ GALERIA ------------

        if (requestCode == 2 && resultCode == RESULT_OK) {

            Intent intent = new Intent(this, ShowImage.class);

            Uri imagenUri = data.getData();
            Bitmap imageBitmap = null;

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagenUri);
            } catch (IOException e) {
                e.printStackTrace();
            }

            miImagen.setEstado(1); // galeria
            miImagen.setBitmapGaleria(imageBitmap);

            startActivity(intent);
        }


    }
}