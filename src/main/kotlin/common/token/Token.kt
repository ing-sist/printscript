package org.example.common.token

import org.example.common.Location

data class Token(val type: TokenType, val lexeme: String, val location: Location)