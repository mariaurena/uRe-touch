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
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class DobleExposicion extends AppCompatActivity {

    public MiImagen miImagen;

    public ImageView imgView;

    public GPUImage     gpuImage;
    public GPUImageView gpuImageView;

    public FloatingActionButton importar;
    public Button botonSi;
    public Button botonNo;

    public Bitmap imagenRecibida;
    public Bitmap imagenElegidaGaleria;
    public Bitmap imagenElegidaResize;

    public GPUImageDissolveBlendFilter filtroMezcla;

    public SeekBar seekbarBlend;

    public static final int REQUEST_WRITE_STORAGE = 111;
    public static final int REQUEST_READ_STORAGE = 222;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doble_exposicion);

        gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

        gpuImageView = new GPUImageView(this);
        importar     = findViewById(R.id.importar);

        seekbarBlend    = findViewById(R.id.seekbarBlend);
        seekbarBlend.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarBlend.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        imgView = findViewById(R.id.muestraImagen);

        seekbarBlend.setVisibility(View.GONE); // al principio no la mostramos

        filtroMezcla    = new GPUImageDissolveBlendFilter();
        filtroMezcla.setMix(0.5f);

        miImagen = new MiImagen();

        imagenRecibida = miImagen.getBitmapActual();

        gpuImageView.setImage(imagenRecibida);

        imgView.setImageBitmap(imagenRecibida);

        importar.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                abrirGaleria();
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

        botonSi = findViewById(R.id.botonSi);
        botonSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miImagen.addVersion(gpuImage.getBitmapWithFilterApplied());
                Intent intent = new Intent(view.getContext(), EditarFoto.class);
                startActivity(intent);
            }
        });

        botonNo = findViewById(R.id.botonNo);
        botonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miImagen.setBitmapActual(miImagen.getBitmap_VersionAnterior());
                Intent intent = new Intent(view.getContext(), EditarFoto.class);
                startActivity(intent);
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
                imgView.setImageBitmap(blendedBitmap);

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

       // gpuImageView.setImage(blendedBitmap);
        imgView.setImageBitmap(blendedBitmap);

        gpuImage.setFilter(filterGroup);

        /*

        miImagen.setBitmapEditada(gpuImage.getBitmapWithFilterApplied());
        miImagen.setEstado(3);

         */

    }

    protected float range(final float percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
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