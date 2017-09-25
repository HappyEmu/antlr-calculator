import antlr.CalculatorBaseVisitor
import antlr.CalculatorLexer
import antlr.CalculatorParser
import antlr.CalculatorParser.*
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import java.io.File

class JasmineCompiler : CalculatorBaseVisitor<Unit>() {
    val builder = File("Calculator.j").printWriter()
    val variables = mutableMapOf<String, Int>()

    override fun visitProg(ctx: CalculatorParser.ProgContext) {
        with(builder) {
            appendln(".bytecode 49.0")
            appendln(".class public Calculator")
            appendln(".super java/lang/Object")

            /*appendln(".method public <init>()V")
            appendln("  aload_0")
            appendln("  invokenonvirtual java/lang/Object/<init>()V")
            appendln("  return")
            appendln(".end method")*/
        }

        with(builder) {
            appendln(".method public static main([Ljava/lang/String;)V")
            appendln("  .limit stack 10")
            appendln("  .limit locals 20")
        }

        visitChildren(ctx)

        with(builder) {
            appendln("  return")
            appendln(".end method")
        }

        builder.close()
    }

    override fun visitAssignStatement(ctx: CalculatorParser.AssignStatementContext) {
        val variable = ctx.ID().text

        if (!variables.containsKey(variable)) {
            variables[variable] = variables.count()
        }

        // Push value of expression onto stack
        visit(ctx.expr())

        with(builder) {
            appendln("  istore ${variables[variable]}")
        }
    }

    override fun visitPrintStatement(ctx: PrintStatementContext) {
        with(builder) {
            appendln("  getstatic java/lang/System/out Ljava/io/PrintStream;")
            visit(ctx.expr())
            appendln("  invokevirtual java/io/PrintStream/println(I)V")
        }
    }

    override fun visitIntLitExpr(ctx: IntLitExprContext) {
        val value = ctx.INT().text.toInt()
        with(builder) {
            appendln("  ldc $value")
        }
    }

    override fun visitIdExpr(ctx: IdExprContext) {
        val variable = ctx.ID().text
        val idx = variables[variable] ?: throw RuntimeException("Variable $variable not found!")

        with(builder) {
            appendln("  iload $idx")
        }
    }

    override fun visitAdditiveExpr(ctx: AdditiveExprContext) {
        visit(ctx.expr(0))
        visit(ctx.expr(1))

        when (ctx.op.text) {
            "+" -> builder.appendln("  iadd")
            "-" -> builder.appendln("  isub")
        }
    }

    override fun visitMultiplicativeExpr(ctx: MultiplicativeExprContext) {
        visit(ctx.expr(0))
        visit(ctx.expr(1))

        when (ctx.op.text) {
            "*" -> builder.appendln("  imul")
            "/" -> builder.appendln("  idiv")
        }
    }

    override fun visitParensExpr(ctx: ParensExprContext) {
        visit(ctx.expr())
    }

    override fun visitEmptyStatement(ctx: EmptyStatementContext?) = Unit

    override fun visitPowerExpr(ctx: PowerExprContext) {
        visit(ctx.expr(0))
        builder.appendln("  i2d")
        visit(ctx.expr(1))
        builder.appendln("  i2d")

        builder.appendln("  invokestatic java/lang/Math/pow(DD)D")
        builder.appendln("  d2i")
    }

}

fun main(args: Array<String>) {
    val prog = """a = 2^2^3
        b = 2*(a + 3)
        c = a + b
        c
        """

    val stream = CharStreams.fromString(prog)

    val lexer = CalculatorLexer(stream)
    val tokens = CommonTokenStream(lexer)
    val parser = CalculatorParser(tokens)

    val compiler = JasmineCompiler()
    compiler.visit(parser.prog())
}