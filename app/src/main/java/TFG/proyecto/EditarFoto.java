package TFG.proyecto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import java.io.File;
import java.io.FileInputStream;


public class EditarFoto extends Activity {

    ImageView imgView;
    String filename = null, rutaImagen = null;
    Bitmap imageBitMap;
    Button botonAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_foto);

        imgView = findViewById(R.id.imageView);

        Bundle bundle = getIntent().getExtras();
        rutaImagen = bundle.getString("bundleRuta");
        filename   = bundle.getString("bundleFileName");

        if (rutaImagen != null){
            // Obtenemos la imagen almacenada en imagenes_capturadas
            Bitmap imgBitmap = BitmapFactory.decodeFile(rutaImagen);
            imgView.setImageBitmap(imgBitmap);
        }
        else if (filename != null){
            // descargamos de disco la imagen (filename)
            try {
                FileInputStream is = this.openFileInput(filename);
                imageBitMap = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            imgView.setImageBitmap(imageBitMap);
        }

        botonAtras = findViewById(R.id.botonAtras);

        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), ShowImage.class);
                // necesario usar 'bundle' para que funcione
                Bundle bundle = new Bundle();
                // imagen cámara
                bundle.putString("bundleRuta",rutaImagen);
                // imagen galeria
                bundle.putString("bundleFileName",filename);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });


    }


}
