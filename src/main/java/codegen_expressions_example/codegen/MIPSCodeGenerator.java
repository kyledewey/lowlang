package codegen_expressions_example.codegen;

import codegen_expressions_example.syntax.*;

import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.ArrayList;

import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.IOException;

// MIPS documentation:
// - MIPS reference card: https://inst.eecs.berkeley.edu/~cs61c/resources/MIPS_Green_Sheet.pdf
// - MIPS system calls: https://www.doc.ic.ac.uk/lab/secondyear/spim/node8.html
//
// You will need SPIM (http://spimsimulator.sourceforge.net/) installed to run this; spim is
// usually available as a binary package though your distribution / Homebrew
//
public class MIPSCodeGenerator {
    private final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs;
    private final List<MIPSInstruction> instructions;
    private final VariableTable variables;
    private int expressionOffset;
    
    public MIPSCodeGenerator(final Map<StructureName, LinkedHashMap<FieldName, Type>> structDecs) {
        this.structDecs = structDecs;
        instructions = new ArrayList<MIPSInstruction>();
        variables = new VariableTable();
        expressionOffset = 0;
    }

    // specifically used in statement contexts, when a statement finishes evaluating an
    // expression inside
    private void resetExpressionOffset() {
        assert(expressionOffset % 4 == 0);
        expressionOffset = 0;
    }
    
    public void compileVariableDeclarationInitializationStmt(final VariableDeclarationInitializationStmt stmt) {
        compileExpression(stmt.exp);
        resetExpressionOffset();
        final VariableDeclaration dec = stmt.varDec;
        // variable's value is now on top of stack
        variables.pushVariable(dec.variable,
                               dec.type,
                               sizeof(dec.type));
    }

    public void compileSequenceStmt(final SequenceStmt stmt) {
        compileStatement(stmt.first);
        compileStatement(stmt.second);
    }
    
    public int lhsOffset(final Lhs lhs) {
        if (lhs instanceof VariableLhs) {
            return variables.variableOffset(((VariableLhs)lhs).variable);
        } else {
            assert(false);
            return 0;
        }
    }

    public int lhsSize(final Lhs lhs) {
        if (lhs instanceof VariableLhs) {
            return variables.variableSize(((VariableLhs)lhs).variable);
        } else {
            assert(false);
            return 0;
        }
    }
    
    public void compileAssignmentStmt(final AssignmentStmt stmt) {
        // establish where we're going to copy
        final int size = lhsSize(stmt.lhs);
        assert(size % 4 == 0);
        final int copyToOffset = lhsOffset(stmt.lhs);
        
        // determine new value
        compileExpression(stmt.exp);
        resetExpressionOffset();
        
        // copy this value into the variable
        // first, deallocate the stack pointer, to line up with the variables
        final MIPSRegister sp = MIPSRegister.SP;
        add(new Addi(sp, sp, size));

        for (int base = 0; base < size; base += 4) {
            final MIPSRegister t0 = MIPSRegister.T0;
            add(new Lw(t0, -(base + 4), sp));
            add(new Sw(t0, copyToOffset + base, sp));
        }
    }

    public void printA0() {
        add(new Li(MIPSRegister.V0, 1));
        add(new Syscall());

        // print a newline
        add(new Li(MIPSRegister.V0, 4));
        add(new La(MIPSRegister.A0, "newline"));
        add(new Syscall());
    }
    
    public void compilePrintStmt(final PrintStmt stmt) {
        compileExpression(stmt.exp);
        resetExpressionOffset();
        
        pop(MIPSRegister.A0);
        printA0();
    }
    
    public void compileStatement(final Stmt stmt) {
        if (stmt instanceof VariableDeclarationInitializationStmt) {
            compileVariableDeclarationInitializationStmt((VariableDeclarationInitializationStmt)stmt);
        } else if (stmt instanceof AssignmentStmt) {
            compileAssignmentStmt((AssignmentStmt)stmt);
        } else if (stmt instanceof SequenceStmt) {
            compileSequenceStmt((SequenceStmt)stmt);
        } else if (stmt instanceof PrintStmt) {
            compilePrintStmt((PrintStmt)stmt);
        } else {
            assert(false);
        }
    }
    
