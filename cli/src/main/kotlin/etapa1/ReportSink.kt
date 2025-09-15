package etapa1

import Diagnostic

interface ReportSink {
    fun emit(d: Diagnostic)
    fun end(s: Summary)
}