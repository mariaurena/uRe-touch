package tfg.proyecto;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.mayank.simplecropview.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
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


public class EditarFoto extends Activity {

    String imagenGaleria = null;
    String imagenCamara  = null;
    String imagenRecortada  = null;
    Bitmap imageBitMap;
    Button botonAtras;
    Button botonRecortar;
    Button botonGuardar;
    GPUImage gpuImage;
    GPUImageView gpuImageView;
    ImageView imagenEditada;
    SeekBar exposicionSeekBar,contrasteSeekBar,sombrasSeekBar,lucesSeekBar,brilloSeekBar,satSeekBar,tonoSeekBar,nitSeekBar;

    TextView textViewExpo,textViewCon,textViewSom,textViewLuc,textViewBr,textViewSat,textViewTono,textViewNit;

    Boolean editada        = false;
    Boolean bajaEficiencia = true; // la pondremos a true para dispositivos lentos

    GPUImageExposureFilter        filtroExposición;
    GPUImageContrastFilter        filtroContraste;
    GPUImageHighlightShadowFilter filtroSombras;
    GPUImageHighlightShadowFilter filtroLuces;
    GPUImageBrightnessFilter      filtroBrillo;
    GPUImageSaturationFilter      filtroSat;
    GPUImageHueFilter             filtroTono;
    GPUImageSharpenFilter         filtroNit;

    public static final int REQUEST_WRITE_STORAGE = 111;
    public static final int REQUEST_READ_STORAGE = 222;

    // parámetros de los filtros básicos
    float exposure   = 0.0f;
    float contrast   = 1.0f;
    float shadow     = 0.0f;
    float highlights = 1.0f;
    float brightness = 0.0f;
    float saturation = 1.0f;
    float hue        = 90.0f; // tono
    float sharpness  = 0.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_foto);

        Bundle bundle   = getIntent().getExtras();
        imagenCamara    = bundle.getString("bundleRuta");
        imagenGaleria   = bundle.getString("bundleFileName");
        imagenRecortada = bundle.getString("bundleCrop");

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

        Handler handler = new Handler(Looper.getMainLooper());

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

                    if (editada == true){
                        Log.e("e","enviamos imagen camara con filtro");
                        String filePath = saveBitmap(gpuImage.getBitmapWithFilterApplied(),"imagen.png");
                        bundle.putString("bundleEditado",filePath);
                    }
                    else{
                        Log.e("e","envio imagen camara sin editar");
                        bundle.putString("bundleRuta",imagenCamara);
                    }
                }
                else if (imagenGaleria != null){
                    if (editada == true){
                        Log.e("e","enviamos imagen galeria con filtro");
                        String filePath = saveBitmap(gpuImage.getBitmapWithFilterApplied(),"imagen.png");
                        bundle.putString("bundleEditado",filePath);

                    }
                    else{
                        Log.e("e","envio imagen galeria sin editar");
                        bundle.putString("bundleFileName",imagenGaleria);
                    }
                }
                // enviamos la imagen recortada si queremos volver a recortarla
                else if (imagenRecortada != null){
                    if (gpuImage.getBitmapWithFilterApplied() != null){
                        Log.e("e","enviamos imagen con filtros");
                        String bitMapEditado = gpuImage.toString();
                        bundle.putString("bundleEditado",bitMapEditado);
                    }
                    else{
                        bundle.putString("bundleCrop",imagenRecortada);
                    }
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
            gpuImage.setImage(imageBitMap);
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
            gpuImage.setImage(imageBitMap);
        }
        else if (imagenRecortada != null){
            Uri myUri = Uri.parse(imagenRecortada);
            gpuImageView.setImage(myUri);
            gpuImage.setImage(myUri);
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

        botonGuardar = findViewById(R.id.botonGuardar);

        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });

        // -- exposición --
        ((SeekBar) findViewById(R.id.seekbarExposicion)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (bajaEficiencia == false){
                    Log.e("a","alta eficiencia");
                    exposure = i;
                    textViewExpo.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
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
                if (bajaEficiencia == true){
                    Log.e("b","baja eficiencia");
                    int i = seekBar.getProgress();
                    exposure = i;
                    textViewExpo.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        // -- contraste --
        ((SeekBar) findViewById(R.id.seekbarContraste)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (bajaEficiencia == false){
                    contrast = i;
                    textViewCon.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    contrast = i;
                    textViewCon.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });


        // -- sombras --
        ((SeekBar) findViewById(R.id.seekbarSombras)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (bajaEficiencia == false){
                    shadow = i;
                    textViewSom.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    shadow = i;
                    textViewSom.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        // -- luces --
        ((SeekBar) findViewById(R.id.seekbarLuces)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (bajaEficiencia == false){
                    highlights = i;
                    textViewLuc.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    highlights = i;
                    textViewLuc.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        // -- brillo --
        ((SeekBar) findViewById(R.id.seekbarBrillo)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (bajaEficiencia == false){
                    brightness = i;
                    textViewBr.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    brightness = i;
                    textViewBr.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        // -- saturación --
        ((SeekBar) findViewById(R.id.seekbarSaturacion)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (bajaEficiencia == false){
                    saturation = i;
                    textViewSat.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    saturation = i;
                    textViewSat.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }
            }
        });

        // -- tono --
        ((SeekBar) findViewById(R.id.seekbarNitidez)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (bajaEficiencia == false){
                    hue = i;
                    textViewTono.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    hue = i;
                    textViewTono.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }
            }
        });

        // -- nitidez --
        ((SeekBar) findViewById(R.id.seekbarNitidez)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (bajaEficiencia == false){
                    sharpness = i;
                    textViewNit.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    sharpness = i;
                    textViewNit.setText("" + i);
                    gpuImage.setFilter(aplicarFiltros(exposure,contrast,shadow,highlights,brightness,saturation,hue,sharpness));
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }
            }
        });

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


        editada = true;

        return filterGroup;
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

    // guardar el bitmap en el almacenamiento externo del dispositivo
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




}

