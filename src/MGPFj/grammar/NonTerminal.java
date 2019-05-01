package MGPFj.grammar;

import MGPFj.holder.Arguments;
import MGPFj.holder.Assignments;


/**
 * Represents a Non-MGPFj.terminal
 * Non-terminals in GP are symbols that are used to generate sentences.
 * They are the inner nodes of the syntax trees.
 */
public class NonTerminal implements Symbol{

    private final String symbolName;

    public NonTerminal(String symbolName) {
        this.symbolName = symbolName;
    }

    /**
     * Creates a set of non-MGPFj.terminal objects
     * @param names names of the non-terminals
     * @return an array of NonTerminal objects
     */
    public static NonTerminal[] createNonTerminals(String... names) {
        NonTerminal[] nonTerminals = new NonTerminal[names.length];
        for (int i = 0; i < names.length; i++) {
            nonTerminals[i] = new NonTerminal(names[i]);
        }
        return nonTerminals;
    }

    @Override
    public String getSymbolName() {
        return this.symbolName;
    }

    @Override
    public Object evaluate(Arguments arguments, Assignments assignments) {
        //A non-MGPFj.terminal doesn't hold any value. So...todo
        return null;
    }

    @Override
    public String toString() {
        return getSymbolName();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NonTerminal && ((NonTerminal) o).symbolName.equals(this.symbolName);
    }
}
