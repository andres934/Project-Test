package test.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.zxing.client.android.CaptureActivity;

import org.w3c.dom.Text;

import java.io.File;

public class searchByBarcode extends Fragment {

    View rootView;
    TextView name, description, price;
    ImageView signature, Image;
    RelativeLayout Search;
    Product product = new Product();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_search_by_barcode, container, false);
        Search = (RelativeLayout) rootView.findViewById(R.id.Search);
        Search.setClickable(true);
        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openScan();
            }
        });
        name = (TextView) rootView.findViewById(R.id.name);
        description = (TextView) rootView.findViewById(R.id.description);
        price = (TextView) rootView.findViewById(R.id.price);
        signature = (ImageView) rootView.findViewById(R.id.signature);
        Image = (ImageView) rootView.findViewById(R.id.Image);
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == getActivity().RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                MySqliteHelper database = new MySqliteHelper(getActivity());
                int barcodeId = database.getBarcode(contents);
                Log.d("Barcode tag", "contents: " + contents + "/ id: "+String.valueOf(barcodeId));
                if (barcodeId != 0){
                    int id = database.getBarcode(contents);
                    product = database.getProductsbyBarcode(id);
                    Log.d("Barcode tag", "contents: " + String.valueOf(product) + "/ id: "+String.valueOf(id));
                    if (product != null){
                        Image.setImageURI(Uri.parse(database.getImagePath(product.getImage_id())));
                        name.setText(product.getName());
                        description.setText(product.getDescription());
                        price.setText(String.valueOf(product.getPrice()));
                        signature.setImageBitmap(loadImageFromDevice(database.getSignaturePath(product.getId())));
                    }
                }else{
                    Log.v("Productos","no se encontraron productos"+String.valueOf(barcodeId)+" / "+contents);
                }
                Log.d("Barcode tag", "contents: " + contents);
            } else if (resultCode == getActivity().RESULT_CANCELED) {
                Log.d("Barcode tag", "RESULT_CANCELED");
            }
        }
    }

    public void openScan(){
        Intent intent = new Intent(getActivity(),CaptureActivity.class);
        intent.setAction("com.google.zxing.client.android.SCAN");
        intent.putExtra("SAVE_HISTORY", false);
        startActivityForResult(intent, 0);
    }

    private Bitmap loadImageFromDevice(String imageURI) {
        Bitmap bitmap = null;
        File file = new File(imageURI);
        if (file.exists()){
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap;
    }
}
