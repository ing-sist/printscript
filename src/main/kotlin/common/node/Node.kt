package org.example.common.node

import org.example.common.token.Token

class Node(
    val token: Token,
    val left: Node?,
    val right: Node?
) {
    private fun isLeaf(): Boolean {
        return left == null && right == null
    }
}