package builders

import AstNode
import Token

interface AstBuilder {
    fun build(tokens: List<Token>): AstNode
}
