package org.devparana.camel.route

import org.apache.camel.builder.RouteBuilder
import org.springframework.stereotype.Component

@Component
class ErrorRouter: RouteBuilder() {
    override fun configure() {
        from("direct:error")
                .log("Erro ocorrido e sendo logado: \${exception}")
    }
}