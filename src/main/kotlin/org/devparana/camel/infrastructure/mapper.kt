package org.devparana.camel.infrastructure

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

val objectMapper = jacksonObjectMapper().apply {
    this.registerModule(JavaTimeModule())
    this.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    this.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
    this.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
}