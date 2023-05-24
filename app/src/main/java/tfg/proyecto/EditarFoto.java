package tfg.proyecto;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import androidx.appcompat.widget.Toolbar;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.mayank.simplecropview.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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



public class EditarFoto extends AppCompatActivity {

    ImageView imgView;

    Bitmap imageBitMap;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    Button botonFiltros;
    Button botonAbrir;
    Button botonRecortar;
    Button botonGuardar;
    Button filtrosAvanzados;
    Button dobleExposicion;

    Button restablecerExpo;
    Button restablecerContraste;
    Button restablecerSombras;
    Button restablecerLuces;
    Button restablecerBrillo;
    Button restablecerSaturacion;
    Button restablecerNitidez;

    Button volverOriginal;

    GPUImage gpuImage;
    GPUImageView gpuImageView;
    SeekBar exposicionSeekBar,contrasteSeekBar,sombrasSeekBar,lucesSeekBar,brilloSeekBar,satSeekBar,nitSeekBar;

    TextView textViewExpo,textViewCon,textViewSom,textViewLuc,textViewBr,textViewSat,textViewNit;

    Boolean editada        = false;

    GPUImageExposureFilter        filtroExposición;
    GPUImageContrastFilter        filtroContraste;
    GPUImageHighlightShadowFilter filtroSombras;
    GPUImageHighlightShadowFilter filtroLuces;
    GPUImageBrightnessFilter      filtroBrillo;
    GPUImageSaturationFilter      filtroSat;
    GPUImageSharpenFilter         filtroNit;

    public static final int REQUEST_WRITE_STORAGE = 111;
    public static final int REQUEST_READ_STORAGE = 222;

