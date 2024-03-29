package tfg.proyecto;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import android.media.ExifInterface;

import java.io.FileInputStream;


public class ShowImage extends Activity {

    public ImageView imgView;
    public Button botonNo,botonSi;

    MiImagen miImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_image);

        imgView = findViewById(R.id.muestraImagen);

        miImagen = new MiImagen();

        botonNo = findViewById(R.id.botonNo);
        botonNo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.back_botton, 0, 0);

        botonNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ShowImage.this,MainActivity.class);
                startActivity(intent);
            }
        });

        botonSi = findViewById(R.id.botonSi);
        botonSi.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.check_botton, 0, 0);

        imgView.setImageBitmap(miImagen.getBitmapActual());

        botonSi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), EditarFoto.class);
                startActivity(intent);
            }
        });
    }
}