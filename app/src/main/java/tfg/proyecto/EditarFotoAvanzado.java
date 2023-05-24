package tfg.proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageView;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGammaFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGaussianBlurFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageHalftoneFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageLuminanceThresholdFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSobelEdgeDetectionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageSphereRefractionFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageToneCurveFilter;
import jp.co.cyberagent.android.gpuimage.filter.GPUImageVibranceFilter;


public class EditarFotoAvanzado extends AppCompatActivity {

    /*

    String imagenGaleria    = null;
    String imagenCamara     = null;
    String imagenRecortada  = null;
    String imagenEditada    = null;

    public static final int REQUEST_WRITE_STORAGE = 111;
    public static final int REQUEST_READ_STORAGE = 222;

    Button botonAtras;
    Button botonGuardar;
    Button dobleExposicion;
    Button botonByN;

    Boolean byn    = false;

    Button restablecerGau;
    Button restablecerViv;
    Button restablecerGamma;
    Button restablecerEsfera;
    Button restablecerByN;

    Boolean editada = false;

    Bitmap imageBitMap;

    GPUImage gpuImage;
    GPUImageView gpuImageView;

    SeekBar gaussianSeekBar;
    SeekBar vivacidadSeekBar;
    SeekBar gammaSeekBar;
    SeekBar esferaSeekBar;

    TextView textViewGau;
    TextView textViewViv;
    TextView textViewGamma;
    TextView textViewEsfera;

    GPUImageGaussianBlurFilter       filtroGausiano;
    GPUImageVibranceFilter           filtroVivacidad;
    GPUImageGammaFilter              filtroGamma;
    GPUImageSphereRefractionFilter   filtroEsfera;
    GPUImageGrayscaleFilter          filtroByN;

    MiImagen miImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_foto_avanzado);

        gpuImageView = findViewById(R.id.gpuimageview);

        gpuImage = new GPUImage(this); // imagen a la que vamos a aplicar los filtros

        filtroGausiano    = new GPUImageGaussianBlurFilter();
        filtroVivacidad   = new GPUImageVibranceFilter();
        filtroGamma       = new GPUImageGammaFilter();
        filtroEsfera      = new GPUImageSphereRefractionFilter();
        filtroByN         = new GPUImageGrayscaleFilter();

        gaussianSeekBar   = findViewById(R.id.seekbarGaussian);
        vivacidadSeekBar  = findViewById(R.id.seekbarVivacidad);
        gammaSeekBar      = findViewById(R.id.seekbarGamma);
        esferaSeekBar     = findViewById(R.id.seekbarEsfera);

        textViewGau       = findViewById(R.id.textViewGau);
        textViewViv       = findViewById(R.id.textViewViv);
        textViewGamma     = findViewById(R.id.textViewGamma);
        textViewEsfera    = findViewById(R.id.textViewEsfera);

        restablecerGau      = findViewById(R.id.restablecerGau);
        restablecerViv      = findViewById(R.id.restablecerViv);
        restablecerGamma    = findViewById(R.id.restablecerGamma);
        restablecerEsfera   = findViewById(R.id.restablecerEsfera);
        restablecerByN      = findViewById(R.id.restablecerBlancoYNegro);

        textViewGau       .setText("0");
        textViewViv       .setText("30");
        textViewGamma     .setText("30");
        textViewEsfera    .setText("0");

        miImagen = new MiImagen();

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

        // --------------- EDITADA AV ---------------

        else if (miImagen.getEstado() == 4){
            imageBitMap = miImagen.getBitmapEditadaAv();
        }

        gpuImageView.setImage(imageBitMap);
        gpuImage.setImage(imageBitMap);

        botonAtras = findViewById(R.id.botonAtras);

        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditarFoto.class);
                // necesario usar 'bundle' para que funcione
                Bundle bundle = new Bundle();

                if (editada == true){
                    aplicarFiltroSinEsfera();
                    String filePath = saveBitmap(gpuImage.getBitmapWithFilterApplied(),"imagen.png");
                    bundle.putString("bundleEditadoAv",filePath);
                }
                else{
                    if (imagenEditada != null){
                        // la enviaremos como imagenCamara porque imagenEditada espera una URI
                        imagenCamara = imagenEditada;
                        Log.e("d","enviamos editada");
                    }
                    imagenCamara = miImagen.getBitmapCamara().toString();
                    Log.e("d","enviamos demas");
                    bundle.putString("bundleRuta",imagenCamara);
                    Log.e("imagenCamara",imagenCamara);
                    bundle.putString("bundleFileName",imagenGaleria);
                   // Log.e("imagenGaleria",imagenGaleria);
                    bundle.putString("bundleCrop",imagenRecortada);
                  //  Log.e("imagenRecortada",imagenRecortada);

                }

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

        dobleExposicion = findViewById(R.id.botonDobleExposicion);
        dobleExposicion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DobleExposicion.class);
                startActivity(intent);
            }
        });

        // -- gaussian blur --
        ((SeekBar) findViewById(R.id.seekbarGaussian)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewGau.setText("" + i);
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
                    textViewGau.setText("" + i);
                    filtroGausiano.setBlurSize(range(i,0.0f,8.0f));
                    aplicarFiltroSinEsfera();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        restablecerGau.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewGau.setText("0");
                filtroGausiano.setBlurSize(0.0f);
                gaussianSeekBar.setProgress(0);
                aplicarFiltroSinEsfera();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        // -- vivacidad --
        ((SeekBar) findViewById(R.id.seekbarVivacidad)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewViv.setText("" + i);
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
                    textViewViv.setText("" + i);
                    filtroVivacidad.setVibrance(range(i,-1.2f,1.2f));
                    aplicarFiltroSinEsfera();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }
            }
        });

        restablecerViv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewViv.setText("30");
                filtroVivacidad.setVibrance(0.0f);
                vivacidadSeekBar.setProgress(30);
                aplicarFiltroSinEsfera();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        // -- gamma (punto negro) --
        ((SeekBar) findViewById(R.id.seekbarGamma)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewGamma.setText("" + i);
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
                    textViewGamma.setText("" + i);
                    filtroGamma.setGamma(range(i,0.0f,3.0f));
                    aplicarFiltroSinEsfera();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }
            }
        });

        restablecerGamma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewGamma.setText("30");
                filtroGamma.setGamma(0.0f);
                gammaSeekBar.setProgress(30);
                aplicarFiltroSinEsfera();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        // -- esfera --
        ((SeekBar) findViewById(R.id.seekbarEsfera)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (miImagen.getBajaEficiencia() == false){
                    textViewEsfera.setText("" + i);
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
                    textViewEsfera.setText("" + i);
                    filtroEsfera.setRadius(0.5f);
                    filtroEsfera.setRefractiveIndex(range(i,0.0f,1.0f));
                    aplicarFiltroEsfera();
                    gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                    gpuImageView.requestRender();
                }

            }
        });

        restablecerEsfera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textViewEsfera.setText("0");
                esferaSeekBar.setProgress(0);
                aplicarFiltroSinEsfera();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        botonByN = findViewById(R.id.botonBlancoyNegro);

        botonByN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byn = true;
                aplicarFiltroConByn();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });

        restablecerByN = findViewById(R.id.restablecerBlancoYNegro);

        restablecerByN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                byn = false;
                aplicarFiltroSinEsfera();
                gpuImageView.setImage(gpuImage.getBitmapWithFilterApplied());
                gpuImageView.requestRender();
            }
        });


    }

    protected float range(final float percentage, final float start, final float end) {
        return (end - start) * percentage / 100.0f + start;
    }

    public void aplicarFiltroEsfera(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroEsfera);

        editada = true;

        gpuImage.setFilter(filterGroup);

        // restablecemos el resto de seekbars de los filtros

        textViewGamma.setText("30");
        gammaSeekBar.setProgress(30);

        textViewViv.setText("30");
        vivacidadSeekBar.setProgress(30);

        textViewGau.setText("0");
        gaussianSeekBar.setProgress(0);

    }

    public void aplicarFiltroSinEsfera(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filtroGausiano.setBlurSize(0.0f);
        filterGroup.addFilter(filtroGausiano);
        filterGroup.addFilter(filtroVivacidad);
        filterGroup.addFilter(filtroGamma);

        if (byn == true){
            filterGroup.addFilter(filtroByN);
        }

        editada = true;

        gpuImage.setFilter(filterGroup);

        miImagen.setBitmapEditadaAv(gpuImage.getBitmapWithFilterApplied());
        miImagen.setEstado(4);
    }

    public void aplicarFiltroConByn(){
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();

        filterGroup.addFilter(filtroGausiano);
        filterGroup.addFilter(filtroVivacidad);
        filterGroup.addFilter(filtroGamma);
        filterGroup.addFilter(filtroByN);

        editada = true;

        gpuImage.setFilter(filterGroup);

        miImagen.setBitmapEditadaAv(gpuImage.getBitmapWithFilterApplied());
        miImagen.setEstado(4);

    }

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


     */

}