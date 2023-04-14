package tfg.proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageAddBlendFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageDissolveBlendFilter;
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

    MiImagen miImagen;

    GPUImage     gpuImage;
    GPUImageView gpuImageView;

    Button exportar;
    Button botonGuardar;

    Bitmap imagenRecibida;
    Bitmap imagenElegidaGaleria;
    Bitmap imagenElegidaResize;

    GPUImageDissolveBlendFilter filtroMezcla;

    SeekBar seekbarBlend;

    public static final int REQUEST_WRITE_STORAGE = 111;
    public static final int REQUEST_READ_STORAGE = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doble_exposicion);

        gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

        gpuImageView    = findViewById(R.id.gpuimageview);
        exportar        = findViewById(R.id.exportar);
        seekbarBlend    = findViewById(R.id.seekbarBlend);

        seekbarBlend.setVisibility(View.GONE); // al principio no la mostramos

        filtroMezcla    = new GPUImageDissolveBlendFilter();
        filtroMezcla.setMix(0.5f);

        miImagen = new MiImagen();

        // --------------- CÁMARA ---------------

        if (miImagen.getEstado() == 0){
            imagenRecibida = miImagen.getBitmapCamara();
        }

        // --------------- GALERIA ---------------

        else if (miImagen.getEstado() == 1){
            imagenRecibida = miImagen.getBitmapGaleria();
        }

        // --------------- RECORTADA ---------------

        else if (miImagen.getEstado() == 2){
            imagenRecibida = miImagen.getBitmapRecortada();
        }

        // --------------- EDITADA ---------------

        else if (miImagen.getEstado() == 3){
            imagenRecibida = miImagen.getBitmapEditada();
        }

        // --------------- EDITADA AV ---------------

        else if (miImagen.getEstado() == 4){
            imagenRecibida = miImagen.getBitmapEditadaAv();
        }

        gpuImageView.setImage(imagenRecibida);

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

        seekbarBlend.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    filtroMezcla.setMix(range(i,0.0f,1.0f));
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (miImagen.getBajaEficiencia() == true){
                    filtroMezcla.setMix(range(seekBar.getProgress(),0.0f,1.0f));
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

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

                // cambiamos el tamaño de la imagen elegida de la galeria para que se ajuste a la imagen recibida
                int width  = imagenRecibida.getWidth();
                int height = imagenRecibida.getHeight();

                imagenElegidaResize = Bitmap.createScaledBitmap(imagenElegidaGaleria,width,height,false);

                gpuImage.setFilter(filtroMezcla);

                filtroMezcla.setBitmap(imagenRecibida);
                gpuImage.setImage(imagenElegidaResize);
                Bitmap blendedBitmap = gpuImage.getBitmapWithFilterApplied();

                gpuImageView.setImage(blendedBitmap);

                //mostramos la seekBar para ajustar la opacidad
                seekbarBlend.setVisibility(View.VISIBLE);
                seekbarBlend.setProgress(60);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void aplicarFiltros(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroMezcla);

        filtroMezcla.setBitmap(imagenRecibida);
        gpuImage.setImage(imagenElegidaResize);
        Bitmap blendedBitmap = gpuImage.getBitmapWithFilterApplied();

        gpuImageView.setImage(blendedBitmap);

        gpuImage.setFilter(filterGroup);

    }

    protected float range(final float percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
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