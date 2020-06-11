package org.devparana.camel.domain

import java.io.Serializable
import java.math.BigDecimal
import java.util.*

data class Product(val id:UUID = UUID.randomUUID(), val sku:String, val exchange:String, val price:BigDecimal, val published:Boolean = false):Serializable