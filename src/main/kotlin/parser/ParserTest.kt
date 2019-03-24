package parser

import lexer.Lexer
import lexer.prepareLexer
import org.junit.Test

class ParserTest {

    @Test
    fun `test parser`() {
        val parser = prepareParser()
        println(parser.parse())
    }
}

fun prepareParser(): Parser {
    val lexer = prepareLexer()
    return Parser(lexer.allTokens())
}