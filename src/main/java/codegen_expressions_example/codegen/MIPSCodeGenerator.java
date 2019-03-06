package codegen_expressions_example.codegen;

import codegen_expressions_example.syntax.*;

import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;

public class MIPSCodeGenerator {
    private final List<MIPSInstruction> instructions;

    public MIPSCodeGenerator() {
        instructions = new ArrayList<MIPSInstruction>();
    }

    public void add(final MIPSInstruction i) {
        instructions.add(i);
    }

    // pushes the contents of this register onto the stack
    public void push(final MIPSRegister register) {
        // addi $sp, $sp, -4
        // sw register, 0($sp)
        final MIPSRegister sp = MIPSRegister.SP;
        add(new Addi(sp, sp, -4));
        add(new Sw(register, 0, sp));
    } // push

    // pops top element of the stack into this register
    public void pop(final MIPSRegister register) {
        // lw register, 0($sp)
        // addi $sp, $sp, 4
        final MIPSRegister sp = MIPSRegister.SP;
        add(new Lw(register, 0, sp));
        add(new Addi(sp, sp, 4));
    } // pop
    
    public void compileIntExp(final IntExp exp) {
        // push this integer onto the stack
        final MIPSRegister t0 = MIPSRegister.T0;
        add(new Li(t0, exp.value));
        push(t0);
    } // compileIntExp

    // boolean: integer that's 0 (false), or 1 (true)
    public void compileBoolExp(final BoolExp exp) {
        final MIPSRegister t0 = MIPSRegister.T0;
        final int value = (exp.value) ? 1 : 0;
        add(new Li(t0, value));
        push(t0);
    } // compileBoolExp

    public void compileOp(final MIPSRegister destination,
                          final MIPSRegister left,
                          final Op op,
                          final MIPSRegister right) {
        if (op instanceof PlusOp) {
            add(new Add(destination, left, right));
        } else if (op instanceof MinusOp) {
            add(new Sub(destination, left, right));
        } else if (op instanceof MultOp) {
            add(new Mult(left, right));
            add(new Mflo(destination));
        } else if (op instanceof DivOp) {
            add(new Div(left, right));
            add(new Mflo(destination));
        } else if (op instanceof EqualsOp) {
            add(new Seq(destination, left, right));
        } else if (op instanceof LessThanOp) {
            add(new Slt(destination, left, right));
        } else {
            assert(false);
        }
    } // compileOp
    
    // compile left, which pushes onto stack
    // compile right, which pushed onto stack
    // pop both, do the operation, then push result onto stack
    public void compileBinopExp(final BinopExp exp) {
        compileExpression(exp.left);
        compileExpression(exp.right);
        final MIPSRegister t0 = MIPSRegister.T0;
        final MIPSRegister t1 = MIPSRegister.T1;
        pop(t0);
        pop(t1);
        compileOp(t0, t0, exp.op, t1);
        push(t0);
    } // compileBinopExp
    
    public void compileExpression(final Exp exp) {
        if (exp instanceof IntExp) {
            compileIntExp((IntExp)exp);
        } else if (exp instanceof BoolExp) {
            compileBoolExp((BoolExp)exp);
        } else if (exp instanceof BinopExp) {
            compileBinopExp((BinopExp)exp);
        } else {
            assert(false);
        }
    } // compileExpression

    public MIPSInstruction[] getInstructions() {
        return instructions.toArray(new MIPSInstruction[instructions.size()]);
    } // getInstructions

    private void mainEnd() {
        // print the thing on top of the stack
        pop(MIPSRegister.A0);
        add(new Li(MIPSRegister.V0, 1));
        add(new Syscall());

        // print a newline
        add(new Li(MIPSRegister.V0, 4));
        add(new La(MIPSRegister.A0, "newline"));
        add(new Syscall());
        
        // exit
        add(new Li(MIPSRegister.V0, 10));
        add(new Syscall());
    } // mainEnd
    
    public void writeCompleteFile(final File file) throws IOException {
        final PrintWriter output =
            new PrintWriter(new BufferedWriter(new FileWriter(file)));
        mainEnd();
        try {
            output.println(".data");
            output.println("newline:");
            output.println(MIPSInstruction.INDENT + ".asciiz \"\\n\"");
            output.println(".text");
            output.println("main:");
            for (final MIPSInstruction instruction : instructions) {
                output.println(instruction.toString());
            }
        } finally {
            output.close();
        }
    } // writeCompleteFile

    public static void writeExpressionToFile(final File file, final Exp exp) throws IOException {
        final MIPSCodeGenerator gen = new MIPSCodeGenerator();
        gen.compileExpression(exp);
        gen.writeCompleteFile(file);
    } // writeExpressionToFile

    public static void main(String[] args) throws IOException {
        // 1 + 2
        final Exp exp = new BinopExp(new IntExp(1), new PlusOp(), new IntExp(2));
        writeExpressionToFile(new File("test.asm"), exp);
    } // main
} // MIPSCodeGenerator

