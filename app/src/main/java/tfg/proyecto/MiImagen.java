package tfg.proyecto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class MiImagen {

    // guardamos el historial de versiones de la edición
    private static ArrayList<Bitmap> historial = new ArrayList<>();
    static private int nVersion = -1;

    public static Boolean bajaEficiencia = false;


    public void addVersion(Bitmap bit){
        historial.add(bit);
        nVersion++;
        listarBitmaps("addVersion");
    }

    private void listarBitmaps( String message )
    {
        Log.e("listando Bitmaps",message);
        Log.e("     nVersion= ",String.valueOf(nVersion));
        Log.e("     tamArray= ",String.valueOf(historial.size()));
        for (int i = 0 ; i<=nVersion ; i++){
            Log.e("       Bitmap: ",String.valueOf(historial.get(i)));
        }
    }

    public Bitmap getBitmapActual(){
        if (nVersion < 0){
            Log.e("error","nVersion es -1");
        }

        Bitmap a_devolver = historial.get(nVersion);
        listarBitmaps("getBitmapActual");
        return a_devolver;
        //return historial.get(nVersion);
    }

    public Boolean getBajaEficiencia(){
        return this.bajaEficiencia;
    }

    public Bitmap getBitmap_VersionAnterior(){
        Bitmap a_devolver = null;

        if (nVersion == 0){
            Log.e("la version anterior es ",String.valueOf(0));
            a_devolver = historial.get(0);
            Log.e("hay estas versiones",String.valueOf(historial.size()));
        }
        else{
            Log.e("la version anterior es ",String.valueOf(nVersion-1));
            a_devolver = historial.get(nVersion-1);
            historial.remove(nVersion);
            nVersion --;
            Log.e("version actual:",String.valueOf(nVersion));
            Log.e("hay estas versiones",String.valueOf(historial.size()));
        }

        listarBitmaps("getBitmap_VersionAnterior");

        return a_devolver;
    }



}