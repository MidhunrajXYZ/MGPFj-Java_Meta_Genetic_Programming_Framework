package MGPFj.terminal.variable;

import MGPFj.grammar.Terminal;
import MGPFj.holder.Arguments;
import MGPFj.holder.Assignments;


public class Variable implements Terminal {

    private static int indexCounter = 0;

    private final String symbolName;
    private final int index;

    private Variable(String symbolName, int index) {
        this.symbolName = symbolName;
        this.index = index;
    }

    public static Variable[] createVariables(String... names) {

        Variable[] v = new Variable[names.length];

        for (int i = 0; i < names.length; i++) {
            v[i] = new Variable(names[i], indexCounter++);
        }

        return v;
    }

    @Override
    public Object evaluate(Arguments arguments, Assignments assignments) {
        return assignments.getAssignment(this.index);
    }

    @Override
    public String getSymbolName() {
        return this.symbolName;
    }

    @Override
    public String toString() {
        return getSymbolName();
    }
}
