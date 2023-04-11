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
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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


public class EditarFoto extends Activity {

    String imagenGaleria = null;
    String imagenCamara  = null;
    String imagenRecortada  = null;
    String imagenEditada = null;

    Bitmap imageBitMap;
    Button botonAtras;
    Button botonRecortar;
    Button botonGuardar;
    GPUImage gpuImage;
    GPUImageView gpuImageView;
    SeekBar exposicionSeekBar,contrasteSeekBar,sombrasSeekBar,lucesSeekBar,brilloSeekBar,satSeekBar,nitSeekBar;

    TextView textViewExpo,textViewCon,textViewSom,textViewLuc,textViewBr,textViewSat,textViewNit;

    Boolean editada        = false;
    Boolean bajaEficiencia = true; // la pondremos a true para dispositivos lentos

    GPUImageExposureFilter        filtroExposición;
    GPUImageContrastFilter        filtroContraste;
    GPUImageHighlightShadowFilter filtroSombras;
    GPUImageHighlightShadowFilter filtroLuces;
    GPUImageBrightnessFilter      filtroBrillo;
    GPUImageSaturationFilter      filtroSat;
    GPUImageSharpenFilter         filtroNit;

    public static final int REQUEST_WRITE_STORAGE = 111;
    public static final int REQUEST_READ_STORAGE = 222;


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_foto);

        Bundle bundle = getIntent().getExtras();

        imagenCamara           = bundle.getString("bundleRuta");
        imagenGaleria          = bundle.getString("bundleFileName");
        imagenRecortada        = bundle.getString("bundleCrop");
        imagenEditada          = bundle.getString("bundleEditado");

        gpuImageView       = findViewById(R.id.gpuimageview);
        exposicionSeekBar  = findViewById(R.id.seekbarExposicion);
        contrasteSeekBar   = findViewById(R.id.seekbarContraste);
        sombrasSeekBar     = findViewById(R.id.seekbarSombras);
        lucesSeekBar       = findViewById(R.id.seekbarLuces);
        brilloSeekBar      = findViewById(R.id.seekbarBrillo);
        satSeekBar         = findViewById(R.id.seekbarSaturacion);
        nitSeekBar         = findViewById(R.id.seekbarNitidez);


        gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

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

        textViewExpo = findViewById(R.id.textViewExpo);
        textViewCon  = findViewById(R.id.textViewCon);
        textViewSom  = findViewById(R.id.textViewSom);
        textViewLuc  = findViewById(R.id.textViewLuc);
        textViewBr   = findViewById(R.id.textViewBr);
        textViewSat  = findViewById(R.id.textViewSat);
        textViewNit  = findViewById(R.id.textViewNit);

        textViewExpo.setText("30");
        textViewCon .setText("30");
        textViewSom .setText("30");
        textViewLuc .setText("30");
        textViewBr  .setText("30");
        textViewSat .setText("30");
        textViewNit .setText("30");


        // -- ENVIAMOS A RECORTAR --
        botonRecortar = findViewById(R.id.botonRecortar);
        botonRecortar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), RecortarImagen.class);
                Bundle bundle = new Bundle();

                // -- CÁMARA --
                if (imagenCamara != null ){

                    if (editada == true){
                        Log.e("e","enviamos imagen camara con filtro (editarFoto)");
                        String filePath = saveBitmap(gpuImage.getBitmapWithFilterApplied(),"imagen.png");
                        bundle.putString("bundleEditado",filePath);
                    }

                    else{
                        Log.e("e","envio imagen camara sin editar (editarFoto)");
                        bundle.putString("bundleRuta",imagenCamara);

                    }
                }

                // -- GALERIA --
                else if (imagenGaleria != null){

                    if (editada == true){
                        Log.e("e","enviamos imagen galeria con filtro (editarFoto)");
                        String filePath = saveBitmap(gpuImage.getBitmapWithFilterApplied(),"imagen.png");
                        bundle.putString("bundleEditado",filePath);

                    }
                    else{
                        Log.e("e","envio imagen galeria sin editar (editarFoto)");
                        bundle.putString("bundleFileName",imagenGaleria);
                    }
                }

                // -- RECORTADA --
                else if (imagenRecortada != null){

                    if (editada == true){
                        Log.e("e","enviamos imagen recortada con filtros (editarFoto)");
                        String filePath = saveBitmap(gpuImage.getBitmapWithFilterApplied(),"imagen.png");
                        bundle.putString("bundleEditado",filePath);
                    }
                    else{
                        Log.e("e","envio imagen recortada sin editar (editarFoto)");
                        bundle.putString("bundleCrop",imagenRecortada);
                    }
                }

                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // -- RECIBIMOS --

        // -- CÁMARA --
        if (imagenCamara != null){
            // Obtenemos la imagen almacenada en imagenes_capturadas
            imageBitMap = BitmapFactory.decodeFile(imagenCamara);
            gpuImageView.setImage(imageBitMap);
            gpuImage.setImage(imageBitMap);
        }

        // -- GALERIA --
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

        // -- RECORTADA --
        else if (imagenRecortada != null){
            Uri myUri = Uri.parse(imagenRecortada);
            Log.e("d","Recibimos imagen recortada sin editar (editarFoto) en bundleCrop ");
            try {
                imageBitMap = obtenerBitMap(this.getBaseContext(),myUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            gpuImageView.setImage(imageBitMap);
            gpuImage.setImage(imageBitMap);
        }

        // -- EDITADA Y RECORTADA --
        else if (imagenEditada != null){
            Log.e("d","Recibimos imagen con filtros (editarFoto) en bundleEditado");
            Uri myUri = Uri.parse(imagenEditada);
            try {
                imageBitMap = obtenerBitMap(this.getBaseContext(),myUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            gpuImageView.setImage(imageBitMap);
            gpuImage.setImage(imageBitMap);
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
                    textViewExpo.setText("" + i);
                    filtroExposición.setExposure(range(i,-10.0f,10.0f));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    textViewExpo.setText("" + i);
                    filtroExposición.setExposure(range(i,-10.0f,10.0f));
                    aplicarFiltros();
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    textViewCon.setText("" + i);
                    filtroContraste.setContrast(range(i,0.0f,4.0f));
                    aplicarFiltros();
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    textViewSom.setText("" + i);
                    filtroSombras.setShadows(range(i,0.0f,1.0f));
                    aplicarFiltros();
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
                    textViewLuc.setText("" + i);
                    filtroLuces.setHighlights(range(i,0.0f,1.0f));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    textViewLuc.setText("" + i);
                    filtroLuces.setHighlights(range(i,0.0f,1.0f));
                    aplicarFiltros();
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
                    textViewBr.setText("" + i);
                    filtroBrillo.setBrightness(range(i,-1.0f,1.0f));
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    textViewBr.setText("" + i);
                    filtroBrillo.setBrightness(range(i,-1.0f,1.0f));
                    aplicarFiltros();
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
                if (bajaEficiencia == true){
                    int i = seekBar.getProgress();
                    textViewSat.setText("" + i);
                    filtroSat.setSaturation(range(i,0.0f,2.0f));
                    aplicarFiltros();
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
                if (bajaEficiencia == true){
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

    public void aplicarFiltros(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroExposición);
        filterGroup.addFilter(filtroContraste);
        filterGroup.addFilter(filtroSombras);
        filterGroup.addFilter(filtroLuces);
        filterGroup.addFilter(filtroBrillo);
        filterGroup.addFilter(filtroNit);
        filterGroup.addFilter(filtroSat);

        editada = true;

        gpuImage.setFilter(filterGroup);

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




}

