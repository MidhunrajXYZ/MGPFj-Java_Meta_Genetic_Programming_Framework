package MGPFj.fitness;

import MGPFj.chromosome.Node;

/**
 * Finds the MGPFj.fitness value of a node.
 */
public interface FitnessFunction {

    /**
     * @param node the tree being evaluated
     * @return the fitness value. 0 is max fitness.
     */
    double evaluate (Node node);
}
