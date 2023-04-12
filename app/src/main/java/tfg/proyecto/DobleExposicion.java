package tfg.proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageAddBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageOpacityFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSubtractBlendFilter;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DobleExposicion extends AppCompatActivity {

    GPUImage     gpuImage;
    GPUImageView gpuImageView;

    Button exportar;
    Button botonGuardar;
    Button botonMezclaResta;

    Bitmap imagenRecibida;
    Bitmap imagenElegidaGaleria;

    String imagenCamara, imagenGaleria;

    GPUImageAddBlendFilter filtroMezcla;
    GPUImageOpacityFilter  filtroOpacidad;


    SeekBar opacidadSeekBar;

    public static final int REQUEST_WRITE_STORAGE = 111;
    public static final int REQUEST_READ_STORAGE = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doble_exposicion);

        Bundle bundle = getIntent().getExtras();
        imagenCamara           = bundle.getString("bundleRuta");
        imagenGaleria          = bundle.getString("bundleFileName");

        gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

        gpuImageView    = findViewById(R.id.gpuimageview);
        exportar        = findViewById(R.id.exportar);
        opacidadSeekBar = findViewById(R.id.seekbarOpacidad);
        opacidadSeekBar.setVisibility(View.GONE); // al principio no la mostramos

        filtroMezcla    = new GPUImageAddBlendFilter();
        filtroOpacidad  = new GPUImageOpacityFilter();


        // -- RECIBIMOS --

        // -- CÁMARA --
        if (imagenCamara != null){
            // Obtenemos la imagen almacenada en imagenes_capturadas
            imagenRecibida = BitmapFactory.decodeFile(imagenCamara);
            gpuImageView.setImage(imagenRecibida);
        }

        // -- GALERIA --
        else if (imagenGaleria != null){
            // descargamos de disco la imagen (filename)
            try {
                FileInputStream is = this.openFileInput(imagenGaleria);
                imagenRecibida = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            gpuImageView.setImage(imagenRecibida);
        }

        exportar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                abrirGaleria();
            }
        });

        botonGuardar = findViewById(R.id.botonGuardar);

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });

        opacidadSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                gpuImage.setImage(imagenRecibida);
                filtroOpacidad.setOpacity(i/100.0f);
                aplicarFiltros();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

    }

    private void abrirGaleria(){
        Intent galeria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(galeria,2);
    }

    // para el resultado de la actividad
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // galeria
        if (requestCode == 2 && resultCode == RESULT_OK) {
            Uri uri = data.getData();
            try {
                imagenElegidaGaleria = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

                gpuImage.setFilter(filtroMezcla);

                filtroMezcla.setBitmap(imagenRecibida);
                gpuImage.setImage(imagenElegidaGaleria);
                Bitmap blendedBitmap = gpuImage.getBitmapWithFilterApplied();

                gpuImageView.setImage(blendedBitmap);

                //mostramos la seekBar para ajustar la opacidad
                opacidadSeekBar.setVisibility(View.VISIBLE);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void aplicarFiltros(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroOpacidad);
        filterGroup.addFilter(filtroMezcla);

        filtroMezcla.setBitmap(imagenRecibida);
        gpuImage.setImage(imagenElegidaGaleria);
        Bitmap blendedBitmap = gpuImage.getBitmapWithFilterApplied();

        gpuImageView.setImage(blendedBitmap);

        gpuImage.setFilter(filterGroup);

    }

    public void saveImage(){

        if (permisos_escritura() && permisos_lectura()) {
            Bitmap bitmap = gpuImageView.getGPUImage().getBitmapWithFilterApplied();
            String displayName = "imagen_editada";

            // Insertar la imagen en la Galería de Android
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

            Uri imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            // Guardar la imagen en la Galería de Android
            try {
                OutputStream imageOut = getContentResolver().openOutputStream(imageUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, imageOut);
                imageOut.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Toast.makeText(this, "La imagen se ha guardado en la galería", Toast.LENGTH_SHORT).show();
        }

    }

    // pedimos permisos de escritura en tiempo de ejecución (necesario a partir de Android 11 API 30)
    public boolean permisos_escritura(){
        // Verificar si se tienen permisos de escritura en el almacenamiento externo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permisos, solicitarlos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
        return true;
    }

    // pedimos permisos de lectura en tiempo de ejecución (necesario a partir de Android 11 API 30)
    public boolean permisos_lectura(){
        // Verificar si se tienen permisos de lectura en el almacenamiento externo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permisos, solicitarlos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_READ_STORAGE);
        }
        return true;
    }

    // manejamos la petición de dichos permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_STORAGE || requestCode == REQUEST_READ_STORAGE) {
            // Verificar si los permisos fueron concedidos
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, realizar la acción deseada
                Log.e("P","PERMISO CONCEDIDO");

            } else {
                // Permiso denegado, mostrar un mensaje al usuario o deshabilitar la función
                Log.e("P","PERMISO DENEGADO");
            }
        }
    }


}