package tfg.proyecto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHighlightShadowFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSaturationFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSphereRefractionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVibranceFilter;

public class aplicarFiltro extends AppCompatActivity {

    int tipoFiltro = -1;

    MiImagen miImagen;
    Bitmap imageBitMap;

    Button botonAtras;

    GPUImageExposureFilter filtroExposición;
    GPUImageContrastFilter filtroContraste;
    GPUImageHighlightShadowFilter filtroSombras;
    GPUImageHighlightShadowFilter filtroLuces;
    GPUImageBrightnessFilter filtroBrillo;
    GPUImageSaturationFilter filtroSat;
    GPUImageSharpenFilter filtroNit;

    GPUImageGaussianBlurFilter filtroGausiano;
    GPUImageVibranceFilter filtroVivacidad;
    GPUImageGammaFilter filtroGamma;
    GPUImageSphereRefractionFilter filtroEsfera;
    GPUImageGrayscaleFilter filtroByN;

    Button restablecerExpo;
    Button restablecerContraste;
    Button restablecerSombras;
    Button restablecerLuces;
    Button restablecerBrillo;
    Button restablecerSaturacion;
    Button restablecerNitidez;

    GPUImage gpuImage;
    GPUImageView gpuImageView;
    ImageView imgView;

    SeekBar exposicionSeekBar,contrasteSeekBar,sombrasSeekBar,lucesSeekBar,brilloSeekBar,satSeekBar,nitSeekBar;
    SeekBar seekBar;

    TextView textView,textViewCon,textViewSom,textViewLuc,textViewBr,textViewSat,textViewNit;

    Boolean editada = false;

    Button restablecer;