    // for simplicity, bools and chars are 4 bytes
    public int sizeof(final Type type) {
        if (type instanceof IntType ||
            type instanceof BoolType ||
            type instanceof CharType ||
            type instanceof PointerType) { // 32-bit word
            return 4;
        } else if (type instanceof StructureType) {
            final LinkedHashMap<FieldName, Type> fields =
                structDecs.get(((StructureType)type).name);
            int sum = 0;
            for (final Type fieldType : fields.values()) {
                sum += sizeof(fieldType);
            }
            assert(sum >= 0);
            assert(sum % 4 == 0);
            return sum;
        } else {
            assert(false);
            return 0;
        }
    } // sizeof

    public void add(final MIPSInstruction i) {
        instructions.add(i);
    } // add

    // pushes the contents of this register onto the stack
    public void push(final MIPSRegister register) {
        // addi $sp, $sp, -4
        // sw register, 0($sp)
        final MIPSRegister sp = MIPSRegister.SP;
        add(new Addi(sp, sp, -4));
        add(new Sw(register, 0, sp));
        expressionOffset += 4;
    } // push

    // uses $t0 as a temp
    public void pushValue(final int value) {
        pushValue(MIPSRegister.T0, value);
    } // pushValue
    
    public void pushValue(final MIPSRegister temp, final int value) {
        add(new Li(temp, value));
        push(temp);
    } // pushValue
    
    // pops top element of the stack into this register
    public void pop(final MIPSRegister register) {
        // lw register, 0($sp)
        // addi $sp, $sp, 4
        final MIPSRegister sp = MIPSRegister.SP;
        add(new Lw(register, 0, sp));
        add(new Addi(sp, sp, 4));
        expressionOffset -= 4;
    } // pop
    
    public void compileIntExp(final IntExp exp) {
        // push this integer onto the stack
        pushValue(exp.value);
    } // compileIntExp

    // boolean: integer that's 0 (false), or 1 (true)
    public void compileBoolExp(final BoolExp exp) {
        pushValue((exp.value) ? 1 : 0);
    } // compileBoolExp

    // char: integer in the range for a char
    public void compileCharExp(final CharExp exp) {
        pushValue((int)exp.value);
    } // compileCharExp

    public void compileSizeofExp(final SizeofExp exp) {
        pushValue(sizeof(exp.type));
    } // compileSizeof

    public void compileMallocExp(final MallocExp exp) {
        compileExpression(exp.amount);
        pop(MIPSRegister.A0);
        add(new Li(MIPSRegister.V0, 9));
        add(new Syscall());
        push(MIPSRegister.V0);
    } // compileMallocExp

    // This language just passes it along and blindly assumes
    // Depending, on the language, this might do something more
    // clever, like:
    // - Do some conversion (e.g., double -> int)
    // - Emit code that checks at runtime that the type makes sense
    //
    public void compileCastExp(final CastExp exp) {
        compileExpression(exp.exp);
    } // compileCastExp

    public void compileDereferenceExp(final DereferenceExp exp) {
        // since structures have differing size, this can push multiple values
        // on the stack.  Additionally, we need to know what the type of the
        // expression is (thanks typechecker!), which will tell us how much
        // to load in
        final int loadSize = sizeof(exp.getExpType());
        
        // memory address is on top of stack
        compileExpression(exp.exp);

        // this address is now in $t0
        final MIPSRegister t0 = MIPSRegister.T0;
        pop(t0);

        // allocate space on the stack for everything
        final MIPSRegister sp = MIPSRegister.SP;        
        add(new Addi(sp, sp, -loadSize));

        // load in from this address, one word at a time.
        // $t1 is used to read in / write out words.
        // first value of structure will be at offset N, next at N - 4, ... until 0
        final MIPSRegister t1 = MIPSRegister.T1;
        for (int offset = loadSize - 4; offset >= 0; offset -= 4) {
            add(new Lw(t1, offset, t0));
            add(new Sw(t1, offset, sp));
        }
    } // compileDereferenceExp
    
    public void compileMakeStructureExp(final MakeStructureExp exp) {
        // each parameter is pushed onto the stack
        // note that by evaluating left-to-right, this means that the
        // _last_ value on the structure will appear on top of the stack
        for (final Exp parameter : exp.parameters) {
            compileExpression(parameter);
        }
    } // compileMakeStructureExp

