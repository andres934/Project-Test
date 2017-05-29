package test.project

import android.app.Application

import uk.co.chrisjenx.calligraphy.CalligraphyConfig

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        CalligraphyConfig.initDefault(CalligraphyConfig.Builder().setDefaultFontPath("fonts/OpenSans-Regular.ttf").setFontAttrId(R.attr.fontPath).build())
    }

    companion object {

        var CurrentPhotoPath: String? = null
    }
}
