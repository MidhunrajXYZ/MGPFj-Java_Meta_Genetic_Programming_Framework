package MGPFj.initialize;

import MGPFj.chromosome.Node;
import MGPFj.grammar.NonTerminal;

import java.util.List;

/**
 * Defines a way to generate an initial population from a CFG
 */
public interface Initializer {

    /**
     * Generate a tree-node
     * @param currentDepth From the depth at which the tree is gonna generate
     *                     (perform maxDepth - currentDepth to find maximum allowed depth of new tree).
     * @param rootSymbol the root node of the tree ( if null, the startBuilding symbol is selected as root )
     */
    List<Node> generate(int currentDepth, int popSize, NonTerminal rootSymbol);

}
