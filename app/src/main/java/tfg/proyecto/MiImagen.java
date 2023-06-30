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

    private static Bitmap versionSiguiente;

    static private Boolean bloquearDeshacer = false;
    static private Boolean bloquearRehacer  = false;

    public void setVersionActual(int i){
        this.nVersion = i;
    }

    public void setBloqueoDeshacer(Boolean bloqueo){
        this.bloquearDeshacer = bloqueo;
        Log.e("boton deshacer a",String.valueOf(bloquearDeshacer));
    }

    public void setBloqueoRehacer(Boolean bloqueo){
        this.bloquearRehacer = bloqueo;
    }

    public Boolean getBloquearDeshacer(){
        return this.bloquearDeshacer;
    }

    public Boolean getBloquearRehacer(){
        return this.bloquearRehacer;
    }

    public void addVersion(Bitmap bit){
        listarBitmaps("addVersion antes de añadir nada");
        Log.e("version siguiente es",String.valueOf(versionSiguiente));
        historial.add(bit);
        nVersion++;
        Log.e("añadida version",String.valueOf(nVersion));
        Log.e("añadiendo bitmap",String.valueOf(bit));
        listarBitmaps("addVersion");
    }

    public void eliminarVersionesSiguientesA(int version){
        for (int i = historial.size() - 1; i > version; i--) {
            Log.e("eliminando v", String.valueOf(i));
            historial.remove(i);
            Log.e("tam del array", String.valueOf(historial.size()));
        }
    }

    public int getNumeroVersiones(){
        return historial.size();
    }

    public Bitmap getVersion(int i){
        return historial.get(i);
    }

    public int getVersionActual(){
        return this.nVersion;
    }

    public void setBitmapActual(Bitmap bit){
        historial.set(nVersion,bit);
    }

    public void resetearVersiones(){
        for (int i = 0 ; i<historial.size() ; i++){
            historial.remove(i);
        }
        historial = new ArrayList<>();
        nVersion = -1;
    }

    private void listarBitmaps( String message )
    {
        Log.e("listando Bitmaps",message);
        Log.e("     nVersion= ",String.valueOf(nVersion));
        Log.e("     tamArray= ",String.valueOf(historial.size()));
        for (int i = 0 ; i<=historial.size()-1 ; i++){
            Log.e("       Bitmap: ",String.valueOf(historial.get(i)));
        }
    }

    public Bitmap getBitmapActual(){
        if (nVersion < 0){
            Log.e("error","nVersion es -1");
        }

        Bitmap a_devolver = historial.get(nVersion);
        Log.e("obtengo bit actual",String.valueOf(nVersion));
        Log.e("que es bitmap",String.valueOf(historial.get(nVersion)));
        //Bitmap a_devolver = historial.get(historial.size()-1);
        //Bitmap a_devolver = historial.get(nVersion);
        listarBitmaps("getBitmapActual");
        return a_devolver;
    }

    public Boolean getBajaEficiencia(){
        return this.bajaEficiencia;
    }

    public Bitmap getBitmap_VersionAnterior(){
        Bitmap a_devolver = null;

        if (nVersion == 0){
            Log.e("la version anterior es ",String.valueOf(0));
            a_devolver = historial.get(0);
            versionSiguiente = historial.get(0);
            //versionSiguiente = historial.get(0);
            Log.e("hay estas versiones",String.valueOf(historial.size()));
        }
        else{
            Log.e("la version anterior es ",String.valueOf(nVersion-1));
            a_devolver = historial.get(nVersion-1);
            Log.e("se guarda version",String.valueOf(nVersion));
            Log.e("que contiene bit: ",String.valueOf(historial.get(nVersion)));
            versionSiguiente = historial.get(nVersion);
            historial.remove(nVersion);
            nVersion --;
            Log.e("version actual:",String.valueOf(nVersion));
            Log.e("hay estas versiones",String.valueOf(historial.size()));
        }

        listarBitmaps("getBitmap_VersionAnterior");

        return a_devolver;
    }

    public Bitmap getBitmap_VersionSiguiente(){
        Bitmap a_devolver = null;
        Log.e("version siguiente es",String.valueOf(versionSiguiente));
        a_devolver = versionSiguiente;
        return a_devolver;
    }

}