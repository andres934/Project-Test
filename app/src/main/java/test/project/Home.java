package test.project;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class Home extends Fragment {

    TextView size;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_home, container, false);
        MySqliteHelper database = new MySqliteHelper(getContext());
        size = (TextView) rootView.findViewById(R.id.size);
        if (database.getProductsbyPriceAsc() != null){
            size.setText(String.valueOf(database.getProductsbyPriceAsc().length));
        }
        else
            size.setText(String.valueOf(0));

        return rootView;
    }

}
