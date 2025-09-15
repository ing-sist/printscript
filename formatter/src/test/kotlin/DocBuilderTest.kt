import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DocBuilderTest {
    @Test
    fun `DocBuilder inicia en linea nueva`() {
        val doc = DocBuilder.inMemory()
        assertTrue(doc.isAtLineStart())
    }

    @Test
    fun `escribir texto cambia estado de linea`() {
        val doc = DocBuilder.inMemory()
        val newDoc = doc.write("hello")

        assertFalse(newDoc.isAtLineStart())
        assertEquals("hello", newDoc.build())
    }

    @Test
    fun `agregar espacio cambia estado de linea`() {
        val doc = DocBuilder.inMemory()
        val newDoc = doc.space()

        assertFalse(newDoc.isAtLineStart())
        assertEquals(" ", newDoc.build())
    }

    @Test
    fun `newline establece estado de linea nueva`() {
        val doc =
            DocBuilder
                .inMemory()
                .write("hello")
                .newline()

        assertTrue(doc.isAtLineStart())
        assertEquals("hello\n", doc.build())
    }

    @Test
    fun `indent solo funciona al inicio de linea`() {
        val doc1 = DocBuilder.inMemory().indent(4)
        assertEquals("    ", doc1.build())

        val doc2 =
            DocBuilder
                .inMemory()
                .write("text")
                .indent(4)
        assertEquals("text", doc2.build())
    }

    @Test
    fun `indent con espacios negativos o cero no hace nada`() {
        val doc1 = DocBuilder.inMemory().indent(0)
        assertEquals("", doc1.build())

        val doc2 = DocBuilder.inMemory().indent(-5)
        assertEquals("", doc2.build())
    }

    @Test
    fun `construir documento complejo`() {
        val doc =
            DocBuilder
                .inMemory()
                .write("if")
                .space()
                .write("(")
                .write("true")
                .write(")")
                .space()
                .write("{")
                .newline()
                .indent(4)
                .write("println")
                .write("(")
                .write("\"hello\"")
                .write(")")
                .newline()
                .write("}")

        assertEquals("if (true) {\n    println(\"hello\")\n}", doc.build())
    }
}
