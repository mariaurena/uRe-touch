package TFG.proyecto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilePermission;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    // para la opción de hacer una foto en el momento
    Button botonCamara;
    // para la opción de elegir la foto de la galeria
    Button botonGaleria;
    ImageView imgView;
    // ruta de la imagen capturada con la cámara
    String imagenCamara;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        botonCamara  = findViewById(R.id.botonCamara);
        botonGaleria = findViewById(R.id.botonGaleria);
        imgView      = findViewById(R.id.imageView);

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
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imagenArchivo = null;

        try{
            imagenArchivo = crearImagenCamara();

        } catch(IOException ex){
            Log.e("Error", ex.toString());
        }

        if (imagenArchivo != null){
            Uri fotoUri = FileProvider.getUriForFile(this, "TFG.proyecto.fileprovider", imagenArchivo);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,fotoUri);
            startActivityForResult(intent,1);
        }
    }

    private void abrirGaleria(){
        Intent galeria = new Intent(Intent.ACTION_PICK,MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria,2);
    }

    // para el resultado de la actividad
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // cámara
        if (requestCode == 1 && resultCode == RESULT_OK) {
            // le enviamos a showImage la ruta de la imagen
            Intent intent = new Intent(this,ShowImage.class);
            //intent.putExtra("rutaImagen",rutaImagen);

            // necesario usar 'bundle' para que funcione
            Bundle bundle = new Bundle();
            // imagen cámara
            bundle.putString("bundleRuta",imagenCamara);
            intent.putExtras(bundle);

            startActivity(intent);
        }
        // galeria
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri imagenUri = data.getData();
            Bitmap imageBitmap = null;

            try {
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),imagenUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // guardamos en disco el bitmap (filename)
            try{
                String imagenGaleria = "bitmap.png";
                FileOutputStream stream = this.openFileOutput(imagenGaleria, Context.MODE_PRIVATE);
                imageBitmap.compress(Bitmap.CompressFormat.PNG,100,stream);

                stream.close();
                imageBitmap.recycle();

                Intent intent = new Intent(this,ShowImage.class);
                // necesario usar 'bundle' para que funcione
                Bundle bundle = new Bundle();
                // imagen galeria
                bundle.putString("bundleFileName",imagenGaleria);
                intent.putExtras(bundle);

                startActivity(intent);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    // para generar la ruta de la imagen realizada con la camara
    private File crearImagenCamara() throws IOException {
        String nombreImagen = "foto_";
        // se guarda en storage/1702-0D07/data/TFG.proyecto.files.Pictures
        File directorio     = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imagen         = File.createTempFile(nombreImagen,".jpg",directorio);

        imagenCamara = imagen.getAbsolutePath();
        return imagen;
    }



}