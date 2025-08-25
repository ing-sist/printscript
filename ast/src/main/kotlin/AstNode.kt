sealed interface AstNode

data class LiteralNode(
    val value: Token,
) : AstNode

data class IdentifierNode(
    val value: Token,
    val name: String,
) : AstNode

data class BinaryOperationNode(
    val left: AstNode,
    val operator: Token,
    val right: AstNode,
) : AstNode

data class DeclarationNode(
    val identifier: IdentifierNode,
    val type: Token,
) : AstNode

data class DeclarationAssignmentNode(
    val identifier: IdentifierNode,
    val type: Token,
    val value: AstNode,
) : AstNode

data class AssignmentNode(
    val identifier: IdentifierNode,
    val expression: AstNode,
) : AstNode

data class PrintlnNode(
    val content: AstNode,
) : AstNode

data class UnaryOperationNode(
    val operator: Token,
    val operand: AstNode,
) : AstNode
