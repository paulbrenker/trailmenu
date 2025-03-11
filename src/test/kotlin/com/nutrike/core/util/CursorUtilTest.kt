package com.nutrike.core.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CursorUtilTest {
    @Test
    fun `getEncodedCursor returns null when hasNext is false`() {
        val result = CursorUtil.getEncodedCursor(emptyMap(), false)
        assertThat(result).isNull()
    }

    @Test
    fun `getEncodedCursor returns cursor when hasNext is true`() {
        val result = CursorUtil.getEncodedCursor(mapOf("key" to "value"), true)
        assertThat(result).isNotNull
        assertThat(result).isEqualTo("eyJrZXkiOiJ2YWx1ZSJ9")
    }

    private fun getDecodedCursorInvalidProvider() =
        Stream.of(
            // not convertible to map
            Arguments.of("abc"),
            // id not convertible to UUID
            Arguments.of("eyJpZCI6ImFiYyJ9"),
            // no not convertible to Int
            Arguments.of("eyJubyI6ImFiYyJ9"),
        )

    @ParameterizedTest
    @MethodSource("getDecodedCursorInvalidProvider")
    fun `getDecodedCursor throws when not convertible to map`(invalidCursor: String) {
        assertThrows<IllegalArgumentException> { CursorUtil.getDecodedCursor(invalidCursor) }
    }

    private fun getEncodedCursorProvider() =
        Stream.of(
            // key is value
            Arguments.of("eyJrZXkiOiJ2YWx1ZSJ9"),
            // id is a uuid
            Arguments.of("eyJpZCI6ImM3MmQ3MmJmLTMwZGEtNDExYy04ZWI3LWE3ZTcxNzhmY2VhOSJ9"),
            // no is 1
            Arguments.of("eyJubyI6IjEifQ=="),
        )

    @ParameterizedTest
    @MethodSource("getEncodedCursorProvider")
    fun `getDecodedCursor converts values successfully`(validCursor: String) {
        assertDoesNotThrow { CursorUtil.getDecodedCursor(validCursor) }
    }
}
