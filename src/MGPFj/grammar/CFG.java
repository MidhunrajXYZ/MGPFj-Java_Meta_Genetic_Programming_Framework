package MGPFj.grammar;

import java.util.*;

/**
 * A class for defining a Context Free Grammar
 */
public final class CFG {
    private final List<Terminal> terminals;
    private final List<NonTerminal> nonTerminals;
    private final List<Production> productions;
    private final NonTerminal startSymbol;

    /**
     * @param nonTerminals an array of {@link NonTerminal} type objects, representing operations.
     * @param terminals an array of {@link Terminal} type objects, representing constants, numbers, etc.
     * @param productions an array of {@link Production} type objects, representing production rules.
     * @param startSymbol the index of the startBuilding symbol from the non MGPFj.terminal array
     */
    private CFG(List<NonTerminal> nonTerminals, List<Terminal> terminals, List<Production> productions, NonTerminal startSymbol) {
        this.nonTerminals = nonTerminals;
        this.terminals = terminals;
        this.productions = productions;
        this.startSymbol = startSymbol;
    }


    public List<Terminal> getTerminals() {
        return terminals;
    }

    public List<NonTerminal> getNonTerminals() {
        return nonTerminals;
    }

    /**
     * Get the startBuilding symbol of the CFG
     * @return startBuilding symbol
     */
    public NonTerminal getStartSymbol() {
        return startSymbol;
    }

    public List<Production> getProductions() {
        return productions;
    }

    /**
     * Returns the Productions, which the lhs non-MGPFj.terminal is the specified one.
     * @param nonTerminal the LHS of the productions
     * @return The set of productions of empty set if none exists
     */
    public List<Production> getProductionsOf(NonTerminal nonTerminal) {
        List<Production> productions = new ArrayList<Production>();

        for (Production production: this.productions) {
            if (production.getLhs().equals(nonTerminal)) {
                productions.add(production);
            }
        }

        return productions;
    }

    public static CFGBuilder startBuilding() {
        return new CFGBuilder();
    }

    public static class CFGBuilder {
        private List<NonTerminal> nonTerminals = new ArrayList<NonTerminal>();
        private List<Terminal> terminals = new ArrayList<Terminal>();
        private List<Production> productions = new ArrayList<Production>();
        private NonTerminal startSymbol;

        private CFGBuilder(){}

        public CFGBuilder addNonTerminals(NonTerminal... nonTerminals) {
            this.nonTerminals.addAll(Arrays.asList(nonTerminals));
            return this;
        }

        public CFGBuilder addTerminals(Terminal... terminals) {
            this.terminals.addAll(Arrays.asList(terminals));
            return this;
        }

        public CFGBuilder addProductions(Production... productions) {
            this.productions.addAll(Arrays.asList(productions));
            return this;
        }

        public CFGBuilder setStartSymbol(NonTerminal startSymbol) {
            this.startSymbol = startSymbol;
            return this;
        }

        public CFG build() {
            //Check integrity

            //check all the tuples have at-least one element
            if (startSymbol == null || nonTerminals.size() == 0 ||
                    terminals.size() == 0 || productions.size() == 0) {
                throw new RuntimeException("CFG do not contain all the required tuples.");
            }

            //check that all the symbols in productions are defined.
            Set<Symbol> productionSymbols = new HashSet<Symbol>();
            for (Production production:productions) {
                productionSymbols.add(production.getLhs());
                productionSymbols.addAll(Arrays.asList(production.getRhs()));
            }
            Set<Symbol> definedSymbols = new HashSet<Symbol>();
            definedSymbols.addAll(nonTerminals);
            definedSymbols.addAll(terminals);

            if (!definedSymbols.containsAll(productionSymbols)) {
                throw new RuntimeException("Productions with undefined symbols");
            }

            //check all non-terminals at-least have one production
            productionSymbols.clear();
            for (Production production:productions) {
                productionSymbols.add(production.getLhs());
            }
            definedSymbols.clear();
            definedSymbols.addAll(nonTerminals);

            if (!productionSymbols.equals(definedSymbols)) {
                throw new RuntimeException("Some NonTerminals do not have productions at all");
            }

            return new CFG(nonTerminals, terminals, productions, startSymbol);
        }

    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Terminals: [");
        for (Terminal terminal:terminals) {
            s.append(terminal).append(", ");
        }
        s.delete(s.length()-2, s.length());
        s.append("]\nNonTerminals: [");
        for (NonTerminal nonTerminal:nonTerminals) {
            s.append(nonTerminal).append(", ");
        }
        s.delete(s.length()-2, s.length());
        s.append("]\nProductions: [");
        for (Production production:productions) {
            s.append(production).append(", ");
        }
        s.delete(s.length()-2, s.length());
        s.append("]\nAnd StartSymbol: [").append(startSymbol).append("]");

        return s.toString();
    }


}
