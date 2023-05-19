package tfg.proyecto;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;

public class MiImagen {

    public static Bitmap bitmapCamara;
    public static Bitmap bitmapGaleria;
    public static Bitmap bitmapRecortada;
    public static Bitmap bitmapEditada;
    public static Bitmap bitmapEditadaAv;

    // Estado de la imagen
    // -1 : no válida
    //  0 : imagen cámara
    //  1 : imagen galeria
    //  2 : imagen recortada
    //  3 : imagen editada
    //  4 : imagen editada avanzada
    public static int estado = -1;

    public static Boolean bajaEficiencia = false;

    // ------------------------ SET ------------------------

    public void setBitmapCamara(Bitmap bit){
        this.bitmapCamara = bit;
    }

    public void setBitmapGaleria(Bitmap bit){
        this.bitmapGaleria = bit;
    }

    public void setBitmapRecortada(Bitmap bit){
        this.bitmapRecortada = bit;
    }

    public void setBitmapEditada(Bitmap bit){
        this.bitmapEditada = bit;
    }

    public void setBitmapEditadaAv(Bitmap bit){
        this.bitmapEditadaAv = bit;
    }

    public void setEstado(int estado){
        this.estado = estado;
    }


    // ------------------------ GET ------------------------

    public Bitmap getBitmapCamara(){
        return this.bitmapCamara;
    }

    public Bitmap getBitmapGaleria(){
        return this.bitmapGaleria;
    }

    public Bitmap getBitmapRecortada(){
        return this.bitmapRecortada;
    }

    public Bitmap getBitmapEditada(){
        return this.bitmapEditada;
    }

    public Bitmap getBitmapEditadaAv(){
        return this.bitmapEditadaAv;
    }

    public int getEstado(){
        return this.estado;
    }

    public Boolean getBajaEficiencia(){
        return this.bajaEficiencia;
    }


}