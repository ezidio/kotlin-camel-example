package org.devparana.camel.controller

import org.devparana.camel.domain.Product
import mu.KLogging
import org.apache.camel.EndpointInject
import org.apache.camel.ProducerTemplate
import org.springframework.web.bind.annotation.*


@RestController
class SimpleController {

    companion object : KLogging()

    @EndpointInject("direct:add-product")
    lateinit var productProducer: ProducerTemplate

    @EndpointInject("direct:add-products")
    lateinit var productsProducer: ProducerTemplate

    @PutMapping("/products/{sku}")
    fun addProduct(@RequestBody product: Product, @PathVariable("sku") sku: String): String {
        productProducer.sendBody(product)
        return ""
    }

    @PostMapping("/products")
    fun addProducts(@RequestBody products: List<Product>): String {
        productsProducer.sendBody(products)
        return ""
    }

}
