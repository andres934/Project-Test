package test.project

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

class listProducts : Fragment() {

    internal var rootView: View? = null
    internal var recycler: RecyclerView? = null
    internal var layoutManager: RecyclerView.LayoutManager? = null
    internal var adapter: RecyclerView.Adapter<*>? = null


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.activity_list_products, container, false)
        recycler = rootView?.findViewById(R.id.recycler) as RecyclerView
        layoutManager = LinearLayoutManager(context)
        recycler?.layoutManager = layoutManager

        val database = MySqliteHelper(context)
        if (products != null) {
            products = arrayOf<Product.Product>()
            products = database.productsbyPriceAsc()

            adapter = ProductsAdapter(context)
            recycler?.adapter = adapter
        }


        return rootView
    }

    companion object {
        var products: Array<Product.Product>? = null
    }

}
