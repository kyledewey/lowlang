package lowlang.parser;

import java.util.List;

public class BlockStmt implements Stmt {
    public final List<Stmt> stmts;

    public BlockStmt(final List<Stmt> stmts) {
        this.stmts = stmts;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof BlockStmt &&
                stmts.equals(((BlockStmt)other).stmts));
    }

    @Override
    public int hashCode() {
        return stmts.hashCode();
    }

    @Override
    public String toString() {
        return "BlockStmt(" + stmts.toString() + ")";
    }
}