    MiImagen miImagen;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_foto);

        miImagen = new MiImagen();
        imgView = findViewById(R.id.muestraImagen);
        //gpuImageView = new GPUImageView(this);
        //gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

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

        imgView.setImageBitmap(imageBitMap);

        // la pondremos en la categoria de "editada" por ejemplo
        miImagen.setBitmapEditada(imageBitMap);
        miImagen.setEstado(3);

        setToolBar();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navview);

        volverOriginal = findViewById(R.id.volverOriginal);
        volverOriginal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miImagen.setEstado(4);
                imageBitMap = miImagen.getBitmapSinFiltro();
                imgView.setImageBitmap(imageBitMap);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case android.R.id.home:
                        drawerLayout.openDrawer(GravityCompat.START);
                        return true;
                    case R.id.exposicion:
                        Intent intent = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent.putExtra("tipoFiltro",0);
                        Log.e("d","mando 0");
                        startActivity(intent);
                        item.setChecked(false);
                        return true;
                    case R.id.contraste:
                        Intent intent1 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent1.putExtra("tipoFiltro",1);
                        Log.e("d","mando 1");
                        startActivity(intent1);
                        item.setChecked(false);
                        return true;
                    case R.id.sombras:
                        Intent intent2 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent2.putExtra("tipoFiltro",2);
                        startActivity(intent2);
                        item.setChecked(false);
                        return true;
                    case R.id.luces:
                        Intent intent3 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent3.putExtra("tipoFiltro",3);
                        startActivity(intent3);
                        item.setChecked(false);
                        return true;
                    case R.id.brillo:
                        Intent intent4 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent4.putExtra("tipoFiltro",4);
                        startActivity(intent4);
                        item.setChecked(false);
                        return true;
                    case R.id.saturación:
                        Intent intent5 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent5.putExtra("tipoFiltro",5);
                        startActivity(intent5);
                        item.setChecked(false);
                        return true;
                    case R.id.nitidez:
                        Intent intent6 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent6.putExtra("tipoFiltro",6);
                        startActivity(intent6);
                        item.setChecked(false);
                        return true;
                    case R.id.gaussiano:
                        Intent intent7 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent7.putExtra("tipoFiltro",7);
                        startActivity(intent7);
                        item.setChecked(false);
                        return true;
                    case R.id.vivacidad:
                        Intent intent8 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent8.putExtra("tipoFiltro",8);
                        startActivity(intent8);
                        item.setChecked(false);
                        return true;
                    case R.id.gamma:
                        Intent intent9 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent9.putExtra("tipoFiltro",9);
                        startActivity(intent9);
                        item.setChecked(false);
                        return true;
                    case R.id.esfera:
                        Intent intent10 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent10.putExtra("tipoFiltro",10);
                        startActivity(intent10);
                        item.setChecked(false);
                        return true;
                    case R.id.byn:
                        Intent intent11 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent11.putExtra("tipoFiltro",11);
                        startActivity(intent11);
                        item.setChecked(false);
                        return true;
                }

                return onOptionsItemSelected(item);
            }
        });

        dobleExposicion = findViewById(R.id.botonDobleExposicion);
        dobleExposicion.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.doble_exposicion_botton, 0, 0);
        dobleExposicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DobleExposicion.class);
                startActivity(intent);
            }
        });

        /*
        imagenCamara           = bundle.getString("bundleRuta");
        imagenGaleria          = bundle.getString("bundleFileName");
        imagenRecortada        = bundle.getString("bundleCrop");
        imagenEditada          = bundle.getString("bundleEditado");
        imagenEditadaAv        = bundle.getString("bundleEditadoAv");


        exposicionSeekBar  = findViewById(R.id.seekbarExposicion);
        contrasteSeekBar   = findViewById(R.id.seekbarContraste);
        sombrasSeekBar     = findViewById(R.id.seekbarSombras);
        sombrasSeekBar.setProgress(0);
        lucesSeekBar       = findViewById(R.id.seekbarLuces);
        lucesSeekBar.setProgress(0);
        brilloSeekBar      = findViewById(R.id.seekbarBrillo);
        satSeekBar         = findViewById(R.id.seekbarSaturacion);
        nitSeekBar         = findViewById(R.id.seekbarNitidez);

        restablecerExpo       = findViewById(R.id.restablecerExpo);
        restablecerContraste  = findViewById(R.id.restablecerContraste);
        restablecerSombras    = findViewById(R.id.restablecerSombras);
        restablecerLuces      = findViewById(R.id.restablecerLuces);
        restablecerBrillo     = findViewById(R.id.restablecerBrillo);
        restablecerSaturacion = findViewById(R.id.restablecerSaturacion);
        restablecerNitidez    = findViewById(R.id.restablecerNitidez);

         */

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

        /*
        textViewExpo = findViewById(R.id.textViewExpo);
        textViewCon  = findViewById(R.id.textViewCon);
        textViewSom  = findViewById(R.id.textViewSom);
        textViewLuc  = findViewById(R.id.textViewLuc);
        textViewBr   = findViewById(R.id.textViewBr);
        textViewSat  = findViewById(R.id.textViewSat);
        textViewNit  = findViewById(R.id.textViewNit);


        textViewExpo.setText("30");
        textViewCon .setText("30");
        textViewSom .setText("0");
        textViewLuc .setText("0");
        textViewBr  .setText("30");
        textViewSat .setText("30");
        textViewNit .setText("30");

         */



        // recortar la imagen
        botonRecortar = findViewById(R.id.botonRecortar);
        botonRecortar.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.crop_botton, 0, 0);

        botonRecortar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RecortarImagen.class);
                startActivity(intent);
            }
        });



        /*
        // filtros avanzados
        filtrosAvanzados = findViewById(R.id.filtrosAvanzados);
        filtrosAvanzados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditarFotoAvanzado.class);
                startActivity(intent);
            }
        });

        botonAbrir = findViewById(R.id.botonAbrir);

        botonAbrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);

                startActivity(intent);
            }
        });

         */

        botonGuardar = findViewById(R.id.botonGuardar);
        botonGuardar.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.save_botton,0,0);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });
        /*

        // -- exposición --
        ((SeekBar) findViewById(R.id.seekbarExposicion)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewExpo.setText("" + i);
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
                    textViewExpo.setText("" + i);
                    filtroExposición.setExposure(range(i,-0.7f,2.0f));
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        restablecerExpo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("d","PULSADO");
                textViewExpo.setText("30");
                filtroExposición.setExposure(0.0f);
                exposicionSeekBar.setProgress(30);
                aplicarFiltros();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        // -- contraste --
        ((SeekBar) findViewById(R.id.seekbarContraste)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewCon.setText("" + i);
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
                    textViewCon.setText("" + i);
                    filtroContraste.setContrast(range(i,0.0f,4.0f));
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        restablecerContraste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewCon.setText("30");
                filtroContraste.setContrast(1.0f);
                contrasteSeekBar.setProgress(30);
                aplicarFiltros();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });


        // -- sombras --
        ((SeekBar) findViewById(R.id.seekbarSombras)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewSom.setText("" + i);
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
                    textViewSom.setText("" + i);
                    filtroSombras.setShadows(range(i,0.0f,1.0f));
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        restablecerSombras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewSom.setText("0");
                filtroSombras.setShadows(0.0f);
                sombrasSeekBar.setProgress(0);
                aplicarFiltros();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        // -- luces --
        ((SeekBar) findViewById(R.id.seekbarLuces)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewLuc.setText("" + i);
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
                    textViewLuc.setText("" + i);
                    filtroLuces.setHighlights(range(i,1.0f,0.0f));
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        restablecerLuces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewLuc.setText("0");
                filtroLuces.setHighlights(1.0f);
                lucesSeekBar.setProgress(0);
                aplicarFiltros();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        // -- brillo --
        ((SeekBar) findViewById(R.id.seekbarBrillo)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewBr.setText("" + i);
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
                    textViewBr.setText("" + i);
                    filtroBrillo.setBrightness(range(i,-0.2f,0.8f));
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        restablecerBrillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewBr.setText("30");
                filtroBrillo.setBrightness(0.0f);
                brilloSeekBar.setProgress(30);
                aplicarFiltros();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        // -- saturación --
        ((SeekBar) findViewById(R.id.seekbarSaturacion)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewSat.setText("" + i);
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
                    textViewSat.setText("" + i);
                    filtroSat.setSaturation(range(i,0.0f,2.0f));
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }
            }
        });

        restablecerSaturacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewSat.setText("30");
                filtroSat.setSaturation(1.0f);
                satSeekBar.setProgress(30);
                aplicarFiltros();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        // -- nitidez --
        ((SeekBar) findViewById(R.id.seekbarNitidez)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewNit.setText("" + i);
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
                    textViewNit.setText("" + i);
                    filtroNit.setSharpness(range(i,-4.0f,4.0f));
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }
            }
        });

        restablecerNitidez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewNit.setText("30");
                filtroNit.setSharpness(0.0f);
                nitSeekBar.setProgress(30);
                aplicarFiltros();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

         */

    }

    protected float range(final float percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
    }

    public void saveImage(){

        if (permisos_escritura() && permisos_lectura()) {
            Bitmap bitmap = imageBitMap;
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

    // método para obtener el string de un bitmap:
    // guarda el bitmap en el almacenamiento externo del dispositivo
    // se guarda en: /storage/emulated/0/Pictures
    public String saveBitmap(Bitmap bitmap, String filename) {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/" + filename;
        File file = new File(filePath);

        if (permisos_escritura() && permisos_lectura()){
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        return filePath;
    }

    // Convertir URI a bitmap
    public static Bitmap obtenerBitMap(Context context, Uri uri) throws FileNotFoundException {

        // Abrir input stream desde la URI
        InputStream inputStream = context.getContentResolver().openInputStream(uri);

        // Decodificar el input stream en un Bitmap
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

        // Cerrar el input stream
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Devolver el bitmap decodificado
        return bitmap;
    }

    private void setToolBar(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_botton);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.exposicion:
                Log.e("d","holaa");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

