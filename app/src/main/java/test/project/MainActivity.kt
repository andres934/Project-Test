package test.project

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.SearchView
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem


import com.facebook.FacebookSdk
//import com.facebook.login.LoginManager

import butterknife.BindView
import butterknife.ButterKnife
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

class MainActivity : AppCompatActivity() {

    @BindView(R.id.toolbar) internal var toolbar: Toolbar? = null
    @BindView(R.id.drawer_layout) internal var mDrawerLayout: DrawerLayout? = null
    @BindView(R.id.navigation_view) internal var mNavView: NavigationView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        FacebookSdk.sdkInitialize(applicationContext)
        setContentView(R.layout.activity_main)
        val i = intent
        userId = i.getIntExtra("userId", 0)
        ButterKnife.bind(this)
        setToolbar()

        supportFragmentManager.addOnBackStackChangedListener { setNavigationIcon() }

        mNavView?.setNavigationItemSelectedListener(NavigationView.OnNavigationItemSelectedListener { item ->
            val f: Fragment

            if (!item.isChecked)
                item.isChecked = true

            mDrawerLayout!!.closeDrawers()

            when (item.itemId) {
                R.id.Home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Home()).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.item_crear -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, addProduct()).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.item_lista -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, listProducts()).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.item_busqueda_barra -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, searchByBarcode()).commit()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.Exit -> {
//                    LoginManager.getInstance().logOut()
                    val intent = Intent(this@MainActivity, LoginActivity::class.java)
                    startActivity(intent)
                    val prefs = getSharedPreferences("UserId", Context.MODE_PRIVATE)
                    val editor = prefs.edit()
                    editor.remove("userId")
                    editor.commit()
                    finish()

                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        })

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().add(R.id.fragment_container, Home()).commit()
        }
    }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = getString(R.string.hint_search)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String): Boolean {
                Log.d("QUERY", s)
                return true
            }

            override fun onQueryTextChange(s: String): Boolean {
                return false
            }
        })

        return true
    }

    private fun setToolbar() {
        setSupportActionBar(toolbar)
        val ab = supportActionBar
        ab?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun setNavigationIcon() {
        val backStackEntryCount = supportFragmentManager.backStackEntryCount
        val ab = supportActionBar

        if (backStackEntryCount == 0)
            ab?.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp)
        else
            ab?.setHomeAsUpIndicator(R.drawable.ic_arrow_back_white_24dp)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val backStackEntryCount = supportFragmentManager.backStackEntryCount

                if (backStackEntryCount == 0)
                    mDrawerLayout!!.openDrawer(GravityCompat.START)
                else
                    super.onBackPressed()

                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        var userId = 0
    }
}
