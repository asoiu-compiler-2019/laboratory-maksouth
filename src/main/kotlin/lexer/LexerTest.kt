package lexer

import org.junit.Assert.*
import org.junit.Test


class LexerTest {
    @Test fun `test code snippet`() {
        val lexer = prepareLexer()
        println(lexer.allTokens())
    }
}

fun prepareLexer(): Lexer {
    val source = """
            [report
                [field :name 'field-name'
                    [square '30.0']
                    [groups
                        for-each {'first' 'second' 'third'}
                            [group it]]

                    [coordinates
                        for-each {{'30' '20'} {'31' '21'}}
                            [coordinate
                                [lat it.0]
                                [lon it.1]]]]
            ]"""

    return Lexer(source)
}