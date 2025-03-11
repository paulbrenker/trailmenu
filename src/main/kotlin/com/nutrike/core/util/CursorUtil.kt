package com.nutrike.core.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.UUID
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

object CursorUtil {
    private val objectMapper = jacksonObjectMapper()

    @OptIn(ExperimentalEncodingApi::class)
    fun getEncodedCursor(
        data: Map<String, Any>,
        hasNext: Boolean,
    ): String? =
        hasNext.takeIf { it }?.let {
            Base64.encode(objectMapper.writeValueAsString(data).toByteArray(Charsets.UTF_8))
        }

    @OptIn(ExperimentalEncodingApi::class)
    fun getDecodedCursor(cursor: String): Map<String, Any> =
        try {
            objectMapper
                .readValue<Map<String, String>>(Base64.decode(cursor).decodeToString())
                .mapValues { (key, value) ->
                    when (key) {
                        "id" -> UUID.fromString(value)
                        "no" -> value.toInt()
                        else -> value
                    }
                }
        } catch (e: Exception) {
            throw IllegalArgumentException("could not decode cursor $cursor")
        }
}
