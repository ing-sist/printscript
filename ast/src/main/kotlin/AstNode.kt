sealed interface AstNode {
    fun children(): List<AstNode> // defino para cada tipo de nodo, que hijos tiene

    fun getLocation(): Location
}

data class LiteralNode(
    val value: Token,
) : AstNode {
    override fun children(): List<AstNode> = emptyList()

    override fun getLocation(): Location = value.location
}

data class IdentifierNode(
    val value: Token,
    val name: String,
) : AstNode {
    override fun children(): List<AstNode> = emptyList()

    override fun getLocation(): Location = value.location
}

data class BinaryOperationNode(
    val left: AstNode,
    val operator: Token,
    val right: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(left, right)

    override fun getLocation(): Location = operator.location
}

data class DeclarationNode(
    val identifier: IdentifierNode,
    val type: Token,
    val isMutable: Boolean = false,
) : AstNode {
    override fun children(): List<AstNode> = listOf(identifier)

    override fun getLocation(): Location = identifier.value.location
}

data class DeclarationAssignmentNode(
    val declaration: DeclarationNode,
    val value: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(declaration, value)

    override fun getLocation(): Location = declaration.getLocation()
}

data class AssignmentNode(
    val identifier: IdentifierNode,
    val expression: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(identifier, expression)

    override fun getLocation(): Location = identifier.value.location
}

data class FunctionCallNode(
    val functionName: String,
    val content: AstNode,
    val isVoid: Boolean = false,
) : AstNode {
    override fun children(): List<AstNode> = listOf(content)

    override fun getLocation(): Location = content.getLocation()
}

data class UnaryOperationNode(
    val operator: Token,
    val operand: AstNode,
) : AstNode {
    override fun children(): List<AstNode> = listOf(operand)

    override fun getLocation(): Location = operator.location
}

data class ConditionalNode(
    val condition: AstNode,
    val thenBody: List<AstNode>,
    val elseBody: List<AstNode>? = null,
) : AstNode {
    override fun children(): List<AstNode> = listOf(condition) + thenBody + (elseBody ?: emptyList())

    override fun getLocation(): Location = condition.getLocation()
}
