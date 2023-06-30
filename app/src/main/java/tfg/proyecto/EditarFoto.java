package tfg.proyecto;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import java.io.OutputStream;

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


public class EditarFoto extends AppCompatActivity {

    public ImageView imgView;

    public Bitmap imageBitMap;

    private DrawerLayout drawerLayout;

    public FloatingActionButton botonRecortar;
    public FloatingActionButton botonGuardar;
    public FloatingActionButton dobleExposicion;
    public FloatingActionButton botonImportar;

    public FloatingActionButton botonRehacer;
    public FloatingActionButton botonDeshacer;
    public FloatingActionButton botonVerEdiciones;
    public FloatingActionButton botonInfo;

    GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

    int contadorbyn = 0;

    TabLayout tabs;

    Button exposicion,contraste,sombras,luces,brillo,saturacion,vivacidad,gamma,
           nitidez, gaussiano, button_esfera, button_byn, dobleExpo;

    SeekBar seekbarExpo,seekbarContraste,seekbarSom,seekbarLuces,seekbarBri,seekbarSat,
            seekbarViv, seekbarGamma,seekbarNit,seekbarGau,seekbarEsf;

    public TextView textViewExpo, textViewContraste, textViewSom, textViewLuces, textViewBri,
            textViewSat, textViewViv, textViewGamma, textViewNit, textViewGau,
            textViewEsf;

    TextView nombre_efecto;

    public static final int REQUEST_WRITE_STORAGE = 111;
    public static final int REQUEST_READ_STORAGE = 222;

    MiImagen miImagen;
    public GPUImage gpuImage;
    public GPUImageView gpuImageView;

    public GPUImageExposureFilter filtroExposición;
    public GPUImageContrastFilter filtroContraste;
    public GPUImageHighlightShadowFilter filtroSombras;
    public GPUImageHighlightShadowFilter filtroLuces;
    public GPUImageBrightnessFilter filtroBrillo;
    public GPUImageSaturationFilter filtroSat;
    public GPUImageSharpenFilter filtroNit;
    public GPUImageGaussianBlurFilter filtroGausiano;
    public GPUImageVibranceFilter filtroVivacidad;
    public GPUImageGammaFilter filtroGamma;
    public GPUImageSphereRefractionFilter filtroEsfera;
    public GPUImageGrayscaleFilter filtroByN;

    public Boolean byn    = false;
    public Boolean esfera = false;

