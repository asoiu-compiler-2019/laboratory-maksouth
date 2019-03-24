package generator

import org.junit.Test
import parser.prepareParser

class GeneratorTest {
    @Test fun `test generator`() {
        val generator = prepareGenerator()
        println(generator.generate())
    }
}

fun prepareGenerator(): Generator {
    val parser = prepareParser()
    val node = parser.parse()
    return Generator(node)
}