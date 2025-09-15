package etapa2.handlers

import Diagnostic
import Location
import utils.Type

object Diags {
    fun fileNotFound(path: String) = Diagnostic(
        ruleId = "CLI.FileNotFound",
        message = "No se pudo leer el archivo '$path'.",
        location = Location(path, 1, 1, 1, 1),
        type = Type.ERROR
    )
    fun readError(path: String, msg: String?) = Diagnostic(
        "CLI.FileReadError", "Error al leer '$path': $msg",
        Location(path, 1, 1, 1, 1), Type.ERROR
    )
    fun internal(path: String, msg: String?) = Diagnostic(
        "Internal.Unexpected", "Error inesperado: $msg",
        Location(path, 1, 1, 1), Type.ERROR
    )
}