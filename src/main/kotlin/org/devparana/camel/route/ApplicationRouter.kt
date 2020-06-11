package org.devparana.camel.route

import org.apache.camel.ExchangePattern.InOnly
import org.apache.camel.builder.AggregationStrategies
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.jcache.JCacheConfiguration
import org.apache.camel.component.jcache.JCacheHelper
import org.apache.camel.component.jcache.policy.JCachePolicy
import org.apache.camel.model.dataformat.JsonLibrary
import org.devparana.camel.domain.Product
import org.redisson.api.RedissonClient
import org.redisson.jcache.configuration.RedissonConfiguration
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.util.*
import javax.cache.expiry.CreatedExpiryPolicy
import javax.cache.expiry.Duration

@Component
class ApplicationRouter(val redissonClient: RedissonClient, val transformService: TransformService) : RouteBuilder() {

    override fun configure() {

        errorHandler(deadLetterChannel("direct:error"))


        from("direct:add-products")
                .routeId("route.product.add.all")
                .split().body().parallelProcessing()
                .to("direct:add-product")

        from("direct:add-product")
                .routeId("route.product.add.one")
                .transform().body(Product::class.java)
                .log("Novo produto: \${body.sku}")
                .to(InOnly,"seda:persist")
                .choice()
                .`when`(simple("\${body.published} == false"))
                    .to(InOnly,"seda:send")
                .endChoice()
                .end()

        from("seda:send")
            .routeId("route.product.api.send")
            .log("Enviando para API via HTTP: \${body.sku}")
            // Agrega todas as mensagems
            .aggregate(constant(true)).aggregationStrategy(AggregationStrategies.groupedBody())
                .completionTimeout(30000) // Dispara em 30 segundos
                .completionSize(50) // Ou dispara a cada 50 mensagens
            // Limita a 100 chamadas por minuto
            .throttle(100).maximumRequestsPerPeriod(60000)
            // Serializa e envia para a API
            .marshal().json(JsonLibrary.Jackson)
            .to("http://localhost:3090/produto")

        from("seda:persist")
            .routeId("route.product.persist")
            .log("Persistindo no banco de dados: \${body.sku}")
            .enrich("seda:cotacao") { oldExchange, newExchange ->
                oldExchange.`in`.setHeader("COTACAO", newExchange.`in`.getBody(BigDecimal::class.java))
                oldExchange
            }
            .setBody(simple("INSERT INTO sku(sku, exchange, price, quote) values ('\${body.sku}', '\${body.exchange}', \${body.price}, \${header[COTACAO]} )"))
            .to("jdbc:default")

        from("seda:cotacao")
                // Cache para que a cotação não seja requisitada  a cada produto
                .policy(createPolicy())
                .toD("http://localhost:3090/cotacao?exchange=\${body.exchange}&httpMethod=GET")
                .unmarshal().json(JsonLibrary.Jackson)
                .setBody(simple("\${body[value]}"))

        // Exemplo usando Timer e transform
//        from("timer://load_products?period=5000")
//                .setBody(constant("select * from sku"))
//                .to("jdbc:default")
//                .split().body()
//                .bean("productTransform", "transform")
//                .log("resultado: \${body}")


    }

    fun createPolicy(): JCachePolicy {
        val configuration = JCacheConfiguration()
        configuration.cacheConfiguration = RedissonConfiguration.fromInstance<String, Any>(redissonClient)
        configuration.cacheName = "cotacao"
        configuration.expiryPolicyFactory = CreatedExpiryPolicy.factoryOf(Duration.THIRTY_MINUTES)
        val cacheManager = JCacheHelper.createManager<String, Any>(configuration)

        val cachePolicy = JCachePolicy()
        cachePolicy.keyExpression = simple("\${body.exchange}")
        cachePolicy.cache = cacheManager.cache
        return cachePolicy
    }
}

