package com.nutrike.core.util

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext

class CursorValidator : ConstraintValidator<ValidCursor, String> {
    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        if (value == null) {
            return true
        }
        return try {
            CursorUtil.getDecodedCursor(value)
            true
        } catch (e: Exception) {
            false
        }
    }
}
