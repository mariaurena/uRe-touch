package TFG.proyecto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.uvstudio.him.photofilterlibrary.PhotoFilter;

import java.io.File;
import java.io.FileInputStream;

import jp.co.cyberagent.android.gpuimage.GPUImage;


public class EditarFoto extends Activity {

    ImageView imgView;
    String imagenGaleria = null, imagenCamara = null;
    Bitmap imageBitMap;
    Button botonAtras;
    PhotoFilter photoFilter;
    GPUImage gpuImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_foto);

        imgView = findViewById(R.id.imageView);

        Bundle bundle = getIntent().getExtras();
        imagenCamara  = bundle.getString("bundleRuta");
        imagenGaleria = bundle.getString("bundleFileName");

        photoFilter = new PhotoFilter();
        gpuImage = new GPUImage(this);

        if (imagenCamara != null){
            // Obtenemos la imagen almacenada en imagenes_capturadas
            Bitmap imgBitmap = BitmapFactory.decodeFile(imagenCamara);
            imgView.setImageBitmap(imgBitmap);
            //imgView.setImageBitmap(photoFilter.three(getApplicationContext(),imgBitmap));
        }
        else if (imagenGaleria != null){
            // descargamos de disco la imagen (filename)
            try {
                FileInputStream is = this.openFileInput(imagenGaleria);
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

                bundle.putString("bundleRuta",imagenCamara);
                bundle.putString("bundleFileName",imagenGaleria);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });


    }


}

