package test.project;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by andresrodriguez on 8/16/16.
 */
public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ProductsViewHolder> {

    private Context mContext;
    Product[] products = listProducts.products;

    public static class ProductsViewHolder extends RecyclerView.ViewHolder {

        // Campos respectivos de un item
        public ImageView productImg;
        public TextView productName;

        public ProductsViewHolder(View v) {
            super(v);
            productImg = (ImageView) v.findViewById(R.id.productImg);
            productName = (TextView) v.findViewById(R.id.productName);
        }
    }

    public ProductsAdapter(Context context) {
        mContext = context;

    }

    @Override
    public int getItemCount() {
        return products.length;
    }

    @Override
    public ProductsViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.product_list, viewGroup, false);
        return new ProductsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ProductsViewHolder viewHolder, int i) {
        final int a = i;
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        MySqliteHelper database = new MySqliteHelper(mContext);
        String imagePath = database.getImagePath(products[i].getImage_id());
//        Bitmap bmp = loadImageFromGallery(imagePath);
//        viewHolder.productImg.setImageBitmap(bmp);
        viewHolder.productImg.setImageURI(Uri.parse(imagePath));
        viewHolder.productName.setText(products[i].getName());

    }

    private Bitmap loadImageFromGallery(String imageURI) {
        Bitmap bitmap = null;
        File file = new File(imageURI);
        if (file.exists()){
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap;
    }

    private Bitmap loadImageFromDevice(String imageURI) {
        Bitmap bitmap = null;
        File file = new File(Environment.getExternalStorageDirectory()+"/SilverbarsImg/"+imageURI);
        if (file.exists()){
            bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        }
        return bitmap;
    }

}