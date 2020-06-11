package org.devparana.camel.route

import org.apache.camel.Body
import org.apache.camel.Header
import org.devparana.camel.domain.Product
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*

@Component("productTransform")
class TransformService {

    fun transform(@Body data:Map<String, Any>):Product {
        return Product(
                id = data["id"] as UUID,
                exchange = data["exchange"] as String,
                price = BigDecimal.valueOf((data["price"] as Number).toDouble()),
                published = false,
                sku = data["sku"] as String
        )
    }
}