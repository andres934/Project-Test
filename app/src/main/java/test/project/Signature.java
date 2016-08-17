package test.project;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Signature extends AppCompatActivity {

    @BindView(R.id.Signature) RelativeLayout parent;

    MyDrawView myDrawView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        ButterKnife.bind(this);
        setToolbar();

        myDrawView = new MyDrawView(this);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        parent.addView(myDrawView);
    }

    public String saveSignature(RelativeLayout parent){
        parent.setDrawingCacheEnabled(true);
        Bitmap b = parent.getDrawingCache();

        String directorio = Environment.getExternalStorageDirectory()+"/Signatures";
        File d = new File(directorio);
        if (!d.exists()) {
            d.mkdir();
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = "Signature_"+timeStamp+".png";
        File f = new File(directorio+"/"+fileName);
        Log.v("File",f.toString());
        FileOutputStream fo = null;

        try {
            f.createNewFile();
            fo = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.PNG, 100, fo);
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return f.toString();
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.firma, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.item_borrar:
                myDrawView.clear();
                return true;
            case R.id.item_agregar:
                Intent returnIntent = new Intent();
                returnIntent.putExtra("FileName",saveSignature(parent));
                setResult(RESULT_OK, returnIntent);
                finish();

                return true;
            default:
                return false;
        }
    }
}