    Boolean byn    = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aplicar_filtro);

        /*
        recibiremos un int de 'editarFoto' que representa:

        0  : exposición
        1  : contraste
        2  : sombras
        3  : luces
        4  : brillo
        5  : saturación
        6  : nitidez
        7  : gaussiano
        8  : vivacidad
        9  : gamma
        10 : esfera
        11 : blanco y negro

         */

        tipoFiltro = getIntent().getIntExtra("tipoFiltro",-1);

        miImagen = new MiImagen();
        gpuImageView = new GPUImageView(this);
        gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

        // --------------- CÁMARA ---------------

        if (miImagen.getEstado() == 0){
            imageBitMap = miImagen.getBitmapCamara();
        }

        // --------------- GALERIA ---------------

        else if (miImagen.getEstado() == 1){
            imageBitMap = miImagen.getBitmapGaleria();
        }

        // --------------- RECORTADA ---------------

        else if (miImagen.getEstado() == 2){
            imageBitMap = miImagen.getBitmapRecortada();
        }

        // --------------- EDITADA ---------------

        else if (miImagen.getEstado() == 3){
            imageBitMap = miImagen.getBitmapEditada();
        }

        // --------------- ORIGINAL ---------------

        else if (miImagen.getEstado() == 4){
            imageBitMap = miImagen.getBitmapSinFiltro();
        }

        imgView = findViewById(R.id.muestraImagen);
        imgView.setImageBitmap(imageBitMap);

        gpuImageView.setImage(imageBitMap);
        gpuImage.setImage(imageBitMap);

        filtroExposición = new GPUImageExposureFilter();
        filtroExposición.setExposure(0.0f);

        filtroContraste = new GPUImageContrastFilter();
        filtroContraste.setContrast(1.0f);

        filtroSombras = new GPUImageHighlightShadowFilter();
        filtroSombras.setShadows(0.0f);

        filtroLuces = new GPUImageHighlightShadowFilter();
        filtroLuces.setHighlights(1.0f);

        filtroBrillo = new GPUImageBrightnessFilter();
        filtroBrillo.setBrightness(0.0f);

        filtroSat = new GPUImageSaturationFilter();
        filtroSat.setSaturation(1.0f);

        filtroNit = new GPUImageSharpenFilter();
        filtroNit.setSharpness(0.0f);


        filtroGausiano    = new GPUImageGaussianBlurFilter();
        filtroVivacidad   = new GPUImageVibranceFilter();
        filtroGamma       = new GPUImageGammaFilter();
        filtroEsfera      = new GPUImageSphereRefractionFilter();
        filtroByN         = new GPUImageGrayscaleFilter();

        botonAtras = findViewById(R.id.botonAtras);
        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(),EditarFoto.class);
                startActivity(intent);
            }
        });

        textView = findViewById(R.id.textView);

        seekBar = findViewById(R.id.seekbar);

        switch(tipoFiltro){
            case 0:
                // -- exposición --
                seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroExposición.setExposure(range(i,-0.7f,2.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroExposición.setExposure(range(i,-0.7f,2.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.e("d","PULSADO");
                        textView.setText("30");
                        filtroExposición.setExposure(0.0f);
                        seekBar.setProgress(30);
                        aplicarFiltros();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 1:
                // -- contraste --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroContraste.setContrast(range(i,0.0f,4.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroContraste.setContrast(range(i,0.0f,4.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("30");
                        filtroContraste.setContrast(1.0f);
                        seekBar.setProgress(30);
                        aplicarFiltros();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 2:
                // -- sombras --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroSombras.setShadows(range(i,0.0f,1.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroSombras.setShadows(range(i,0.0f,1.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("0");
                        filtroSombras.setShadows(0.0f);
                        seekBar.setProgress(0);
                        aplicarFiltros();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 3:
                // -- luces --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroLuces.setHighlights(range(i,1.0f,0.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroLuces.setHighlights(range(i,1.0f,0.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("0");
                        filtroLuces.setHighlights(1.0f);
                        seekBar.setProgress(0);
                        aplicarFiltros();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 4:
                // -- brillo --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroBrillo.setBrightness(range(i,-0.2f,0.8f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroBrillo.setBrightness(range(i,-0.2f,0.8f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("30");
                        filtroBrillo.setBrightness(0.0f);
                        seekBar.setProgress(30);
                        aplicarFiltros();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 5:
                // -- saturación --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroSat.setSaturation(range(i,0.0f,2.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroSat.setSaturation(range(i,0.0f,2.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("30");
                        filtroSat.setSaturation(1.0f);
                        seekBar.setProgress(30);
                        aplicarFiltros();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 6:
                // -- nitidez --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroNit.setSharpness(range(i,-4.0f,4.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroNit.setSharpness(range(i,-4.0f,4.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("30");
                        filtroNit.setSharpness(0.0f);
                        seekBar.setProgress(30);
                        aplicarFiltros();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 7:
                seekBar.setProgress(0);
                // -- gaussiano --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroGausiano.setBlurSize(range(i,0.0f,8.0f));
                            aplicarFiltroSinEsfera();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroGausiano.setBlurSize(range(i,0.0f,8.0f));
                            aplicarFiltroSinEsfera();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("0");
                        filtroGausiano.setBlurSize(0.0f);
                        seekBar.setProgress(0);
                        aplicarFiltroSinEsfera();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 8:
                // -- vivacidad --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroVivacidad.setVibrance(range(i,-1.2f,1.2f));
                            aplicarFiltroSinEsfera();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroVivacidad.setVibrance(range(i,-1.2f,1.2f));
                            aplicarFiltroSinEsfera();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("30");
                        filtroVivacidad.setVibrance(0.0f);
                        seekBar.setProgress(30);
                        aplicarFiltroSinEsfera();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 9:
                // -- gamma (punto negro) --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroGamma.setGamma(range(i,0.0f,3.0f));
                            aplicarFiltroSinEsfera();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroGamma.setGamma(range(i,0.0f,3.0f));
                            aplicarFiltroSinEsfera();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("30");
                        filtroGamma.setGamma(0.0f);
                        seekBar.setProgress(30);
                        aplicarFiltroSinEsfera();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
                break;

            case 10:
                // -- esfera --
                ((SeekBar) findViewById(R.id.seekbar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textView.setText("" + i);
                            filtroEsfera.setRadius(0.5f);
                            filtroEsfera.setRefractiveIndex(range(i,0.0f,1.0f));
                            aplicarFiltroEsfera();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    // en dispositivos que no admiten tantos frames usaremos este método que únicamente
                    // recoge el valor del seekBar cuando soltamos el dedo
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        if (miImagen.getBajaEficiencia() == true){
                            int i = seekBar.getProgress();
                            textView.setText("" + i);
                            filtroEsfera.setRadius(0.5f);
                            filtroEsfera.setRefractiveIndex(range(i,0.0f,1.0f));
                            aplicarFiltroEsfera();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textView.setText("0");
                        seekBar.setProgress(0);
                        aplicarFiltroSinEsfera();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
            break;

            case 11:
                byn = true;
                aplicarFiltroConByn();
                seekBar.setVisibility(View.INVISIBLE);
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();

                restablecer = findViewById(R.id.restablecer);
                restablecer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        byn = false;
                        aplicarFiltroSinEsfera();
                        gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                        gpuImageView.requestRender();
                    }
                });
            break;

        }

    }

    public void aplicarFiltros(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroBrillo);
        filterGroup.addFilter(filtroExposición);
        filterGroup.addFilter(filtroContraste);
        filterGroup.addFilter(filtroSombras);
        filterGroup.addFilter(filtroLuces);
        filterGroup.addFilter(filtroNit);
        filterGroup.addFilter(filtroSat);

        editada = true;

        gpuImage.setFilter(filterGroup);

        miImagen.setBitmapEditada(gpuImage.getBitmapWithFilterApplied());
        miImagen.setEstado(3);

        imgView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());

    }

    public void aplicarFiltroSinEsfera(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroGausiano);
        filterGroup.addFilter(filtroVivacidad);
        filterGroup.addFilter(filtroGamma);

        if (byn == true){
            filterGroup.addFilter(filtroByN);
        }

        editada = true;

        gpuImage.setFilter(filterGroup);

        miImagen.setBitmapEditada(gpuImage.getBitmapWithFilterApplied());
        miImagen.setEstado(3);

        imgView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
    }

    public void aplicarFiltroEsfera(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroEsfera);

        editada = true;

        gpuImage.setFilter(filterGroup);

        miImagen.setBitmapEditada(gpuImage.getBitmapWithFilterApplied());
        miImagen.setEstado(3);

        imgView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());

    }

    public void aplicarFiltroConByn(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroGausiano);
        filterGroup.addFilter(filtroVivacidad);
        filterGroup.addFilter(filtroGamma);
        filterGroup.addFilter(filtroByN);

        editada = true;

        gpuImage.setFilter(filterGroup);

        miImagen.setEstado(3);
        miImagen.setBitmapEditada(gpuImage.getBitmapWithFilterApplied());

        imgView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());
    }

    protected float range(final float percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
    }
}