package etapa2.handlers

import Result
import etapa1.CliFileError
import java.io.File

object FileInputReader {
    fun readFile(filePath: String): Result<String, CliFileError> {
        return try {
            val file = File(filePath)
            val content = file.readText()
            Result.Success(content)
        } catch (e: Exception) {
            return Result.Failure(CliFileError(filePath))
        }
    }
}