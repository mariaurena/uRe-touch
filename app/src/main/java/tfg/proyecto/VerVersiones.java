package tfg.proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.material.navigation.NavigationView;

public class VerVersiones extends AppCompatActivity {

    private ImageView imgView;
    private MiImagen miImagen;

    private int numero_versiones;

    private int vActual = 0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    Menu menu;

    private Button botonOK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ver_versiones);

        miImagen = new MiImagen();

        imgView = findViewById(R.id.muestraImagen);
        imgView.setImageBitmap(miImagen.getBitmapActual());

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
                    case R.id.filtro:
                        Log.e("h","hoa");
                        return true;
                }

                return onOptionsItemSelected(item);
            }
        });

        // añdimos una opción al menú por cada versión que vayamos creando
        // (inicialmente tenemos la versión original)
        numero_versiones = miImagen.getNumeroVersiones();
        menu = navigationView.getMenu();


        for ( int i = 0 ; i < numero_versiones ; i++ ){
            MenuItem menuItem = menu.add(Menu.NONE,R.id.filtro,Menu.NONE,"Versión "+i);
            final int version_actual = i;
            menuItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Log.e("hola version numero",String.valueOf(version_actual));
                    vActual = version_actual;
                    imgView.setImageBitmap(miImagen.getVersion(version_actual));
                    drawerLayout.closeDrawers();
                    return true;
                }
            });
        }

        // para reflejar los cambios en la vista del menú
        navigationView.invalidate();

        botonOK = findViewById(R.id.botonOK);
        botonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable drawable = imgView.getDrawable();
                //miImagen.addVersion(((BitmapDrawable)drawable).getBitmap());
                miImagen.setVersionActual(vActual);
                miImagen.eliminarVersionesSiguientesA(vActual);
                Log.e("elimindo v siguientes a",String.valueOf(vActual));
                miImagen.setBloqueoRehacer(false);
                miImagen.setBloqueoDeshacer(false);
                Log.e("version ACTUAL",String.valueOf(miImagen.getVersionActual()));
                Intent intent = new Intent(getBaseContext(),EditarFoto.class);
                startActivity(intent);
            }
        });

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