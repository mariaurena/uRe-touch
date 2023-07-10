package tfg.proyecto;

import android.Manifest;
import android.app.Activity;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yalantis.ucrop.UCrop;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.content.res.ResourcesCompat;

import com.mayank.simplecropview.CropImageView;
import com.mayank.simplecropview.callback.CropCallback;
import com.mayank.simplecropview.callback.LoadCallback;
import com.mayank.simplecropview.callback.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecortarImagen extends AppCompatActivity {

    public Bitmap imageBitMap;
    public CropImageView mCropView;

    MiImagen miImagen;

    private static final String SAMPLE_CROPPED_IMAGE_NAME = "SampleCropImage.jpg";

    public static final int REQUEST_WRITE_STORAGE = 111;
    public static final int REQUEST_READ_STORAGE = 222;

    public String destinationFileName = SAMPLE_CROPPED_IMAGE_NAME;

    public Uri uriARecortar;
    public Uri uriRecortada;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recortar_imagen);

        mCropView = (CropImageView) findViewById(R.id.cropImageView);

        miImagen = new MiImagen();

        imageBitMap = miImagen.getBitmapActual();
        //imageBitMap = miImagen.getVersion(miImagen.getVersionActual());

        uriARecortar = getImageUri(getBaseContext(),imageBitMap);

        // guardaremos la uri de la imagen recortada en la caché
        uriRecortada = Uri.fromFile(new File(getCacheDir(), destinationFileName));

        // Configuración de uCrop
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(80);
        options.withAspectRatio(mCropView.getWidth(),mCropView.getHeight());

        // Inicia la actividad de uCrop
        UCrop.of(uriARecortar, uriRecortada)
                .withOptions(options)
                .start(this);

    }

    // método que obtiene una URI a partir del bitmap dado
    public Uri getImageUri(Context context, Bitmap bitmap) {
        Uri uri = null;
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_" + System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");

        if (permisos_escritura() && permisos_lectura()) {
            try {
                ContentResolver resolver = context.getContentResolver();

                // guardamos la imagen temporalmente en la caché
                uri =  Uri.fromFile(new File(getCacheDir(), destinationFileName));

                OutputStream outstream = resolver.openOutputStream(uri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outstream);
                outstream.flush();
                outstream.close();


            } catch (IOException e) {
                if (uri != null) {
                    ContentResolver resolver = context.getContentResolver();
                    resolver.delete(uri, null, null);
                    uri = null;
                }
            }
        }

        else{
        }
        return uri;
    }

    // pedimos permisos de escritura en tiempo de ejecución (necesario a partir de Android 11 API 30)
    public boolean permisos_escritura(){
        // Verificar si se tienen permisos de escritura en el almacenamiento externo
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Si no hay permisos, solicitarlos
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }
        else{

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
        else{
        }
        return true;
    }

    // manejamos la petición de dichos permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_WRITE_STORAGE || requestCode == REQUEST_READ_STORAGE) {
            // Verificar si los permisos fueron concedidos
            Log.e("P","PERMISO CONCEDIDO");
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, realizar la acción deseada

            } else {
                // Permiso denegado, mostrar un mensaje al usuario o deshabilitar la función
                Log.e("P","PERMISO DENEGADO");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // se ha recortado correctamente
        if (requestCode == UCrop.REQUEST_CROP && resultCode == RESULT_OK) {

            // La uri de la imagen recortada
            Uri resultUri = UCrop.getOutput(data);

            Log.e("Uri guardada en",resultUri.toString());

            if (resultUri != null) {

                Intent intent = new Intent(getApplicationContext(), EditarFoto.class);

                Bitmap bitmapRecortada = null;

                try {
                    bitmapRecortada = obtenerBitMap(this.getBaseContext(),resultUri);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                miImagen.addVersion(bitmapRecortada);
                miImagen.addEdicion("Transformacion");
                startActivity(intent);

            }
            else if (resultCode == UCrop.RESULT_ERROR) {
                final Throwable cropError = UCrop.getError(data);
                if (cropError != null) {
                    cropError.printStackTrace();
                }
            }

        } else if (requestCode == UCrop.REQUEST_CROP && resultCode == UCrop.RESULT_ERROR) {
            // Maneja el error en caso de que ocurra
            Throwable cropError = UCrop.getError(data);
            Log.e("Error onActivityResult",cropError.getMessage());
            cropError.printStackTrace();
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



}
