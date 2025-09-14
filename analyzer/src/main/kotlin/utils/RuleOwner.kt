package utils

sealed interface RuleOwner {
    object USER : RuleOwner

    object ENGINE : RuleOwner
}
