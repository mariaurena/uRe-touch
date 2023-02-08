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
import android.widget.SeekBar;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;



public class EditarFoto extends Activity {

    String imagenGaleria = null;
    String imagenCamara  = null;
    Bitmap imageBitMap;
    Button botonAtras;
    GPUImage gpuImage;
    GPUImageView gpuImageView;
    SeekBar exposicionSeekBar,contrasteSeekBar,sombrasSeekBar,lucesSeekBar,brilloSeekBar;

    GPUImageExposureFilter filtroExposición;
    GPUImageContrastFilter filtroContraste;
    GPUImageHighlightShadowFilter filtroSombras;
    GPUImageHighlightShadowFilter filtroLuces;
    GPUImageBrightnessFilter filtroBrillo;

    // parámetros de los filtros básicos
    int exposure   = 0;
    int contrast   = 1; // por defecto, 1
    int shadow     = 0;
    int highlights = 1; // por defecto, 1
    int brightness = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_foto);

        Bundle bundle = getIntent().getExtras();
        imagenCamara  = bundle.getString("bundleRuta");
        imagenGaleria = bundle.getString("bundleFileName");

        gpuImageView = findViewById(R.id.gpuimageview);
        exposicionSeekBar = findViewById(R.id.seekbarExposicion);
        contrasteSeekBar = findViewById(R.id.seekbarContraste);
        sombrasSeekBar = findViewById(R.id.seekbarSombras);
        lucesSeekBar = findViewById(R.id.seekbarLuces);
        brilloSeekBar = findViewById(R.id.seekbarBrillo);

        gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

        filtroExposición = new GPUImageExposureFilter();
        filtroContraste = new GPUImageContrastFilter();
        filtroSombras = new GPUImageHighlightShadowFilter();
        filtroLuces = new GPUImageHighlightShadowFilter();
        filtroBrillo = new GPUImageBrightnessFilter();

        if (imagenCamara != null){
            // Obtenemos la imagen almacenada en imagenes_capturadas
            Bitmap imgBitmap = BitmapFactory.decodeFile(imagenCamara);
            gpuImageView.setImage(imgBitmap);
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
            gpuImageView.setImage(imageBitMap);
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

        // -- exposición --
        ((SeekBar) findViewById(R.id.seekbarExposicion)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                exposure = i;
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // -- contraste --
        ((SeekBar) findViewById(R.id.seekbarContraste)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                contrast = i;
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // -- sombras --
        ((SeekBar) findViewById(R.id.seekbarSombras)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                shadow = i;
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // -- luces --
        ((SeekBar) findViewById(R.id.seekbarLuces)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                highlights = i;
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // -- brillo --
        ((SeekBar) findViewById(R.id.seekbarBrillo)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                brightness = i;
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    protected float range(final int percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
    }

    public GPUImageFilterGroup aplicarFiltros(int exposure, int contrast, int shadow, int highlights, int brightness){

        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();
        filtroExposición.setExposure(range(exposure,-10.0f,10.0f));
        filtroContraste.setContrast(range(contrast,0.0f,4.0f));
        filtroSombras.setShadows(range(shadow,0.0f,1.0f));
        filtroLuces.setHighlights(range(highlights,1.0f,0.0f));
        filtroBrillo.setBrightness(range(brightness,-1.0f,1.0f));

        filterGroup.addFilter(filtroExposición);
        filterGroup.addFilter(filtroContraste);
        filterGroup.addFilter(filtroSombras);
        filterGroup.addFilter(filtroLuces);
        filterGroup.addFilter(filtroBrillo);

        return filterGroup;
    }


}

