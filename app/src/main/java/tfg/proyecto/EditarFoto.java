package tfg.proyecto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.mayank.simplecropview.CropImageView;

import java.io.FileInputStream;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;

import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHueFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter;


public class EditarFoto extends Activity {

    String imagenGaleria = null;
    String imagenCamara  = null;
    Bitmap imageBitMap;
    Button botonAtras;
    Button botonRecortar;
    GPUImage gpuImage;
    GPUImageView gpuImageView;
    SeekBar exposicionSeekBar,contrasteSeekBar,sombrasSeekBar,lucesSeekBar,brilloSeekBar,satSeekBar,tonoSeekBar,nitSeekBar;

    TextView textViewExpo,textViewCon,textViewSom,textViewLuc,textViewBr,textViewSat,textViewTono,textViewNit;

    GPUImageExposureFilter        filtroExposición;
    GPUImageContrastFilter        filtroContraste;
    GPUImageHighlightShadowFilter filtroSombras;
    GPUImageHighlightShadowFilter filtroLuces;
    GPUImageBrightnessFilter      filtroBrillo;
    GPUImageSaturationFilter      filtroSat;
    GPUImageHueFilter             filtroTono;
    GPUImageSharpenFilter         filtroNit;

    // parámetros de los filtros básicos
    float exposure   = 0.0f;
    float contrast   = 1.0f;
    float shadow     = 0.0f;
    float highlights = 1.0f;
    float brightness = 0.0f;
    float saturation = 1.0f;
    float hue        = 90.0f; // tono
    float sharpness  = 0.0f;

    CropImageView mCropView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_foto);

        Bundle bundle = getIntent().getExtras();
        imagenCamara  = bundle.getString("bundleRuta");
        imagenGaleria = bundle.getString("bundleFileName");

        gpuImageView       = findViewById(R.id.gpuimageview);
        exposicionSeekBar  = findViewById(R.id.seekbarExposicion);
        contrasteSeekBar   = findViewById(R.id.seekbarContraste);
        sombrasSeekBar     = findViewById(R.id.seekbarSombras);
        lucesSeekBar       = findViewById(R.id.seekbarLuces);
        brilloSeekBar      = findViewById(R.id.seekbarBrillo);
        satSeekBar         = findViewById(R.id.seekbarSaturacion);
        tonoSeekBar        = findViewById(R.id.seekbarTono);
        nitSeekBar         = findViewById(R.id.seekbarNitidez);

        gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

        filtroExposición  = new GPUImageExposureFilter();
        filtroContraste   = new GPUImageContrastFilter();
        filtroSombras     = new GPUImageHighlightShadowFilter();
        filtroLuces       = new GPUImageHighlightShadowFilter();
        filtroBrillo      = new GPUImageBrightnessFilter();
        filtroSat         = new GPUImageSaturationFilter();
        filtroTono        = new GPUImageHueFilter();
        filtroNit         = new GPUImageSharpenFilter();

        textViewExpo = findViewById(R.id.textViewExpo);
        textViewCon  = findViewById(R.id.textViewCon);
        textViewSom  = findViewById(R.id.textViewSom);
        textViewLuc  = findViewById(R.id.textViewLuc);
        textViewBr   = findViewById(R.id.textViewBr);
        textViewSat  = findViewById(R.id.textViewSat);
        textViewTono = findViewById(R.id.textViewTono);
        textViewNit  = findViewById(R.id.textViewNit);

        textViewExpo.setText("0");
        textViewCon .setText("0");
        textViewSom .setText("0");
        textViewLuc .setText("0");
        textViewBr  .setText("0");
        textViewSat .setText("0");
        textViewTono.setText("0");
        textViewNit .setText("0");


        botonRecortar = findViewById(R.id.botonRecortar);
        // para escuchar en el momento de presionar el boton de recortar
        botonRecortar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RecortarImagen.class);
                // necesario usar 'bundle' para que funcione
                Bundle bundle = new Bundle();
                // enviamos 'imagen camara' o 'imagen galeria'
                if (imagenCamara != null){
                    bundle.putString("bundleRuta",imagenCamara);
                }
                else if (imagenGaleria != null){
                    bundle.putString("bundleFileName",imagenGaleria);
                }
                // imagen galeria
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        if (imagenCamara != null){
            // Obtenemos la imagen almacenada en imagenes_capturadas
            imageBitMap = BitmapFactory.decodeFile(imagenCamara);
            gpuImageView.setImage(imageBitMap);
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
                textViewExpo.setText("" + i);
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                gpuImageView.requestRender();
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
                textViewCon.setText("" + i);
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                gpuImageView.requestRender();
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
                textViewSom.setText("" + i);
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                gpuImageView.requestRender();
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
                textViewLuc.setText("" + i);
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                gpuImageView.requestRender();
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
                textViewBr.setText("" + i);
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                gpuImageView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // -- saturación --
        ((SeekBar) findViewById(R.id.seekbarSaturacion)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                saturation = i;
                textViewSat.setText("" + i);
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                gpuImageView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // -- tono --
        ((SeekBar) findViewById(R.id.seekbarNitidez)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hue = i;
                textViewTono.setText("" + i);
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                gpuImageView.requestRender();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // -- nitidez --
        ((SeekBar) findViewById(R.id.seekbarNitidez)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                sharpness = i;
                textViewNit.setText("" + i);
                gpuImageView.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
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
    protected float range(final float percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
    }

    public GPUImageFilterGroup aplicarFiltros(float exposure, float contrast, float shadow, float highlights, float brightness, float saturation, float hue, float sharpness){

        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();
        filtroExposición.setExposure(range(exposure,0.0f,10.0f));
        filtroContraste .setContrast(range(contrast,0.0f,4.0f));
        filtroSombras   .setShadows(range(shadow,0.0f,1.0f));
        filtroLuces     .setHighlights(range(highlights,1.0f,0.0f));
        filtroBrillo    .setBrightness(range(brightness,-1.0f,1.0f));
        filtroSat       .setSaturation(range(saturation,0.0f,2.0f));
        filtroTono      .setHue(range(hue,0.0f,180.0f));
        filtroNit       .setSharpness(range(sharpness,-4.0f,4.0f));

        filterGroup.addFilter(filtroExposición);
        filterGroup.addFilter(filtroContraste);
        filterGroup.addFilter(filtroSombras);
        filterGroup.addFilter(filtroLuces);
        filterGroup.addFilter(filtroBrillo);
        filterGroup.addFilter(filtroSat);
        filterGroup.addFilter(filtroTono);
        filterGroup.addFilter(filtroNit);

        return filterGroup;
    }




}

