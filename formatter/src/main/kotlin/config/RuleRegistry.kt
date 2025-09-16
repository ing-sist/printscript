package config

import Indentation
import InlineBraceIfStatement
import parseEntries
import rules.definitions.CommaSpacingDef
import rules.definitions.IfBraceBelowLineDef
import rules.definitions.IndentationDef
import rules.definitions.InlineIfBraceIfStatementDef
import rules.definitions.KeywordDef
import rules.definitions.LineBreakAfterSemiColonDef
import rules.definitions.LineBreakBeforePrintlnDef
import rules.definitions.Rule
import rules.definitions.SpaceAfterColonDef
import rules.definitions.SpaceAroundAssignmentDef
import rules.definitions.SpaceAroundOperatorsDef
import rules.definitions.SpaceBeforeColonDef
import rules.implementations.ColonSpacing
import rules.implementations.CommaSpacing
import rules.implementations.IfBraceBelowLine
import rules.implementations.KeywordSpacing
import rules.implementations.LineBreakAfterSemicolon
import rules.implementations.LineBreakBeforePrintln
import rules.implementations.RuleImplementation
import rules.implementations.SpaceAroundAssignment
import rules.implementations.SpaceAroundOperators
import java.io.File

val RULE_TO_IMPL: Map<String, RuleImplementation> =
    mapOf(
        SpaceAroundAssignmentDef.id to SpaceAroundAssignment,
        SpaceAroundOperatorsDef.id to SpaceAroundOperators,
        SpaceBeforeColonDef.id to ColonSpacing,
        SpaceAfterColonDef.id to ColonSpacing,
        LineBreakAfterSemiColonDef.id to LineBreakAfterSemicolon,
        LineBreakBeforePrintlnDef.id to LineBreakBeforePrintln,
        InlineIfBraceIfStatementDef.id to InlineBraceIfStatement,
        IndentationDef.id to Indentation,
        IfBraceBelowLineDef.id to IfBraceBelowLine,
        CommaSpacingDef.id to CommaSpacing,
        KeywordDef.id to KeywordSpacing,
    )

fun selectImplementations(activeIds: Set<String>): List<RuleImplementation> = activeIds.mapNotNull { RULE_TO_IMPL[it] }

fun activeImplementationsFromJson(
    json: String,
    defs: List<Rule<*>>,
): List<RuleImplementation> {
    // 1) Engine ids
    val engineIds = defs.filter { it.owner == RuleOwner.ENGINE }.map { it.id }.toSet()

    // 2) User ids (normalizados con alias)
    val inner =
        json
            .trim()
            .removePrefix("{")
            .removeSuffix("}")
            .trim()
    val userIds: Set<String> =
        if (inner.isBlank()) {
            emptySet()
        } else {
            parseEntries(inner).keys // tu parseEntries ya aplica alias+transform
        }

    // 3) Engine impls + User impls
    val impls = mutableListOf<RuleImplementation>()
    impls += engineIds.mapNotNull { RULE_TO_IMPL[it] }
    impls += userIds.mapNotNull { RULE_TO_IMPL[it] }

    return impls.distinct()
}

fun activeImplementationsFromFile(
    file: File,
    defs: List<Rule<*>>,
): List<RuleImplementation> = activeImplementationsFromJson(file.readText(), defs)

fun activeImplementationsFromPath(
    path: String,
    defs: List<Rule<*>>,
): List<RuleImplementation> = activeImplementationsFromFile(File(path), defs)
