package org.example.common.result

// out permite extender de las implmentaciones de los hijos
sealed interface Result<out T, out E> {
    val isSuccess: Boolean
    val isFailure: Boolean get() = !isSuccess

    fun getOrNull(): T?

    fun errorOrNull(): E?

    // (T) -> U es una funcion lambda que recibe un T y devuelve un U
    fun <U> map(transform: (T) -> U): Result<U, E>

    fun <U> flatMap(transform: (T) -> Result<U, @UnsafeVariance E>): Result<U, E>

    fun <R> fold(
        onSuccess: (T) -> R,
        onFailure: (E) -> R,
    ): R

    // data class genera automaticamente equals, hashCode, toString
    data class Success<T>(val value: T) : Result<T, Nothing> {
        override val isSuccess: Boolean = true

        override fun getOrNull(): T = value

        override fun errorOrNull(): Nothing? = null

        override fun <U> map(transform: (T) -> U): Result<U, Nothing> = Success(transform(value))

        override fun <U> flatMap(transform: (T) -> Result<U, Nothing>): Result<U, Nothing> = transform(value)

        override fun <R> fold(
            onSuccess: (T) -> R,
            onFailure: (Nothing) -> R,
        ): R = onSuccess(value)
    }

    data class Failure<E>(val error: E) : Result<Nothing, E> {
        override val isSuccess: Boolean = false

        override fun getOrNull(): Nothing? = null

        override fun errorOrNull(): E = error

        override fun <U> map(transform: (Nothing) -> U): Result<U, E> = this

        override fun <U> flatMap(transform: (Nothing) -> Result<U, E>): Result<U, E> = this

        override fun <R> fold(
            onSuccess: (Nothing) -> R,
            onFailure: (E) -> R,
        ): R = onFailure(error)
    }
}
