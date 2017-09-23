import antlr.CalculatorBaseVisitor
import antlr.CalculatorLexer
import antlr.CalculatorParser
import antlr.CalculatorParser.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class CalculatorVisitor : CalculatorBaseVisitor<Int>() {

    override fun visitIntLit(ctx: IntLitContext): Int = ctx.text.toInt()
    override fun visitParExpr(ctx: ParExprContext): Int = visit(ctx.expr())
    override fun visitProg(ctx: ProgContext): Int = visit(ctx.expr().last())

    override fun visitDotExpr(ctx: DotExprContext): Int {
        val op1 = visit(ctx.op1)
        val op2 = visit(ctx.op2)

        return when (ctx.op.text) {
            "*" -> op1 * op2
            "/" -> op1 / op2
            else -> 1
        }
    }

    override fun visitLineExpr(ctx: LineExprContext): Int {
        val op1 = visit(ctx.op1)
        val op2 = visit(ctx.op2)

        return when (ctx.op.text) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            else -> 0
        }
    }
}

fun main(args: Array<String>) {

    while (true) {
        print("> ")
        val input = readLine()

        if (input == "exit") break
        if (input.isNullOrBlank()) continue

        val stream = CharStreams.fromString(input + "\n")

        val lexer = CalculatorLexer(stream)
        val tokens = CommonTokenStream(lexer)

        val parser = CalculatorParser(tokens)
        val visitor = CalculatorVisitor()

        val result = visitor.visit(parser.prog())

        println(" = $result")
    }
}