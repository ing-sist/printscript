package viejos

import etapa1.Operation
import etapa1.ProgressSink
import etapa1.ReportSink





data class OperationRequest(
    val operation: Operation,          // Validation | Execution | Formatting | Analyzing
    val sourceFile: String,            // ruta del archivo a procesar
    val specVersion: String,           // ej. "1.0"
    val report: ReportSink,            // a dónde mandar diagnósticos
    val progress: ProgressSink         // a dónde mandar progreso
)
