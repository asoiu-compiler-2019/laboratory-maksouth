package lexer

import lexer.TokenType.*

enum class TokenType(val literal: String) {
    Identifier("identifier"),
    Integer("number"),
    StringValue("string"),

    ListType("list"),

    Loop("for-each"),
    It("it"),

    OpenBracket("["),
    CloseBracket("]"),

    OpenBrace("{"),
    CloseBrace("}"),

    Semicolon(":"),
    Dot("."),

    EndOfInput("end of input"),
    Unrecognized("unrecognized");

    override fun toString() = literal
}

fun getKeywordType(keyword: String): TokenType = when(keyword) {
    It.literal -> It
    Loop.literal -> Loop
    else -> TokenType.Unrecognized
}

fun getSpecialSymbolType(char: Char): TokenType = when(char) {
    '.' -> Dot
    ':' -> Semicolon
    else -> Unrecognized
}

fun getBracketType(char: Char): TokenType = when(char) {
    '[' -> OpenBracket
    ']' -> CloseBracket
    '{' -> OpenBrace
    '}' -> CloseBrace
    else -> Unrecognized
}

