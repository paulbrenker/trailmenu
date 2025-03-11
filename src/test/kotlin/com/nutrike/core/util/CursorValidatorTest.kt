package com.nutrike.core.util

import io.mockk.every
import io.mockk.mockkObject
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CursorValidatorTest {
    private lateinit var validator: CursorValidator

    @BeforeEach
    fun setUp() {
        validator = CursorValidator()
    }

    @Test
    fun `isValid returns true when called with null`() {
        val result = validator.isValid(null, null)
        assertThat(result).isTrue
    }

    @Test
    fun `isValid returns false when CursorUtil fails`() {
        mockkObject(CursorUtil)
        every { CursorUtil.getDecodedCursor(any()) } throws IllegalArgumentException()

        val result = validator.isValid("test-value", null)

        assertThat(result).isFalse
    }

    @Test
    fun `isValid returns true when CursorUtil succeeds`() {
        mockkObject(CursorUtil)
        every { CursorUtil.getDecodedCursor(any()) } returns emptyMap()

        val result = validator.isValid("test-value", null)

        assertThat(result).isTrue
    }
}
