// con walk es como que llamo recursivo, porque como ya se que cantidad de hijos tiene cada nodo
// los voy recorriendo
fun walk(
    root: AstNode,
    enter: (AstNode) -> Boolean,
) {
    if (!enter(root)) return
    root.children().forEach { child ->
        walk(child, enter)
    }
}
