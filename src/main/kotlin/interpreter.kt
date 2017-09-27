import antlr.CalculatorBaseVisitor
import antlr.CalculatorLexer
import antlr.CalculatorParser
import antlr.CalculatorParser.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream

class CalculatorInterpreter : CalculatorBaseVisitor<Int>() {

    private val memory = mutableMapOf<String, Int>()

    override fun visitIntLitExpr(ctx: IntLitExprContext): Int = ctx.text.toInt()

    override fun visitParensExpr(ctx: ParensExprContext): Int = visit(ctx.expr())

    override fun visitMultiplicativeExpr(ctx: MultiplicativeExprContext): Int {
        val op1 = visit(ctx.expr(0))
        val op2 = visit(ctx.expr(1))

        return when (ctx.op.text) {
            "*" -> op1 * op2
            "/" -> op1 / op2
            else -> 1
        }
    }

    override fun visitAdditiveExpr(ctx: AdditiveExprContext): Int {
        val op1 = visit(ctx.expr(0))
        val op2 = visit(ctx.expr(1))

        return when (ctx.op.text) {
            "+" -> op1 + op2
            "-" -> op1 - op2
            else -> 0
        }
    }

    override fun visitAssignStatement(ctx: AssignStatementContext): Int {
        val id = ctx.ID().text
        val value = visit(ctx.expr())

        memory[id] = value

        return value
    }

    override fun visitEmptyStatement(ctx: EmptyStatementContext): Int = 0

    override fun visitPrintStatement(ctx: PrintStatementContext): Int {
        println(visit(ctx.expr()))
        return 0
    }

    override fun visitIdExpr(ctx: IdExprContext): Int {
        val id = ctx.ID().text

        return memory[id] ?: throw RuntimeException("Line ${ctx.ID().symbol.line}: Variable '$id' not assigned!")
    }

    override fun visitPowerExpr(ctx: PowerExprContext): Int {
        return Math.pow(visit(ctx.expr(0)).toDouble(), visit(ctx.expr(1)).toDouble()).toInt()
    }
}

fun main(args: Array<String>) {

    val prog = """
        a = 2^2^3
        b = 2*(a + 3)
        c = a + b
        c
        """

    val stream = CharStreams.fromString(prog)

    val lexer = CalculatorLexer(stream)
    val tokens = CommonTokenStream(lexer)
    val parser = CalculatorParser(tokens)

    val interpreter = CalculatorInterpreter()
    interpreter.visit(parser.prog())
}