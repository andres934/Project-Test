package test.project;

import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class listProducts extends Fragment {

    View rootView;
    RecyclerView recycler;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView.Adapter adapter;
    public static Product[] products = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_list_products, container, false);
        recycler = (RecyclerView) rootView.findViewById(R.id.recycler);
        layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);

        MySqliteHelper database = new MySqliteHelper(getContext());
        if (products != null){
            products = new Product[database.getProductsbyPriceAsc().length];
            products = database.getProductsbyPriceAsc();

            adapter = new ProductsAdapter(getContext());
            recycler.setAdapter(adapter);
        }


        return rootView;
    }

}
