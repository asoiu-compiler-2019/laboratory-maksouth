package semantic

import org.junit.Test
import parser.prepareParser

class SemanticKtTest{
    @Test fun `validate input`() {
        val parser = prepareParser()
        val node = parser.parse()
        val result = validate(node)

        println("Semantic result $result")
    }
}