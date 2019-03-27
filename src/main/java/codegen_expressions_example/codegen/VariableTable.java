package codegen_expressions_example.codegen;

import codegen_expressions_example.syntax.Variable;
import codegen_expressions_example.syntax.Type;

import java.util.LinkedList;

public class VariableTable {
    private LinkedList<VariableEntry> variables;

    public VariableTable() {
        variables = new LinkedList<VariableEntry>();
    }

    // returns a point where we were before the push
    public int pushVariable(final Variable variable,
                            final Type type,
                            final int size) {
        final int resetPoint = variables.size();
        variables.push(new VariableEntry(variable, type, size));
        return resetPoint;
    }

    public void resetTo(final int resetPoint) {
        final int size = variables.size();
        assert(resetPoint <= size);
        for (int cur = size; cur > resetPoint; cur--) {
            variables.pop();
        }
    }

    // gets starting position in memory
    public int variableOffset(final Variable variable) {
        int offset = 0;
        for (final VariableEntry entry : variables) {
            if (entry.variable.equals(variable)) {
                return offset;
            } else {
                offset += entry.size;
            }
        }

        assert(false);
        return offset;
    }

    public int variableSize(final Variable variable) {
        for (final VariableEntry entry : variables) {
            if (entry.variable.equals(variable)) {
                return entry.size;
            }
        }
        assert(false);
        return 0;
    }
} // VariableTable
