@file:JvmName("Compiler")
import generator.Generator
import lexer.Lexer
import parser.Parser
import semantic.Failure
import semantic.validate
import java.io.File

const val outputFile = "src/main/kotlin/generated/GenerateXml.kt"

fun main(args: Array<String>) {
    println(args.asList())
    val fileName = args[0]
    val content = readFile(fileName)

    val lexer = Lexer(content)
    val parser = Parser(lexer.allTokens())
    val rootNode = parser.parse()
    val semanticCheck = validate(rootNode)

    if(semanticCheck is Failure) {
        println(semanticCheck.message)
        return
    }

    val generator = Generator(rootNode)
    val output = generator.generate()
    writeToFile(output, outputFile)
}

fun readFile(fileName: String): String = File(fileName).readText()

fun writeToFile(output: String, fileName: String) =
        File(fileName).writeText(output)