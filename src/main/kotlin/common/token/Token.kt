package org.example.common.token

import org.example.common.Location
import org.example.common.token.TokenType

data class Token(val type: TokenType, val lexeme: String, val location: Location)