package MGPFj.grammar;

import MGPFj.terminal.constant.Constant;
import MGPFj.terminal.operation.Operation;
import MGPFj.terminal.variable.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * represents a production rule <p>
 * a production rule is in BNF form: a =:: b   <p>
 * where 'a' belongs to a non-MGPFj.terminal and 'b' belongs to a combination of both.
 */
public final class Production {
    private final NonTerminal lhs;
    private final Symbol[] rhs;

    public Production(NonTerminal lhs, Symbol... rhsSymbols) {
        this.lhs = lhs;

        List<Symbol> rhsList = new ArrayList<Symbol>(Arrays.asList(rhsSymbols));

        if (this.lhs == null) {
            throw new RuntimeException("Production with no lhs");
        }

        if (rhsList.size() == 0) {
            throw new RuntimeException("Production with no rhs");
        }

        if (rhsList.size() == 1 && rhsList.get(0) instanceof Operation) {
            throw new RuntimeException("Production with no rhs symbols");
        }

        int opCount = 0;
        int opIndex = 0;

        if (rhsList.size() > 1) {
            for (int i = 0; i < rhsList.size(); i++) {
                if (rhsList.get(i) instanceof Operation) {
                    opCount++;
                    opIndex = i;
                }
            }
        }

        if (opCount > 1) {
            throw new RuntimeException("Production with more than one operations");
        }

        //Moving operation to the 1st position
        if (opIndex > 0) {
            Symbol op = rhsList.remove(opIndex);
            rhsList.add(0, op);
        }

        this.rhs = rhsList.toArray(new Symbol[0]);
    }

    public NonTerminal getLhs() {
        return lhs;
    }

    public Symbol[] getRhs() {
        return rhs;
    }

    public static Production[] createVariableProductions(NonTerminal nonTerminal, Variable... variables) {
        Production[] productions = new Production[variables.length];

        for (int i = 0; i < variables.length; i++) {
            productions[i] = new Production(nonTerminal, variables[i]);
        }

        return productions;
    }

    public static Production[] createConstantProductions(NonTerminal nonTerminal, Constant... constants) {
        Production[] productions = new Production[constants.length];

        for (int i = 0; i < constants.length; i++) {
            productions[i] = new Production(nonTerminal, constants[i]);
        }

        return productions;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder(lhs + " =:: ");
        for (Symbol symbol: this.rhs) {
            s.append(symbol.getSymbolName()).append(" ");
        }
        return s.toString();
    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof Production)) return false;

        if (((Production) o).lhs != lhs) return false;

        if (((Production) o).rhs.length != rhs.length) return false;

        for (int i = 0; i < rhs.length; i++) {
            if (rhs[i] != ((Production) o).rhs[i]) return false;
        }

        return true;
    }
}