    Button botonCapas;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_foto);

        miImagen = new MiImagen();
        imgView = findViewById(R.id.muestraImagen);

        imageBitMap = miImagen.getBitmapActual();

        gpuImageView = new GPUImageView(this);
        gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

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

        exposicion    = new Button(getBaseContext());
        contraste     = new Button(getBaseContext());
        sombras       = new Button(getBaseContext());
        luces         = new Button(getBaseContext());
        brillo        = new Button(getBaseContext());
        saturacion    = new Button(getBaseContext());
        vivacidad     = new Button(getBaseContext());
        gamma         = new Button(getBaseContext());
        nitidez       = new Button(getBaseContext());
        gaussiano     = new Button(getBaseContext());
        button_esfera = new Button(getBaseContext());
        button_byn    = new Button(getBaseContext());
        dobleExpo     = new Button(getBaseContext());

        exposicion       .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.exposicion_button, 0, 0);
        contraste        .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.contraste_button, 0, 0);
        sombras          .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.sombras_button, 0, 0);
        luces            .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.luces_button, 0, 0);
        brillo           .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.brillo_button, 0, 0);
        saturacion       .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.saturacion_button, 0, 0);
        vivacidad        .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.vivacidad_button, 0, 0);
        gamma            .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.gamma_button, 0, 0);
        nitidez          .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.nitidez_button, 0, 0);
        gaussiano        .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.gaussiano_button, 0, 0);
        button_esfera    .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.esfera_button, 0, 0);
        button_byn       .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.byn_button, 0, 0);
        dobleExpo        .setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.dobleexpo_button, 0, 0);

        exposicion       .setBackgroundColor(Color.TRANSPARENT);
        contraste        .setBackgroundColor(Color.TRANSPARENT);
        sombras          .setBackgroundColor(Color.TRANSPARENT);
        luces            .setBackgroundColor(Color.TRANSPARENT);
        brillo           .setBackgroundColor(Color.TRANSPARENT);
        saturacion       .setBackgroundColor(Color.TRANSPARENT);
        vivacidad        .setBackgroundColor(Color.TRANSPARENT);
        gamma            .setBackgroundColor(Color.TRANSPARENT);
        nitidez          .setBackgroundColor(Color.TRANSPARENT);
        gaussiano        .setBackgroundColor(Color.TRANSPARENT);
        button_esfera    .setBackgroundColor(Color.TRANSPARENT);
        button_byn       .setBackgroundColor(Color.TRANSPARENT);
        dobleExpo        .setBackgroundColor(Color.TRANSPARENT);

        seekbarExpo      = new SeekBar(this);
        seekbarContraste = new SeekBar(this);
        seekbarSom       = new SeekBar(this);
        seekbarLuces     = new SeekBar(this);
        seekbarBri       = new SeekBar(this);
        seekbarSat       = new SeekBar(this);
        seekbarViv       = new SeekBar(this);
        seekbarGamma     = new SeekBar(this);
        seekbarNit       = new SeekBar(this);
        seekbarGau       = new SeekBar(this);
        seekbarEsf       = new SeekBar(this);

        seekbarExpo.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarExpo.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarContraste.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarContraste.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarSom.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarSom.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarLuces.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarLuces.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarBri.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarBri.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarSat.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarSat.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarViv.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarViv.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarGamma.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarGamma.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarNit.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarNit.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarGau.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarGau.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        seekbarEsf.getProgressDrawable().setColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY);
        seekbarEsf.getThumb().setColorFilter(getResources().getColor(R.color.gris), PorterDuff.Mode.SRC_ATOP);

        LinearLayout linearLayout = findViewById(R.id.ajustes);

        LinearLayout.LayoutParams seekBarParams = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.MATCH_PARENT, 1);
        LinearLayout.LayoutParams textviewParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        seekbarExpo.setLayoutParams(seekBarParams);
        seekbarContraste.setLayoutParams(seekBarParams);
        seekbarSom.setLayoutParams(seekBarParams);
        seekbarLuces.setLayoutParams(seekBarParams);
        seekbarBri.setLayoutParams(seekBarParams);
        seekbarSat.setLayoutParams(seekBarParams);
        seekbarViv.setLayoutParams(seekBarParams);
        seekbarGamma.setLayoutParams(seekBarParams);
        seekbarNit.setLayoutParams(seekBarParams);
        seekbarGau.setLayoutParams(seekBarParams);
        seekbarEsf.setLayoutParams(seekBarParams);

        seekbarExpo.setPadding(20,0,50,0);
        seekbarContraste.setPadding(20,0,50,0);
        seekbarSom.setPadding(20,0,50,0);
        seekbarLuces.setPadding(20,0,50,0);
        seekbarBri.setPadding(20,0,50,0);
        seekbarSat.setPadding(20,0,50,0);
        seekbarViv.setPadding(20,0,50,0);
        seekbarGamma.setPadding(20,0,50,0);
        seekbarNit.setPadding(20,0,50,0);
        seekbarGau.setPadding(20,0,50,0);
        seekbarEsf.setPadding(20,0,50,0);

        textViewExpo      = new TextView(this);
        textViewContraste = new TextView(this);
        textViewSom       = new TextView(this);
        textViewBri       = new TextView(this);
        textViewLuces     = new TextView(this);
        textViewSat       = new TextView(this);
        textViewViv       = new TextView(this);
        textViewGamma     = new TextView(this);
        textViewNit       = new TextView(this);
        textViewGau       = new TextView(this);
        textViewEsf       = new TextView(this);

        textViewExpo.setLayoutParams(textviewParams);
        textViewContraste.setLayoutParams(textviewParams);
        textViewSom.setLayoutParams(textviewParams);
        textViewLuces.setLayoutParams(textviewParams);
        textViewSat.setLayoutParams(textviewParams);
        textViewViv.setLayoutParams(textviewParams);
        textViewGamma.setLayoutParams(textviewParams);
        textViewNit.setLayoutParams(textviewParams);
        textViewGau.setLayoutParams(textviewParams);
        textViewEsf.setLayoutParams(textviewParams);

        textViewExpo.setPadding(0,0,50,0);
        textViewContraste.setPadding(0,0,50,0);
        textViewSom.setPadding(0,0,50,0);
        textViewLuces.setPadding(0,0,50,0);
        textViewSat.setPadding(0,0,50,0);
        textViewViv.setPadding(0,0,50,0);
        textViewGamma.setPadding(0,0,50,0);
        textViewNit.setPadding(0,0,50,0);
        textViewGau.setPadding(0,0,50,0);
        textViewEsf.setPadding(0,0,50,0);

        restablecer_seekbars();

        nombre_efecto = findViewById(R.id.nombre_efecto);

        botonCapas = new Button(this);
        botonCapas.setCompoundDrawablesWithIntrinsicBounds(null,
                null, null, ContextCompat.getDrawable(this, R.drawable.versiones3_button));
        botonCapas.setBackgroundColor(Color.TRANSPARENT);

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(100, ViewGroup.LayoutParams.MATCH_PARENT);
        botonCapas.setLayoutParams(layoutParams2);
        //setToolBar();

        tabs = (TabLayout) findViewById(R.id.tab);
        tabs.addTab(tabs.newTab().setText("Tono y color"));
        tabs.addTab(tabs.newTab().setText("Textura"));
        tabs.addTab(tabs.newTab().setText("Artístico"));

        tabs.clearOnTabSelectedListeners();
        tabs.selectTab(null);

        drawerLayout = findViewById(R.id.drawer_layout);

        LinearLayout menuLayout = findViewById(R.id.menuLayout);

        menuLayout.removeAllViews();
        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            int click_0,click_1,click_2;
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                int selectedTabIndex = tab.getPosition();

                switch (selectedTabIndex) {
                    case 0: // Tono y color
                        click_0++;

                        menuLayout.removeAllViews();

                        menuLayout.addView(exposicion);
                        menuLayout.addView(contraste);
                        menuLayout.addView(sombras);
                        menuLayout.addView(luces);
                        menuLayout.addView(brillo);
                        menuLayout.addView(saturacion);
                        menuLayout.addView(vivacidad);
                        menuLayout.addView(gamma);

                        break;
                    case 1: // Textura
                        click_1++;

                        menuLayout.removeAllViews();

                        menuLayout.addView(nitidez);
                        menuLayout.addView(gaussiano);


                        break;
                    case 2: // Artístico
                        click_2++;

                        menuLayout.removeAllViews();

                        menuLayout.addView(button_esfera);
                        menuLayout.addView(button_byn);
                        menuLayout.addView(dobleExpo);

                        break;
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {


            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                int selectedTabIndex = tab.getPosition();

                switch (selectedTabIndex) {
                    case 0: // Tono y color
                        click_0++;
                        if ((click_0 % 2) == 0){
                            menuLayout.removeAllViews();
                            linearLayout.setVisibility(View.INVISIBLE);
                            nombre_efecto.setVisibility(View.INVISIBLE);
                        }
                        else {
                            menuLayout.removeAllViews();
                            linearLayout.setVisibility(View.VISIBLE);
                            nombre_efecto.setVisibility(View.VISIBLE);

                            menuLayout.addView(exposicion);
                            menuLayout.addView(contraste);
                            menuLayout.addView(sombras);
                            menuLayout.addView(luces);
                            menuLayout.addView(brillo);
                            menuLayout.addView(saturacion);
                            menuLayout.addView(vivacidad);
                            menuLayout.addView(gamma);
                        }

                        break;
                    case 1: // Textura
                        click_1++;
                        if ((click_1 % 2) == 0) {
                            menuLayout.removeAllViews();
                            linearLayout.setVisibility(View.INVISIBLE);
                            nombre_efecto.setVisibility(View.INVISIBLE);
                        }
                        else {
                            menuLayout.removeAllViews();

                            linearLayout.setVisibility(View.VISIBLE);
                            nombre_efecto.setVisibility(View.VISIBLE);
                            menuLayout.addView(nitidez);
                            menuLayout.addView(gaussiano);
                        }

                        break;
                    case 2: // Artístico
                        click_2++;

                        if ((click_2 % 2) == 0) {
                            menuLayout.removeAllViews();
                            linearLayout.setVisibility(View.INVISIBLE);
                            nombre_efecto.setVisibility(View.INVISIBLE);
                        }
                        else {
                            menuLayout.removeAllViews();

                            linearLayout.setVisibility(View.VISIBLE);
                            nombre_efecto.setVisibility(View.VISIBLE);
                            menuLayout.addView(button_esfera);
                            menuLayout.addView(button_byn);
                            menuLayout.addView(dobleExpo);
                        }

                        break;
                }
            }
        });

        exposicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Exposición");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarExpo);
                linearLayout.addView(textViewExpo);
                linearLayout.setVisibility(View.VISIBLE);
                miImagen.setBloqueoDeshacer(true);
                // -- exposición --
                seekbarExpo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false) {
                            textViewExpo.setText("" + i);
                            filtroExposición.setExposure(range(i, -0.7f, 2.0f));
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
                        if (miImagen.getBajaEficiencia() == true) {
                            int i = seekBar.getProgress();
                            textViewExpo.setText("" + i);
                            filtroExposición.setExposure(range(i, -0.7f, 2.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });
            }
        });

        contraste.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Contraste");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarContraste);
                linearLayout.addView(textViewContraste);
                linearLayout.setVisibility(View.VISIBLE);
                // -- contraste --
                seekbarContraste.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textViewContraste.setText("" + i);
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
                            textViewContraste.setText("" + i);
                            filtroContraste.setContrast(range(i,0.0f,4.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });
            }
        });

        sombras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Sombras");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarSom);
                linearLayout.addView(textViewSom);
                linearLayout.setVisibility(View.VISIBLE);
                seekbarSom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
            }
        });

        luces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Luces");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarLuces);
                linearLayout.addView(textViewLuces);
                linearLayout.setVisibility(View.VISIBLE);
                seekbarLuces.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textViewLuces.setText("" + i);
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
                            textViewLuces.setText("" + i);
                            filtroLuces.setHighlights(range(i,1.0f,0.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });
            }
        });

        brillo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Brillo");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarBri);
                linearLayout.addView(textViewBri);
                linearLayout.setVisibility(View.VISIBLE);
                seekbarBri.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textViewBri.setText("" + i);
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
                            textViewBri.setText("" + i);
                            filtroBrillo.setBrightness(range(i,-0.2f,0.8f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });
            }
        });

        saturacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Saturación");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarSat);
                linearLayout.addView(textViewSat);
                linearLayout.setVisibility(View.VISIBLE);
                seekbarSat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
            }
        });

        nitidez.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Nitidez");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarNit);
                linearLayout.addView(textViewNit);
                linearLayout.setVisibility(View.VISIBLE);
                seekbarNit.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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
            }
        });

        gaussiano.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Gaussiano");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarGau);
                linearLayout.addView(textViewGau);
                linearLayout.setVisibility(View.VISIBLE);
                seekbarGau.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textViewGau.setText("" + i);
                            filtroGausiano.setBlurSize(range(i,0.0f,8.0f));
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
                            textViewGau.setText("" + i);
                            filtroGausiano.setBlurSize(range(i,0.0f,8.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });
            }
        });

        vivacidad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Vivacidad");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarViv);
                linearLayout.addView(textViewViv);
                linearLayout.setVisibility(View.VISIBLE);
                seekbarViv.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textViewViv.setText("" + i);
                            filtroVivacidad.setVibrance(range(i,-1.2f,1.2f));
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
                            textViewViv.setText("" + i);
                            filtroVivacidad.setVibrance(range(i,-1.2f,1.2f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });
            }
        });

        gamma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                nombre_efecto.setText("Gamma");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarGamma);
                linearLayout.addView(textViewGamma);
                linearLayout.setVisibility(View.VISIBLE);
                seekbarGamma.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            textViewGamma.setText("" + i);
                            filtroGamma.setGamma(range(i,0.0f,3.0f));
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
                            textViewGamma.setText("" + i);
                            filtroGamma.setGamma(range(i,0.0f,3.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });
            }
        });

        button_esfera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // -- esfera --
                linearLayout.removeAllViews();
                nombre_efecto.setText("Ojo de pez");
                linearLayout.addView(botonCapas);
                linearLayout.addView(seekbarEsf);
                linearLayout.addView(textViewEsf);
                linearLayout.setVisibility(View.VISIBLE);
                seekbarEsf.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                        if (miImagen.getBajaEficiencia() == false){
                            if (seekBar.getProgress() == 0){
                                esfera = false;
                                textViewEsf.setText("0");
                                aplicarFiltros();
                            }
                            else{
                                esfera = true;
                                textViewEsf.setText("" + i);
                                filtroEsfera.setRadius(0.5f);
                                filtroEsfera.setRefractiveIndex(range(i,0.0f,1.0f));
                                aplicarFiltros();
                                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                                gpuImageView.requestRender();
                            }
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
                            textViewEsf.setText("" + i);
                            filtroEsfera.setRadius(0.5f);
                            filtroEsfera.setRefractiveIndex(range(i,0.0f,1.0f));
                            aplicarFiltros();
                            gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                            gpuImageView.requestRender();
                        }

                    }
                });
            }
        });

        button_byn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                linearLayout.removeAllViews();
                linearLayout.addView(botonCapas);
                nombre_efecto.setText("Blanco y negro");
                contadorbyn++;
                if ((contadorbyn % 2) == 0){
                    byn = false;
                    aplicarFiltros();
                }
                else{
                    byn = true;
                    aplicarFiltros();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }
            }
        });

        dobleExpo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miImagen.setBitmapActual(gpuImage.getBitmapWithFilterApplied());
                Intent intent = new Intent(view.getContext(), DobleExposicion.class);
                startActivity(intent);
            }
        });

        botonCapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miImagen.addVersion(gpuImage.getBitmapWithFilterApplied());
                Toast.makeText(getBaseContext(), "Se ha añadido una nueva versión", Toast.LENGTH_SHORT).show();
                botonDeshacer.setEnabled(true);
                botonDeshacer.setAlpha(1.0f);
            }
        });

        // recortar la imagen
        botonRecortar = findViewById(R.id.botonRecortar);

        botonRecortar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RecortarImagen.class);
                startActivity(intent);
            }
        });

        botonGuardar = findViewById(R.id.botonGuardar);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });

        botonImportar = findViewById(R.id.botonImportar);
        botonImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        botonRehacer = findViewById(R.id.rehacer);
        botonDeshacer = findViewById(R.id.deshacer);

        botonDeshacer.setEnabled(false);
        botonDeshacer.setAlpha(0.5f);

        // al principio, no puede deshacerse nada
        botonDeshacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (miImagen.getNumeroVersiones() > 1){
                    restablecer_seekbars();
                    miImagen.setBloqueoRehacer(true);
                    botonRehacer.setEnabled(true);
                    botonRehacer.setAlpha(1.0f);
                    Log.e("boton rehacer a true","h");
                    Bitmap bitmapVersionAnterior = miImagen.getBitmap_VersionAnterior();
                    imgView.setImageBitmap(bitmapVersionAnterior);
                }
                //miImagen.addVersion(bitmapVersionAnterior);
            }
        });

        botonVerEdiciones = findViewById(R.id.verEdiciones);
        botonVerEdiciones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), VerVersiones.class);
                startActivity(intent);
            }
        });

        botonRehacer.setEnabled(miImagen.getBloquearRehacer());
        if (miImagen.getBloquearRehacer() == false){
            Log.e("esta a false","");
            botonRehacer.setAlpha(0.5f);
        }
        else{
            Log.e("esta a true","");
            botonRehacer.setAlpha(1.0f);
        }

        botonRehacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap bitmapVersionSiguiente = miImagen.getBitmap_VersionSiguiente();
                Log.e("versio siguiente editar",String.valueOf(bitmapVersionSiguiente));
                imgView.setImageBitmap(bitmapVersionSiguiente);
                miImagen.addVersion(bitmapVersionSiguiente);
                // al pulsar una vez en rehacer, se bloquea el boton
                miImagen.setBloqueoRehacer(false);
                botonRehacer.setEnabled(miImagen.getBloquearRehacer());
                botonRehacer.setAlpha(0.5f);
                restablecer_seekbars();
            }
        });

        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // Se ha presionado la imagen
                        if (miImagen.getNumeroVersiones() > 1){
                            imgView.setImageBitmap(miImagen.getVersion(0)); // Mostrar la imagen presionada
                        }
                        break;
                    case MotionEvent.ACTION_UP: // Se ha soltado la imagen
                    case MotionEvent.ACTION_CANCEL: // Se ha cancelado el evento táctil
                        if (miImagen.getNumeroVersiones() > 1){
                            imgView.setImageBitmap(miImagen.getBitmapActual()); // Mostrar la imagen original
                        }
                        break;
                }
                return true; // Indicar que se ha procesado el evento táctil
            }
        });

        botonInfo = findViewById(R.id.botonInfo);
        botonInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://uretouch6.wordpress.com/");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });


    }

    public void saveImage(){

        if (permisos_escritura() && permisos_lectura()) {
            Bitmap bitmap = gpuImage.getBitmapWithFilterApplied();
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

    protected float range(final float percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
    }

    public void aplicarFiltros(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroExposición);
        filterGroup.addFilter(filtroContraste);
        filterGroup.addFilter(filtroSombras);
        filterGroup.addFilter(filtroLuces);
        filterGroup.addFilter(filtroBrillo);
        filterGroup.addFilter(filtroNit);
        filterGroup.addFilter(filtroSat);
        filterGroup.addFilter(filtroGausiano);
        filterGroup.addFilter(filtroVivacidad);
        filterGroup.addFilter(filtroGamma);



        if (esfera == true){
            filterGroup.addFilter(filtroEsfera);
        }

        if (byn == true){
            filterGroup.addFilter(filtroByN);
        }

        gpuImage.setFilter(filterGroup);

        imgView.setImageBitmap(gpuImage.getBitmapWithFilterApplied());

    }

    public void restablecer_seekbars(){
        seekbarExpo.setProgress(50);
        seekbarContraste.setProgress(50);
        seekbarSom.setProgress(50);
        seekbarLuces.setProgress(50);
        seekbarBri.setProgress(50);
        seekbarSat.setProgress(50);
        seekbarViv.setProgress(50);
        seekbarGamma.setProgress(50);
        seekbarNit.setProgress(50);
        seekbarGau.setProgress(50);
        seekbarEsf.setProgress(0);
    }

}

