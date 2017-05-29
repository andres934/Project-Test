package test.project

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainMenu : AppCompatActivity() {

    internal var agregar: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_menu)

        agregar = findViewById(R.id.agregar) as Button
        agregar?.setOnClickListener {
            val i = Intent(this@MainMenu, addProduct::class.java)
            startActivity(i)
        }
    }
}
