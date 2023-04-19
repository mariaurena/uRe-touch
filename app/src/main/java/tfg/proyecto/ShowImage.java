package tfg.proyecto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.FileInputStream;


public class ShowImage extends Activity {
    ImageView imgView;
    String imagenCamara;
    Button botonNo,botonSi,botonAtras;
    Bitmap imageBitMap = null;
    String imagenGaleria;

    MiImagen miImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image);

        imgView = findViewById(R.id.muestraImagen);

        botonNo = findViewById(R.id.botonNo);

        miImagen = new MiImagen();

        botonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowImage.this,MainActivity.class);
                startActivity(intent);
            }
        });

        botonSi = findViewById(R.id.botonSi);

        // --------------- CÁMARA ---------------

        if (miImagen.getEstado() == 0){
            imgView.setImageBitmap(miImagen.getBitmapCamara());
        }

        // --------------- GALERIA ---------------

        if (miImagen.getEstado() == 1){
            imgView.setImageBitmap(miImagen.getBitmapGaleria());
        }

        botonSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditarFoto.class);
                // necesario usar 'bundle' para que funcione
                Bundle bundle = new Bundle();
                // imagen cámara
                bundle.putString("bundleRuta",imagenCamara);
                // imagen galeria
                bundle.putString("bundleFileName",imagenGaleria);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        botonAtras = findViewById(R.id.botonAtras);

        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });
    }
}