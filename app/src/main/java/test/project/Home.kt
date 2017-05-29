package test.project


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


class Home : Fragment() {

    internal var size: TextView? = null
    internal var rootView: View? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        rootView = inflater!!.inflate(R.layout.fragment_home, container, false)
        val database = MySqliteHelper(context)
        size = rootView?.findViewById(R.id.size) as TextView
        if (database.productsbyPriceAsc() != null) {
            size?.text = database.productsbyPriceAsc()?.size.toString()
        } else
            size?.text = 0.toString()

        return rootView
    }

}
