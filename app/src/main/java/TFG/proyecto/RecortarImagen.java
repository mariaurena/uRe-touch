package TFG.proyecto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;

import com.mayank.simplecropview.CropImageView;
import com.mayank.simplecropview.callback.CropCallback;
import com.mayank.simplecropview.callback.LoadCallback;
import com.mayank.simplecropview.callback.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;

public class RecortarImagen extends AppCompatActivity {

    String imagenGaleria = null;
    String imagenCamara  = null;
    Bitmap imageBitMap;
    CropImageView mCropView;
    Button botonAtras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recortar_imagen);

        Bundle bundle = getIntent().getExtras();
        imagenCamara  = bundle.getString("bundleRuta");
        imagenGaleria = bundle.getString("bundleFileName");

        if (imagenCamara != null){
            // Obtenemos la imagen almacenada en imagenes_capturadas
            imageBitMap = BitmapFactory.decodeFile(imagenCamara);
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
        }

        mCropView = (CropImageView) findViewById(R.id.cropImageView);
        Uri uri = getImageUri(getBaseContext(),imageBitMap);
        LoadCallback mLoadCallBack = new LoadCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onError(Throwable e) {

            }
        };
        mCropView.load(uri).execute(mLoadCallBack);

        SaveCallback mSaveCallback = new SaveCallback() {
            @Override
            public void onSuccess(Uri uri) {

            }

            @Override
            public void onError(Throwable e) {

            }
        };

        mCropView.crop(uri).execute(new CropCallback() {
            @Override
            public void onSuccess(Bitmap cropped) {
                mCropView.save(cropped).execute(uri,mSaveCallback);
            }

            @Override
            public void onError(Throwable e) {

            }
        });

        mCropView.setHandleSizeInDp(4);

        botonAtras = findViewById(R.id.botonAtras);

        botonAtras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditarFoto.class);
                // necesario usar 'bundle' para que funcione
                Bundle bundle = new Bundle();

                bundle.putString("bundleRuta",imagenCamara);
                bundle.putString("bundleFileName",imagenGaleria);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


}