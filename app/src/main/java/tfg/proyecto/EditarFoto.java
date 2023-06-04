package tfg.proyecto;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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

import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mayank.simplecropview.CropImageView;

import androidx.appcompat.widget.AppCompatButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.net.ssl.ExtendedSSLSession;

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

    Button botonRecortar;
    Button botonGuardar;
    Button dobleExposicion;
    Button botonImportar;

    FloatingActionButton botonRehacer;
    FloatingActionButton botonDeshacer;
    FloatingActionButton botonVerEdiciones;

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

        imageBitMap = miImagen.getBitmapActual();
        //imageBitMap = miImagen.getVersion(miImagen.getVersionActual());

        imgView.setImageBitmap(imageBitMap);

        setToolBar();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navview);

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
                        startActivity(intent);
                        item.setChecked(false);
                        return true;
                    case R.id.contraste:
                        Intent intent1 = new Intent(getBaseContext(), aplicarFiltro.class);
                        intent1.putExtra("tipoFiltro",1);
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

        botonGuardar = findViewById(R.id.botonGuardar);
        botonGuardar.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.save_botton,0,0);
        botonGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveImage();
            }
        });

        botonImportar = findViewById(R.id.botonImportar);
        botonImportar.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.home_button,0,0);
        botonImportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        botonRehacer = findViewById(R.id.rehacer);
        botonDeshacer = findViewById(R.id.deshacer);

        botonDeshacer.setEnabled(miImagen.getBloquearDeshacer());
        if (miImagen.getBloquearDeshacer() == false){
            Log.e("boton deshacer a false","");
            botonDeshacer.setAlpha(0.5f);
        }
        else{
            Log.e("boton deshacer a true","");
            botonDeshacer.setAlpha(1.0f);
        }

        // al principio, no puede deshacerse nada (controlar mediante miImagen con un bool!!)
        botonDeshacer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                miImagen.setBloqueoRehacer(true);
                botonRehacer.setEnabled(true);
                botonRehacer.setAlpha(1.0f);
                Log.e("boton rehacer a true","h");
                Bitmap bitmapVersionAnterior = miImagen.getBitmap_VersionAnterior();
                imgView.setImageBitmap(bitmapVersionAnterior);
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
            }
        });

        imgView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: // Se ha presionado la imagen
                        imgView.setImageBitmap(miImagen.getVersion(0)); // Mostrar la imagen presionada
                        break;
                    case MotionEvent.ACTION_UP: // Se ha soltado la imagen
                    case MotionEvent.ACTION_CANCEL: // Se ha cancelado el evento táctil
                        imgView.setImageBitmap(miImagen.getBitmapActual()); // Mostrar la imagen original
                        break;
                }
                return true; // Indicar que se ha procesado el evento táctil
            }
        });
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
        }

        return super.onOptionsItemSelected(item);
    }

}

