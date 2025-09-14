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
    val isMutable: Boolean = false,
) : AstNode {
    override fun children(): List<AstNode> = listOf(identifier)
}

data class DeclarationAssignmentNode(
    val declaration: DeclarationNode,
    val value: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(declaration, value)
}

data class AssignmentNode(
    val identifier: IdentifierNode,
    val expression: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(identifier, expression)
}

data class FunctionCallNode(
    val functionName: String,
    val content: AstNode,
    val isVoid: Boolean = false,
) : AstNode {
    override fun children(): List<AstNode> = listOf(content)
}

data class UnaryOperationNode(
    val operator: Token,
    val operand: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(operand)
}

// New nodes for PrintScript 1.1

data class ConditionalNode(
    val condition: AstNode,
    val thenBody: List<AstNode>,
    val elseBody: List<AstNode>? = null,
) : AstNode {
    override fun children(): List<AstNode> = listOf(condition) + thenBody + (elseBody ?: emptyList())
}


