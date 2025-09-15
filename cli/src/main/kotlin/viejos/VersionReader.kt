package viejos

import Result
import etapa1.CliVersionError

object VersionReader {
    fun getVersion(version: Int): Result<Int, CliVersionError> {
        return when (version) {
            1 -> Result.Success(1)
            2 -> Result.Success(1)
            else -> Result.Failure(CliVersionError(version))
        }
    }
}