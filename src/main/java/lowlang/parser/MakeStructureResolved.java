package lowlang.parser;

public class MakeStructureResolved implements CallLikeResolved {
    public final StructureName structureName;

    public MakeStructureResolved(final StructureName structureName) {
        this.structureName = structureName;
    }

    @Override
    public boolean equals(final Object other) {
        return (other instanceof MakeStructureResolved &&
                structureName.equals(((MakeStructureResolved)other).structureName));
    }

    @Override
    public int hashCode() {
        return structureName.hashCode();
    }

    @Override
    public String toString() {
        return "MakeStructureResolved(" + structureName.toString() + ")";
    }
}
