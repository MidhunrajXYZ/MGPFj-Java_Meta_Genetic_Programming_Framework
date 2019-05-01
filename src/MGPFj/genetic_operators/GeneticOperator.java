package MGPFj.genetic_operators;

import MGPFj.chromosome.Node;

import java.util.List;


/**
 * Evolves a set of tree nodes from a set of tree nodes.
 */
public interface GeneticOperator {

    /**
     * @param nodes are the input nodes.
     * @return the generated nodes.
     */
    List<Node> evolve(List<Node> nodes);
}
