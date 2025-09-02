package rules

import AssignmentNode
import AstNode
import BinaryOperationNode
import DeclarationAssignmentNode
import DeclarationNode
import IdentifierNode
import LiteralNode
import Location
import PrintlnNode
import UnaryOperationNode

// asi paso un nodo siempre y aca busco la loc, porque dependiendo del tipo de nodo la loc etsa en ptrp luagr
fun lookForLocation(node: AstNode): Location =
    when (node) {
        is IdentifierNode -> node.value.location
        is LiteralNode -> node.value.location
        is BinaryOperationNode -> node.operator.location
        is UnaryOperationNode -> node.operator.location
        is DeclarationNode -> node.identifier.value.location
        is DeclarationAssignmentNode -> node.identifier.value.location
        is AssignmentNode -> node.identifier.value.location
        is PrintlnNode -> node.content.let(::lookForLocation)
    }
