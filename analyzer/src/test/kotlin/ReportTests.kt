// src/test/kotlin/analyzer/ReportTests.kt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import utils.Type

class ReportTests {
    private fun loc(
        line: Int = 10,
        col: Int = 3,
    ) = Location(line, col, col)

    @Test
    @DisplayName("Report.inMemory: arranca vacío, agrega diagnósticos, expone size/first y soporta chaining")
    fun inMemoryReport_collectsAndExposesDiagnostics() {
        val report = Report.inMemory()

        // Inicialmente vacío
        assertTrue(report.isEmpty())
        assertEquals(0, report.size())

        // addDiagnostic devuelve this (permite chaining)
        val returned = report.addDiagnostic("R1", "boom", loc(10, 3), Type.ERROR)
        assertSame(report, returned)

        // Ahora hay 1 diagnóstico
        assertFalse(report.isEmpty())
        assertEquals(1, report.size())

        val d1 = report.first()
        assertEquals("R1", d1.ruleId)
        assertEquals("boom", d1.message)
        assertEquals(10, d1.location.line)
        assertEquals(3, d1.location.startCol)
        assertEquals(Type.ERROR, d1.type)

        // Agrego otro
        report.addDiagnostic("R2", "warn msg", loc(5, 1), Type.WARNING)
        assertEquals(2, report.size(), "Debe contar ambos diagnósticos")
        // first sigue siendo el primero
        val stillFirst = report.first()
        assertEquals("R1", stillFirst.ruleId)
    }

    @Test
    @DisplayName("Report.to(Appendable): escribe en streaming con formato y no guarda en memoria")
    fun streamingReport_writesAndHasNoMemory() {
        val sb = StringBuilder()
        val report = Report.to(sb)

        // Sin memoria interna
        assertTrue(report.isEmpty())
        assertEquals(0, report.size())

        // Escribe primera línea
        report.addDiagnostic("ruleX", "Mensaje de error", loc(7, 2), Type.ERROR)
        val out1 = sb.toString()
        // Formato: TYPE \t RULE \t LINE:COL \t MESSAGE \n
        assertTrue(
            out1.startsWith("${Type.ERROR}\truleX\t7:2\tMensaje de error"),
            "Salida inesperada: $out1",
        )

        // Suma una segunda línea
        report.addDiagnostic("ruleY", "Otro mensaje", loc(1, 9), Type.WARNING)
        val out2 = sb.toString()
        // Debe contener la segunda línea también
        assertTrue(out2.contains("${Type.WARNING}\truleY\t1:9\tOtro mensaje"), "No encontró la segunda línea: $out2")

        // Al no tener memoria, first() debe fallar con el mensaje específico
        val ex =
            assertThrows(IllegalStateException::class.java) {
                report.first()
            }
        assertTrue(
            ex.message!!.contains("Streaming report has no in-memory diagnostics"),
            "Mensaje esperado no encontrado: ${ex.message}",
        )

        // Y size/isEmpty siguen reportando como si no hubiera memoria
        assertTrue(report.isEmpty())
        assertEquals(0, report.size())
    }

    @Test
    @DisplayName("InMemoryReport vía Report.inMemory: cubre emit/isEmpty/size/first con múltiples entradas")
    fun inMemoryReport_multipleEntries() {
        val report = Report.inMemory()
        report
            .addDiagnostic("A", "uno", loc(2, 1), Type.INFO)
            .addDiagnostic("B", "dos", loc(3, 4), Type.WARNING)
            .addDiagnostic("C", "tres", loc(9, 9), Type.ERROR)

        assertFalse(report.isEmpty())
        assertEquals(3, report.size())

        val first = report.first()
        assertEquals("A", first.ruleId)
        assertEquals(Type.INFO, first.type)
        assertEquals(2, first.location.line)
        assertEquals(1, first.location.startCol)
        assertEquals("uno", first.message)
    }
}
