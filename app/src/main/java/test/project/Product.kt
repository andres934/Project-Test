package test.project

import java.io.Serializable

/**
 * Created by andre_000 on 8/14/2016.
 */
class Product {

    data class Product(val id: Int = 0,
                       val name: String,
                       val description: String,
                       val price: Int = 0,
                       val user_id : Int = 0,
                       val image_id: Int = 0,
                       val location_id: Int = 0,
                       val barcode_id: Int = 0,
                       val signature_state: String): Serializable

}
