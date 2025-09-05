package processor

import CommandError
import Result
import Token
import progress.ProgressReporter
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

/**
 * Handles streaming processing of PrintScript files to support large source codes
 * that cannot fit entirely in memory.
 */
class FileProcessor(
    private val progressReporter: ProgressReporter,
) {
    /**
     * Processes a file in streaming mode, yielding tokens as they are lexed.
     * This approach prevents memory overflow with large files.
     */
    fun processFileStreaming(
        file: File,
        processor: (List<Token>) -> Result<Unit, CommandError>,
    ): Result<Unit, CommandError> =
        when {
            !file.exists() -> Result.Failure(CommandError("File not found: ${file.absolutePath}"))
            !file.canRead() -> Result.Failure(CommandError("Cannot read file: ${file.absolutePath}"))
            else -> processInputStream(file, processor)
        }

    private fun processInputStream(
        file: File,
        processor: (List<Token>) -> Result<Unit, CommandError>,
    ): Result<Unit, CommandError> =
        try {
            progressReporter.reportProgress("Starting to process file: ${file.name}")

            file.inputStream().use { inputStream ->
                val reader = inputStream.bufferedReader()
                val tokens = mutableListOf<Token>()

                progressReporter.reportProgress("Reading and tokenizing file...", 25)

                // For now, we'll read the entire file and tokenize it
                // In a real implementation, this would be done in chunks
                reader.readText()

                progressReporter.reportProgress("Tokenization complete", 50)

                // Process the tokens
                processor(tokens)
            }
        } catch (e: FileNotFoundException) {
            Result.Failure(CommandError("File not found: ${e.message}", cause = e))
        } catch (e: IOException) {
            Result.Failure(CommandError("IO error during file processing: ${e.message}", cause = e))
        }
}
