package rules.implementations

enum class SpaceIntent { DEFAULT, FORBID }

class SpaceForbid(
    var beforeNext: SpaceIntent = SpaceIntent.DEFAULT,
    var afterNext: SpaceIntent = SpaceIntent.DEFAULT,
) {
    fun forbidBefore() {
        beforeNext = SpaceIntent.FORBID
    }

    fun forbidAfter() {
        afterNext = SpaceIntent.FORBID
    }

    fun reset() {
        beforeNext = SpaceIntent.DEFAULT
        afterNext = SpaceIntent.DEFAULT
    }
}
