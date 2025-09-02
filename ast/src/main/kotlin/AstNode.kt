sealed interface AstNode {
    fun children(): List<AstNode> // defino para cada tipo de nodo, que hijos tiene
}

data class LiteralNode(
    val value: Token,
) : AstNode {
    override fun children(): List<AstNode> = emptyList()
}

data class IdentifierNode(
    val value: Token,
    val name: String,
) : AstNode {
    override fun children(): List<AstNode> = emptyList()
}

data class BinaryOperationNode(
    val left: AstNode,
    val operator: Token,
    val right: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(left, right)
}

data class DeclarationNode(
    val identifier: IdentifierNode,
    val type: Token,
) : AstNode {
    override fun children(): List<AstNode> = listOf(identifier)
}

data class DeclarationAssignmentNode(
    val identifier: IdentifierNode,
    val type: Token,
    val value: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(identifier, value)
}

data class AssignmentNode(
    val identifier: IdentifierNode,
    val expression: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(identifier, expression)
}

data class PrintlnNode(
    val content: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(content)
}

data class UnaryOperationNode(
    val operator: Token,
    val operand: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(operand)
}
