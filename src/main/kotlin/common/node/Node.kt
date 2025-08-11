package org.example.common.node

import org.example.common.token.Token
import org.example.common.token.TokenType

class Node(
    val value: Token,
    val left: Node?,
    val right: Node?
) {
    fun getValue(): Token {
        return value
    }

    fun isLeaf(): Boolean {
        return left == null && right == null
    }
}