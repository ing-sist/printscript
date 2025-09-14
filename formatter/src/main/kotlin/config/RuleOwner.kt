package config

sealed interface RuleOwner {
    object USER : RuleOwner

    object ENGINE : RuleOwner
}
