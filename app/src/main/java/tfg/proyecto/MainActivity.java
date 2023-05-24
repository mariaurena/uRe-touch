package tfg.proyecto;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.drawable.Drawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Gravity;
import android.view.View;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // para la opción de hacer una foto en el momento
    Button botonCamara;
    // para la opción de elegir la foto de la galeria
    Button botonGaleria;

    MiImagen miImagen;

    File tempFile = null;

    private CameraManager cameraManager;
    private String cameraId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        miImagen = new MiImagen();

        // vamos a hacer que haya un botón que ocupe la primera mitad de la pantalla
        // para elegir la imagen de la cámara

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int screenHeight = metrics.heightPixels;
        int desiredHeight = screenHeight / 2;

        botonCamara = findViewById(R.id.botonCamara);
        botonCamara.setHeight(desiredHeight);

        botonCamara.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_botton, 0, 0);
        botonCamara.setText("Pulsa aqui para hacer una foto");
        botonCamara.setTextColor(getResources().getColor(R.color.icono_add));

        botonCamara.setPadding(0, desiredHeight/3, 0, 0);


        // para escuchar en el momento de presionar el boton de cámara
        botonCamara.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirCamara();
            }
        });

        // vamos a hacer que haya un botón que ocupe la segunda mitad de la pantalla
        // para elegir la imagen de la galeria

        botonGaleria = findViewById(R.id.botonGaleria);
        int secondHalfHeight = screenHeight - desiredHeight;
        botonGaleria.setHeight(secondHalfHeight);

        botonGaleria.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.add_botton, 0, 0);
        botonGaleria.setText("Pulsa aqui para cargar la imagen");
        botonGaleria.setTextColor(getResources().getColor(R.color.icono_add));

        botonGaleria.setPadding(0, desiredHeight/3, 0, 0);

        // para escuchar en el momento de presionar el boton de galeria
        botonGaleria.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                abrirGaleria();
            }
        });
    }

    private void abrirCamara(){
        Intent camara = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        // pedir permisos en tiempo de ejecución
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 123);

        } else {
            // creamos archivo temporal
            try {
                tempFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri imageUri = FileProvider.getUriForFile(this, "tfg.proyecto.fileprovider", tempFile);
            camara.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
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

            Bitmap imageBitmap = BitmapFactory.decodeFile(tempFile.getAbsolutePath());

            miImagen.setEstado(0); // cámara
            miImagen.setBitmapCamara(imageBitmap);

            // para cuando queramos acceder a la imagen sin editar
            miImagen.setBitmapSinFiltro(imageBitmap);

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

            // para cuando queramos acceder a la imagen sin editar
            miImagen.setBitmapSinFiltro(imageBitmap);

            startActivity(intent);
        }
    }

    // usaremos un archivo temporal para almacenar la imagen capturada con la cámara
    // para que no pierda calidad al intentar mostrarla en la ImageView se ShowImage
    private File createImageFile() throws IOException {
        // Crear un archivo temporal con un nombre único
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageFileName, ".jpg", storageDir);
        return imageFile;
    }
}