    public int fieldOffset(final StructureName structureName,
                           final FieldName fieldName) {
        // last value has offset zero
        final LinkedHashMap<FieldName, Type> fields =
            structDecs.get(structureName);
        assert(fields != null);

        int offset = (fields.size() - 1) * 4;
        for (final FieldName currentFieldName : fields.keySet()) {
            if (currentFieldName.equals(fieldName)) {
                return offset;
            }
            offset -= 4;
        }

        assert(false);
        return 0;
    } // fieldOffset
            
    public void compileFieldAccessExp(final FieldAccessExp exp) {
        // access a given field of a structure
        // will consume the entire structure on the stack
        final StructureName structName = exp.getExpStructure();
        final int wholeStructureSize = sizeof(new StructureType(structName));
        final int offset = fieldOffset(structName, exp.field);
        final int accessSize = sizeof(structDecs.get(structName).get(exp.field));
        
        // structure will be on the stack afterward
        compileExpression(exp.exp);

        // TRICKY BIT: accessSize is arbitrarily large.  As such, we need to
        // copy from the offset to where the top of the stack _will be_ after
        // we deallocate
        final int finalSpMove = wholeStructureSize - accessSize;
        assert(finalSpMove >= 0);
        
        final MIPSRegister sp = MIPSRegister.SP;
        final MIPSRegister t0 = MIPSRegister.T0;
        for (int subAmount = 0; subAmount < accessSize; subAmount += 4) {
            add(new Lw(t0, offset - subAmount, sp));
            add(new Sw(t0, finalSpMove - subAmount, sp));
        }

        add(new Addi(sp, sp, finalSpMove));
    } // compileFieldAccessExp        
        
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
        pop(t1); // right is on top of the stack...
        pop(t0); // followed by left
        compileOp(t0, t0, exp.op, t1);
        push(t0);
    } // compileBinopExp

    public void compileVariableExp(final VariableExp exp) {
        // copy variable's value to top of stack
        final int size = variables.variableSize(exp.variable);
        assert(size % 4 == 0);
        final int copyFromOffset = variables.variableOffset(exp.variable) + expressionOffset;
        
        final MIPSRegister sp = MIPSRegister.SP;
        for (int base = 0; base < size; base += 4) {
            final MIPSRegister t0 = MIPSRegister.T0;
            add(new Lw(t0, copyFromOffset + base, sp));
            add(new Sw(t0, -(base + 4), sp));
        }
        add(new Addi(sp, sp, -size));
        expressionOffset += size;
    }
    
    public void compileExpression(final Exp exp) {
        if (exp instanceof IntExp) {
            compileIntExp((IntExp)exp);
        } else if (exp instanceof BoolExp) {
            compileBoolExp((BoolExp)exp);
        } else if (exp instanceof CharExp) {
            compileCharExp((CharExp)exp);
        } else if (exp instanceof BinopExp) {
            compileBinopExp((BinopExp)exp);
        } else if (exp instanceof VariableExp) {
            compileVariableExp((VariableExp)exp);
        } else if (exp instanceof SizeofExp) {
            compileSizeofExp((SizeofExp)exp);
        } else if (exp instanceof MallocExp) {
            compileMallocExp((MallocExp)exp);
        } else if (exp instanceof CastExp) {
            compileCastExp((CastExp)exp);
        } else if (exp instanceof DereferenceExp) {
            compileDereferenceExp((DereferenceExp)exp);
        } else if (exp instanceof MakeStructureExp) {
            compileMakeStructureExp((MakeStructureExp)exp);
        } else if (exp instanceof FieldAccessExp) {
            compileFieldAccessExp((FieldAccessExp)exp);
        } else {
            assert(false);
        }
    } // compileExpression

    public MIPSInstruction[] getInstructions() {
        return instructions.toArray(new MIPSInstruction[instructions.size()]);
    } // getInstructions

    private void mainEnd(final boolean printTopOfStack) {
        if (printTopOfStack) {
            pop(MIPSRegister.A0);
            printA0();
        }
        
        // exit
        add(new Li(MIPSRegister.V0, 10));
        add(new Syscall());
    } // mainEnd
    
    public void writeCompleteFile(final File file,
                                  final boolean printTopOfStack) throws IOException {
        final PrintWriter output =
            new PrintWriter(new BufferedWriter(new FileWriter(file)));
        mainEnd(printTopOfStack);
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
} // MIPSCodeGenerator

