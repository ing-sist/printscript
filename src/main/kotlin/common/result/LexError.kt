package org.example.common.result

import org.example.common.Location

sealed class LexError {
    data class SyntaxError(val reason: String) : LexError()
    data class InvalidVersion(val reason: String) : LexError()
}